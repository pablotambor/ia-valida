package com.avanade.validador.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_validacao_documento")
public class ValidacaoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;
    private String nomeTitular;
    private String cpf;
    private String cep;
    private String cidade;
    private String dataEmissao;
    private String status;
    
    @Column(length = 1000)
    private String parecer;
    
    private LocalDateTime dataProcessamento;

    public ValidacaoDocumento() {}

    public ValidacaoDocumento(String nomeArquivo, String nomeTitular, String cpf, String cep, String cidade, String dataEmissao, String status, String parecer) {
        this.nomeArquivo = nomeArquivo;
        this.nomeTitular = nomeTitular;
        this.cpf = cpf;
        this.cep = cep;
        this.cidade = cidade;
        this.dataEmissao = dataEmissao;
        this.status = status;
        this.parecer = parecer;
        this.dataProcessamento = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNomeArquivo() { return nomeArquivo; }
    public String getNomeTitular() { return nomeTitular; }
    public String getCpf() { return cpf; }
    public String getCep() { return cep; }
    public String getCidade() { return cidade; }
    public String getDataEmissao() { return dataEmissao; }
    public String getStatus() { return status; }
    public String getParecer() { return parecer; }
    public LocalDateTime getDataProcessamento() { return dataProcessamento; }
}
