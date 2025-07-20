package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import clients.OrderClient;
import models.Order;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreationTest {
    private final String[] colors;
    private final OrderClient orderClient = new OrderClient();
    private Integer trackId;

    public OrderCreationTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        });
    }

    @After
    public void tearDown() {
        if (trackId != null) {
            orderClient.cancelOrder(trackId);
        }
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    public void orderCanBeCreatedWithDifferentColors() {
        Order order = new Order(colors);
        Response response = orderClient.create(order);
        extractTrackId(response); // Заменяем прямую операцию на вызов шага
        checkResponseSuccessCreation(response);
    }

    @Step("Получить trackId созданного заказа")
    private void extractTrackId(Response response) {
        trackId = response.then().extract().path("track");
    }

    @Step("Проверить успешное создание заказа")
    private void checkResponseSuccessCreation(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}