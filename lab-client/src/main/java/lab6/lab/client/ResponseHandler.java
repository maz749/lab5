//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.client;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import lab6.lab.common.CommandResponse;

public class ResponseHandler {
    public ResponseHandler() {
    }

    public void handleResponse(CommandResponse response) {
        if (response.isSuccess()) {
            System.out.println(response.getMessage());
            if (response.getData() != null) {
                List var10000 = response.getData();
                PrintStream var10001 = System.out;
                Objects.requireNonNull(var10001);
                var10000.forEach(var10001::println);
            }
        } else {
            System.out.println("Ошибка: " + response.getMessage());
        }

    }
}
