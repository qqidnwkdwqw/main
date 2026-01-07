package labSystem.service.impl;

import labSystem.dao.DeviceDao;
import labSystem.dao.RepairDao;
import labSystem.dao.UserDao;
import labSystem.entity.Device;
import labSystem.entity.Repair;
import labSystem.entity.User;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;
import labSystem.service.AuthService;
import labSystem.service.RepairService;
import labSystem.util.ValidationUtil;

import java.util.*;
import java.util.stream.Collectors;

public class RepairServiceImpl implements RepairService {

    private final RepairDao repairDao;
    private final DeviceDao deviceDao;
    private final UserDao userDao;
    private final AuthService authService;

    public RepairServiceImpl(RepairDao repairDao, DeviceDao deviceDao, UserDao userDao, AuthService authService) {
        this.repairDao = repairDao;
        this.deviceDao = deviceDao;
        this.userDao = userDao;
        this.authService = authService;
    }

    //辅助方法 ：填充一张报修单关联数据（设备信息、报修人、处理人）
    private void fillAssociatedData(Repair repair) throws DAOException {
        if (repair == null) return;

        // 设备信息
        Device device = deviceDao.findById(repair.getDeviceId());
        // 报修人信息
        User reporter = userDao.findById(repair.getUserId());
        // 处理人信息
        User resolver = repair.getResolvedBy() != null ? userDao.findById(repair.getResolvedBy()) : null;

        if (device != null) {
            repair.setDeviceCode(device.getDeviceCode());
            repair.setDeviceName(device.getDeviceName());
        }
        if (reporter != null) {
            repair.setUserName(reporter.getUsername());
            repair.setUserRealName(reporter.getRealName());
        }
        if (resolver != null) {
            repair.setResolverName(resolver.getRealName());
        }
    }

    // 创建报修单
    @Override
    public Repair createRepair(String userToken, Repair repair) throws BusinessException, DAOException {
        User currentUser = authService.checkLogin(userToken);
        if (repair.getDeviceId() == null || repair.getDeviceId() <= 0) throw new BusinessException("设备ID无效");
        if (ValidationUtil.isEmpty(repair.getTitle())) throw new BusinessException("报修标题不能为空");
        if (ValidationUtil.isEmpty(repair.getDescription())) throw new BusinessException("问题描述不能为空");

        Device device = deviceDao.findById(repair.getDeviceId());
        if (device == null) throw new BusinessException("设备不存在");

        repair.setUserId(currentUser.getUserId());
        repair.setUpdatedAt(new Date());

        //插入数据，返回报修id
        int newRepairId = repairDao.insert(repair);

        return getRepairById(userToken, newRepairId);
    }

    // 根据报修id查询报修单
    @Override
    public Repair getRepairById(String operatorToken, int repairId) throws BusinessException, DAOException {
        User operator = authService.checkLogin(operatorToken);
        Repair repair = repairDao.findById(repairId);
        if (repair == null) throw new BusinessException("报修单不存在");

        if (!"admin".equals(operator.getUserRole()) && !operator.getUserId().equals(repair.getUserId()) && (repair.getResolvedBy() == null || !operator.getUserId().equals(repair.getResolvedBy()))) {
            throw new BusinessException("权限不足，无法查看此报修单");
        }

        fillAssociatedData(repair);
        return repair;
    }

    // 用户查询自己的报修单列表
    @Override
    public List<Repair> getMyRepairs(String userToken) throws BusinessException, DAOException {
        User currentUser = authService.checkLogin(userToken);

        //根据用户id查找用户所有报修列表
        List<Repair> repairs = repairDao.findByUserId(currentUser.getUserId());

        // 给每个报修单填充关联数据
        repairs.forEach(this::fillAssociatedData);

        return repairs;
    }

    // 管理员查询指定设备的报修单列表
    @Override
    public List<Repair> getRepairsByDevice(String operatorToken, int deviceId) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (deviceId <= 0) throw new BusinessException("设备ID无效");
        List<Repair> repairs = repairDao.findByDeviceId(deviceId);
        repairs.forEach(this::fillAssociatedData);
        return repairs;
    }

    // 管理员查询所有报修单列表（分页）
    @Override
    public List<Repair> getAllRepairs(String adminToken, int page, int pageSize) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (page <= 0 || pageSize <= 0) throw new BusinessException("页码或每页大小无效");
        return repairDao.findByPage(page, pageSize);
    }

    // 技术员处理报修单
    @Override
    public void processRepair(String technicianToken, int repairId) throws BusinessException, DAOException {
        User technician = authService.checkLogin(technicianToken);

        //User暂时没有technician，后续可以拓展
        if (!"admin".equals(technician.getUserRole()) && !"technician".equals(technician.getUserRole())) {
            throw new BusinessException("权限不足，只有技术员或管理员可以处理报修单");
        }

        Repair repair = repairDao.findById(repairId);
        if (repair == null) throw new BusinessException("报修单不存在");
        if (!repair.canBeProcessed()) throw new BusinessException("当前报修单状态为【" + repair.getStatusDisplayName() + "】，无法开始处理");

        repair.setStatus("processing");
        repair.setResolvedBy(technician.getUserId());
        repair.setUpdatedAt(new Date());
        repairDao.update(repair);
    }

    // 技术员标记报修单为已解决
    @Override
    public void resolveRepair(String technicianToken, int repairId, String repairNotes) throws BusinessException, DAOException {
        User technician = authService.checkLogin(technicianToken);
        if (!"admin".equals(technician.getUserRole()) && !"technician".equals(technician.getUserRole())) {
            throw new BusinessException("权限不足，只有技术员或管理员可以解决报修单");
        }

        Repair repair = repairDao.findById(repairId);
        if (repair == null) throw new BusinessException("报修单不存在");
        if (!repair.canBeResolved()) throw new BusinessException("当前报修单状态为【" + repair.getStatusDisplayName() + "】，无法标记为已解决");

        
        repair.setStatus("resolved");
        //维修说明
        repair.setRepairNotes(repairNotes);

        repair.setResolvedAt(new Date());
        repair.setUpdatedAt(new Date());

        repairDao.update(repair);
    }

    // 管理员关闭报修单
    @Override
    public void closeRepair(String operatorToken, int repairId) throws BusinessException, DAOException {
        User operator = authService.checkLogin(operatorToken);
        Repair repair = repairDao.findById(repairId);
        if (repair == null) throw new BusinessException("报修单不存在");

        if (!"admin".equals(operator.getUserRole()) && !operator.getUserId().equals(repair.getUserId())) {
            throw new BusinessException("权限不足，只有管理员或报修人可以关闭此报修单");
        }
        if (!repair.canBeClosed()) throw new BusinessException("当前报修单状态为【" + repair.getStatusDisplayName() + "】，无法关闭");

        repair.setStatus("closed");
        repair.setUpdatedAt(new Date());
        repairDao.update(repair);
    }

    // 管理员查询报修单状态统计
    @Override
    public Map<String, Integer> getRepairStatusStatistics(String adminToken) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        return repairDao.countByStatus();
    }

    // 管理员或技术员根据报修状态查询报修单列表（分页）
    @Override
    public List<Repair> getRepairsByStatus(String operatorToken, String status, int page, int pageSize) throws BusinessException, DAOException {
        User operator = authService.checkLogin(operatorToken);
        if (ValidationUtil.isEmpty(status)) throw new BusinessException("报修状态不能为空");
        if (page <= 0 || pageSize <= 0) throw new BusinessException("页码或每页大小无效");

        List<Repair> allRepairs;
        if ("admin".equals(operator.getUserRole()) || "technician".equals(operator.getUserRole())) {
            allRepairs = repairDao.findAll();
        } 
        // 普通用户只能查看自己的报修单
        else {
            allRepairs = repairDao.findByUserId(operator.getUserId());
        }

        // 过滤报修单状态
        List<Repair> filteredRepairs = allRepairs.stream()
                //过滤条件，只保留状态等于参数status的报修单
                .filter(r -> status.equals(r.getStatus()))
                //将过滤后的结果重新收集为列表
                .collect(Collectors.toList());

        int start = (page - 1) * pageSize;
        if (start >= filteredRepairs.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + pageSize, filteredRepairs.size());
        return filteredRepairs.subList(start, end);
    }

    // 技术员查询已指派的报修单（分页）
    @Override
    public List<Repair> getMyAssignedRepairs(String technicianToken) throws BusinessException, DAOException {
        User technician = authService.checkLogin(technicianToken);
        if (!"technician".equals(technician.getUserRole()) && !"admin".equals(technician.getUserRole())) {
            throw new BusinessException("权限不足，仅技术员或管理员可查看指派的报修单");
        }

        List<Repair> allRepairs = repairDao.findAll();
        return allRepairs.stream()
                .filter(r -> technician.getUserId().equals(r.getResolvedBy()) && !r.isClosed())
                .collect(Collectors.toList());
    }

    // 管理员更新报修单严重程度
    @Override
    public void updateRepairSeverity(String adminToken, int repairId, String severity) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (!List.of("low", "medium", "high", "critical").contains(severity)) {
            throw new BusinessException("严重程度值无效");
        }

        Repair repair = repairDao.findById(repairId);
        if (repair == null) throw new BusinessException("报修单不存在");

        repair.setSeverity(severity);
        repair.setUpdatedAt(new Date());
        repairDao.update(repair);
    }

    // 报修人或管理员取消报修单
    @Override
    public void cancelRepair(String operatorToken, int repairId) throws BusinessException, DAOException {
        User operator = authService.checkLogin(operatorToken);
        Repair repair = repairDao.findById(repairId);
        if (repair == null) throw new BusinessException("报修单不存在");

        if (!"admin".equals(operator.getUserRole()) && !operator.getUserId().equals(repair.getUserId())) {
            throw new BusinessException("权限不足，仅报修人或管理员可取消报修单");
        }
        if (!repair.isPending()) {
            throw new BusinessException("仅待处理状态的报修单可取消");
        }

        repair.setStatus("closed"); // "closed"状态表示取消/关闭
        repair.setUpdatedAt(new Date());
        repairDao.update(repair);
    }

    // 管理员或技术员查询紧急报修单（按创建时间排序）
    @Override
    public List<Repair> getUrgentRepairs(String operatorToken) throws BusinessException, DAOException {
        User operator = authService.checkLogin(operatorToken);

        List<Repair> allRepairs;
        if ("admin".equals(operator.getUserRole()) || "technician".equals(operator.getUserRole())) {
            allRepairs = repairDao.findAll();
        } else {
            allRepairs = repairDao.findByUserId(operator.getUserId());
        }

        return allRepairs.stream()
                .filter(r -> (r.isHighSeverity() || r.isCriticalSeverity()) && !r.isClosed())
                .sorted(Comparator.comparing(Repair::getCreatedAt))
                .collect(Collectors.toList());
    }
}