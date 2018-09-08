package br.ucdb.pos.engenhariasoftware.testesoftware.controller;

import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.Categoria;
import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.TipoLancamento;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.*;

class LancamentoControllerHandler {

    private static final String URL = "http://localhost:8080";

    private static final String URL_LANCAMENTO = URL + "/lancamento";

    private static final String URL_BUSCA_LANCAMENTOS = URL + "/buscaLancamentos";

    private static final String URL_SALVAR_LANCAMENTO = URL + "/salvar";

    private static final String URL_REMOVER_LANCAMENTO = URL + "/remover";

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DecimalFormat decimalFormat = new DecimalFormat("#,00");


    private final String prefixo;

    LancamentoControllerHandler(String prefixo) {
        this.prefixo = prefixo;
        decimalFormat.setParseBigDecimal(true);
    }

    Response criaLancamento(TipoLancamento tipoLancamento, String descricao, LocalDate data, BigDecimal valor, Categoria categoria) {
        return given().config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)))
                .formParam("id", 0)
                .formParam("tipoLancamento", tipoLancamento.name())
                .formParam("descricao", prefixo + " " + descricao)
                .formParam("dataLancamento", data.format(dateFormat))
                .formParam("valor", decimalFormat.format(valor))
                .formParam("categoria", categoria.name())
                .post(URL_SALVAR_LANCAMENTO);
    }

    Response buscaLancamentos() {
        return this.buscaLancamentos("");
    }

    Response buscaLancamentos(String busca) {
        return given().body(prefixo + " " + busca).post(URL_BUSCA_LANCAMENTOS);
    }


    List<Response> removeTodosLancamentos() {
        return this.buscaLancamentos().jsonPath().getList("lancamentos.id", Integer.class).stream().map(id -> this.removeLancamento(id)).collect(toList());
    }

    private Response removeLancamento(int id) {
        return given().get(URL_REMOVER_LANCAMENTO + "/" + id);
    }
}
