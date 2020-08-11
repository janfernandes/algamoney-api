package com.estudos.algamoneyapi.resource;


import com.estudos.algamoneyapi.dto.LancamentoEstatisticaCategoria;
import com.estudos.algamoneyapi.dto.LancamentoEstatisticaDia;
import com.estudos.algamoneyapi.event.RecursoCriadoEvent;
import com.estudos.algamoneyapi.exceptionhandler.AlgamoneyExceptionHandler.Erro;
import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.repository.LancamentoRepository;
import com.estudos.algamoneyapi.repository.filter.LancamentoFilter;
import com.estudos.algamoneyapi.service.LancamentoService;
import com.estudos.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping("/estatisticas/por-dia")
    public List<LancamentoEstatisticaDia> porDia(){
        return this.lancamentoRepository.porDia(LocalDate.now());
    }

    @GetMapping("/estatisticas/por-categoria")
    public List<LancamentoEstatisticaCategoria> porCategoria(){
        return this.lancamentoRepository.porCategoria(LocalDate.now());
    }

    @GetMapping
    public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoRepository.filtrar(lancamentoFilter, pageable);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
        return lancamentoRepository.findById(codigo).isPresent() ? ResponseEntity.ok(lancamentoRepository.findById(codigo).get())
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
        Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Lancamento> atualizar(@PathVariable Long codigo, @Valid @RequestBody Lancamento lancamento) {
        Lancamento lancamentoSalvo = lancamentoService.atualizar(codigo, lancamento);
        return ResponseEntity.ok(lancamentoSalvo);
    }

    @ExceptionHandler({ PessoaInexistenteOuInativaException.class })
    public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
        String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long codigo) {
        lancamentoRepository.deleteById(codigo);
    }

    @GetMapping("/relatorios/por-pessoa")
    public ResponseEntity<byte[]> relatorioPorPessoa(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) throws JRException {
        byte[] relatorio = lancamentoService.relatorioPorPessoa(inicio, fim);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE).body(relatorio);
    }

    @PostMapping("/anexo")
    public String uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\ferna\\OneDrive\\Documentos\\anexo --" + anexo.getOriginalFilename());
        fileOutputStream.write(anexo.getBytes());
        fileOutputStream.close();
        return "ok";
    }
}
