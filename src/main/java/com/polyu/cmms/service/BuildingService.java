package com.polyu.cmms.service;

import java.sql.SQLException;
import java.util.*;

/**
 * 建筑物服务类，提供建筑物相关的数据访问功能
 */
public class BuildingService extends BaseService {
    private static BuildingService instance;
    
    // 单例模式
    private BuildingService() {}
    
    public static synchronized BuildingService getInstance() {
        if (instance == null) {
            instance = new BuildingService();
        }
        return instance;
    }
    
    /**
     * 添加建筑物记录
     * @param buildingData 建筑物数据
     * @return 插入是否成功
     * @throws SQLException SQL异常
     */
    public boolean addBuilding(Map<String, Object> buildingData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO buildings (");
        StringBuilder valuesBuilder = new StringBuilder("VALUES (");
        List<Object> params = new ArrayList<>();
        
        // 添加必要字段
        if (buildingData.containsKey("buildingCode")) {
            sqlBuilder.append("building_code, ");
            valuesBuilder.append("?, ");
            params.add(buildingData.get("buildingCode"));
        }
        if (buildingData.containsKey("constructionDate")) {
            sqlBuilder.append("construction_date, ");
            valuesBuilder.append("?, ");
            params.add(buildingData.get("constructionDate"));
        }
        if (buildingData.containsKey("addressId")) {
            sqlBuilder.append("address_id, ");
            valuesBuilder.append("?, ");
            params.add(buildingData.get("addressId"));
        }
        if (buildingData.containsKey("numFloors")) {
            sqlBuilder.append("num_floors, ");
            valuesBuilder.append("?, ");
            params.add(buildingData.get("numFloors"));
        }
        if (buildingData.containsKey("supervisorStaffId")) {
            sqlBuilder.append("supervisor_staff_id, ");
            valuesBuilder.append("?, ");
            params.add(buildingData.get("supervisorStaffId"));
        }
        
        // 添加默认启用状态
        sqlBuilder.append("active_flag");
        valuesBuilder.append("'Y'");
        
        String sql = sqlBuilder.toString() + ") " + valuesBuilder.toString() + ")";
        int result = executeUpdate(sql, params.toArray());
        return result > 0;
    }
    
    /**
     * 更新建筑物信息
     * @param buildingId 建筑物ID
     * @param updates 更新的字段和值
     * @return 更新是否成功
     * @throws SQLException SQL异常
     */
    public boolean updateBuilding(int buildingId, Map<String, Object> updates) throws SQLException {
        if (updates.isEmpty()) {
            return true;
        }
        
        StringBuilder sqlBuilder = new StringBuilder("UPDATE buildings SET ");
        List<Object> params = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String dbColumn = convertToDbColumn(entry.getKey());
            sqlBuilder.append(dbColumn).append(" = ?, ");
            params.add(entry.getValue());
        }
        
        sqlBuilder.setLength(sqlBuilder.length() - 2); // 移除最后一个逗号和空格
        sqlBuilder.append(" WHERE building_id = ?");
        params.add(buildingId);
        
        int result = executeUpdate(sqlBuilder.toString(), params.toArray());
        return result > 0;
    }
    
    /**
     * 删除建筑物（软删除）
     * @param buildingId 建筑物ID
     * @return 删除是否成功
     * @throws SQLException SQL异常
     */
    public boolean deleteBuilding(int buildingId) throws SQLException {
        String sql = "UPDATE buildings SET active_flag = 'N' WHERE building_id = ?";
        int result = executeUpdate(sql, buildingId);
        return result > 0;
    }
    
    /**
     * 根据条件查询建筑物
     * @param conditions 查询条件
     * @return 建筑物列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> queryBuildings(Map<String, Object> conditions) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM buildings WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (conditions != null) {
            // 转换Java属性名为数据库列名
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String dbColumn = convertToDbColumn(entry.getKey());
                sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                params.add(entry.getValue());
            }
        }
        
        return executeQuery(sqlBuilder.toString(), params.toArray());
    }
    
    /**
     * 分页查询建筑物
     * @param page 页码
     * @param pageSize 每页大小
     * @param conditions 查询条件
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     * @return 分页查询结果
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getBuildingsByPage(int page, int pageSize, Map<String, Object> conditions, String sortField, String sortOrder) throws SQLException {
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 30;
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM buildings WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // 添加过滤条件
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String dbColumn = convertToDbColumn(entry.getKey());
                sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                params.add(entry.getValue());
            }
        }
        
        // 添加排序
        if (sortField != null && !sortField.trim().isEmpty()) {
            String dbColumn = convertToDbColumn(sortField);
            String order = sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "DESC" : "ASC";
            sqlBuilder.append(" ORDER BY ").append(dbColumn).append(" ").append(order);
        } else {
            // 默认按ID排序
            sqlBuilder.append(" ORDER BY building_id ASC");
        }
        
        // 添加分页
        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        // 获取数据
        List<Map<String, Object>> data = executeQuery(sqlBuilder.toString(), params.toArray());
        
        // 获取总数
        StringBuilder countSqlBuilder = new StringBuilder("SELECT COUNT(*) FROM buildings WHERE 1=1");
        List<Object> countParams = new ArrayList<>();
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String dbColumn = convertToDbColumn(entry.getKey());
                countSqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                countParams.add(entry.getValue());
            }
        }
        
        List<Map<String, Object>> countResult = executeQuery(countSqlBuilder.toString(), countParams.toArray());
        int total = 0;
        
        // 安全获取总记录数，避免空指针异常
        if (countResult != null && !countResult.isEmpty()) {
            Map<String, Object> firstRow = countResult.get(0);
            if (firstRow != null) {
                Object countObj = firstRow.get("count");
                if (countObj instanceof Number) {
                    total = ((Number) countObj).intValue();
                } else if (countObj != null) {
                    try {
                        total = Integer.parseInt(countObj.toString());
                    } catch (NumberFormatException e) {
                        total = 0;
                    }
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        return result;
    }
    
    /**
     * 根据ID获取建筑物详情
     * @param buildingId 建筑物ID
     * @return 建筑物详情
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getBuildingById(int buildingId) throws SQLException {
        String sql = "SELECT * FROM buildings WHERE building_id = ?";
        List<Map<String, Object>> results = executeQuery(sql, buildingId);
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * 获取所有活跃的建筑物
     * @return 活跃建筑物列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> getAllActiveBuildings() throws SQLException {
        String sql = "SELECT * FROM buildings WHERE active_flag = 'Y' ORDER BY building_code";
        return executeQuery(sql);
    }
    
    /**
     * 根据负责经理ID查询建筑物
     * @param supervisorStaffId 负责经理ID
     * @return 建筑物列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> getBuildingsBySupervisor(int supervisorStaffId) throws SQLException {
        String sql = "SELECT * FROM buildings WHERE supervisor_staff_id = ? AND active_flag = 'Y'";
        return executeQuery(sql, supervisorStaffId);
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
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}