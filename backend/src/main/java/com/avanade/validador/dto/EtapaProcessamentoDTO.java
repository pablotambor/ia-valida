package com.avanade.validador.dto;

public record EtapaProcessamentoDTO(
    int passo,
    String modulo,
    String descricao,
    String resultado
) {}
