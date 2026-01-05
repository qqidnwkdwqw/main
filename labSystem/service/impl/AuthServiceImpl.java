    package labSystem.service.impl;

    import labSystem.dao.UserDao;
    import labSystem.entity.User;
    import labSystem.exception.AuthException;
    import labSystem.exception.BusinessException;
    import labSystem.service.AuthService;
    import labSystem.util.MD5Util;
    import labSystem.util.ValidationUtil;

    import java.util.Date;
    import java.util.Map;

    //生成唯一token
    import java.util.UUID;

    import java.util.concurrent.ConcurrentHashMap;

    public class AuthServiceImpl implements AuthService {

        private static final Map<String, User> SESSION_STORE = new ConcurrentHashMap<>();
        private final UserDao userDao;

        public AuthServiceImpl(UserDao userDao) {
            this.userDao = userDao;
        }

        //注册
        @Override
        public User register(User user) {
            //使用ValidationUtil参数校验
            if (!ValidationUtil.isValidUsername(user.getUsername())) {
                throw new BusinessException("用户名格式错误，需为4-20位字母或数字！");
            }
            if (!ValidationUtil.isValidPassword(user.getPassword())) {
                throw new BusinessException("密码格式错误，需为6-20位字母和数字的组合！");
            }
            if (ValidationUtil.isEmpty(user.getRealName())) {
                throw new BusinessException("真实姓名不能为空！");
            }
            if (user.getEmail() != null && !ValidationUtil.isValidEmail(user.getEmail())) {
                throw new BusinessException("邮箱格式错误！");
            }
            if (user.getPhone() != null && !ValidationUtil.isValidPhone(user.getPhone())) {
                throw new BusinessException("手机号格式错误！");
            }

            //检查用户名是否已存在
            if (userDao.findByUsername(user.getUsername()) != null) {
                throw new BusinessException("用户名 '" + user.getUsername() + "' 已被注册！");
            }

            //准备用户数据
            user.setPassword(MD5Util.encrypt(user.getPassword()));
            // 如果角色未提供或无效，则设置默认角色
            if (!ValidationUtil.isValidUserRole(user.getUserRole())) {
                user.setUserRole("student");
            }

            //设置默认初始属性
            user.setStatus("active"); 
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            //插入数据
            int newUserId = userDao.insert(user);
            if (newUserId <= 0) {
                throw new BusinessException("注册失败，数据库插入失败！");
            }
            user.setUserId(newUserId);

            return user;
        }

        //登录，返回token
        @Override
        public String login(String username, String password){
            //使用 ValidationUtil 进行参数校验
            if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
                throw new AuthException("用户名和密码不能为空！");
            }

            //登录验证（查找对应用户）
            User user = userDao.login(username, MD5Util.encrypt(password));

            //处理登录结果
            if (user == null) {
                throw new AuthException("用户名或密码错误！");
            }

            //登录时检查状态
            if (!"active".equals(user.getStatus())) {
                throw new AuthException("账号状态异常：" + user.getStatusDisplayName());
            }

            //更新用户最后登录时间
            userDao.updateLastLogin(user.getUserId());

            //生成并返回Token
            String token = UUID.randomUUID().toString();
            SESSION_STORE.put(token, user);
            return token;
        }

        //更改密码
        @Override
        public void changePassword(String token, String oldPassword, String newPassword) {
            //校验用户登录状态
            User currentUser = checkLogin(token);
            if (currentUser == null) {
                throw new AuthException("您尚未登录！");
            }

            if (ValidationUtil.isEmpty(oldPassword)) {
                throw new BusinessException("旧密码不能为空！");
            }
            if (!ValidationUtil.isValidPassword(newPassword)) {
                throw new BusinessException("新密码格式错误，需为6-20位字母和数字的组合！");
            }
            if (oldPassword.equals(newPassword)) {
                throw new BusinessException("新密码不能与旧密码相同！");
            }

            //验证旧密码是否正确（用户输入旧密码的和数据库中加密后的相比较）
            User dbUser = userDao.findById(currentUser.getUserId());
            if (dbUser == null || !MD5Util.verify(oldPassword, dbUser.getPassword())) {
                throw new AuthException("旧密码错误！");
            }

            //调用DAO层更新密码
            int rows = userDao.changePassword(currentUser.getUserId(), MD5Util.encrypt(newPassword));
            if (rows <= 0) {
                throw new BusinessException("密码修改失败！");
            }

            //修改成功后强制登出
            logout(token);
        }

        //登出
        @Override
        public void logout(String token) {

            if (token == null || !SESSION_STORE.containsKey(token)) {
                throw new AuthException("Token无效，登出失败！");
            }

            //销毁token
            SESSION_STORE.remove(token);
        }

        //验证是否已经登录
        @Override
        public User checkLogin(String token) {

            //根据token查找用户
            User user = SESSION_STORE.get(token);

            if (user == null) {
                throw new AuthException("登录已失效，请重新登录！");
            }

            return user;
        }

        //权限校验
        @Override
        public void checkPermission(String token, String requiredRole) {
            User user = checkLogin(token);
            if (!requiredRole.equals(user.getUserRole())) {
                throw new AuthException("权限不足，无法执行此操作！");
            }
        }
    }