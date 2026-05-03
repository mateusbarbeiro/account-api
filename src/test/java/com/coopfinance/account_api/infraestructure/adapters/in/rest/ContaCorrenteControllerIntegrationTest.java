package com.coopfinance.account_api.infraestructure.adapters.in.rest;

import com.coopfinance.account_api.infraestructure.BaseIntegrationTest;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.AberturaContaCorrenteRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class ContaCorrenteControllerIntegrationTest extends BaseIntegrationTest {

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
