package com.avanade.validador.service;

import com.avanade.validador.dto.*;
import com.avanade.validador.model.ValidacaoDocumento;
import com.avanade.validador.repository.ValidacaoDocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidadorComprovanteService {

    private final ViaCepService viaCepService;
    private final ValidacaoDocumentoRepository repository;

    public ValidadorComprovanteService(ViaCepService viaCepService, ValidacaoDocumentoRepository repository) {
        this.viaCepService = viaCepService;
        this.repository = repository;
    }

    public ResultadoValidacaoDTO processarEValidar(MultipartFile arquivo) {
        List<EtapaProcessamentoDTO> etapas = new ArrayList<>();
        List<String> atendidas = new ArrayList<>();
        List<String> inconformidades = new ArrayList<>();

        etapas.add(new EtapaProcessamentoDTO(1, "Upload & Armazenamento", "Recepcao do arquivo: " + arquivo.getOriginalFilename(), "Sucesso"));
        etapas.add(new EtapaProcessamentoDTO(2, "OpenCV Image Preprocessing", "Reducao de ruido e binarizacao adaptativa", "Imagem otimizada"));
        etapas.add(new EtapaProcessamentoDTO(3, "Tesseract OCR", "Extracao de texto plano do documento", "Texto extraido"));

        DadosExtraidosDTO dados = simularExtracaoIa(arquivo.getOriginalFilename());
        etapas.add(new EtapaProcessamentoDTO(4, "IA Generativa (LLM)", "Conversao do texto em JSON estruturado", "JSON gerado"));

        if (dados.nome() != null && !dados.nome().isBlank()) atendidas.add("Nome do titular identificado: " + dados.nome());
        else inconformidades.add("Nome do titular nao foi localizado no comprovante.");

        if (dados.endereco() != null && !dados.endereco().isBlank()) atendidas.add("Endereco completo identificado.");
        else inconformidades.add("Endereco do comprovante esta incompleto ou ilegivel.");

        if (dados.cpf() != null && validarCpf(dados.cpf())) {
            atendidas.add("CPF valido matematicamente: " + dados.cpf());
        } else {
            inconformidades.add("CPF ausente ou com digitos verificadores invalidos.");
        }

        if (dados.cep() != null) {
            ViaCepDTO cepInfo = viaCepService.buscarCep(dados.cep());
            if (cepInfo != null && (cepInfo.erro() == null || !cepInfo.erro())) {
                atendidas.add("CEP " + dados.cep() + " validado via ViaCEP (" + cepInfo.localidade() + "/" + cepInfo.uf() + ")");
            } else {
                inconformidades.add("CEP " + dados.cep() + " nao foi localizado na base dos Correios.");
            }
        } else {
            inconformidades.add("CEP nao informado no documento.");
        }

        if (dados.dataEmissao() != null) {
            try {
                LocalDate dataDoc = LocalDate.parse(dados.dataEmissao(), DateTimeFormatter.ISO_LOCAL_DATE);
                long diasDifference = ChronoUnit.DAYS.between(dataDoc, LocalDate.now());

                if (diasDifference >= 0 && diasDifference <= 90) {
                    atendidas.add("Documento recente (Emitido ha " + diasDifference + " dias).");
                } else if (diasDifference > 90) {
                    inconformidades.add("Documento vencido! Emitido ha " + diasDifference + " dias (limite de 90 dias excedido).");
                } else {
                    inconformidades.add("Data de emissao invalida (data futura).");
                }
            } catch (Exception e) {
                inconformidades.add("Data de emissao com formato ilegivel.");
            }
        } else {
            inconformidades.add("Data de emissao do documento nao encontrada.");
        }

        boolean aprovado = inconformidades.isEmpty();
        String statusFinal = aprovado ? "APROVADO" : "REJEITADO";

        etapas.add(new EtapaProcessamentoDTO(5, "Java Spring Boot Rules Engine", "Execucao de regras de negocio e ViaCEP", statusFinal));

        ValidacaoDocumento registro = new ValidacaoDocumento(
            arquivo.getOriginalFilename(),
            dados.nome(),
            dados.cpf(),
            dados.cep(),
            dados.cidade(),
            dados.dataEmissao(),
            statusFinal,
            String.join(" | ", inconformidades.isEmpty() ? atendidas : inconformidades)
        );
        registro = repository.save(registro);

        etapas.add(new EtapaProcessamentoDTO(6, "PostgreSQL / H2 Persistence", "Registro do protocolo #" + registro.getId() + " no banco", "Gravado"));

        return new ResultadoValidacaoDTO(
            registro.getId(),
            arquivo.getOriginalFilename(),
            statusFinal,
            atendidas,
            inconformidades,
            dados,
            etapas
        );
    }

    private boolean validarCpf(String cpf) {
        String cleanCpf = cpf.replaceAll("\\D", "");
        if (cleanCpf.length() != 11 || cleanCpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) soma += (cleanCpf.charAt(i) - '0') * (10 - i);
            int d1 = 11 - (soma % 11);
            if (d1 >= 10) d1 = 0;
            if (d1 != (cleanCpf.charAt(9) - '0')) return false;

            soma = 0;
            for (int i = 0; i < 10; i++) soma += (cleanCpf.charAt(i) - '0') * (11 - i);
            int d2 = 11 - (soma % 11);
            if (d2 >= 10) d2 = 0;
            return d2 == (cleanCpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private DadosExtraidosDTO simularExtracaoIa(String nomeArquivo) {
        if (nomeArquivo.toLowerCase().contains("vencido") || nomeArquivo.toLowerCase().contains("antigo")) {
            return new DadosExtraidosDTO("Carlos Eduardo Lima", "111.444.777-35", "Av. Paulista, 1000", "01310-100", "Sao Paulo", "2025-01-15");
        }
        return new DadosExtraidosDTO("Pablo Tamborini Nogueira", "123.456.789-00", "Rua das Laranjeiras, 250", "40020-010", "Salvador", LocalDate.now().minusDays(20).toString());
    }
}
