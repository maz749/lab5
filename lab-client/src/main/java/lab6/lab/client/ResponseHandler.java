package lab6.lab.client;

import lab6.lab.common.CommandResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ResponseHandler {
    private static final Logger logger = LogManager.getLogger(ResponseHandler.class);

    public void handleResponse(CommandResponse response) {
        if (response.isSuccess()) {
            logger.info("Command executed successfully: {}", response.getMessage());
            System.out.println(response.getMessage());
            if (response.getData() != null && !response.getData().isEmpty()) {
                logger.debug("Received {} data items", response.getData().size());
                response.getData().forEach(System.out::println);
            }
        } else {
            logger.error("Command failed: {}", response.getMessage());
            System.out.println("Ошибка: " + response.getMessage());
        }
    }
}