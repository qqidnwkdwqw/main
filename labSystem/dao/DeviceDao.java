package labSystem.dao;

import labSystem.entity.Device;
import java.sql.SQLException;
import java.util.List;

/**
 * 设备DAO接口
 */
public interface DeviceDao extends BaseDao<Device, Integer> {
    
    /**
     * 根据设备编号查询
     * @param deviceCode 设备编号
     * @return 设备对象
     * @throws SQLException
     */
    Device findByCode(String deviceCode);
    
    /**
     * 根据分类查询设备
     * @param categoryId 分类ID
     * @return 设备列表
     * @throws SQLException
     */
    List<Device> findByCategory(int categoryId);
    
    /**
     * 根据状态查询设备
     * @param status 状态（available/in_use/under_repair/retired）
     * @return 设备列表
     * @throws SQLException
     */
    List<Device> findByStatus(String status);
    
    /**
     * 根据位置查询设备
     * @param location 位置
     * @return 设备列表
     * @throws SQLException
     */
    List<Device> findByLocation(String location);
    
    /**
     * 更新设备状态
     * @param deviceId 设备ID
     * @param status 新状态
     * @return 影响的行数
     * @throws SQLException
     */
    int updateStatus(int deviceId, String status);
    
    /**
     * 更新设备当前使用者
     * @param deviceId 设备ID
     * @param userId 用户ID（null表示无人使用）
     * @return 影响的行数
     * @throws SQLException
     */
    int updateCurrentUser(int deviceId, Integer userId);
    
    /**
     * 搜索设备（名称、编号、描述模糊搜索）
     * @param keyword 关键词
     * @return 设备列表
     * @throws SQLException
     */
    List<Device> search(String keyword);
    
    /**
     * 统计各状态设备数量
     * @return 状态-数量映射
     * @throws SQLException
     */
    java.util.Map<String, Integer> countByStatus();
}