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

public class CourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private int courierId;
    private boolean isCourierCreated = false;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void tearDown() {
        try {
            if (isCourierCreated && courier != null) {
                getCourierId(); // Получаем ID, если курьер был создан
            }
            if (courierId != 0) {
                courierClient.delete(courierId);
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @Step("Получить ID созданного курьера")
    private void getCourierId() {
        if (courier != null) {
            courierId = courierClient.login(CourierCredentials.from(courier))
                    .then()
                    .extract()
                    .path("id");
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    public void courierCanBeCreated() {
        courier = Courier.getRandom();
        Response response = courierClient.create(courier);
        checkResponseSuccessCreation(response);
        isCourierCreated = true;
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void duplicateCourierCannotBeCreated() {
        courier = Courier.getRandom();
        courierClient.create(courier);
        isCourierCreated = true;

        Response response = courierClient.create(courier);
        checkResponseDuplicateCreation(response);
    }


    @Test
    @DisplayName("Создание курьера без логина")
    public void courierCannotBeCreatedWithoutLogin() {
        courier = new Courier("", "password", "name");
        Response response = courierClient.create(courier);
        checkResponseMissingField(response);
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    public void courierCannotBeCreatedWithoutPassword() {
        courier = new Courier("login", "", "name");
        Response response = courierClient.create(courier);
        checkResponseMissingField(response);
    }

    @Step("Проверить успешное создание курьера")
    private void checkResponseSuccessCreation(Response response) {
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Проверить ошибку при дублировании курьера")
    private void checkResponseDuplicateCreation(Response response) {
        response.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Проверить ошибку при отсутствии обязательного поля")
    private void checkResponseMissingField(Response response) {
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}