package labSystem.exception;


/**
 * 通用业务模块异常
 * 用于处理设备、预约、报修、用户管理等非认证类业务失败
 * 支持自定义错误码，适配前后端分离场景
 */
public class BusinessException extends RuntimeException {
    // 自定义业务错误码
    private Integer errorCode;

    // 无参构造
    public BusinessException() {
        super();
    }

    // 仅带错误信息（常用）
    public BusinessException(String message) {
        super(message);
    }

    // 带错误码+错误信息（便于前端判断错误类型）
    public BusinessException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    // 带错误信息+根异常
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    // 带错误码+错误信息+根异常
    public BusinessException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    // 带根异常的构造
    public BusinessException(Throwable cause) {
        super(cause);
    }

    // 获取错误码
    public Integer getErrorCode() {
        return errorCode;
    }

    // 设置错误码（可选）
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}