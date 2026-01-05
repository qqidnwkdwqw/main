package labSystem.exception;

/**
 * 认证模块专属异常
 * 用于处理登录、注册、权限校验等认证场景的业务失败
 */
public class AuthException extends RuntimeException {

    // 无参构造
    public AuthException() {
        super();
    }

    // 带自定义错误信息的构造（最常用）
    public AuthException(String message) {
        super(message);
    }

    // 带错误信息+根异常（用于异常链传递，包装底层异常）
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    // 带根异常的构造
    public AuthException(Throwable cause) {
        super(cause);
    }
}