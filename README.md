# IA-Valida - Validador Inteligente de Documentos

Este repositório contém a implementação completa do microsserviço de **Validação Inteligente de Documentos**, desenvolvido em parceria com o programa Avanade/GrowUp.

## 🚀 Diferenciais da Solução
- **Upload Real de Arquivos (`MultipartFile`)**: Suporte a upload direto de PDFs e imagens (PNG/JPG).
- **Validação Cruzada de CEP (ViaCEP)**: Integração via REST com a API dos Correios (`viacep.com.br`) para verificar a autenticidade do CEP e município.
- **Validação Matemática de CPF**: Cálculo dos dois dígitos verificadores e detecção de CPFs com sequências repetidas.
- **Análise de validade do Comprovante de Residência (< 90 dias)**: Cálculo de datas via `java.time.LocalDate` e `ChronoUnit.DAYS`.
- **Persistência em Banco de Dados**: Histórico completo de validações armazenado via Spring Data JPA em banco H2 (editável para PostgreSQL).
- **Interface Web Moderna**: Frontend autoral com suporte a Drag & Drop, linha do tempo das 6 etapas e visualização do parecer final.

---

## 🛠️ Arquitetura do Projeto

```text
validador-documentos-avanade/
├── backend/                  # API REST em Java 21 / Spring Boot 3
│   ├── src/main/java/com/avanade/validador/
│   │   ├── ValidadorApplication.java
│   │   ├── controller/      # Endpoints REST (/api/v1/validacao/upload)
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # Entidade JPA (ValidacaoDocumento)
│   │   ├── repository/      # Interface Spring Data JPA
│   │   └── service/         # Regras de Negócio, ViaCEP e Pipeline
│   └── pom.xml
└── frontend/                 # Interface Web
    ├── index.html
    ├── style.css
    └── app.js
```

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos
- **Java 21**
- **Maven 3.8+**
- Navegador Web atualizado

### 1. Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```
O servidor iniciará em `http://localhost:8080`.
Você pode acessar o console do banco H2 em `http://localhost:8080/h2-console`.

### 2. Frontend
Basta abrir o arquivo `frontend/index.html` em qualquer navegador web.
Arraste um comprovante de residência (PDF, PNG ou JPG) para testar o fluxo de validação automatizado.

---

## 📄 Licença
Projeto desenvolvido para fins acadêmicos e de avaliação técnica no programa Avanade/GrowUp.
