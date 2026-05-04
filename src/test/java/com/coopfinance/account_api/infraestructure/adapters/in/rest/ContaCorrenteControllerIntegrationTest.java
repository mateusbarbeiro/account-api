package com.coopfinance.account_api.infraestructure.adapters.in.rest;

import com.coopfinance.account_api.infraestructure.BaseIntegrationTest;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.AberturaContaCorrenteRequest;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.DepositoRequest;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.SaqueRequest;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.TransferenciaRequest;
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

    @Nested
    @DisplayName("POST /contas/saque - Saque de Conta Corrente")
    @Sql(scripts = "/db/db_load_saque.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
    @Sql(scripts = "/db/db_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
    class SaqueContaCorrenteTest {

        @Test
        @DisplayName("Deve realizar saque com sucesso")
        void deveRealizarSaqueComSucesso() {
            // Act & Assert: Realizar saque
            var depositoRequest = new SaqueRequest();
            depositoRequest.setNumeroConta(NUMERO_CONTA);
            depositoRequest.setValor(100.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/saque")
            .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("Deve retornar bad request para valor inválido")
        void deveRetornarBadRequestParaValorInvalido() {
            var depositoRequest = new SaqueRequest();
            depositoRequest.setNumeroConta(NUMERO_CONTA);
            depositoRequest.setValor(0.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/saque")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Deve retornar bad request para número de conta inválido")
        void deveRetornarBadRequestParaNumeroContaInvalido() {
            var depositoRequest = new SaqueRequest();
            depositoRequest.setNumeroConta("123");
            depositoRequest.setValor(50.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/saque")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Deve retornar not found para conta inexistente")
        void deveRetornarNotFoundParaContaInexistente() {
            var depositoRequest = new SaqueRequest();
            depositoRequest.setNumeroConta("7992739871-3");
            depositoRequest.setValor(50.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/saque")
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("Deve realizar múltiplos saques na mesma conta")
        void deveRealizarMultiplosSaquesNaMesmaConta() {
            // Act & Assert: Primeiro depósito
            var depositoRequest1 = new SaqueRequest();
            depositoRequest1.setNumeroConta(NUMERO_CONTA);
            depositoRequest1.setValor(50.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest1)
            .when()
                    .post("/contas/saque")
            .then()
                    .statusCode(200);

            // Act & Assert: Segundo depósito
            var depositoRequest2 = new SaqueRequest();
            depositoRequest2.setNumeroConta(NUMERO_CONTA);
            depositoRequest2.setValor(75.50);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest2)
            .when()
                    .post("/contas/saque")
            .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("Deve retornar bad request para valor negativo")
        void deveRetornarBadRequestParaValorNegativo() {
            var depositoRequest = new SaqueRequest();
            depositoRequest.setNumeroConta(NUMERO_CONTA);
            depositoRequest.setValor(-50.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/saque")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Deve retornar bad request para valor menor que o mínimo")
        void deveRetornarBadRequestParaValorMenorQueMinimo() {
            var depositoRequest = new SaqueRequest();
            depositoRequest.setNumeroConta(NUMERO_CONTA);
            depositoRequest.setValor(0.001);

            given()
                    .contentType(ContentType.JSON)
                    .body(depositoRequest)
            .when()
                    .post("/contas/saque")
            .then()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("POST /contas/transferencia - Transferência entre Contas Correntes")
    @Sql(scripts = "/db/db_load_transferencia.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
    @Sql(scripts = "/db/db_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
    class TransferenciaContaCorrenteTest {

        public static final String NUMERO_CONTA_ORIGEM = "12345-5";
        public static final String NUMERO_CONTA_DESTINO = "54321-5";

        @Test
        @DisplayName("Deve realizar transferência com sucesso")
        void deveRealizarTransferenciaComSucesso() {
            var transferenciaRequest = new TransferenciaRequest();
            transferenciaRequest.setContaOrigem(NUMERO_CONTA_ORIGEM);
            transferenciaRequest.setContaDestino(NUMERO_CONTA_DESTINO);
            transferenciaRequest.setValor(100.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(transferenciaRequest)
                    .when()
                    .post("/contas/transferencia")
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("Deve retornar bad request para saldo insuficiente")
        void deveRetornarBadRequestParaSaldoInsuficiente() {
            var transferenciaRequest = new TransferenciaRequest();
            transferenciaRequest.setContaOrigem(NUMERO_CONTA_ORIGEM);
            transferenciaRequest.setContaDestino(NUMERO_CONTA_DESTINO);
            transferenciaRequest.setValor(2000.00); // Saldo é 1000.00

            given()
                    .contentType(ContentType.JSON)
                    .body(transferenciaRequest)
                    .when()
                    .post("/contas/transferencia")
                    .then()
                    .statusCode(422)
                    .body("message", equalTo("Saldo insuficiente."));
        }

        @Test
        @DisplayName("Deve retornar not found para conta de origem inexistente")
        void deveRetornarNotFoundParaContaOrigemInexistente() {
            var transferenciaRequest = new TransferenciaRequest();
            transferenciaRequest.setContaOrigem("7992739871-3");
            transferenciaRequest.setContaDestino(NUMERO_CONTA_DESTINO);
            transferenciaRequest.setValor(100.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(transferenciaRequest)
                    .when()
                    .post("/contas/transferencia")
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("Deve retornar not found para conta de destino inexistente")
        void deveRetornarNotFoundParaContaDestinoInexistente() {
            var transferenciaRequest = new TransferenciaRequest();
            transferenciaRequest.setContaOrigem(NUMERO_CONTA_ORIGEM);
            transferenciaRequest.setContaDestino("7992739871-3");
            transferenciaRequest.setValor(100.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(transferenciaRequest)
                    .when()
                    .post("/contas/transferencia")
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("Deve retornar bad request para valor de transferência inválido")
        void deveRetornarBadRequestParaValorTransferenciaInvalido() {
            var transferenciaRequest = new TransferenciaRequest();
            transferenciaRequest.setContaOrigem(NUMERO_CONTA_ORIGEM);
            transferenciaRequest.setContaDestino(NUMERO_CONTA_DESTINO);
            transferenciaRequest.setValor(0.00);

            given()
                    .contentType(ContentType.JSON)
                    .body(transferenciaRequest)
                    .when()
                    .post("/contas/transferencia")
                    .then()
                    .statusCode(400);
        }
    }
}