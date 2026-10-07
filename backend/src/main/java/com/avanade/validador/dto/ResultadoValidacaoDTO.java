package com.avanade.validador.dto;

import java.util.List;

public record ResultadoValidacaoDTO(
    Long protocolo,
    String nomeArquivo,
    String status,
    List<String> regrasAtendidas,
    List<String> inconformidades,
    DadosExtraidosDTO dadosExtraidos,
    List<EtapaProcessamentoDTO> pipelineEtapas
) {}
