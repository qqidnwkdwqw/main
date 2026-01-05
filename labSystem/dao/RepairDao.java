package labSystem.dao;

import labSystem.entity.Repair;
import java.sql.SQLException;
import java.util.List;

/**
 * 报修DAO接口
 */
public interface RepairDao extends BaseDao<Repair, Integer> {
    
    /**
     * 根据设备ID查询报修记录
     * @param deviceId 设备ID
     * @return 报修列表
     * @throws SQLException
     */
    List<Repair> findByDeviceId(int deviceId);
    
    /**
     * 根据用户ID查询报修记录
     * @param userId 用户ID
     * @return 报修列表
     * @throws SQLException
     */
    List<Repair> findByUserId(int userId);
    
    /**
     * 根据状态查询报修记录
     * @param status 状态（pending/processing/completed）
     * @return 报修列表
     * @throws SQLException
     */
    List<Repair> findByStatus(String status);
    
    /**
     * 根据优先级查询报修记录
     * @param priority 优先级（1-低，2-中，3-高）
     * @return 报修列表
     * @throws SQLException
     */
    List<Repair> findByPriority(int priority);
    
    /**
     * 查询待处理的报修（pending状态）
     * @return 报修列表
     * @throws SQLException
     */
    List<Repair> findPendingRepairs();
    
    /**
     * 查询分配给技术员的报修
     * @param technicianId 技术员ID
     * @return 报修列表
     * @throws SQLException
     */
    List<Repair> findByTechnicianId(int technicianId);
    
    /**
     * 更新报修状态
     * @param repairId 报修ID
     * @param status 新状态
     * @return 影响的行数
     * @throws SQLException
     */
    int updateStatus(int repairId, String status);
    
    /**
     * 分配技术员处理报修
     * @param repairId 报修ID
     * @param technicianId 技术员ID
     * @return 影响的行数
     * @throws SQLException
     */
    int assignTechnician(int repairId, int technicianId);
    
    /**
     * 完成报修
     * @param repairId 报修ID
     * @param solution 解决方案
     * @param cost 维修成本
     * @return 影响的行数
     * @throws SQLException
     */
    int completeRepair(int repairId, String solution, double cost);
    
    /**
     * 统计各状态报修数
     * @return 状态-数量映射
     * @throws SQLException
     */
    java.util.Map<String, Integer> countByStatus();
    
    /**
     * 统计设备报修次数
     * @return 设备ID-报修次数映射
     * @throws SQLException
     */
    java.util.Map<Integer, Integer> countByDevice();
}