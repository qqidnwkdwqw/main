package labSystem.util;

import labSystem.config.DBconfig;
import java.sql.*;



/**
 * 数据库连接工具类
 */
public class DButil {
    
    // 私有构造，防止实例化
    private DButil() {}
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // 加载驱动
            Class.forName(DBconfig.getDriver());
            // 创建连接
            conn = DriverManager.getConnection(
                DBconfig.getUrl(),
                DBconfig.getUsername(),
                DBconfig.getPassword()
            );
        } catch (Exception e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
    
    /**
     * 关闭连接
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("关闭连接失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 关闭Statement
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("关闭Statement失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 关闭ResultSet
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("关闭ResultSet失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 关闭所有资源
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }
    
    /**
     * 关闭连接和Statement
     */
    public static void close(Connection conn, Statement stmt) {
        close(stmt);
        close(conn);
    }
    
    /**
     * 开启事务
     */
    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }
    
    /**
     * 提交事务
     */
    public static void commitTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }
    
    /**
     * 回滚事务
     */
    public static void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("回滚事务失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 执行查询（简化版）
     */
    public static ResultSet executeQuery(Connection conn, String sql, Object... params) 
            throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setParameters(pstmt, params);
        return pstmt.executeQuery();
    }
    
    /**
     * 执行更新（增删改）
     */
    public static int executeUpdate(Connection conn, String sql, Object... params) 
            throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        } finally {
            close(pstmt);
        }
    }
    
    /**
     * 设置PreparedStatement参数
     */
    private static void setParameters(PreparedStatement pstmt, Object... params) 
            throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }
    
    /**
     * 查询单条记录（简化版）
     */
    public static <T> T queryOne(Connection conn, String sql, ResultSetHandler<T> handler, Object... params) 
            throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return handler.handle(rs);
            }
            return null;
        } finally {
            close(rs);
            close(pstmt);
        }
    }
    
    /**
     * 查询记录数
     */
    public static int count(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            close(rs);
            close(pstmt);
        }
    }
    
    /**
     * 检查表是否存在
     */
    public static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, tableName, null);
        boolean exists = rs.next();
        close(rs);
        return exists;
    }
    
    /**
     * ResultSet处理器接口
     */
    public interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }
}