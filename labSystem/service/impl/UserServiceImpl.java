package labSystem.service.impl;

import labSystem.dao.UserDao;
import labSystem.entity.User;

import labSystem.exception.BusinessException;

import labSystem.service.AuthService;
import labSystem.service.UserService;
import labSystem.util.MD5Util;
import labSystem.util.ValidationUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final AuthService authService;

    public UserServiceImpl(UserDao userDao, AuthService authService) {
        this.userDao = userDao;
        this.authService = authService;
    }

    //根据id查找用户
    @Override
    public User findUserById(String operatorToken, int userId) {
        authService.checkLogin(operatorToken);
        if (userId <= 0) {
            throw new BusinessException("用户ID无效！");
        }
        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在！");
        }
        return user;
    }

    
    @Override
    public User findUserByUsername(String username){
        if (ValidationUtil.isEmpty(username)) {
            throw new BusinessException("用户名不能为空！");
        }
        return userDao.findByUsername(username);
    }

    //查询同一类角色的用户
    @Override
    public List<User> findUsersByRole(String operatorToken, String role) {

        //校验管理员身份
        authService.checkPermission(operatorToken, "admin");

        //校验入参
        validateRole(role);

        return userDao.findByRole(role);
    }

    //分页查询
    @Override
    public List<User> findUsersByPage(String operatorToken, int page, int pageSize) {
        authService.checkPermission(operatorToken, "admin");
        if (page <= 0 || pageSize <= 0) {
            throw new BusinessException("页码或每页大小无效！");
        }
        return userDao.findByPage(page, pageSize);
    }

    //Map<String, String> searchCriteria-->>ui预定义查询键和查询字段（一致且硬编码），只需拼接值
    @Override
    public List<User> searchUsers(String operatorToken, Map<String, String> searchCriteria) {
        authService.checkPermission(operatorToken, "admin");
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            throw new BusinessException("搜索条件不能为空！");
        }
        
        // 适配UserDao的search(String keyword)：将多个条件拼接为一个关键词
        StringBuilder keywordBuilder = new StringBuilder();

        //循环遍历输入的所有值，拼接
        for (String value : searchCriteria.values()) {
            if (!ValidationUtil.isEmpty(value)) {
                keywordBuilder.append(value).append(" ");
            }
        }

        String keyword = keywordBuilder.toString().trim();

        if (ValidationUtil.isEmpty(keyword)) {
            throw new BusinessException("搜索关键词不能为空！");
        }
        
        return userDao.search(keyword);
    }

    @Override
    public long countUsers(String operatorToken){
        authService.checkPermission(operatorToken, "admin");
        return userDao.count();
    }

    @Override
    public User createUser(String adminToken, User newUser) {
        authService.checkPermission(adminToken, "admin");
        
        if (ValidationUtil.isEmpty(newUser.getUsername()) || ValidationUtil.isEmpty(newUser.getPassword()) ||
            ValidationUtil.isEmpty(newUser.getRealName()) || ValidationUtil.isEmpty(newUser.getUserRole())) {
            throw new BusinessException("用户名、密码、真实姓名和角色不能为空！");
        }

        validateRole(newUser.getUserRole());

        if (userDao.findByUsername(newUser.getUsername()) != null) {
            throw new BusinessException("用户名已存在！");
        }

        newUser.setPassword(MD5Util.encrypt(newUser.getPassword()));
        newUser.setStatus(ValidationUtil.isEmpty(newUser.getStatus()) ? "active" : newUser.getStatus());
        newUser.setCreatedAt(new Date());
        newUser.setUpdatedAt(new Date());
        
        int newUserId = userDao.insert(newUser);
        newUser.setUserId(newUserId);
        
        // 返回创建的用户信息，但密码字段清空，防止泄露
        newUser.setPassword(null);
        return newUser;
    }

    @Override
    public void updateUser(String operatorToken, User userToUpdate) {
        User operator = authService.checkLogin(operatorToken);
        if (userToUpdate.getUserId() <= 0) {
            throw new BusinessException("用户ID无效！");
        }

        User dbUser = userDao.findById(userToUpdate.getUserId());
        if (dbUser == null) {
            throw new BusinessException("用户不存在！");
        }

        // 权限判断：只能修改自己，或者管理员可以修改任何人
        if (!"admin".equals(operator.getUserRole()) && operator.getUserId() != dbUser.getUserId()) {
            throw new BusinessException("权限不足，无法修改他人信息！");
        }

        // 非管理员不能修改角色和状态
        if (!"admin".equals(operator.getUserRole())) {
            userToUpdate.setUserRole(null); // 清空角色，防止被普通用户修改
            userToUpdate.setStatus(null);   // 清空状态，防止被普通用户修改
        }

        // 合并更新：只更新传入的非空字段
        if (userToUpdate.getRealName() != null) dbUser.setRealName(userToUpdate.getRealName());
        if (userToUpdate.getEmail() != null) {
            User existingUserByEmail = userDao.findByEmail(userToUpdate.getEmail());
            if (existingUserByEmail != null && existingUserByEmail.getUserId() != dbUser.getUserId()) {
                throw new BusinessException("邮箱已被其他用户使用！");
            }
            dbUser.setEmail(userToUpdate.getEmail());
        }
        if (userToUpdate.getPhone() != null) dbUser.setPhone(userToUpdate.getPhone());
        if (userToUpdate.getDepartment() != null) dbUser.setDepartment(userToUpdate.getDepartment());
        if (userToUpdate.getAvatarUrl() != null) dbUser.setAvatarUrl(userToUpdate.getAvatarUrl());
        if (userToUpdate.getUserRole() != null) {
            validateRole(userToUpdate.getUserRole());
            dbUser.setUserRole(userToUpdate.getUserRole());
        }
        if (userToUpdate.getStatus() != null) dbUser.setStatus(userToUpdate.getStatus());
        
        dbUser.setUpdatedAt(new Date());
        userDao.update(dbUser);
    }

    @Override
    public void changeUserStatus(String adminToken, int userId, String newStatus) {
        authService.checkPermission(adminToken, "admin");
        if (userId <= 0 || ValidationUtil.isEmpty(newStatus)) {
            throw new BusinessException("用户ID或状态不能为空！");
        }

        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在！");
        }
        
        user.setStatus(newStatus);
        user.setUpdatedAt(new Date());
        userDao.update(user);
    }

    @Override
    public void resetUserPassword(String adminToken, int userId, String newPassword) {
        authService.checkPermission(adminToken, "admin");
        if (userId <= 0 || ValidationUtil.isEmpty(newPassword)) {
            throw new BusinessException("用户ID或新密码不能为空！");
        }

        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在！");
        }
        
        user.setPassword(MD5Util.encrypt(newPassword));
        user.setUpdatedAt(new Date());
        userDao.update(user);
    }

    @Override
    public void changeOwnPassword(String operatorToken, String oldPassword, String newPassword) {
        User operator = authService.checkLogin(operatorToken);
        
        if (ValidationUtil.isEmpty(oldPassword) || ValidationUtil.isEmpty(newPassword)) {
            throw new BusinessException("原密码和新密码不能为空！");
        }

        if (oldPassword.equals(newPassword)) {
            throw new BusinessException("新密码不能与原密码相同！");
        }

        User dbUser = userDao.findById(operator.getUserId());
        if (dbUser == null) {
            throw new BusinessException("用户不存在！");
        }

        if (!dbUser.getPassword().equals(MD5Util.encrypt(oldPassword))) {
            throw new BusinessException("原密码错误！");
        }
        
        dbUser.setPassword(MD5Util.encrypt(newPassword));
        dbUser.setUpdatedAt(new Date());
        userDao.update(dbUser);
    }

    @Override
    public void deleteUser(String adminToken, int userId) {
        authService.checkPermission(adminToken, "admin");
        if (userId <= 0) {
            throw new BusinessException("用户ID无效！");
        }

        User userToDelete = userDao.findById(userId);
        if (userToDelete == null) {
            throw new BusinessException("用户不存在！");
        }

        User admin = authService.checkLogin(adminToken);
        if (admin.getUserId() == userId) {
            throw new BusinessException("不能删除自身管理员账号！");
        }
        
        userDao.deleteById(userId);
    }

    @Override
    public void updateUserLastLoginTime(String username) {
        if (ValidationUtil.isEmpty(username)) {
            throw new BusinessException("用户名不能为空！");
        }
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在！");
        }
        user.setLastLoginTime(new Date());
        user.setUpdatedAt(new Date());
        userDao.update(user);
    }

    @Override
    public boolean checkUserStatusAvailable(int userId) {
        if (userId <= 0) {
            throw new BusinessException("用户ID无效！");
        }
        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在！");
        }
        return "active".equals(user.getStatus());
    }

    @Override
    public void updateOwnProfile(String operatorToken, Map<String, String> profileUpdates){
        User operator = authService.checkLogin(operatorToken);
        if (profileUpdates == null || profileUpdates.isEmpty()) {
            throw new BusinessException("更新资料不能为空！");
        }

        User dbUser = userDao.findById(operator.getUserId());
        if (dbUser == null) {
            throw new BusinessException("用户不存在！");
        }

        // 逐个处理更新字段
        if (profileUpdates.containsKey("realName")) {
            dbUser.setRealName(profileUpdates.get("realName"));
        }
        if (profileUpdates.containsKey("email")) {
            String newEmail = profileUpdates.get("email");
            User existingUserByEmail = userDao.findByEmail(newEmail);
            if (existingUserByEmail != null && existingUserByEmail.getUserId() != dbUser.getUserId()) {
                throw new BusinessException("邮箱已被其他用户使用！");
            }
            dbUser.setEmail(newEmail);
        }
        if (profileUpdates.containsKey("phone")) {
            dbUser.setPhone(profileUpdates.get("phone"));
        }
        if (profileUpdates.containsKey("department")) {
            dbUser.setDepartment(profileUpdates.get("department"));
        }
        if (profileUpdates.containsKey("avatarUrl")) {
            dbUser.setAvatarUrl(profileUpdates.get("avatarUrl"));
        }
        
        dbUser.setUpdatedAt(new Date());
        userDao.update(dbUser);
    }
    
    /**
     * 辅助方法：校验角色的合法性
     */
    private void validateRole(String role){
        if (ValidationUtil.isEmpty(role)) {
            throw new BusinessException("角色不能为空！");
        }
        if (!"admin".equals(role) && !"teacher".equals(role) && !"student".equals(role)) {
            throw new BusinessException("角色只能是admin/teacher/student！");
        }
    }
}