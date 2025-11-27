package com.polyu.cmms.service;

import java.sql.SQLException;
import java.util.*;

/**
 * 楼层服务类，提供楼层相关的数据访问功能
 */
public class LevelService extends BaseService {
    private static LevelService instance;
    
    // 单例模式
    private LevelService() {}
    
    public static synchronized LevelService getInstance() {
        if (instance == null) {
            instance = new LevelService();
        }
    
        return instance;
    }
    
    
    /**
     * 将Java驼峰命名转换为数据库下划线命名
     * @param javaName Java属性名（驼峰命名）
     * @return 数据库列名（下划线命名）
     */
    private String convertToDbColumn(String javaName) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < javaName.length(); i++) {
            char c = javaName.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append('_');
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }

    /**
     * 根据条件查询楼层
     * @param conditions 查询条件
     * @return 楼层列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> queryLevels(Map<String, Object> conditions) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM levels WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String dbColumn = convertToDbColumn(entry.getKey());
                sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                params.add(entry.getValue());
            }
        }
        
        return executeQuery(sqlBuilder.toString(), params.toArray());
    }
    
    /**
     * 根据ID获取楼层详情
     * @param levelId 楼层ID
     * @return 楼层详情
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getLevelById(int levelId) throws SQLException {
        String sql = "SELECT * FROM levels WHERE level_id = ?";
        List<Map<String, Object>> results = executeQuery(sql, levelId);
        return results != null && !results.isEmpty() ? results.get(0) : null;
    }
    
    /**
     * 分页查询楼层
     * @param page 页码
     * @param pageSize 每页大小
     * @param conditions 查询条件
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     * @return 分页查询结果
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getLevelsByPage(int page, int pageSize, Map<String, Object> conditions, String sortField, String sortOrder) throws SQLException {
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 30;
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM levels WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String dbColumn = convertToDbColumn(entry.getKey());
                sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                params.add(entry.getValue());
            }
        }
        
        // 添加分页
        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        List<Map<String, Object>> data = executeQuery(sqlBuilder.toString(), params.toArray());
        
        // 返回数据和分页信息
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    /**
     * 根据ID删除楼层
     * @param levelId 楼层ID
     * @return 是否删除成功
     * @throws SQLException SQL异常
     */
    public boolean deleteLevel(int levelId) throws SQLException {
        String sql = "DELETE FROM levels WHERE level_id = ?";
        return executeUpdate(sql, levelId) > 0;
    }
}