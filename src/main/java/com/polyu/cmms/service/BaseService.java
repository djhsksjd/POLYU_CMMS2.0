package com.polyu.cmms.service;

import com.polyu.cmms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础服务类，提供通用的数据库操作方法
 */
public abstract class BaseService {
    
    /**
     * 获取数据库连接
     * @return Connection 对象
     * @throws SQLException SQL异常
     */
    protected Connection getConnection() throws SQLException {
        return DatabaseUtil.getConnection();
    }
    
    /**
     * 将数据库下划线命名转换为Java驼峰命名
     * @param dbName 数据库列名（下划线命名）
     * @return Java属性名（驼峰命名）
     */
    protected String convertToJavaName(String dbName) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (int i = 0; i < dbName.length(); i++) {
            char c = dbName.charAt(i);
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 执行SQL查询，返回结果集
     * @param sql SQL查询语句
     * @param params 参数列表
     * @return 查询结果列表
     * @throws SQLException SQL异常
     */
    protected List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params);
             ResultSet rs = stmt.executeQuery()) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    // 将数据库下划线命名转换为Java驼峰命名
                    String javaName = convertToJavaName(columnName);
                    Object value = rs.getObject(i);
                    row.put(javaName, value);
                }
                results.add(row);
            }
        }
        return results;
    }
    
    /**
     * 执行更新操作（INSERT、UPDATE、DELETE）
     * @param sql SQL语句
     * @param params 参数列表
     * @return 受影响的行数
     * @throws SQLException SQL异常
     */
    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params)) {
            return stmt.executeUpdate();
        }
    }
    
    /**
     * 执行批量更新操作
     * @param sql SQL语句
     * @param paramsList 参数列表的列表
     * @return 每个更新操作受影响的行数数组
     * @throws SQLException SQL异常
     */
    protected int[] executeBatch(String sql, List<Object[]> paramsList) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Object[] params : paramsList) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.addBatch();
            }
            
            return stmt.executeBatch();
        }
    }
    
    /**
     * 执行事务
     * @param operations 事务操作列表
     * @throws SQLException SQL异常
     */
    protected void executeTransaction(List<TransactionOperation> operations) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            for (TransactionOperation operation : operations) {
                try (PreparedStatement stmt = prepareStatement(conn, operation.getSql(), operation.getParams())) {
                    stmt.executeUpdate();
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // 记录回滚异常，但不覆盖原始异常
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 准备带有参数的PreparedStatement
     * @param conn 数据库连接
     * @param sql SQL语句
     * @param params 参数列表
     * @return PreparedStatement 对象
     * @throws SQLException SQL异常
     */
    private PreparedStatement prepareStatement(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt;
    }
    
    /**
     * 事务操作接口
     */
    public static class TransactionOperation {
        private final String sql;
        private final Object[] params;
        
        public TransactionOperation(String sql, Object... params) {
            this.sql = sql;
            this.params = params;
        }
        
        public String getSql() {
            return sql;
        }
        
        public Object[] getParams() {
            return params;
        }
    }
}