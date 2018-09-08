package br.ucdb.pos.engenhariasoftware.testesoftware.controller;

import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.Categoria;
import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.TipoLancamento;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

import static org.testng.Assert.assertEquals;


public class LancamentoControllerTest {

    private LancamentoControllerHandler handler = new LancamentoControllerHandler("TesteREST");


    @Test
    public void listaComTamanho1() {
        double valor = 100.0;
        handler.removeTodosLancamentos();
        handler.criaLancamento(
                TipoLancamento.SAIDA,
                "Lancamento Cenario 1",
                LocalDate.of(2018, 1, 1),
                BigDecimal.valueOf(valor),
                Categoria.ALIMENTACAO
        );

        assertEquals(getMinimo(handler.buscaLancamentos()), valor);
        assertEquals(getMinimoUsandoJsonPath(handler.buscaLancamentos()), valor);
    }


    @Test
    public void listaComTamanho2Positivos() {
        double valor1 = 100.0;
        double valor2 = 70.0;
        handler.removeTodosLancamentos();

        handler.criaLancamento(
                TipoLancamento.SAIDA,
                "Lancamento Cenario 2 Alimentacao",
                LocalDate.of(2018, 1, 1),
                BigDecimal.valueOf(valor1),
                Categoria.ALIMENTACAO
        );
        handler.criaLancamento(
                TipoLancamento.SAIDA,
                "Lancamento Cenario 2 Emprestimo",
                LocalDate.of(2018, 1, 2),
                BigDecimal.valueOf(valor2),
                Categoria.EMPRESTIMO
        );
        assertEquals(getMinimo(handler.buscaLancamentos()), valor2);
        assertEquals(getMinimoUsandoJsonPath(handler.buscaLancamentos()), valor2);
    }


    @Test
    public void listaComTamanho08Aleatorio() {
        Random rand = new Random();
        int valorMaximo = 1000;
        double menorValor = valorMaximo;
        handler.removeTodosLancamentos();
        for (int i = 0; i < 8; i++) {
            double valor = rand.nextInt(valorMaximo);
            if (valor < menorValor) {
                menorValor = valor;
            }
            Categoria categoria = Categoria.values()[i % Categoria.values().length];
            handler.criaLancamento(
                    i % 2 == 0 ? TipoLancamento.SAIDA : TipoLancamento.ENTRADA,
                    "Lancamento Cenario 4 - " + categoria + " NR:" + i,
                    LocalDate.of(2018, (i % 11) + 1, (i % 28) + 1),
                    BigDecimal.valueOf(valor),
                    categoria
            );
        }

        assertEquals(getMinimo(handler.buscaLancamentos()), menorValor);
        assertEquals(getMinimoUsandoJsonPath(handler.buscaLancamentos()), menorValor);
    }


    private double getMinimoUsandoJsonPath(Response response) {
        return response.jsonPath(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL))
                .getDouble("lancamentos.valor*.replace(',','.')*.toDouble().min()");
    }

    private double getMinimo(Response response) {
        return response.jsonPath().getList("lancamentos.valor*.replace(',','.')", Double.class)
                .stream().mapToDouble(v -> v).min().orElse(0.0);
    }


}
