package com.polyu.cmms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 通用查询服务类，提供SQL执行和结果处理功能
 */
public class QueryService extends BaseService {
    
    /**
     * 执行自定义SQL查询
     * @param sql SQL查询语句
     * @param params 参数列表
     * @return 查询结果
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> executeCustomQuery(String sql, Object... params) throws SQLException {
        // 安全检查：防止危险的SQL注入
        if (sql.toUpperCase().contains("DELETE") || sql.toUpperCase().contains("DROP") || 
            sql.toUpperCase().contains("ALTER") || sql.toUpperCase().contains("TRUNCATE")) {
            throw new SQLException("不允许执行危险的SQL语句");
        }
        
        return executeQuery(sql, params);
    }
    
    /**
     * 执行分页查询
     * @param sql SQL查询语句（不包含LIMIT子句）
     * @param page 页码
     * @param pageSize 每页大小
     * @param params 参数列表
     * @return 分页查询结果
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> executePagedQuery(String sql, int page, int pageSize, Object... params) throws SQLException {
        // 添加分页子句
        String pagedSql = sql + " LIMIT ? OFFSET ?";
        
        // 扩展参数数组
        Object[] pagedParams = new Object[params.length + 2];
        System.arraycopy(params, 0, pagedParams, 0, params.length);
        pagedParams[params.length] = pageSize;
        pagedParams[params.length + 1] = (page - 1) * pageSize;
        
        return executeQuery(pagedSql, pagedParams);
    }
    
    /**
     * 获取查询结果总数
     * @param sql SQL查询语句（不包含LIMIT子句）
     * @param params 参数列表
     * @return 结果总数
     * @throws SQLException SQL异常
     */
    public int getQueryCount(String sql, Object... params) throws SQLException {
        // 构造COUNT查询
        String countSql = "SELECT COUNT(*) as count FROM (" + sql + ") as temp";
        
        List<Map<String, Object>> results = executeQuery(countSql, params);
        if (results == null || results.isEmpty()) {
            return 0;
        }
        
        Map<String, Object> firstRow = results.get(0);
        if (firstRow == null) {
            return 0;
        }
        
        Object countObj = firstRow.get("count");
        if (countObj instanceof Number) {
            return ((Number) countObj).intValue();
        } else if (countObj != null) {
            try {
                return Integer.parseInt(countObj.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}