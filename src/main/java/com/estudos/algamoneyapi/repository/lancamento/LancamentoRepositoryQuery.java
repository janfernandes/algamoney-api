package com.estudos.algamoneyapi.repository.lancamento;

import com.estudos.algamoneyapi.dto.LancamentoEstatisticaCategoria;
import com.estudos.algamoneyapi.dto.LancamentoEstatisticaDia;
import com.estudos.algamoneyapi.dto.LancamentoEstatisticaPessoa;
import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.repository.filter.LancamentoFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepositoryQuery {

    List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);
    List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);
    List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim);

    Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);

//    Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
}
