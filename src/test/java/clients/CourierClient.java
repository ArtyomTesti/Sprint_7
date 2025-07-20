package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;
import static io.restassured.RestAssured.given;

public class CourierClient {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private static final String CREATE_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";
    private static final String DELETE_PATH = "/api/v1/courier/{id}";

    @Step("Создать нового курьера")
    public Response create(Courier courier) {
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(CREATE_PATH);
    }

    @Step("Авторизовать курьера")
    public Response login(CourierCredentials creds) {
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(creds)
                .when()
                .post(LOGIN_PATH);
    }

    @Step("Удалить курьера с ID {id}")
    public Response delete(int id) {
        return given()
                .baseUri(BASE_URI)
                .pathParam("id", id)
                .when()
                .delete(DELETE_PATH);
    }
}