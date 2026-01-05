package labSystem.util;

import java.util.regex.Pattern;
import java.sql.Date;

/**
 * 验证工具类（精简版）
 * 实验室管理系统专用
 */
public class ValidationUtil {
    
    private ValidationUtil() {
        throw new IllegalStateException("Utility class");
    }
    
    // ==================== 基础验证 ====================
    
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    // ==================== 用户验证 ====================
    
    // 用户名：4-20位字母数字
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{4,20}$");
    
    // 密码：6-20位，字母和数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,20}$");
    
    // 邮箱
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+@\\w+\\.\\w+$");
    
    // 手机号
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    public static boolean isValidUsername(String username) {
        return isNotEmpty(username) && USERNAME_PATTERN.matcher(username).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }
    
    // 验证用户角色
    public static boolean isValidUserRole(String role) {
        if (isEmpty(role)) return false;
        return role.equals("student") || role.equals("teacher") || 
               role.equals("admin") || role.equals("technician");
    }
    
    // ==================== 设备验证 ====================
    
    // 设备编号：字母数字，3-10位
    private static final Pattern DEVICE_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9]{3,10}$");
    
    public static boolean isValidDeviceCode(String code) {
        return isNotEmpty(code) && DEVICE_CODE_PATTERN.matcher(code).matches();
    }
    
    // 验证设备状态
    public static boolean isValidDeviceStatus(String status) {
        if (isEmpty(status)) return false;
        return status.equals("available") || status.equals("in_use") || 
               status.equals("under_repair") || status.equals("retired");
    }
    
    // ==================== 预约验证 ====================
    
    /**
     * 验证预约时间
     * @return 错误信息，null表示验证通过
     */
    public static String validateReservationTime(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return "时间不能为空";
        }
        
        if (!startTime.before(endTime)) {
            return "开始时间必须早于结束时间";
        }
        
        // 检查预约是否至少1小时
        long diff = endTime.getTime() - startTime.getTime();
        long hours = diff / (1000 * 60 * 60);
        if (hours < 1) {
            return "预约时间至少1小时";
        }
        
        // 最多8小时
        if (hours > 8) {
            return "单次预约不能超过8小时";
        }
        
        return null; // 验证通过
    }
    
    // ==================== 报修验证 ====================
    
    /**
     * 验证报修描述
     */
    public static boolean isValidRepairDescription(String desc) {
        return isNotEmpty(desc) && desc.length() >= 10 && desc.length() <= 500;
    }
    
    /**
     * 验证报修优先级
     */
    public static boolean isValidPriority(int priority) {
        return priority >= 1 && priority <= 3; // 1-低, 2-中, 3-高
    }
    
    // ==================== 数字验证 ====================
    
    public static boolean isPositiveInteger(Integer num) {
        return num != null && num > 0;
    }
    
    public static boolean isNonNegative(Integer num) {
        return num != null && num >= 0;
    }
    
    public static boolean isValidPageNumber(Integer page, Integer pageSize) {
        return page != null && page > 0 && pageSize != null && pageSize > 0;
    }
}