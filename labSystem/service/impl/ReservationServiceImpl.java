package labSystem.service.impl;

import labSystem.dao.DeviceDao;
import labSystem.dao.ReservationDao;
import labSystem.dao.UserDao;
import labSystem.entity.Device;
import labSystem.entity.Reservation;
import labSystem.entity.User;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;
import labSystem.service.AuthService;
import labSystem.service.ReservationService;
import labSystem.util.ValidationUtil;
import labSystem.util.DateUtil; 

import java.util.*;
import java.util.stream.Collectors;

public class ReservationServiceImpl implements ReservationService {

    private final ReservationDao reservationDao;
    private final DeviceDao deviceDao;
    private final UserDao userDao;
    private final AuthService authService;

    public ReservationServiceImpl(ReservationDao reservationDao, DeviceDao deviceDao, UserDao userDao, AuthService authService) {
        this.reservationDao = reservationDao;
        this.deviceDao = deviceDao;
        this.userDao = userDao;
        this.authService = authService;
    }

    @Override
    public Reservation createReservation(String userToken, Reservation reservation) throws BusinessException, DAOException {
        User currentUser = authService.checkLogin(userToken);

        if (reservation.getDeviceId() == null || !ValidationUtil.isPositiveInteger(reservation.getDeviceId())) {
            throw new BusinessException("设备ID无效");
        }

        // 使用 DateUtil 进行全面的时间验证
        String timeValidationError = DateUtil.validateReservationTime(reservation.getStartTime(), reservation.getEndTime());
        if (timeValidationError != null) {
            throw new BusinessException(timeValidationError);
        }

        Device device = deviceDao.findById(reservation.getDeviceId());
        if (device == null) {
            throw new BusinessException("设备不存在");
        }

        if (!isTimeSlotAvailable(reservation.getDeviceId(), reservation.getStartTime(), reservation.getEndTime(), null)) {
            throw new BusinessException("该时间段已被占用");
        }

        reservation.setUserId(currentUser.getUserId());
        reservation.setStatus("pending");
        reservation.setCreatedAt(new Date());
        reservation.setUpdatedAt(new Date());

        int newReservationId = reservationDao.insert(reservation);
        return getReservationById(userToken, newReservationId);
    }

    @Override
    public Reservation getReservationById(String operatorToken, int reservationId) throws BusinessException, DAOException {
        User operator = authService.checkLogin(operatorToken);
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }

        if (!"admin".equals(operator.getUserRole()) && !operator.getUserId().equals(reservation.getUserId())) {
            throw new BusinessException("权限不足，无法查看此预约");
        }
        return reservation;
    }

    @Override
    public List<Reservation> getMyReservations(String userToken) throws BusinessException, DAOException {
        User currentUser = authService.checkLogin(userToken);
        return reservationDao.findByUserId(currentUser.getUserId());
    }

    @Override
    public List<Reservation> getReservationsByDevice(String operatorToken, int deviceId) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效");
        }
        return reservationDao.findByDeviceId(deviceId);
    }

    @Override
    public List<Reservation> getAllReservations(String adminToken, int page, int pageSize) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (!ValidationUtil.isValidPageNumber(page, pageSize)) {
            throw new BusinessException("页码或每页大小无效");
        }
        return reservationDao.findByPage(page, pageSize);
    }

    @Override
    public void reviewReservation(String adminToken, int reservationId, boolean isApproved, String adminNotes) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (!"pending".equals(reservation.getStatus())) {
            throw new BusinessException("当前预约状态为【" + reservation.getStatus() + "】，无法审核");
        }

        reservation.setStatus(isApproved ? "approved" : "rejected");
        reservation.setAdminNotes(adminNotes);
        reservation.setUpdatedAt(new Date());
        reservationDao.update(reservation);
    }

    @Override
    public void cancelReservation(String userToken, int reservationId, String userNotes) throws BusinessException, DAOException {
        User currentUser = authService.checkLogin(userToken);
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }

        if (!currentUser.getUserId().equals(reservation.getUserId())) {
            throw new BusinessException("权限不足，无法取消他人的预约");
        }
        
        if (!"pending".equals(reservation.getStatus()) && !"approved".equals(reservation.getStatus())) {
            throw new BusinessException("当前预约状态为【" + reservation.getStatus() + "】，无法取消");
        }

        reservation.setStatus("cancelled");
        reservation.setUserNotes(userNotes);
        reservation.setUpdatedAt(new Date());
        reservationDao.update(reservation);
    }

    @Override
    public void completeReservation(String userToken, int reservationId) throws BusinessException, DAOException {
        User currentUser = authService.checkLogin(userToken);
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }

        if (!currentUser.getUserId().equals(reservation.getUserId())) {
            throw new BusinessException("权限不足，无法完成他人的预约");
        }
        
        if (!"approved".equals(reservation.getStatus())) {
            throw new BusinessException("当前预约状态为【" + reservation.getStatus() + "】，无法完成");
        }

        reservation.setStatus("completed");
        reservation.setActualEndTime(new Date());
        reservation.setUpdatedAt(new Date());
        reservationDao.update(reservation);
    }

    @Override
    public boolean isTimeSlotAvailable(int deviceId, Date startTime, Date endTime, Integer excludeReservationId) throws DAOException {
        List<Reservation> existingReservations = reservationDao.findByDeviceId(deviceId);
        for (Reservation r : existingReservations) {
            if (excludeReservationId != null && r.getReservationId().equals(excludeReservationId)) {
                continue;
            }
            // 时间冲突判断逻辑
            if (r.getStartTime().before(endTime) && r.getEndTime().after(startTime)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updateExpiredReservations() throws DAOException {
        // 假设通过分页查询所有活跃预约
        int page = 1;
        int pageSize = 100;
        List<Reservation> activeReservations;
        Date now = new Date();

        do {
            activeReservations = reservationDao.findByPage(page, pageSize);
            for (Reservation r : activeReservations) {
                if (("pending".equals(r.getStatus()) || "approved".equals(r.getStatus())) && r.getEndTime().before(now)) {
                    r.setStatus("expired");
                    r.setUpdatedAt(now);
                    reservationDao.update(r);
                }
            }
            page++;
        } while (activeReservations.size() == pageSize);
    }

    @Override
    public void batchReviewReservations(String adminToken, List<Integer> reservationIds, boolean isApproved, String adminNotes) throws BusinessException, DAOException {
        authService.checkPermission(adminToken, "admin");
        if (reservationIds == null || reservationIds.isEmpty()) {
            throw new BusinessException("请选择需要审核的预约");
        }
        if (ValidationUtil.isEmpty(adminNotes)) {
            throw new BusinessException("批量审核备注不能为空");
        }

        Date now = new Date();
        for (Integer id : reservationIds) {
            Reservation reservation = reservationDao.findById(id);
            if (reservation == null) {
                throw new BusinessException("预约ID:" + id + " 不存在，批量审核终止");
            }
            if (!"pending".equals(reservation.getStatus())) {
                throw new BusinessException("预约ID:" + id + " 状态为【" + reservation.getStatus() + "】，无法审核，批量审核终止");
            }

            reservation.setStatus(isApproved ? "approved" : "rejected");
            reservation.setAdminNotes(adminNotes);
            reservation.setUpdatedAt(now);
            reservationDao.update(reservation);
        }
    }

    @Override
    public Map<String, Integer> getMyReservationStatistics(String userToken, Date startDate, Date endDate) throws BusinessException, DAOException {
        User currentUser = authService.checkLogin(userToken);
        if (startDate == null || endDate == null) {
            throw new BusinessException("统计时间段不能为空");
        }
        if (startDate.after(endDate)) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }

        List<Reservation> reservations = reservationDao.findByUserId(currentUser.getUserId());

        return reservations.stream()
                .filter(r -> r.getCreatedAt() != null && !r.getCreatedAt().before(startDate) && !r.getCreatedAt().after(endDate))
                .collect(Collectors.groupingBy(Reservation::getStatus, Collectors.summingInt(r -> 1)));
    }

    @Override
    public List<String> getUpcomingReservationsForDevice(String operatorToken, int deviceId, int days) throws BusinessException, DAOException {
        authService.checkLogin(operatorToken);
        if (!ValidationUtil.isPositiveInteger(deviceId)) {
            throw new BusinessException("设备ID无效");
        }
        if (days <= 0 || days > 30) {
            throw new BusinessException("查询天数需在1-30天范围内");
        }

        Date now = new Date();
        Date futureDate = DateUtil.addDays(now, days); // 使用 DateUtil 计算未来日期

        List<Reservation> allReservations = reservationDao.findByDeviceId(deviceId);

        return allReservations.stream()
                .filter(r -> r.getStartTime() != null && r.getStartTime().after(now) && r.getStartTime().before(futureDate))
                .sorted(Comparator.comparing(Reservation::getStartTime))
                .map(r -> DateUtil.formatDateTime(r.getStartTime()) + " - " + DateUtil.formatDateTime(r.getEndTime())) // 使用 DateUtil 格式化
                .collect(Collectors.toList());
    }

    @Override
    public void extendReservation(String userToken, int reservationId, Date newEndTime, String reason) throws BusinessException, DAOException {
        User currentUser = authService.checkLogin(userToken);
        if (newEndTime == null) {
            throw new BusinessException("新的结束时间不能为空");
        }
        if (ValidationUtil.isEmpty(reason)) {
            throw new BusinessException("延长理由不能为空");
        }

        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }

        if (!currentUser.getUserId().equals(reservation.getUserId())) {
            throw new BusinessException("权限不足，无法延长他人的预约");
        }
        
        if (!"approved".equals(reservation.getStatus())) {
            throw new BusinessException("仅已批准的预约可申请延长");
        }

        // 使用 DateUtil 验证延长后的总时间
        String timeValidationError = DateUtil.validateReservationTime(reservation.getStartTime(), newEndTime);
        if (timeValidationError != null) {
            throw new BusinessException("延长失败: " + timeValidationError);
        }

        if (!isTimeSlotAvailable(reservation.getDeviceId(), reservation.getStartTime(), newEndTime, reservationId)) {
            throw new BusinessException("延长后的时间段与其他预约冲突");
        }

        reservation.setEndTime(newEndTime);
        String newNotes = (reservation.getUserNotes() == null ? "" : reservation.getUserNotes() + " | ") + "延长理由：" + reason;
        reservation.setUserNotes(newNotes);
        reservation.setUpdatedAt(new Date());
        reservationDao.update(reservation);
    }
}