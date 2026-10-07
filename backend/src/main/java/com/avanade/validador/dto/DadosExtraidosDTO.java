package com.avanade.validador.dto;

public record DadosExtraidosDTO(
    String nome,
    String cpf,
    String endereco,
    String cep,
    String cidade,
    String dataEmissao
) {}
