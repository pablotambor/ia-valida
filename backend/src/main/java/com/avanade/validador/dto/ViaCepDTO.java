package com.avanade.validador.dto;

public record ViaCepDTO(
    String cep,
    String logradouro,
    String localidade,
    String uf,
    Boolean erro
) {}
