package labSystem.service.impl;

import labSystem.dao.DeviceDao;
import labSystem.entity.Device;
import labSystem.entity.User;
import labSystem.exception.BusinessException;

import labSystem.service.AuthService;
import labSystem.service.DeviceService;
import labSystem.util.ValidationUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

//计算日期
import java.util.Calendar;

public class DeviceServiceImpl implements DeviceService {

    private final DeviceDao deviceDao;
    private final AuthService authService;

    public DeviceServiceImpl(DeviceDao deviceDao, AuthService authService) {
        this.deviceDao = deviceDao;
        this.authService = authService;
    }

    // 辅助方法
    private void checkDeviceExistsAndNotScrapped(Device device){
        if (device == null) {
            throw new BusinessException("设备不存在！");
        }
        if (device.getIsDeleted() || "scrapped".equals(device.getStatus())) {
            throw new BusinessException("设备已报废，无法操作！");
        }
    }
    
    @Override
    public Device findDeviceById(String operatorToken, Integer deviceId){
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);
        return device;
    }

    @Override
    public Device findDeviceByCode(String operatorToken, String deviceCode) {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isValidDeviceCode(deviceCode)) {
            throw new BusinessException("设备编号格式错误！");
        }

        Device device = deviceDao.findByCode(deviceCode);
        checkDeviceExistsAndNotScrapped(device);
        return device;
    }

    @Override
    public List<Device> findDevicesByCategory(String operatorToken, Integer categoryId) {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(categoryId)) {
            throw new BusinessException("分类ID无效！");
        }

        return deviceDao.findByCategory(categoryId);
    }

    @Override
    public List<Device> findDevicesByStatus(String operatorToken, String status) {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isValidDeviceStatus(status)) {
            throw new BusinessException("设备状态无效！");
        }

        return deviceDao.findByStatus(status);
    }

    @Override
    public List<Device> findAllDevicesByPage(String adminToken, int page, int pageSize) {
        authService.checkPermission(adminToken, "admin");
        if (!ValidationUtil.isValidPageNumber(page, pageSize)) {
            throw new BusinessException("页码和每页大小必须大于0！");
        }

        return deviceDao.findByPage(page, pageSize);
    }

    @Override
    public List<Device> searchDevices(String operatorToken, String keyword) {
        authService.checkLogin(operatorToken);
        if (ValidationUtil.isEmpty(keyword)) {
            throw new BusinessException("搜索关键词不能为空！");
        }

        return deviceDao.search(keyword);
    }

    //管理员添加设备
    @Override
    public Device addDevice(String adminToken, Device newDevice) {
        authService.checkPermission(adminToken, "admin");

        if (!ValidationUtil.isValidDeviceCode(newDevice.getDeviceCode())) {
            throw new BusinessException("设备编号格式错误！");
        }
        if (ValidationUtil.isEmpty(newDevice.getDeviceName())) {
            throw new BusinessException("设备名称不能为空！");
        }
        if (!ValidationUtil.isPositiveInteger(newDevice.getCategoryId())) {
            throw new BusinessException("分类ID无效！");
        }
        if (ValidationUtil.isEmpty(newDevice.getLocation())) {
            throw new BusinessException("存放位置不能为空！");
        }

        if (deviceDao.findByCode(newDevice.getDeviceCode()) != null) {
            throw new BusinessException("设备编号 '" + newDevice.getDeviceCode() + "' 已存在！");
        }

        //初始化状态
        newDevice.setStatus("available");
        newDevice.setIsDeleted(false);

        //初始化使用统计参数
        newDevice.setTotalUsageCount(0);
        newDevice.setTotalUsageHours(0.0);

        //初始化时间参数
        newDevice.setCreatedAt(new Date());
        newDevice.setUpdatedAt(new Date());

        //添加设备返回设备Id
        int newDeviceId = deviceDao.insert(newDevice);
        if (newDeviceId <= 0) {
            throw new BusinessException("添加设备失败！");
        }
        newDevice.setDeviceId(newDeviceId);
        
        return newDevice;
    }

    @Override
    public void updateDevice(String adminToken, Device deviceToUpdate) {
        authService.checkPermission(adminToken, "admin");

        if (!ValidationUtil.isPositiveInteger(deviceToUpdate.getDeviceId())) {
            throw new BusinessException("设备ID无效！");
        }

        Device dbDevice = deviceDao.findById(deviceToUpdate.getDeviceId());
        checkDeviceExistsAndNotScrapped(dbDevice);

        dbDevice.setDeviceName(deviceToUpdate.getDeviceName());
        dbDevice.setCategoryId(deviceToUpdate.getCategoryId());
        dbDevice.setModel(deviceToUpdate.getModel());
        dbDevice.setBrand(deviceToUpdate.getBrand());
        dbDevice.setSpecifications(deviceToUpdate.getSpecifications());
        dbDevice.setLocation(deviceToUpdate.getLocation());
        dbDevice.setDescription(deviceToUpdate.getDescription());
        dbDevice.setPurchaseDate(deviceToUpdate.getPurchaseDate());
        dbDevice.setPrice(deviceToUpdate.getPrice());
        dbDevice.setWarrantyMonths(deviceToUpdate.getWarrantyMonths());
        dbDevice.setManagerId(deviceToUpdate.getManagerId());
        dbDevice.setUpdatedAt(new Date());

        int rows = deviceDao.update(dbDevice);
        if (rows <= 0) {
            throw new BusinessException("更新设备信息失败！");
        }
    }

    //设置设备为报废状态
    @Override
    public void scrapDevice(String adminToken, Integer deviceId) {
        authService.checkPermission(adminToken, "admin");
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        if (device == null) {
            throw new BusinessException("设备不存在！");
        }
        if (device.getIsDeleted() || "scrapped".equals(device.getStatus())) {
            throw new BusinessException("设备已报废！");
        }
        if ("in_use".equals(device.getStatus())) {
            throw new BusinessException("设备正在使用中，无法修改为报废！");
        }

        //更改状态
        device.setStatus("scrapped");

        //软删除
        device.setIsDeleted(true);
        device.setUpdatedAt(new Date());
        
        int rows = deviceDao.update(device);
        if (rows <= 0) {
            throw new BusinessException("报废设备失败！");
        }
    }

    //设备送修
    //修改设备状态为：maintenance
    @Override
    public void sendDeviceForRepair(String operatorToken, Integer deviceId) {
        User operator = authService.checkLogin(operatorToken);
        if (!"admin".equals(operator.getUserRole()) && !"teacher".equals(operator.getUserRole())) {
            throw new BusinessException("权限不足，只有管理员和教师可以送修设备！");
        }
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);

        if (!"available".equals(device.getStatus())) {
            throw new BusinessException("只有“可用”状态的设备才能送修！");
        }

        int rows = deviceDao.updateStatus(deviceId, "maintenance");
        if (rows <= 0) {
            throw new BusinessException("送修设备失败！");
        }
    }

    //设备维修完成
    @Override
    public void returnDeviceFromRepair(String operatorToken, Integer deviceId){
        User operator = authService.checkLogin(operatorToken);
        if (!"admin".equals(operator.getUserRole()) && !"teacher".equals(operator.getUserRole())) {
            throw new BusinessException("权限不足，只有管理员和教师可以完成维修！");
        }
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);

        if (!"maintenance".equals(device.getStatus())) {
            throw new BusinessException("设备当前状态不是“维修中”！");
        }

        int rows = deviceDao.updateStatus(deviceId, "available");
        if (rows <= 0) {
            throw new BusinessException("完成设备维修失败！");
        }
    }

    //管理员获取设备状态统计
    @Override
    public Map<String, Integer> getDeviceStatusStatistics(String adminToken) {
        authService.checkPermission(adminToken, "admin");
        return deviceDao.countByStatus();
    }

    //管理员恢复报废设备
    @Override
    public void restoreScrappedDevice(String adminToken, Integer deviceId) {
        authService.checkPermission(adminToken, "admin");
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        if (device == null) {
            throw new BusinessException("设备不存在！");
        }
        if (!device.getIsDeleted() || !"scrapped".equals(device.getStatus())) {
            throw new BusinessException("设备未报废，无需恢复！");
        }

        device.setStatus("available");
        device.setIsDeleted(false);
        device.setUpdatedAt(new Date());
        
        int rows = deviceDao.update(device);
        if (rows <= 0) {
            throw new BusinessException("恢复报废设备失败！");
        }
    }

    @Override
    public List<Device> findDevicesByLocation(String operatorToken, String location) {
        authService.checkLogin(operatorToken);
        if (ValidationUtil.isEmpty(location)) {
            throw new BusinessException("设备存放位置不能为空！");
        }
        return deviceDao.findByLocation(location);
    }

    //更新设备使用统计
    @Override
    public void updateDeviceUsageStats(String operatorToken, Integer deviceId, Double usageHours) {
        // 权限校验：管理员/教师可操作
        User operator = authService.checkLogin(operatorToken);
        if (!"admin".equals(operator.getUserRole()) && !"teacher".equals(operator.getUserRole())) {
            throw new BusinessException("权限不足，只有管理员和教师可更新设备使用统计！");
        }
        
        // 参数校验
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }
        if (usageHours == null || usageHours <= 0) {
            throw new BusinessException("使用时长必须大于0！");
        }

        // 设备存在性校验
        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);

        // 更新统计信息
        device.setTotalUsageCount(device.getTotalUsageCount() + 1);
        device.setTotalUsageHours(device.getTotalUsageHours() + usageHours);
        device.setUpdatedAt(new Date());

        int rows = deviceDao.update(device);
        if (rows <= 0) {
            throw new BusinessException("更新设备使用统计失败！");
        }
    }

    //检查设备是否可预约
    @Override
    public boolean checkDeviceReservable(String operatorToken, Integer deviceId){
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        if (device == null) {
            throw new BusinessException("设备不存在！");
        }
        return device.isValidForReservation();
    }

    //管理员批量更新设备存放位置
    @Override
    public void batchUpdateDeviceLocation(String adminToken, List<Integer> deviceIds, String newLocation) {
        authService.checkPermission(adminToken, "admin");
        
        // 参数校验
        if (deviceIds == null || deviceIds.isEmpty()) {
            throw new BusinessException("待更新的设备ID列表不能为空！");
        }
        if (ValidationUtil.isEmpty(newLocation)) {
            throw new BusinessException("新的存放位置不能为空！");
        }

        // 批量更新(统计添加失败的个数)
        int failCount = 0;
        for (Integer deviceId : deviceIds) {
            try {
                if (!ValidationUtil.isPositiveInteger(deviceId)) {
                    failCount++;
                    continue;
                }
                Device device = deviceDao.findById(deviceId);
                if (device == null || device.getIsDeleted()) {
                    failCount++;
                    continue;
                }
                device.setLocation(newLocation);
                device.setUpdatedAt(new Date());
                deviceDao.update(device);
            } catch (Exception e) {
                failCount++;
            }
        }

        //返回更新信息
        if (failCount == deviceIds.size()) {
            throw new BusinessException("批量更新设备位置失败，所有设备均未更新！");
        } else if (failCount > 0) {
            throw new BusinessException("部分设备位置更新失败，共失败" + failCount + "个！");
        }
    }

    //检查设备是否在保修期内
    @Override
    public boolean checkDeviceInWarranty(String operatorToken, Integer deviceId) {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);

        // 无购买日期或无保修期 -->> 判定为超保
        if (device.getPurchaseDate() == null || device.getWarrantyMonths() == null || device.getWarrantyMonths() <= 0) {
            return false;
        }

        // 计算保修期截止日期, 购买日期 + 保修期月数
        Calendar cal = Calendar.getInstance();
        cal.setTime(device.getPurchaseDate());
        cal.add(Calendar.MONTH, device.getWarrantyMonths());
        Date warrantyEndDate = cal.getTime();

        // 对比当前时间
        return new Date().before(warrantyEndDate);
    }
        
    //获取设备使用信息
    @Override
    public String getDeviceUsageInfo(String operatorToken, Integer deviceId){
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);

        return device.getUsageInfo();
    }
}