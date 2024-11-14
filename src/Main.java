import control.Controller;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        try {
            controller.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
