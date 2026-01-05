package labSystem.service;

import labSystem.entity.statisticData;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 * 此接口定义了所有统计相关的服务，包括基础数据查询和高级分析功能
 * 所有方法都需要管理员权限才能调用
 */
public interface StatisticService {

    /**
     * 获取系统概览统计
     * @param adminToken 管理员的token
     * @return 系统概览统计数据
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    statisticData.SystemOverview getSystemOverview(String adminToken) throws BusinessException, DAOException;

    /**
     * 获取设备使用统计
     * @param adminToken 管理员的token
     * @param request 包含时间范围等条件的请求对象
     * @return 设备使用统计列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData.DeviceUsageStats> getDeviceUsageStats(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取预约趋势统计
     * @param adminToken 管理员的token
     * @param request 包含时间范围和分组方式的请求对象
     * @return 预约趋势统计列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData.ReservationTrend> getReservationTrend(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取报修统计
     * @param adminToken 管理员的token
     * @param request 包含时间范围的请求对象
     * @return 报修统计列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData.RepairStats> getRepairStats(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取用户活跃度统计
     * @param adminToken 管理员的token
     * @param request 包含时间范围的请求对象
     * @return 用户活跃度统计列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData.UserActivityStats> getUserActivityStats(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取最常用设备Top N
     * @param adminToken 管理员的token
     * @param topN 前N名
     * @param request 包含时间范围的请求对象
     * @return 最常用设备统计列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData.DeviceUsageStats> getTopUsedDevices(String adminToken, int topN, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取最活跃用户Top N
     * @param adminToken 管理员的token
     * @param topN 前N名
     * @param request 包含时间范围的请求对象
     * @return 最活跃用户统计列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData.UserActivityStats> getTopActiveUsers(String adminToken, int topN, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取设备空闲率统计
     * @param adminToken 管理员的token
     * @param deviceId 设备ID（null表示所有设备）
     * @param request 包含时间范围的请求对象
     * @return 空闲率数据，通常包含总时长、空闲时长和空闲率
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    Map<String, Object> getDeviceIdleRate(String adminToken, Integer deviceId, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取设备故障率统计
     * @param adminToken 管理员的token
     * @return 设备故障率列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData> getDeviceFailureRate(String adminToken) throws BusinessException, DAOException;

    /**
     * 获取资源利用率统计
     * @param adminToken 管理员的token
     * @param request 包含时间范围的请求对象
     * @return 利用率数据，通常包含总容量、已用容量和利用率
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    Map<String, Object> getResourceUtilization(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取月度报告
     * @param adminToken 管理员的token
     * @param year 年份
     * @param month 月份（1-12）
     * @return 月度报告统计数据
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    statisticData getMonthlyReport(String adminToken, int year, int month) throws BusinessException, DAOException;

    /**
     * 获取年度报告
     * @param adminToken 管理员的token
     * @param year 年份
     * @return 年度报告统计数据
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    statisticData getYearlyReport(String adminToken, int year) throws BusinessException, DAOException;

    /**
     * 清空统计缓存
     * @param adminToken 管理员的token
     * @return 影响的行数
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    int clearStatisticsCache(String adminToken) throws BusinessException, DAOException;

    /**
     * 更新统计缓存（定时任务调用）
     * @throws DAOException 数据访问失败时抛出
     */
    void updateStatisticsCache() throws DAOException;

    /**
     * 按设备类型统计使用情况
     * 此方法用于分析不同类型设备的使用分布，非常适合生成饼图或柱状图
     * 返回的数据包含每种类型的名称、总使用次数以及其占总使用次数的百分比
     * @param adminToken 管理员的token
     * @param request 包含时间范围的请求对象
     * @return 一个包含 {@link statisticData.PieChartData} 对象的列表，每个对象代表一种设备类型的统计信息。
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData.PieChartData> getUsageByDeviceType(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 统计用户预约时段分布。
     * 此方法将一天划分为多个时段（如上午、下午、晚上），并统计每个时段的预约次数
     * 有助于管理员了解实验室的高峰使用时段，从而进行合理的资源调配和开放时间调整
     * @param adminToken 管理员的token
     * @param request 包含时间范围的请求对象
     * @return 一个包含 {@link statisticData.ChartDataPoint} 对象的列表，每个对象代表一个时段及其预约数。
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<statisticData.ChartDataPoint> getReservationTimeDistribution(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 对比多个设备的性能指标
     * 此方法用于横向比较指定设备列表的关键性能指标，如使用率和故障率
     * 这对于评估设备的可靠性、识别问题设备或进行采购决策非常有价值
     * @param adminToken 管理员的token
     * @param deviceIds 要对比的设备ID列表
     * @param request 包含时间范围的请求对象
     * @return 一个包含Map的列表，每个Map代表一个设备的性能数据，键为指标名称（如"deviceName", "usageRate", "failureRate"），值为对应的统计值。
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<Map<String, Object>> compareDevicesPerformance(String adminToken, List<Integer> deviceIds, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;

    /**
     * 获取用户预约行为统计
     * 此方法提供对用户群体预约习惯的洞察，例如平均预约时长、预约频率等
     * 数据可以按用户角色（如学生、教师）进行分组，以便进行更精细的分析
     * @param adminToken 管理员的token
     * @param request 包含时间范围和用户角色过滤条件的请求对象
     * @return 一个包含Map的列表，每个Map代表一个用户角色的行为统计，键为统计项名称，值为统计结果。
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<Map<String, Object>> getUserReservationBehavior(String adminToken, statisticData.TimeRangeRequest request) throws BusinessException, DAOException;
}