package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import clients.OrderClient;
import static org.hamcrest.Matchers.*;

public class OrderListTest {
    private final OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получение списка заказов")
    public void orderListCanBeRetrieved() {
        Response response = orderClient.getList();
        checkResponseSuccessList(response);
    }

    @Step("Проверить успешное получение списка заказов")
    private void checkResponseSuccessList(Response response) {
        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
    }
}