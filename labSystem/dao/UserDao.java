package labSystem.dao;

import labSystem.entity.User;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户DAO接口
 */
public interface UserDao extends BaseDao<User, Integer> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     * @throws SQLException
     */
    User findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象
     * @throws SQLException
     */
    User findByEmail(String email);
    
    /**
     * 根据角色查询用户
     * @param role 角色
     * @return 用户列表
     * @throws SQLException
     */
    List<User> findByRole(String role);
    
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码（MD5加密后的）
     * @return 用户对象，验证失败返回null
     * @throws SQLException
     */
    User login(String username, String password);
    
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态（active/inactive）
     * @return 影响的行数
     * @throws SQLException
     */
    int updateStatus(int userId, String status);
    
    /**
     * 更新最后登录时间
     * @param userId 用户ID
     * @return 影响的行数
     * @throws SQLException
     */
    int updateLastLogin(int userId) ;
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param newPassword 新密码（MD5加密后的）
     * @return 影响的行数
     * @throws SQLException
     */
    int changePassword(int userId, String newPassword);
    
    /**
     * 搜索用户（用户名、姓名模糊搜索）
     * @param keyword 关键词
     * @return 用户列表
     * @throws SQLException
     */
    List<User> search(String keyword) ;
}