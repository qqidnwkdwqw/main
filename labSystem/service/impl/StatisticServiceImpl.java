package labSystem.service.impl;

import labSystem.dao.StatisticDao;
import labSystem.entity.statisticData;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;
import labSystem.service.AuthService;
import labSystem.service.StatisticService;
import labSystem.util.ValidationUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticServiceImpl implements StatisticService {

    private final StatisticDao statisticDao;
    private final AuthService authService;

    public StatisticServiceImpl(StatisticDao statisticDao, AuthService authService) {
        this.statisticDao = statisticDao;
        this.authService = authService;
    }

    @Override
    public statisticData.SystemOverview getSystemOverview(String adminToken) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        statisticData data = statisticDao.getSystemOverview();
        return data.getSystemOverview();
    }

    @Override
    public List<statisticData.DeviceUsageStats> getDeviceUsageStats(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);
        List<statisticData> dataList = statisticDao.getDeviceUsageStats(request.getStartDate(), request.getEndDate());
        return convertToDeviceUsageStatsList(dataList);
    }

    @Override
    public List<statisticData.ReservationTrend> getReservationTrend(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);
        if (request.getGroupBy() == null || (!"day".equals(request.getGroupBy()) && !"week".equals(request.getGroupBy()) && !"month".equals(request.getGroupBy()))) {
            throw new BusinessException("无效的分组方式！请使用 'day', 'week', 或 'month'。");
        }
        List<statisticData> dataList = statisticDao.getReservationTrend(request.getGroupBy(), request.getStartDate(), request.getEndDate());
        return convertToReservationTrendList(dataList);
    }

    @Override
    public List<statisticData.RepairStats> getRepairStats(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);
        List<statisticData> dataList = statisticDao.getRepairStats(request.getStartDate(), request.getEndDate());
        return convertToRepairStatsList(dataList);
    }

    @Override
    public List<statisticData.UserActivityStats> getUserActivityStats(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);
        List<statisticData> dataList = statisticDao.getUserUsageStats(request.getStartDate(), request.getEndDate());
        return convertToUserActivityStatsList(dataList);
    }

    @Override
    public List<statisticData.DeviceUsageStats> getTopUsedDevices(String adminToken, int topN, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (topN <= 0 || topN > 100) {
            throw new BusinessException("无效的Top N值！请在1到100之间。");
        }
        validateTimeRangeRequest(request);
        List<statisticData> dataList = statisticDao.getTopUsedDevices(topN, request.getStartDate(), request.getEndDate());
        return convertToDeviceUsageStatsList(dataList);
    }

    @Override
    public List<statisticData.UserActivityStats> getTopActiveUsers(String adminToken, int topN, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (topN <= 0 || topN > 100) {
            throw new BusinessException("无效的Top N值！请在1到100之间。");
        }
        validateTimeRangeRequest(request);
        List<statisticData> dataList = statisticDao.getTopActiveUsers(topN, request.getStartDate(), request.getEndDate());
        return convertToUserActivityStatsList(dataList);
    }

    @Override
    public Map<String, Object> getDeviceIdleRate(String adminToken, Integer deviceId, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);
        return statisticDao.getDeviceIdleRate(deviceId, request.getStartDate(), request.getEndDate());
    }

    @Override
    public List<statisticData> getDeviceFailureRate(String adminToken) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        return statisticDao.getDeviceFailureRate();
    }

    @Override
    public Map<String, Object> getResourceUtilization(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);
        return statisticDao.getResourceUtilization(request.getStartDate(), request.getEndDate());
    }

    @Override
    public statisticData getMonthlyReport(String adminToken, int year, int month) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (year < 2000 || year > 2100 || month < 1 || month > 12) {
            throw new BusinessException("无效的年份或月份！");
        }
        return statisticDao.getMonthlyReport(year, month);
    }

    @Override
    public statisticData getYearlyReport(String adminToken, int year) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (year < 2000 || year > 2100) {
            throw new BusinessException("无效的年份！");
        }
        return statisticDao.getYearlyReport(year);
    }

    @Override
    public int clearStatisticsCache(String adminToken) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        return statisticDao.clearStatisticsCache();
    }

    @Override
    public void updateStatisticsCache() throws DAOException {
        statisticDao.updateStatisticsCache();
    }

    /**
     * 按设备类型统计使用情况
     * 实现逻辑：
     * 1. 调用DAO获取所有设备的使用统计
     * 2. 使用Java Stream API按设备类型对统计数据进行分组
     * 3. 对每个分组，汇总使用次数，并计算其占总使用次数的百分比
     * 4. 将结果封装到 PieChartData 对象中返回
     */
    @Override
    public List<statisticData.PieChartData> getUsageByDeviceType(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);

        // 1. 从DAO获取所有设备的使用统计
        List<statisticData.DeviceUsageStats> statsList = getDeviceUsageStats(adminToken, request);

        // 2. 按设备类型分组并汇总
        Map<String, Integer> typeUsageMap = statsList.stream()
                .collect(Collectors.groupingBy(
                        statisticData.DeviceUsageStats::getCategoryName,
                        Collectors.summingInt(statisticData.DeviceUsageStats::getTotalReservations)
                ));

        // 3. 计算总数以计算百分比
        int totalUsage = typeUsageMap.values().stream().mapToInt(Integer::intValue).sum();

        // 4. 转换为 PieChartData 列表
        List<statisticData.PieChartData> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : typeUsageMap.entrySet()) {
            statisticData.PieChartData dataPoint = new statisticData.PieChartData();
            dataPoint.setName(entry.getKey());
            dataPoint.setValue(entry.getValue());
            if (totalUsage > 0) {
                dataPoint.setPercentage((double) entry.getValue() / totalUsage * 100);
            } else {
                dataPoint.setPercentage(0.0);
            }
            resultList.add(dataPoint);
        }

        return resultList;
    }

    /**
     * 统计用户预约时段分布
     * 实现逻辑：
     * 1. 调用DAO获取按天分组的预约趋势数据。
     * 2. 遍历每个预约记录，根据开始时间的小时数判断其所属时段。
     * 3. 对每个时段的预约数进行累加。
     * 4. 将结果封装到 ChartDataPoint 对象中返回。
     */
    @Override
    public List<statisticData.ChartDataPoint> getReservationTimeDistribution(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);

        // 确保按天分组，以便获取更细粒度的数据
        request.setGroupBy("day");
        List<statisticData.ReservationTrend> trendList = getReservationTrend(adminToken, request);

        // 时段计数器
        Map<String, Integer> timeSlotCounter = new HashMap<>();
        timeSlotCounter.put("00:00-06:00", 0);
        timeSlotCounter.put("06:00-12:00", 0);
        timeSlotCounter.put("12:00-18:00", 0);
        timeSlotCounter.put("18:00-24:00", 0);

        // 遍历预约数据，统计时段
        for (statisticData.ReservationTrend trend : trendList) {
            Date startTime = trend.getStartDate();
            if (startTime == null) continue;

            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            int hour = cal.get(Calendar.HOUR_OF_DAY);

            String timeSlot;
            if (hour >= 0 && hour < 6) {
                timeSlot = "00:00-06:00";
            } else if (hour >= 6 && hour < 12) {
                timeSlot = "06:00-12:00";
            } else if (hour >= 12 && hour < 18) {
                timeSlot = "12:00-18:00";
            } else {
                timeSlot = "18:00-24:00";
            }
            timeSlotCounter.put(timeSlot, timeSlotCounter.get(timeSlot) + trend.getTotalReservations());
        }

        // 转换为 ChartDataPoint 列表
        List<statisticData.ChartDataPoint> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : timeSlotCounter.entrySet()) {
            statisticData.ChartDataPoint dataPoint = new statisticData.ChartDataPoint();
            dataPoint.setLabel(entry.getKey());
            dataPoint.setValue(entry.getValue());
            resultList.add(dataPoint);
        }

        return resultList;
    }

    /**
     * 对比多个设备的性能指标
     * 实现逻辑：
     * 1. 对输入的设备ID列表进行非空校验。
     * 2. 分别调用DAO获取这些设备的使用统计和报修统计。
     * 3. 遍历每个设备ID，从两个统计列表中查找对应数据。
     * 4. 计算使用率（使用时长/总时长）和故障率（报修次数/使用次数）。
     * 5. 将计算结果封装到Map中并返回
     */
    @Override
    public List<Map<String, Object>> compareDevicesPerformance(String adminToken, List<Integer> deviceIds, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (deviceIds == null || deviceIds.isEmpty()) {
            throw new BusinessException("设备ID列表不能为空！");
        }
        validateTimeRangeRequest(request);

        // 1. 获取设备使用和报修的原始数据
        List<statisticData.DeviceUsageStats> usageStatsList = getDeviceUsageStats(adminToken, request);
        List<statisticData.RepairStats> repairStatsList = getRepairStats(adminToken, request);

        // 2. 转换为Map以便快速查找
        Map<Integer, statisticData.DeviceUsageStats> usageMap = usageStatsList.stream()
                .collect(Collectors.toMap(statisticData.DeviceUsageStats::getDeviceId, s -> s));
        Map<Integer, statisticData.RepairStats> repairMap = repairStatsList.stream()
                .collect(Collectors.toMap(statisticData.RepairStats::getDeviceId, s -> s));

        // 3. 计算并构建结果
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Integer deviceId : deviceIds) {
            statisticData.DeviceUsageStats usageStats = usageMap.get(deviceId);
            statisticData.RepairStats repairStats = repairMap.get(deviceId);

            Map<String, Object> devicePerformance = new HashMap<>();
            devicePerformance.put("deviceId", deviceId);
            devicePerformance.put("deviceName", usageStats != null ? usageStats.getDeviceName() : "未知设备");

            // 计算使用率 (使用小时数 / 总可用小时数)
            double usageRate = 0.0;
            long totalHoursInPeriod = (request.getEndDate().getTime() - request.getStartDate().getTime()) / (1000 * 60 * 60);
            if (usageStats != null && totalHoursInPeriod > 0) {
                usageRate = (usageStats.getTotalHours() != null ? usageStats.getTotalHours() : 0.0) / totalHoursInPeriod;
            }
            devicePerformance.put("usageRate", usageRate);

            // 计算故障率 (报修次数 / 使用次数)
            double failureRate = 0.0;
            if (usageStats != null && usageStats.getTotalReservations() != null && usageStats.getTotalReservations() > 0) {
                int repairCount = (repairStats != null && repairStats.getTotalRepairs() != null) ? repairStats.getTotalRepairs() : 0;
                failureRate = (double) repairCount / usageStats.getTotalReservations();
            }
            devicePerformance.put("failureRate", failureRate);

            resultList.add(devicePerformance);
        }

        return resultList;
    }

    /**
     * 获取用户预约行为统计
     * 实现逻辑：
     * 1. 调用DAO获取用户活跃度统计数据
     * 2. 按用户角色对数据进行分组
     * 3. 对每个角色分组，计算平均预约次数、平均使用设备数等指标
     * 4. 将计算结果封装到Map中并返回
     */
    @Override
    public List<Map<String, Object>> getUserReservationBehavior(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        validateTimeRangeRequest(request);

        // 1. 获取用户活跃度原始数据
        List<statisticData.UserActivityStats> userStatsList = getUserActivityStats(adminToken, request);

        // 如果有角色过滤，则先过滤
        if (ValidationUtil.isNotEmpty(request.getUserRole())) {
            userStatsList = userStatsList.stream()
                    .filter(stat -> request.getUserRole().equals(stat.getUserRole()))
                    .collect(Collectors.toList());
        }

        // 2. 按用户角色分组
        Map<String, List<statisticData.UserActivityStats>> roleGroupMap = userStatsList.stream()
                .collect(Collectors.groupingBy(statisticData.UserActivityStats::getUserRole));

        // 3. 计算每个角色的行为指标
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, List<statisticData.UserActivityStats>> entry : roleGroupMap.entrySet()) {
            String role = entry.getKey();
            List<statisticData.UserActivityStats> statsInRole = entry.getValue();

            Map<String, Object> behaviorStats = new HashMap<>();
            behaviorStats.put("userRole", role);
            behaviorStats.put("userRoleDisplayName", getRoleDisplayName(role));
            behaviorStats.put("userCount", statsInRole.size());

            // 计算平均值
            double avgReservations = statsInRole.stream()
                    .mapToInt(s -> s.getTotalReservations() != null ? s.getTotalReservations() : 0)
                    .average()
                    .orElse(0.0);
            behaviorStats.put("avgReservations", avgReservations);

            double avgDevicesUsed = statsInRole.stream()
                    .mapToInt(s -> s.getDevicesUsed() != null ? s.getDevicesUsed() : 0)
                    .average()
                    .orElse(0.0);
            behaviorStats.put("avgDevicesUsed", avgDevicesUsed);
            
            // 计算活跃用户数 (例如，预约数 > 0)
            long activeUserCount = statsInRole.stream()
                    .filter(s -> s.getTotalReservations() != null && s.getTotalReservations() > 0)
                    .count();
            behaviorStats.put("activeUserCount", activeUserCount);
            behaviorStats.put("activityRate", statsInRole.size() > 0 ? (double) activeUserCount / statsInRole.size() : 0.0);

            resultList.add(behaviorStats);
        }

        return resultList;
    }

    private void validateTimeRangeRequest(statisticData.TimeRangeRequest request) throws BusinessException {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null) {
            throw new BusinessException("开始时间和结束时间不能为空！");
        }
        if (request.getStartDate().after(request.getEndDate())) {
            throw new BusinessException("开始时间不能晚于结束时间！");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(request.getStartDate());
        cal.add(Calendar.YEAR, 1);
        if (request.getEndDate().after(cal.getTime())) {
            throw new BusinessException("查询时间范围不能超过一年！");
        }
    }

    @SuppressWarnings("unchecked")
    private List<statisticData.DeviceUsageStats> convertToDeviceUsageStatsList(List<statisticData> dataList) {
        if (dataList == null) return new ArrayList<>();
        return (List<statisticData.DeviceUsageStats>) (List<?>) dataList;
    }

    @SuppressWarnings("unchecked")
    private List<statisticData.ReservationTrend> convertToReservationTrendList(List<statisticData> dataList) {
        if (dataList == null) return new ArrayList<>();
        return (List<statisticData.ReservationTrend>) (List<?>) dataList;
    }

    @SuppressWarnings("unchecked")
    private List<statisticData.RepairStats> convertToRepairStatsList(List<statisticData> dataList) {
        if (dataList == null) return new ArrayList<>();
        return (List<statisticData.RepairStats>) (List<?>) dataList;
    }

    @SuppressWarnings("unchecked")
    private List<statisticData.UserActivityStats> convertToUserActivityStatsList(List<statisticData> dataList) {
        if (dataList == null) return new ArrayList<>();
        return (List<statisticData.UserActivityStats>) (List<?>) dataList;
    }
    
    /**
     * 辅助方法：将角色代码转换为显示名称。
     * @param role 角色代码 (e.g., "admin", "teacher")
     * @return 角色显示名称 (e.g., "管理员", "教师")
     */
    private String getRoleDisplayName(String role) {
        if (role == null) return "未知";
        switch (role) {
            case "admin": return "管理员";
            case "teacher": return "教师";
            case "student": return "学生";
            case "technician": return "技术员";
            default: return role;
        }
    }
}