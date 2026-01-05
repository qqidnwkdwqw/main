package labSystem.exception;

/**
 * DAO层数据操作异常
 * 用于封装数据库连接、SQL执行、结果映射等技术异常
 * 隐藏底层数据库细节，对外暴露统一业务提示
 */
public class DAOException extends RuntimeException {

    // 无参构造
    public DAOException() {
        super();
    }

    // 带自定义错误信息的构造
    public DAOException(String message) {
        super(message);
    }

    // 带错误信息+根异常（核心：包装SQLException等底层技术异常）
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    // 带根异常的构造
    public DAOException(Throwable cause) {
        super(cause);
    }
}