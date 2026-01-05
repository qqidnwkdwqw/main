package labSystem.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 基础DAO接口  
 * 定义通用的数据库操作方法
 * @param <T> 实体类型
 * @param <K> 主键类型
 */
public interface BaseDao<T, K> {
    
    /**
     * 插入记录
     * @param entity 实体对象
     * @return 影响的行数
     * @throws SQLException
     */
    int insert(T entity);
    
    /**
     * 根据ID删除记录
     * @param id 主键ID
     * @return 影响的行数
     * @throws SQLException
     */
    int deleteById(K id);
    
    /**
     * 更新记录
     * @param entity 实体对象
     * @return 影响的行数
     * @throws SQLException
     */
    int update(T entity);
    
    /**
     * 根据ID查询记录
     * @param id 主键ID
     * @return 实体对象，未找到返回null
     * @throws SQLException
     */
    T findById(K id);
    
    /**
     * 查询所有记录
     * @return 实体列表
     * @throws SQLException
     */
    List<T> findAll();
    
    /**
     * 分页查询
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 实体列表
     * @throws SQLException
     */
    List<T> findByPage(int page, int pageSize);
    
    /**
     * 统计记录总数
     * @return 总数
     * @throws SQLException
     */
    long count();
    
    /**
     * 使用外部连接执行操作
     * @param conn 数据库连接
     * @param entity 实体对象
     * @return 影响的行数
     * @throws SQLException
     */
    int insert(Connection conn, T entity);
    
    /**
     * 使用外部连接更新
     * @param conn 数据库连接
     * @param entity 实体对象
     * @return 影响的行数
     * @throws SQLException
     */
    int update(Connection conn, T entity);
}