package labSystem.service;

import labSystem.entity.User;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 用户管理服务接口
 * 提供对用户信息的创建、查询、更新、删除、状态管理和角色分配等功能
 * 所有操作都包含权限校验
 */
public interface UserService {

    /**
     * 根据用户ID查询用户信息。
     * @param operatorToken 操作用户的会话令牌，用于权限校验。
     * @param userId 要查询的用户ID。
     * @return 用户对象。
     * @throws BusinessException 如果用户不存在或操作用户权限不足。
     * @throws DAOException 如果数据库操作失败。
     */
    User findUserById(String operatorToken, int userId);

    /**
     * 根据用户名查询用户信息（主要用于登录）。
     * @param username 用户名。
     * @return 用户对象，如果不存在则返回null。
     * @throws DAOException 如果数据库操作失败。
     */
    User findUserByUsername(String username);

    /**
     * 根据角色查询用户列表。
     * @param operatorToken 操作用户的会话令牌。
     * @param role 角色名称 ("admin", "teacher", "student")。
     * @return 符合条件的用户列表。
     * @throws BusinessException 如果操作用户权限不足。
     * @throws DAOException 如果数据库操作失败。
     */
    List<User> findUsersByRole(String operatorToken, String role);

    /**
     * 分页查询所有用户。
     * @param operatorToken 操作用户的会话令牌。
     * @param page 页码（从1开始）。
     * @param pageSize 每页大小。
     * @return 用户列表。
     * @throws BusinessException 如果操作用户权限不足。
     * @throws DAOException 如果数据库操作失败。
     */
    List<User> findUsersByPage(String operatorToken, int page, int pageSize);

    /**
     * 高级搜索用户
     * @param operatorToken 操作用户的会话令牌
     * @param searchCriteria 搜索条件，key可以是 "keyword", "role", "status", "department" 等。
     * @return 匹配的用户列表。
     * @throws BusinessException 如果操作用户权限不足或参数无效。
     * @throws DAOException 如果数据库操作失败。
     */
    List<User> searchUsers(String operatorToken, Map<String, String> searchCriteria);

    /**
     * 统计用户总数。
     * @param operatorToken 操作用户的会话令牌。
     * @return 用户总数。
     * @throws BusinessException 如果操作用户权限不足。
     * @throws DAOException 如果数据库操作失败。
     */
    long countUsers(String operatorToken);

    /**
     * 管理员创建新用户。
     * @param adminToken 管理员的会话令牌。
     * @param newUser 包含新用户信息的对象。
     * @return 创建成功的用户对象。
     * @throws BusinessException 如果参数无效或数据库操作失败。
     * @throws DAOException 如果数据库操作失败。
     */
    User createUser(String adminToken, User newUser);

    /**
     * 更新用户信息。
     * 普通用户可以更新自己的个人信息，管理员可以更新任何用户的所有信息。
     * @param operatorToken 操作用户的会话令牌。
     * @param userToUpdate 包含要更新信息的用户对象，必须包含userId。
     * @throws BusinessException 如果用户不存在、权限不足或更新信息无效。
     * @throws DAOException 如果数据库操作失败。
     */
    void updateUser(String operatorToken, User userToUpdate);

    /**
     * 管理员禁用/启用用户账号。
     * @param adminToken 管理员的会话令牌。
     * @param userId 要操作的用户ID。
     * @param newStatus 新的状态 ("active", "inactive", "banned")。
     * @throws BusinessException 如果用户不存在、操作用户非管理员或状态无效。
     * @throws DAOException 如果数据库操作失败。
     */
    void changeUserStatus(String adminToken, int userId, String newStatus);

    /**
     * 管理员重置用户密码。
     * @param adminToken 管理员的会话令牌。
     * @param userId 要重置密码的用户ID。
     * @param newPassword 新的明文密码。
     * @throws BusinessException 如果用户不存在或操作用户非管理员。
     * @throws DAOException 如果数据库操作失败。
     */
    void resetUserPassword(String adminToken, int userId, String newPassword);

    /**
     * 用户自行修改密码。
     * @param operatorToken 操作用户的会话令牌。
     * @param oldPassword 原密码（明文）。
     * @param newPassword 新密码（明文）。
     * @throws BusinessException 密码错误、格式无效或权限不足。
     * @throws DAOException 数据库操作失败。
     */
    void changeOwnPassword(String operatorToken, String oldPassword, String newPassword);

    /**
     * 管理员删除用户。
     * @param adminToken 管理员的会话令牌。
     * @param userId 要删除的用户ID。
     * @throws BusinessException 如果用户不存在或操作用户非管理员。
     * @throws DAOException 如果数据库操作失败。
     */
    void deleteUser(String adminToken, int userId);

    /**
     * 更新用户最后登录时间。
     * @param username 用户名。
     * @throws BusinessException 用户不存在。
     * @throws DAOException 数据库操作失败。
     */
    void updateUserLastLoginTime(String username);

    /**
     * 检查用户状态是否可用。
     * @param userId 用户ID。
     * @return true-可用 false-不可用。
     * @throws BusinessException 用户不存在。
     * @throws DAOException 数据库操作失败。
     */
    boolean checkUserStatusAvailable(int userId);

    /**
     * 用户更新自己的个人资料（如昵称、头像、个人简介等）。
     * @param operatorToken 操作用户的会话令牌。
     * @param profileUpdates 包含要更新的资料字段的Map，例如: {"realName": "新名字", "avatarUrl": "http://..."}。
     * @throws BusinessException 权限不足或参数无效。
     * @throws DAOException 数据库操作失败。
     */
    void updateOwnProfile(String operatorToken, Map<String, String> profileUpdates);
}