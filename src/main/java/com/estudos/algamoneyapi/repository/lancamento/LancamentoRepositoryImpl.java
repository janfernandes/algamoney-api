package com.estudos.algamoneyapi.repository.lancamento;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.estudos.algamoneyapi.dto.LancamentoEstatisticaCategoria;
import com.estudos.algamoneyapi.dto.LancamentoEstatisticaDia;
import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.repository.filter.LancamentoFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaDia> criteriaQuery = criteriaBuilder.createQuery(LancamentoEstatisticaDia.class);
        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
        criteriaQuery.select(criteriaBuilder.construct(
                LancamentoEstatisticaDia.class, root.get("tipo"), root.get("dataVencimento"),
                criteriaBuilder.sum(root.get("valor"))));
        LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
        LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
        criteriaQuery.where(
                criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimento"), primeiroDia),
                criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimento"), ultimoDia)
        );
        criteriaQuery.groupBy(root.get("tipo"), root.get("dataVencimento"));
        TypedQuery<LancamentoEstatisticaDia> typedQuery = manager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    @Override
    public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaCategoria> criteriaQuery = criteriaBuilder.createQuery(LancamentoEstatisticaCategoria.class);
        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
        criteriaQuery.select(criteriaBuilder.construct(
                LancamentoEstatisticaCategoria.class, root.get("categoria"),
                criteriaBuilder.sum(root.get("valor"))));
        LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
        LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
        criteriaQuery.where(
                criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimento"), primeiroDia),
                criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimento"), ultimoDia)
        );
        criteriaQuery.groupBy(root.get("categoria"));
        TypedQuery<LancamentoEstatisticaCategoria> typedQuery = manager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    @Override
    public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
        criteria.where(predicates);

        TypedQuery<Lancamento> query = manager.createQuery(criteria);
        adicionarRestricoesDePaginacao(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
    }

    private void adicionarRestricoesDePaginacao(TypedQuery<Lancamento> query, Pageable pageable) {
        int paginaAtual = pageable.getPageNumber();
        int totalRegistrosPorPagina = pageable.getPageSize();
        int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;

        query.setFirstResult(primeiroRegistroDaPagina);
        query.setMaxResults(totalRegistrosPorPagina);
    }

    private Long total(LancamentoFilter lancamentoFilter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));
        return manager.createQuery(criteria).getSingleResult();
    }

    private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
                                        Root<Lancamento> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
            predicates.add(builder.like(
                    builder.lower(root.get("descricao")), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
        }

        if (lancamentoFilter.getDataVencimentoDe() != null) {
            predicates.add(
                    builder.greaterThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoDe()));
        }

        if (lancamentoFilter.getDataVencimentoAte() != null) {
            predicates.add(
                    builder.lessThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoAte()));
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }

}
