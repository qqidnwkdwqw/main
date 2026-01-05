package labSystem.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 * 用于用户密码加密存储
 */
public class MD5Util {
    
    // 私有构造，防止实例化
    private MD5Util() {}
    
    /**
     * MD5加密（32位小写）
     */
    public static String encrypt(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        try {
            // 获取MD5加密实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            // 计算MD5值
            byte[] digest = md.digest(input.getBytes());
            
            // 转换为16进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            // 不应该发生，MD5是所有Java版本都支持的
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * MD5加密（32位大写）
     */
    public static String encryptUpperCase(String input) {
        return encrypt(input).toUpperCase();
    }
    
    /**
     * 加盐MD5加密
     * @param input 原始字符串
     * @param salt 盐值
     */
    public static String encryptWithSalt(String input, String salt) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        if (salt == null) {
            salt = "";
        }
        // 使用固定格式：salt + password + salt
        return encrypt(salt + input + salt);
    }
    
    /**
     * 验证密码
     * @param input 用户输入的密码
     * @param encrypted 数据库中存储的加密密码
     */
    public static boolean verify(String input, String encrypted) {
        if (input == null || encrypted == null) {
            return false;
        }
        return encrypt(input).equals(encrypted);
    }
    
    /**
     * 验证加盐密码
     */
    public static boolean verifyWithSalt(String input, String salt, String encrypted) {
        if (input == null || encrypted == null) {
            return false;
        }
        if (salt == null) {
            salt = "";
        }
        return encrypt(salt + input + salt).equals(encrypted);
    }
    
    /**
     * 生成随机盐值（16位）
     */
    public static String generateSalt() {
        StringBuilder salt = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 16; i++) {
            int index = (int) (Math.random() * chars.length());
            salt.append(chars.charAt(index));
        }
        return salt.toString();
    }
    
    /**
     * 获取MD5特征值（用于快速比较）
     */
    public static String getChecksum(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String md5 = encrypt(input);
        // 取前8位和后8位作为特征值
        return md5.substring(0, 8) + md5.substring(md5.length() - 8);
    }
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        System.out.println("=== MD5加密测试 ===");
        
        // 测试普通加密
        String password = "admin123";
        String encrypted = encrypt(password);
        System.out.println("原始密码: " + password);
        System.out.println("MD5加密: " + encrypted);
        System.out.println("长度: " + encrypted.length());
        
        // 测试验证
        System.out.println("\n验证测试:");
        System.out.println("正确密码验证: " + verify("admin123", encrypted));
        System.out.println("错误密码验证: " + verify("wrongpass", encrypted));
        
        // 测试加盐加密
        String salt = generateSalt();
        String saltedEncrypted = encryptWithSalt(password, salt);
        System.out.println("\n加盐加密:");
        System.out.println("盐值: " + salt);
        System.out.println("加盐MD5: " + saltedEncrypted);
        System.out.println("验证: " + verifyWithSalt(password, salt, saltedEncrypted));
        
        // 常见测试用例
        System.out.println("\n常见测试用例:");
        System.out.println("空字符串: \"" + encrypt("") + "\"");
        System.out.println("123456: " + encrypt("123456"));
        System.out.println("test: " + encrypt("test"));
    }
}