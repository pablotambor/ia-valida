package com.avanade.validador.repository;

import com.avanade.validador.model.ValidacaoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidacaoDocumentoRepository extends JpaRepository<ValidacaoDocumento, Long> {}
