package labSystem.dao;

import labSystem.entity.statisticData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * 统计DAO接口
 */
public interface StatisticDao extends BaseDao<statisticData, Integer> {
    
    /**
     * 获取系统概览统计
     * @return 统计对象
     * @throws SQLException
     */
    statisticData getSystemOverview();
    
    /**
     * 获取用户使用统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计列表
     * @throws SQLException
     */
    List<statisticData> getUserUsageStats(Date startDate, Date endDate);
    
    /**
     * 获取设备使用统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计列表
     * @throws SQLException
     */
    List<statisticData> getDeviceUsageStats(Date startDate, Date endDate);
    
    /**
     * 获取预约趋势统计（按天/周/月）
     * @param period 统计周期（day/week/month）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计列表
     * @throws SQLException
     */
    List<statisticData> getReservationTrend(String period, Date startDate, Date endDate);
    
    /**
     * 获取报修统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计列表
     * @throws SQLException
     */
    List<statisticData> getRepairStats(Date startDate, Date endDate);
    
    /**
     * 获取最常用设备Top N
     * @param topN 前N名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 设备统计列表
     * @throws SQLException
     */
    List<statisticData> getTopUsedDevices(int topN, Date startDate, Date endDate);
    
    /**
     * 获取最活跃用户Top N
     * @param topN 前N名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用户统计列表
     * @throws SQLException
     */
    List<statisticData> getTopActiveUsers(int topN, Date startDate, Date endDate);
    
    /**
     * 获取设备空闲率统计
     * @param deviceId 设备ID（null表示所有设备）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 空闲率数据
     * @throws SQLException
     */
    Map<String, Object> getDeviceIdleRate(Integer deviceId, Date startDate, Date endDate);
    
    /**
     * 获取月度报告
     * @param year 年份
     * @param month 月份（1-12）
     * @return 月度统计数据
     * @throws SQLException
     */
    statisticData getMonthlyReport(int year, int month);
    
    /**
     * 获取年度报告
     * @param year 年份
     * @return 年度统计数据
     * @throws SQLException
     */
    statisticData getYearlyReport(int year);
    
    /**
     * 获取设备故障率统计
     * @return 设备故障率列表
     * @throws SQLException
     */
    List<statisticData> getDeviceFailureRate() ;
    
    /**
     * 获取资源利用率统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 利用率数据
     * @throws SQLException
     */
    Map<String, Object> getResourceUtilization(Date startDate, Date endDate);
    
    /**
     * 清空统计缓存
     * @return 影响的行数
     * @throws SQLException
     */
    int clearStatisticsCache();
    
    /**
     * 更新统计缓存（定时任务调用）
     * @return 是否成功
     * @throws SQLException
     */
    boolean updateStatisticsCache();
}