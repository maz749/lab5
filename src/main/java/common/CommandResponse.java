package common;

import java.io.Serializable;
import java.util.List;

/**
 * Класс для представления ответа сервера на команду клиента.
 */
public class CommandResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message; // Сообщение о результате выполнения
    private List<?> data; // Данные (например, список MusicBand для команд show)
    private boolean success; // Флаг успешности выполнения

    public CommandResponse(String message, List<?> data, boolean success) {
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public List<?> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}