package client;

import common.CommandResponse;

/**
 * Класс для обработки ответов от сервера и вывода результатов пользователю.
 */
public class ResponseHandler {
    public void handleResponse(CommandResponse response) {
        if (response.isSuccess()) {
            System.out.println(response.getMessage());
            if (response.getData() != null) {
                response.getData().forEach(System.out::println);
            }
        } else {
            System.out.println("Ошибка: " + response.getMessage());
        }
    }
}