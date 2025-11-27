package com.polyu.cmms.service;

import java.sql.SQLException;
import java.util.*;

/**
 * 化学品服务类，提供化学品相关的数据访问功能
 */
public class ChemicalService extends BaseService {
    private static ChemicalService instance;
    
    // 单例模式
    private ChemicalService() {}
    
    public static synchronized ChemicalService getInstance() {
        if (instance == null) {
            instance = new ChemicalService();
        }
        return instance;
    }
    
    /**
     * 添加化学品记录
     * @param chemicalData 化学品数据
     * @return 插入是否成功
     * @throws SQLException SQL异常
     */
    public boolean addChemical(Map<String, Object> chemicalData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO chemical (");
        StringBuilder valuesBuilder = new StringBuilder("VALUES (");
        List<Object> params = new ArrayList<>();
        
        // 添加必要字段
        if (chemicalData.containsKey("productCode")) {
            sqlBuilder.append("product_code, ");
            valuesBuilder.append("?, ");
            params.add(chemicalData.get("productCode"));
        }
        if (chemicalData.containsKey("name")) {
            sqlBuilder.append("name, ");
            valuesBuilder.append("?, ");
            params.add(chemicalData.get("name"));
        }
        if (chemicalData.containsKey("type")) {
            sqlBuilder.append("type, ");
            valuesBuilder.append("?, ");
            params.add(chemicalData.get("type"));
        }
        if (chemicalData.containsKey("manufacturer")) {
            sqlBuilder.append("manufacturer, ");
            valuesBuilder.append("?, ");
            params.add(chemicalData.get("manufacturer"));
        }
        if (chemicalData.containsKey("msdsUrl")) {
            sqlBuilder.append("msds_url, ");
            valuesBuilder.append("?, ");
            params.add(chemicalData.get("msdsUrl"));
        }
        if (chemicalData.containsKey("hazardCategory")) {
            sqlBuilder.append("hazard_category, ");
            valuesBuilder.append("?, ");
            params.add(chemicalData.get("hazardCategory"));
        }
        if (chemicalData.containsKey("storageRequirements")) {
            sqlBuilder.append("storage_requirements, ");
            valuesBuilder.append("?, ");
            params.add(chemicalData.get("storageRequirements"));
        }
        
        // 添加默认启用状态
        sqlBuilder.append("active_flag");
        valuesBuilder.append("'Y'");
        
        String sql = sqlBuilder.toString() + ") " + valuesBuilder.toString() + ")";
        int result = executeUpdate(sql, params.toArray());
        return result > 0;
    }
    
    /**
     * 更新化学品信息
     * @param chemicalId 化学品ID
     * @param updates 更新的字段和值
     * @return 更新是否成功
     * @throws SQLException SQL异常
     */
    public boolean updateChemical(int chemicalId, Map<String, Object> updates) throws SQLException {
        if (updates.isEmpty()) {
            return true;
        }
        
        StringBuilder sqlBuilder = new StringBuilder("UPDATE chemical SET ");
        List<Object> params = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String dbColumn = convertToDbColumn(entry.getKey());
            sqlBuilder.append(dbColumn).append(" = ?, ");
            params.add(entry.getValue());
        }
        
        sqlBuilder.setLength(sqlBuilder.length() - 2); // 移除最后一个逗号和空格
        sqlBuilder.append(" WHERE chemical_id = ?");
        params.add(chemicalId);
        
        int result = executeUpdate(sqlBuilder.toString(), params.toArray());
        return result > 0;
    }
    
    /**
     * 删除化学品（软删除）
     * @param chemicalId 化学品ID
     * @return 删除是否成功
     * @throws SQLException SQL异常
     */
    public boolean deleteChemical(int chemicalId) throws SQLException {
        String sql = "UPDATE chemical SET active_flag = 'N' WHERE chemical_id = ?";
        int result = executeUpdate(sql, chemicalId);
        return result > 0;
    }
    
    /**
     * 根据条件查询化学品
     * @param conditions 查询条件
     * @return 化学品列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> queryChemicals(Map<String, Object> conditions) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM chemical WHERE 1=1");
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
     * 分页查询化学品
     * @param page 页码
     * @param pageSize 每页大小
     * @param conditions 查询条件
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     * @return 分页查询结果
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getChemicalsByPage(int page, int pageSize, Map<String, Object> conditions, String sortField, String sortOrder) throws SQLException {
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 30;
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM chemical WHERE 1=1");
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
            sqlBuilder.append(" ORDER BY chemical_id ASC");
        }
        
        // 添加分页
        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        // 获取数据
        List<Map<String, Object>> data = executeQuery(sqlBuilder.toString(), params.toArray());
        
        // 获取总数
        StringBuilder countSqlBuilder = new StringBuilder("SELECT COUNT(*) FROM chemical WHERE 1=1");
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
        // 安全计算总页数，避免类型转换问题
        int totalPages = 0;
        if (pageSize > 0) {
            try {
                totalPages = (int) Math.ceil((double) total / pageSize);
            } catch (Exception e) {
                totalPages = 0;
            }
        }
        result.put("totalPages", totalPages);
        
        return result;
    }
    
    /**
     * 根据ID获取化学品详情
     * @param chemicalId 化学品ID
     * @return 化学品详情
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getChemicalById(int chemicalId) throws SQLException {
        String sql = "SELECT * FROM chemical WHERE chemical_id = ?";
        List<Map<String, Object>> results = executeQuery(sql, chemicalId);
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * 获取所有活跃的化学品
     * @return 活跃化学品列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> getAllActiveChemicals() throws SQLException {
        String sql = "SELECT * FROM chemical WHERE active_flag = 'Y' ORDER BY name";
        return executeQuery(sql);
    }
    
    /**
     * 根据危险类别查询化学品
     * @param hazardCategory 危险类别
     * @return 化学品列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> getChemicalsByHazardCategory(String hazardCategory) throws SQLException {
        String sql = "SELECT * FROM chemical WHERE hazard_category = ? AND active_flag = 'Y' ORDER BY name";
        return executeQuery(sql, hazardCategory);
    }
    
    /**
     * 根据类型查询化学品
     * @param type 化学品类型
     * @return 化学品列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> getChemicalsByType(String type) throws SQLException {
        String sql = "SELECT * FROM chemical WHERE type = ? AND active_flag = 'Y' ORDER BY name";
        return executeQuery(sql, type);
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