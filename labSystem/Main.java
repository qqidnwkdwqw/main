package labSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    // MySQL连接配置
    private static final String URL = "jdbc:mysql://localhost:3306/mysql?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; // 你的MySQL用户名
    private static final String PASSWORD = "mmxx159357.120"; // 你的MySQL密码

    public static void main(String[] args) {
        // 1. 加载驱动（MySQL 8.0+可省略，自动加载com.mysql.cj.jdbc.Driver）
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("驱动加载成功！");

            // 2. 建立数据库连接
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("数据库连接成功！");

            // 3. 关闭连接（实际开发中需放在finally块）
           // if (connection != null && !connection.isClosed()) {
           //     connection.close();
           //     System.out.println("连接已关闭！");
           // }
        } catch (ClassNotFoundException e) {
            System.err.println("驱动加载失败：" + e.getMessage());
        } catch (SQLException e) {
            System.err.println("数据库连接失败：" + e.getMessage());
        }
    }
}