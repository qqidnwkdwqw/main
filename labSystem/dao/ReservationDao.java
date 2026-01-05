package labSystem.dao;

import labSystem.entity.Reservation;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 预约DAO接口
 */
public interface ReservationDao extends BaseDao<Reservation, Integer> {
    
    /**
     * 根据用户ID查询预约记录
     * @param userId 用户ID
     * @return 预约列表
     * @throws SQLException
     */
    List<Reservation> findByUserId(int userId);
    
    /**
     * 根据设备ID查询预约记录
     * @param deviceId 设备ID
     * @return 预约列表
     * @throws SQLException
     */
    List<Reservation> findByDeviceId(int deviceId);
    
    /**
     * 查询指定时间范围内的预约
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 预约列表
     * @throws SQLException
     */
    List<Reservation> findByTimeRange(int deviceId, Date startTime, Date endTime);
    
    /**
     * 查询今天开始的预约
     * @return 预约列表
     * @throws SQLException
     */
    List<Reservation> findTodayReservations();
    
    /**
     * 查询待处理的预约（pending状态）
     * @return 预约列表
     * @throws SQLException
     */
    List<Reservation> findPendingReservations();
    
    /**
     * 查询进行中的预约
     * @return 预约列表
     * @throws SQLException
     */
    List<Reservation> findOngoingReservations();
    
    /**
     * 查询已完成的预约
     * @param userId 用户ID（可选，null表示查询所有）
     * @return 预约列表
     * @throws SQLException
     */
    List<Reservation> findCompletedReservations(Integer userId);
    
    /**
     * 更新预约状态
     * @param reservationId 预约ID
     * @param status 新状态（pending/approved/rejected/in_progress/completed/cancelled）
     * @return 影响的行数
     * @throws SQLException
     */
    int updateStatus(int reservationId, String status);
    
    /**
     * 批量更新过期预约状态
     * @return 更新的记录数
     * @throws SQLException
     */
    int updateExpiredReservations();
    
    /**
     * 检查时间段是否可用
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeReservationId 排除的预约ID（用于更新时检查）
     * @return true表示时间段可用，false表示冲突
     * @throws SQLException
     */
    boolean isTimeSlotAvailable(int deviceId, Date startTime, Date endTime, Integer excludeReservationId);
    
    /**
     * 统计用户预约次数
     * @param userId 用户ID
     * @return 预约次数
     * @throws SQLException
     */
    int countUserReservations(int userId);
}