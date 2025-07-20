package clients;



import io.restassured.response.Response;

import models.Order;



import static io.restassured.RestAssured.given;



public class OrderClient {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";

    private static final String CREATE_PATH = "/api/v1/orders";

    private static final String LIST_PATH = "/api/v1/orders";





    public Response create(Order order) {

        return given()

                .baseUri(BASE_URI)

                .header("Content-type", "application/json")

                .body(order)

                .when()

                .post(CREATE_PATH);

    }



    public Response getList() {

        return given()

                .baseUri(BASE_URI)

                .when()

                .get(LIST_PATH);

    }

}