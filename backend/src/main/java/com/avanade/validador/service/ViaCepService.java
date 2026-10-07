package com.avanade.validador.service;

import com.avanade.validador.dto.ViaCepDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ViaCepDTO buscarCep(String cep) {
        if (cep == null) return null;
        String cleanCep = cep.replaceAll("\\D", "");
        if (cleanCep.length() != 8) return null;

        try {
            String url = "https://viacep.com.br/ws/" + cleanCep + "/json/";
            return restTemplate.getForObject(url, ViaCepDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
}
