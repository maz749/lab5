//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.client;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.start();
        } catch (Exception var2) {
            Exception e = var2;
            System.out.println("Ошибка при запуске клиента: " + e.getMessage());
            System.exit(1);
        }

    }
}
