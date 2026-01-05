package labSystem.service;

import labSystem.entity.User;
import labSystem.exception.AuthException;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;

/**
 * 认证服务接口
 * 定义了用户注册、登录、登出、权限校验和修改密码等核心认证功能。
 */
public interface AuthService {

    /**
     * 用户注册
     * @param user 待注册的用户信息对象。
     * @return 注册成功后的用户对象。
     * @throws BusinessException 如果用户名或邮箱已被注册，或用户输入的信息不符合业务规则。
     * @throws DAOException      如果在数据库操作过程中发生错误。
     */
    User register(User user);

    /**
     * 用户登录
     * @param username 用户输入的用户名。
     * @param password 用户输入的明文密码。
     * @return 登录成功后生成的唯一会话令牌（Token）。
     * @throws AuthException 如果用户名不存在、密码错误，或者用户账号状态异常。
     * @throws DAOException  如果在数据库操作过程中发生错误。
     */
    String login(String username, String password);

    /**
     * 用户退出
     * @param token 登录时获取的会话令牌。
     * @throws AuthException 如果提供的 Token 无效。
     */
    void logout(String token);

    /**
     * 检查用户是否已登录
     * @param token 会话令牌
     * @return 如果用户已登录，返回与该 Token 关联的 User 对象；否则返回 null
     */
    User checkLogin(String token);

    /**
     * 校验用户权限
     * @param token        会话令牌
     * @param requiredRole 执行操作所需的角色
     * @throws AuthException 如果用户未登录，或已登录但角色不满足要求
     */
    void checkPermission(String token, String requiredRole);
    
    /**
     * 修改用户密码
     * @param token       会话令牌
     * @param oldPassword 用户输入的旧密码（明文
     * @param newPassword 用户输入的新密码（明文）
     * @throws AuthException   如果用户未登录，或旧密码验证失败
     * @throws BusinessException 如果新密码不符合业务规则
     * @throws DAOException    如果在数据库更新密码过程中发生错误
     */
    void changePassword(String token, String oldPassword, String newPassword);
}