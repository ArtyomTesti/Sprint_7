package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import clients.CourierClient;
import models.Courier;
import models.CourierCredentials;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = Courier.getRandom();
        courierClient.create(courier);
        courierId = courierClient.login(CourierCredentials.from(courier))
                .then().extract().path("id");
    }

    @After
    public void tearDown() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    public void courierCanLogin() {
        CourierCredentials creds = CourierCredentials.from(courier);
        Response response = courierClient.login(creds);
        checkResponseSuccessLogin(response);
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    public void courierCannotLoginWithWrongLogin() {
        CourierCredentials creds = new CourierCredentials("wrong", courier.getPassword());
        Response response = courierClient.login(creds);
        checkResponseFailedLogin(response);
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void courierCannotLoginWithWrongPassword() {
        CourierCredentials creds = new CourierCredentials(courier.getLogin(), "wrong");
        Response response = courierClient.login(creds);
        checkResponseFailedLogin(response);
    }

    @Test
    @DisplayName("Авторизация без логина")
    public void courierCannotLoginWithoutLogin() {
        CourierCredentials creds = new CourierCredentials("", courier.getPassword());
        Response response = courierClient.login(creds);
        checkResponseMissingField(response);
    }

    @Test
    @DisplayName("Авторизация без пароля")
    public void courierCannotLoginWithoutPassword() {
        CourierCredentials creds = new CourierCredentials(courier.getLogin(), "");
        Response response = courierClient.login(creds);
        checkResponseMissingField(response);
    }

    @Step("Проверить успешную авторизацию")
    private void checkResponseSuccessLogin(Response response) {
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Проверить ошибку при авторизации")
    private void checkResponseFailedLogin(Response response) {
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Проверить ошибку при отсутствии обязательного поля")
    private void checkResponseMissingField(Response response) {
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}