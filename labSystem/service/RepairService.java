package labSystem.service;

import labSystem.entity.Repair;
import labSystem.exception.BusinessException;
import labSystem.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 报修服务接口
 */
public interface RepairService {

    /**
     * 创建新的报修单
     * @param userToken 报修用户的token
     * @param repair 报修信息 (deviceId, title, description 等)
     * @return 创建成功的报修单
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    Repair createRepair(String userToken, Repair repair) throws BusinessException, DAOException;

    /**
     * 根据ID获取报修单详情
     * @param operatorToken 操作用户的token
     * @param repairId 报修单ID
     * @return 报修单详情
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    Repair getRepairById(String operatorToken, int repairId) throws BusinessException, DAOException;

    /**
     * 获取当前用户的报修列表
     * @param userToken 用户的token
     * @return 报修列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<Repair> getMyRepairs(String userToken) throws BusinessException, DAOException;

    /**
     * 获取指定设备的报修历史
     * @param operatorToken 操作用户的token
     * @param deviceId 设备ID
     * @return 报修列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<Repair> getRepairsByDevice(String operatorToken, int deviceId) throws BusinessException, DAOException;

    /**
     * 获取所有报修单（分页），管理员使用
     * @param adminToken 管理员的token
     * @param page 页码
     * @param pageSize 每页大小
     * @return 报修列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<Repair> getAllRepairs(String adminToken, int page, int pageSize) throws BusinessException, DAOException;

    /**
     * 技术员开始处理报修单
     * @param technicianToken 技术员的token
     * @param repairId 报修单ID
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    void processRepair(String technicianToken, int repairId) throws BusinessException, DAOException;

    /**
     * 技术员标记报修单为已解决
     * @param technicianToken 技术员的token
     * @param repairId 报修单ID
     * @param repairNotes 维修说明
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    void resolveRepair(String technicianToken, int repairId, String repairNotes) throws BusinessException, DAOException;

    /**
     * 用户或管理员关闭已解决的报修单
     * @param operatorToken 操作用户的token
     * @param repairId 报修单ID
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    void closeRepair(String operatorToken, int repairId) throws BusinessException, DAOException;

    /**
     * 获取报修单状态统计
     * @param adminToken 管理员的token
     * @return 状态-数量的映射
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    Map<String, Integer> getRepairStatusStatistics(String adminToken) throws BusinessException, DAOException;

    /**
     * 管理员或技术员按状态筛选报修单（分页）
     * @param operatorToken 操作用户token
     * @param status 报修状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 报修列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<Repair> getRepairsByStatus(String operatorToken, String status, int page, int pageSize) throws BusinessException, DAOException;

    /**
     * 获取技术员待处理的报修单
     * @param technicianToken 技术员token
     * @return 报修列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<Repair> getMyAssignedRepairs(String technicianToken) throws BusinessException, DAOException;

    /**
     * 管理员更新报修单的严重程度
     * @param adminToken 管理员token
     * @param repairId 报修单ID
     * @param severity 新的严重程度
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    void updateRepairSeverity(String adminToken, int repairId, String severity) throws BusinessException, DAOException;

    /**
     * 报修人或管理员取消未处理的报修单
     * @param operatorToken 操作用户token
     * @param repairId 报修单ID
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    void cancelRepair(String operatorToken, int repairId) throws BusinessException, DAOException;

    /**
     * 获取需要紧急处理的报修单（高或严重级别且未关闭）
     * @param operatorToken 操作用户token
     * @return 报修列表
     * @throws BusinessException 业务规则不满足时抛出
     * @throws DAOException 数据访问失败时抛出
     */
    List<Repair> getUrgentRepairs(String operatorToken) throws BusinessException, DAOException;
}