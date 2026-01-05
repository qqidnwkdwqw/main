package labSystem.config;



import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 数据库配置类
 * 从配置文件读取数据库连接信息
 */
public class DBconfig {
    
    private static Properties props = new Properties();
    
    static {
        loadConfig();
    }
    
    /**
     * 加载配置文件
     */
    private static void loadConfig() {
        InputStream input = null;
        try {
            // 从类路径加载配置文件
            input = DBconfig.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            
            if (input != null) {
                props.load(input);
            } else {
                // 如果文件不存在，使用默认配置
                setDefaultConfig();
            }
        } catch (IOException e) {
            System.err.println("加载配置文件失败，使用默认配置");
            setDefaultConfig();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 设置默认配置
     */
    private static void setDefaultConfig() {
        props.setProperty("db.url", "jdbc:mysql://localhost:3306/lab_management_system?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8");
        props.setProperty("db.username", "root");
        props.setProperty("db.password", "123456");
        props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }
    
    /**
     * 获取数据库URL
     */
    public static String getUrl() {
        return props.getProperty("db.url");
    }
    
    /**
     * 获取用户名
     */
    public static String getUsername() {
        return props.getProperty("db.username");
    }
    
    /**
     * 获取密码
     */
    public static String getPassword() {
        return props.getProperty("db.password");
    }
    
    /**
     * 获取驱动类名
     */
    public static String getDriver() {
        return props.getProperty("db.driver");
    }
    
    /**
     * 获取连接超时时间（秒）
     */
    public static int getConnectionTimeout() {
        return Integer.parseInt(props.getProperty("db.connection.timeout", "30"));
    }
    
    /**
     * 获取最大连接数
     */
    public static int getMaxConnections() {
        return Integer.parseInt(props.getProperty("db.max.connections", "10"));
    }
    
    /**
     * 检查配置是否有效
     */
    public static boolean isValid() {
        String url = getUrl();
        String username = getUsername();
        String password = getPassword();
        String driver = getDriver();
        
        return url != null && !url.trim().isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               driver != null && !driver.trim().isEmpty();
    }
    
    /**
     * 打印配置信息（调试用）
     */
    public static void printConfig() {
        System.out.println("=== 数据库配置 ===");
        System.out.println("URL: " + getUrl());
        System.out.println("用户名: " + getUsername());
        System.out.println("密码: " + (getPassword().length() > 0 ? "***" : "空"));
        System.out.println("驱动: " + getDriver());
        System.out.println("配置有效: " + isValid());
        System.out.println("=================");
    }
    
    /**
     * 重新加载配置（热更新用）
     */
    public static void reload() {
        props.clear();
        loadConfig();
        System.out.println("数据库配置已重新加载");
    }
}