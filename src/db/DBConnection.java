package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null || isConnectionClosed()) {
            try {
                String url = "jdbc:oracle:thin:@localhost:1521:xe";
                String user = "BBK";
                String pwd = "1234";
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conn = DriverManager.getConnection(url, user, pwd);
                System.out.println("데이터베이스 연결 성공");
            } catch (Exception e) {
                System.out.println("데이터베이스 연결 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return conn;
    }

    private static boolean isConnectionClosed() {
        try {
            return conn == null || conn.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public static void close() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("데이터베이스 연결 종료");
                }
            } catch (Exception e) {
                System.out.println("데이터베이스 연결 종료 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
        conn = null;
    }
}