package com.avanade.validador.controller;

import com.avanade.validador.dto.ResultadoValidacaoDTO;
import com.avanade.validador.service.ValidadorComprovanteService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/validacao")
@CrossOrigin(origins = "*")
public class ValidadorController {

    private final ValidadorComprovanteService service;

    public ValidadorController(ValidadorComprovanteService service) {
        this.service = service;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultadoValidacaoDTO> uploadComprovante(@RequestParam("arquivo") MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ResultadoValidacaoDTO resultado = service.processarEValidar(arquivo);
        return ResponseEntity.ok(resultado);
    }
}
