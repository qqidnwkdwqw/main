package labSystem.service;

import labSystem.entity.Reservation;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约服务接口
 */
public interface ReservationService {

    /**
     * 创建新的预约
     * @param userToken 用户的会话令牌
     * @param reservation 预约信息
     * @return 创建成功的预约对象
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    Reservation createReservation(String userToken, Reservation reservation) throws BusinessException, DAOException;

    /**
     * 根据ID获取预约详情
     * @param operatorToken 操作用户的会话令牌
     * @param reservationId 预约ID
     * @return 预约详情对象
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    Reservation getReservationById(String operatorToken, int reservationId) throws BusinessException, DAOException;

    /**
     * 获取当前用户的所有预约
     * @param userToken 用户的会话令牌
     * @return 预约列表
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    List<Reservation> getMyReservations(String userToken) throws BusinessException, DAOException;

    /**
     * 获取指定设备的预约历史
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @return 预约列表
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    List<Reservation> getReservationsByDevice(String operatorToken, int deviceId) throws BusinessException, DAOException;

    /**
     * 获取所有预约（分页），通常管理员使用
     * @param adminToken 管理员的会话令牌
     * @param page 页码
     * @param pageSize 每页大小
     * @return 预约列表
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    List<Reservation> getAllReservations(String adminToken, int page, int pageSize) throws BusinessException, DAOException;

    /**
     * 管理员审核预约
     * @param adminToken 管理员的会话令牌
     * @param reservationId 预约ID
     * @param isApproved 是否批准
     * @param adminNotes 管理员备注
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    void reviewReservation(String adminToken, int reservationId, boolean isApproved, String adminNotes) throws BusinessException, DAOException;

    /**
     * 用户取消自己的预约
     * @param userToken 用户的会话令牌
     * @param reservationId 预约ID
     * @param userNotes 用户备注
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    void cancelReservation(String userToken, int reservationId, String userNotes) throws BusinessException, DAOException;

    /**
     * 用户完成自己的预约
     * @param userToken 用户的会话令牌
     * @param reservationId 预约ID
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    void completeReservation(String userToken, int reservationId) throws BusinessException, DAOException;

    /**
     * 检查指定时间段是否可用
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeReservationId 排除的预约ID（用于更新预约时检查）
     * @return true-可用 false-不可用
     * @throws DAOException 数据访问异常
     */
    boolean isTimeSlotAvailable(int deviceId, Date startTime, Date endTime, Integer excludeReservationId) throws DAOException;

    /**
     * 定时任务：更新已过期的预约状态
     * @throws DAOException 数据访问异常
     */
    void updateExpiredReservations() throws DAOException;

    /**
     * 管理员批量审核预约
     * @param adminToken 管理员的会话令牌
     * @param reservationIds 预约ID列表
     * @param isApproved 是否批准
     * @param adminNotes 批量审核备注
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    void batchReviewReservations(String adminToken, List<Integer> reservationIds, boolean isApproved, String adminNotes) throws BusinessException, DAOException;

    /**
     * 获取用户指定时间段内的预约统计
     * @param userToken 用户的会话令牌
     * @param startDate 统计开始日期
     * @param endDate 统计结束日期
     * @return 状态到数量的映射
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    Map<String, Integer> getMyReservationStatistics(String userToken, Date startDate, Date endDate) throws BusinessException, DAOException;

    /**
     * 获取设备未来N天的已预约时间段
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @param days 未来天数
     * @return 格式化的时间段列表
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    List<String> getUpcomingReservationsForDevice(String operatorToken, int deviceId, int days) throws BusinessException, DAOException;

    /**
     * 用户延长已批准的预约
     * @param userToken 用户的会话令牌
     * @param reservationId 预约ID
     * @param newEndTime 新的结束时间
     * @param reason 延长理由
     * @throws BusinessException 业务异常
     * @throws DAOException 数据访问异常
     */
    void extendReservation(String userToken, int reservationId, Date newEndTime, String reason) throws BusinessException, DAOException;

}