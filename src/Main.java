import presentation.MainMenu;
import presentation.admin.CategoryMenu;
import util.DBConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println(" Sẵn sàng hoạt động!");
        }
        new MainMenu().show();
        new CategoryMenu().show();
        DBConnection.closeConnection();

    }
}