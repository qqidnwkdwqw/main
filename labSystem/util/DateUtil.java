package labSystem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类（精简版）
 */
public class DateUtil {
    
    // 常用格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private DateUtil() {}
    
    // === 核心方法 ===
    
    /** 格式化日期 */
    public static String format(Date date, String pattern) {
        if (date == null) return "";
        return new SimpleDateFormat(pattern).format(date);
    }
    
    /** 格式化为 yyyy-MM-dd */
    public static String formatDate(Date date) {
        return format(date, DATE_FORMAT);
    }
    
    /** 格式化为 yyyy-MM-dd HH:mm:ss */
    public static String formatDateTime(Date date) {
        return format(date, DATE_TIME_FORMAT);
    }
    
    /** 解析字符串为日期 */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return new SimpleDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期格式错误: " + dateStr, e);
        }
    }
    
    /** 解析 yyyy-MM-dd */
    public static Date parseDate(String dateStr) {
        return parse(dateStr, DATE_FORMAT);
    }
    
    /** 解析 yyyy-MM-dd HH:mm:ss */
    public static Date parseDateTime(String dateStr) {
        return parse(dateStr, DATE_TIME_FORMAT);
    }
    
    // === 日期计算 ===
    
    /** 添加天数 */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
    
    /** 添加小时 */
    public static Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }
    
    /** 添加分钟 */
    public static Date addMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }
    
    // === 日期比较 ===
    
    /** 检查是否为同一天 */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
    
    /** 是否为今天 */
    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }
    
    /** 计算相差小时数 */
    public static long hoursBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        long diff = end.getTime() - start.getTime();
        return diff / (1000 * 60 * 60);
    }
    
    // === 数据库转换 ===
    
    /** Java Date → SQL Date */
    public static java.sql.Date toSqlDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()) : null;
    }
    
    /** Java Date → SQL Timestamp */
    public static java.sql.Timestamp toSqlTimestamp(Date date) {
        return date != null ? new java.sql.Timestamp(date.getTime()) : null;
    }
    
    /** SQL Date → Java Date */
    public static Date fromSqlDate(java.sql.Date sqlDate) {
        return sqlDate != null ? new Date(sqlDate.getTime()) : null;
    }
    
    /** SQL Timestamp → Java Date */
    public static Date fromSqlTimestamp(java.sql.Timestamp timestamp) {
        return timestamp != null ? new Date(timestamp.getTime()) : null;
    }
    
    // === 预约系统专用 ===
    
    /** 验证预约时间 */
    public static String validateReservationTime(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) return "时间不能为空";
        
        Date now = new Date();
        
        // 不能预约过去
        if (startTime.before(now)) return "不能预约过去的时间";
        
        // 至少提前1小时
        if (hoursBetween(now, startTime) < 1) return "请至少提前1小时预约";
        
        // 开始<结束
        if (!startTime.before(endTime)) return "开始时间必须早于结束时间";
        
        // 最多8小时
        if (hoursBetween(startTime, endTime) > 8) return "单次预约不能超过8小时";
        
        return null; // 验证通过
    }
    
    /** 获取当前时间字符串（用于日志） */
    public static String getCurrentTimeString() {
        return formatDateTime(new Date());
    }
}