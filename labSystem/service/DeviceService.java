package labSystem.service;

import labSystem.entity.Device;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 设备管理服务接口。
 * 提供对设备信息的增、删、改、查、状态管理、搜索等功能。
 * 所有操作都包含权限校验。
 */
public interface DeviceService {

    /**
     * 根据设备ID查询设备详情
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @return 设备对象
     * @throws BusinessException 如果设备不存在、已报废或权限不足
     * @throws DAOException 如果数据库操作失败
     */
    Device findDeviceById(String operatorToken, Integer deviceId);

    /**
     * 根据设备编号查询设备
     * @param operatorToken 操作用户的会话令牌
     * @param deviceCode 设备编号
     * @return 设备对象
     * @throws BusinessException 如果设备不存在、已报废或权限不足
     * @throws DAOException 如果数据库操作失败
     */
    Device findDeviceByCode(String operatorToken, String deviceCode);

    /**
     * 根据分类ID查询设备列表
     * @param operatorToken 操作用户的会话令牌
     * @param categoryId 分类ID
     * @return 设备列表
     * @throws BusinessException 如果操作用户未登录
     * @throws DAOException 如果数据库操作失败
     */
    List<Device> findDevicesByCategory(String operatorToken, Integer categoryId);

    /**
     * 根据状态查询设备列表
     * @param operatorToken 操作用户的会话令牌
     * @param status 设备状态
     * @return 设备列表
     * @throws BusinessException 如果操作用户未登录或状态无效
     * @throws DAOException 如果数据库操作失败
     */
    List<Device> findDevicesByStatus(String operatorToken, String status);

    /**
     * 分页查询所有设备（包含已报废但未软删除的）
     * @param adminToken 管理员的会话令牌
     * @param page 页码
     * @param pageSize 每页大小
     * @return 设备列表
     * @throws BusinessException 如果操作用户非管理员或分页参数无效
     * @throws DAOException 如果数据库操作失败
     */
    List<Device> findAllDevicesByPage(String adminToken, int page, int pageSize);

    /**
     * 搜索设备（根据名称、编号、描述等模糊搜索）
     * @param operatorToken 操作用户的会话令牌
     * @param keyword 搜索关键词
     * @return 匹配的设备列表
     * @throws BusinessException 如果操作用户未登录
     * @throws DAOException 如果数据库操作失败
     */
    List<Device> searchDevices(String operatorToken, String keyword);

    /**
     * 管理员添加新设备
     * @param adminToken 管理员的会话令牌
     * @param newDevice 包含新设备信息的对象
     * @return 创建成功的设备对象
     * @throws BusinessException 如果设备编号已存在、信息不完整或操作用户非管理员
     * @throws DAOException 如果数据库操作失败
     */
    Device addDevice(String adminToken, Device newDevice);

    /**
     * 管理员更新设备信息
     * @param adminToken 管理员的会话令牌
     * @param deviceToUpdate 包含要更新信息的设备对象。
     * @throws BusinessException 如果设备不存在、已报废、信息无效或操作用户非管理员
     * @throws DAOException 如果数据库操作失败
     */
    void updateDevice(String adminToken, Device deviceToUpdate);

    /**
     * 管理员报废设备（软删除）
     * @param adminToken 管理员的会话令牌
     * @param deviceId 设备ID
     * @throws BusinessException 如果设备不存在、已报废、正在使用中或操作用户非管理员
     * @throws DAOException 如果数据库操作失败
     */
    void scrapDevice(String adminToken, Integer deviceId);

    /**
     * 管理员或教师将设备状态改为维修中
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @throws BusinessException 如果设备不存在、已报废、状态不允许或权限不足
     * @throws DAOException 如果数据库操作失败
     */
    void sendDeviceForRepair(String operatorToken, Integer deviceId);

    /**
     * 管理员或教师将设备状态从维修中改为可用
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @throws BusinessException 如果设备不存在、已报废、状态不是维修中或权限不足
     * @throws DAOException 如果数据库操作失败
     */
    void returnDeviceFromRepair(String operatorToken, Integer deviceId);

    /**
     * 统计各状态设备的数量
     * @param adminToken 管理员的会话令牌
     * @return 状态到数量的映射
     * @throws BusinessException 如果操作用户非管理员
     * @throws DAOException 如果数据库操作失败
     */
    Map<String, Integer> getDeviceStatusStatistics(String adminToken);

    /**
     * 管理员恢复已报废的设备
     * @param adminToken 管理员的会话令牌
     * @param deviceId 设备ID
     * @throws BusinessException 如果设备不存在、未报废或操作用户非管理员
     * @throws DAOException 如果数据库操作失败
     */
    void restoreScrappedDevice(String adminToken, Integer deviceId);

    /**
     * 根据存放位置查询设备列表
     * @param operatorToken 操作用户的会话令牌
     * @param location 设备存放位置
     * @return 该位置下的设备列表
     * @throws BusinessException 如果操作用户未登录或位置为空
     * @throws DAOException 如果数据库操作失败
     */
    List<Device> findDevicesByLocation(String operatorToken, String location);

    /**
     * 更新设备使用统计信息（次数+1，累加使用时长）
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @param usageHours 本次使用时长（小时）
     * @throws BusinessException 如果设备不存在、已报废、权限不足或时长无效
     * @throws DAOException 如果数据库操作失败
     */
    void updateDeviceUsageStats(String operatorToken, Integer deviceId, Double usageHours);

    /**
     * 检查设备是否可预约
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @return true-可预约 false-不可预约
     * @throws BusinessException 如果设备不存在或操作用户未登录
     * @throws DAOException 如果数据库操作失败
     */
    boolean checkDeviceReservable(String operatorToken, Integer deviceId);

    /**
     * 管理员批量更新设备存放位置
     * @param adminToken 管理员的会话令牌
     * @param deviceIds 待更新的设备ID列表
     * @param newLocation 新的存放位置
     * @throws BusinessException 如果操作用户非管理员、设备ID无效或位置为空
     * @throws DAOException 如果数据库操作失败
     */
    void batchUpdateDeviceLocation(String adminToken, List<Integer> deviceIds, String newLocation);

    /**
     * 查询设备是否在保修期内
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @return true-在保 false-超保
     * @throws BusinessException 如果设备不存在、无购买日期或操作用户未登录
     * @throws DAOException 如果数据库操作失败
     */
    boolean checkDeviceInWarranty(String operatorToken, Integer deviceId);

    /**
     * 获取设备详细使用统计信息
     * @param operatorToken 操作用户的会话令牌
     * @param deviceId 设备ID
     * @return 格式化的使用统计信息（如：使用5次 累计8.5小时）
     * @throws BusinessException 如果设备不存在或操作用户未登录
     * @throws DAOException 如果数据库操作失败
     */
    String getDeviceUsageInfo(String operatorToken, Integer deviceId);
}