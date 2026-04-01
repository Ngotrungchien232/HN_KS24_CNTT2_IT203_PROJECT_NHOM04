import presentation.MainMenu;
import util.DBConnection;


import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println(" San sang hoat dong!");
        }
        new MainMenu().show();
        DBConnection.closeConnection();
    }

}
