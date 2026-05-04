package com.coopfinance.account_api.infraestructure.adapters.in.rest;

import com.coopfinance.account_api.infraestructure.BaseIntegrationTest;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.AberturaContaCorrenteRequest;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.DepositoRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Conta Corrente Controller")
public class ContaCorrenteControllerIntegrationTest extends BaseIntegrationTest {

    public static final String NUMERO_CONTA = "12345-5";
    public static final String CONTA_UUID = "550e8400-e29b-41d4-a716-446655440000";

    @Nested
    @DisplayName("POST /contas - Abertura de Conta Corrente")
    class AberturaContaCorrenteTest {

        @Test
        @DisplayName("Deve abrir uma conta corrente com sucesso")
        void deveAbrirContaCorrenteComSucesso() {
            AberturaContaCorrenteRequest request = new AberturaContaCorrenteRequest();
            request.setCpfCnpj("12345678909");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post("/contas")
            .then()
                    .statusCode(201)
                    .body("numeroConta", notNullValue())
                    .body("digitoVerificador", notNullValue())
                    .body("cpfCnpj", notNullValue());
        }

        @Test
        @DisplayName("Deve retornar bad request para documento invalido")
        void deveRetornarBadRequestParaDocumentoInvalido() {
            AberturaContaCorrenteRequest request = new AberturaContaCorrenteRequest();
            request.setCpfCnpj("12345");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/contas")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("CPF ou CNPJ inválido."));
        }
    }

    @Nested
    @DisplayName("POST /contas/deposito - Depósito de Conta Corrente")
    @Sql(scripts = "/db/db_load.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
    @Sql(scripts = "/db/db_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
    class DepositoContaCorrenteTest {

        @Test
        @DisplayName("Deve realizar depósito com sucesso")
        void deveRealizarDepositoComSucesso() {
            // Act & Assert: Realizar depósito
            var depositoRequest = new DepositoRequest();
            depositoRequest.setNumeroConta(NUMERO_CONTA);
            depositoRequest.setValor(100.50);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/deposito")
            .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("Deve retornar bad request para valor inválido")
        void deveRetornarBadRequestParaValorInvalido() {
            var depositoRequest = new DepositoRequest();
            depositoRequest.setNumeroConta(NUMERO_CONTA);
            depositoRequest.setValor(0.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/deposito")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Deve retornar bad request para número de conta inválido")
        void deveRetornarBadRequestParaNumeroContaInvalido() {
            var depositoRequest = new DepositoRequest();
            depositoRequest.setNumeroConta("123");
            depositoRequest.setValor(50.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/deposito")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Deve retornar not found para conta inexistente")
        void deveRetornarNotFoundParaContaInexistente() {
            var depositoRequest = new DepositoRequest();
            depositoRequest.setNumeroConta("7992739871-3");
            depositoRequest.setValor(50.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/deposito")
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("Deve realizar múltiplos depósitos na mesma conta")
        void deveRealizarMultiplosDepositosNaMesmaConta() {
            // Act & Assert: Primeiro depósito
            var depositoRequest1 = new DepositoRequest();
            depositoRequest1.setNumeroConta(NUMERO_CONTA);
            depositoRequest1.setValor(50.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest1)
            .when()
                    .post("/contas/deposito")
            .then()
                    .statusCode(200);

            // Act & Assert: Segundo depósito
            var depositoRequest2 = new DepositoRequest();
            depositoRequest2.setNumeroConta(NUMERO_CONTA);
            depositoRequest2.setValor(75.50);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest2)
            .when()
                    .post("/contas/deposito")
            .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("Deve retornar bad request para valor negativo")
        void deveRetornarBadRequestParaValorNegativo() {
            var depositoRequest = new DepositoRequest();
            depositoRequest.setNumeroConta(NUMERO_CONTA);
            depositoRequest.setValor(-50.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/deposito")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Deve retornar bad request para valor menor que o mínimo")
        void deveRetornarBadRequestParaValorMenorQueMinimo() {
            var depositoRequest = new DepositoRequest();
            depositoRequest.setNumeroConta(NUMERO_CONTA);
            depositoRequest.setValor(0.001);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/deposito")
            .then()
                    .statusCode(400);
        }
    }
}
