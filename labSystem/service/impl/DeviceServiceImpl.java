package labSystem.service.impl;

import labSystem.dao.DeviceDao;
import labSystem.entity.Device;
import labSystem.entity.User;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;
import labSystem.service.AuthService;
import labSystem.service.DeviceService;
import labSystem.util.ValidationUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

public class DeviceServiceImpl implements DeviceService {

    private final DeviceDao deviceDao;
    private final AuthService authService;

    public DeviceServiceImpl(DeviceDao deviceDao, AuthService authService) {
        this.deviceDao = deviceDao;
        this.authService = authService;
    }

    // --- 辅助方法 ---
    private void checkDeviceExistsAndNotScrapped(Device device) throws BusinessException {
        if (device == null) {
            throw new BusinessException("设备不存在！");
        }
        if (device.getIsDeleted() || "scrapped".equals(device.getStatus())) {
            throw new BusinessException("设备已报废，无法操作！");
        }
    }

    //
    @Override
    public Device findDeviceById(String operatorToken, Integer deviceId) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);
        return device;
    }

    @Override
    public Device findDeviceByCode(String operatorToken, String deviceCode) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isValidDeviceCode(deviceCode)) {
            throw new BusinessException("设备编号格式错误！");
        }

        Device device = deviceDao.findByCode(deviceCode);
        checkDeviceExistsAndNotScrapped(device);
        return device;
    }

    @Override
    public List<Device> findDevicesByCategory(String operatorToken, Integer categoryId) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(categoryId)) {
            throw new BusinessException("分类ID无效！");
        }

        return deviceDao.findByCategory(categoryId);
    }

    @Override
    public List<Device> findDevicesByStatus(String operatorToken, String status) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isValidDeviceStatus(status)) {
            throw new BusinessException("设备状态无效！");
        }

        return deviceDao.findByStatus(status);
    }

    @Override
    public List<Device> findAllDevicesByPage(String adminToken, int page, int pageSize) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (!ValidationUtil.isValidPageNumber(page, pageSize)) {
            throw new BusinessException("页码和每页大小必须大于0！");
        }

        return deviceDao.findByPage(page, pageSize);
    }

    @Override
    public List<Device> searchDevices(String operatorToken, String keyword) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (ValidationUtil.isEmpty(keyword)) {
            throw new BusinessException("搜索关键词不能为空！");
        }

        return deviceDao.search(keyword);
    }

    @Override
    public Device addDevice(String adminToken, Device newDevice) throws BusinessException, DAOException {
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

        newDevice.setStatus("available");
        newDevice.setIsDeleted(false);
        newDevice.setTotalUsageCount(0);
        newDevice.setTotalUsageHours(0.0);
        newDevice.setCreatedAt(new Date());
        newDevice.setUpdatedAt(new Date());

        int newDeviceId = deviceDao.insert(newDevice);
        if (newDeviceId <= 0) {
            throw new BusinessException("添加设备失败！");
        }
        newDevice.setDeviceId(newDeviceId);
        
        return newDevice;
    }

    @Override
    public void updateDevice(String adminToken, Device deviceToUpdate) throws BusinessException, DAOException {
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

    @Override
    public void scrapDevice(String adminToken, Integer deviceId) throws BusinessException, DAOException {
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
            throw new BusinessException("设备正在使用中，无法报废！");
        }

        device.setStatus("scrapped");
        device.setIsDeleted(true);
        device.setUpdatedAt(new Date());
        
        int rows = deviceDao.update(device);
        if (rows <= 0) {
            throw new BusinessException("报废设备失败！");
        }
    }

    @Override
    public void sendDeviceForRepair(String operatorToken, Integer deviceId) throws BusinessException, DAOException {
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

    @Override
    public void returnDeviceFromRepair(String operatorToken, Integer deviceId) throws BusinessException, DAOException {
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

    @Override
    public Map<String, Integer> getDeviceStatusStatistics(String adminToken) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        return deviceDao.countByStatus();
    }

    @Override
    public void restoreScrappedDevice(String adminToken, Integer deviceId) throws BusinessException, DAOException {
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
    public List<Device> findDevicesByLocation(String operatorToken, String location) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (ValidationUtil.isEmpty(location)) {
            throw new BusinessException("设备存放位置不能为空！");
        }
        return deviceDao.findByLocation(location);
    }

    @Override
    public void updateDeviceUsageStats(String operatorToken, Integer deviceId, Double usageHours) throws BusinessException, DAOException {
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

    @Override
    public boolean checkDeviceReservable(String operatorToken, Integer deviceId) throws BusinessException, DAOException {
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

    @Override
    public void batchUpdateDeviceLocation(String adminToken, List<Integer> deviceIds, String newLocation) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        
        // 参数校验
        if (deviceIds == null || deviceIds.isEmpty()) {
            throw new BusinessException("待更新的设备ID列表不能为空！");
        }
        if (ValidationUtil.isEmpty(newLocation)) {
            throw new BusinessException("新的存放位置不能为空！");
        }

        // 批量更新
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

        if (failCount == deviceIds.size()) {
            throw new BusinessException("批量更新设备位置失败，所有设备均未更新！");
        } else if (failCount > 0) {
            throw new BusinessException("部分设备位置更新失败，共失败" + failCount + "个！");
        }
    }

    @Override
    public boolean checkDeviceInWarranty(String operatorToken, Integer deviceId) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);

        // 无购买日期或无保修期 → 判定为超保
        if (device.getPurchaseDate() == null || device.getWarrantyMonths() == null || device.getWarrantyMonths() <= 0) {
            return false;
        }

        // 计算保修期截止日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(device.getPurchaseDate());
        cal.add(Calendar.MONTH, device.getWarrantyMonths());
        Date warrantyEndDate = cal.getTime();

        // 对比当前时间
        return new Date().before(warrantyEndDate);
    }

    @Override
    public String getDeviceUsageInfo(String operatorToken, Integer deviceId) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效！");
        }

        Device device = deviceDao.findById(deviceId);
        checkDeviceExistsAndNotScrapped(device);

        return device.getUsageInfo();
    }
}