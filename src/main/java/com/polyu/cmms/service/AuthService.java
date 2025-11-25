package com.polyu.cmms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 认证服务类，提供用户认证和授权相关的功能
 */
public class AuthService extends BaseService {
    private static AuthService instance;
    private static String currentUsername = null;
    private static Integer currentUserId = null;
    private static String currentRole = null;
    
    // 静态初始化块 - 用于默认初始化管理员身份
    static {
        // 默认设置管理员身份
        currentUserId = 1;
        currentRole = "管理员";
        currentUsername = "admin";
    }
    
    // 单例模式
    private AuthService() {}
    
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * 用户登录验证 - 简化版本
     * @param username 用户名（员工编号）
     * @param password 密码（固定为123）
     * @return 验证是否成功
     * @throws SQLException SQL异常
     */
    public boolean login(String username, String password) throws SQLException {
        // 简化登录：用户名是员工编号，密码固定为123
        if (!"123".equals(password)) {
            return false;
        }
        
        // 查询staff表检查员工是否存在
        String sql = "SELECT staff_id, role_id FROM staff WHERE staff_number = ? AND active_flag = 'Y'";
        List<Map<String, Object>> results = executeQuery(sql, username);
        if (!results.isEmpty()) {
            currentUsername = username;
            // 安全获取staff_id和role_id，避免空指针异常
            Map<String, Object> result = results.get(0);
            if (result != null) {
                currentUserId = result.get("staff_id") instanceof Integer ? (Integer) result.get("staff_id") : null;
                // 根据role_id简单设置角色名称
                int roleId = getIntValue(result.get("role_id"), 0);
                switch (roleId) {
                    case 1:
                        currentRole = "管理员";
                        break;
                    case 2:
                        currentRole = "主管";
                        break;
                    default:
                        currentRole = "普通员工";
                        break;
                }
            } else {
                // 如果result为null，设置默认值
                currentUserId = null;
                currentRole = "普通员工";
            }
            return true;
        }
        return false;
    }
    
    /**
     * 获取用户角色 - 简化版本
     * @param username 用户名（员工编号）
     * @return 用户角色
     * @throws SQLException SQL异常
     */
    public String getUserRole(String username) throws SQLException {
        // 如果已经登录，直接返回当前角色
        if (currentUsername != null && currentUsername.equals(username)) {
            return currentRole;
        }
        
        // 查询staff表获取角色信息
        String sql = "SELECT role_id FROM staff WHERE staff_number = ? AND active_flag = 'Y'";
        List<Map<String, Object>> results = executeQuery(sql, username);
        if (results.isEmpty()) {
            return "普通员工";
        }
        
        // 确保使用getIntValue方法进行安全类型转换
        Map<String, Object> result = results.get(0);
        int roleId = getIntValue(result != null ? result.get("role_id") : null, 0);
        switch (roleId) {
            case 1:
                return "管理员";
            case 2:
                return "主管";
            default:
                return "普通员工";
        }
    }
    
    /**
     * 检查用户是否有特定权限 - 简化版本
     * @param username 用户名
     * @param permission 权限代码
     * @return 是否有权限
     * @throws SQLException SQL异常
     */
    public boolean hasPermission(String username, String permission) throws SQLException {
        // 获取用户角色
        String role = getUserRole(username);
        
        // 根据角色简单判断权限
        // 管理员有所有权限
        if ("管理员".equals(role)) {
            return true;
        }
        // 主管有部分管理权限
        else if ("主管".equals(role)) {
            // 主管可以管理活动和查看报表，但不能管理员工
            return !"MANAGE_STAFF".equals(permission);
        }
        // 普通员工只有查看权限
        else {
            return permission.startsWith("VIEW_");
        }
    }
    
    /**
     * 检查当前登录用户是否有特定权限 - 简化版本
     * @param permission 权限代码
     * @return 是否有权限
     * @throws SQLException SQL异常
     */
    // 安全获取整数值的辅助方法
    private int getIntValue(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public boolean hasPermission(String permission) throws SQLException {
        if (currentUsername == null) {
            return false;
        }
        
        // 根据当前角色直接判断权限
        if ("管理员".equals(currentRole)) {
            return true;
        } else if ("主管".equals(currentRole)) {
            return !"MANAGE_STAFF".equals(permission);
        } else {
            return permission.startsWith("VIEW_");
        }
    }
    
    /**
     * 获取当前登录用户ID
     * @return 当前用户ID
     */
    public Integer getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * 获取当前登录用户角色
     * @return 当前用户角色
     */
    public String getCurrentRole() {
        return currentRole;
    }
    
    /**
     * 用户登出
     */
    public void logout() {
        currentUsername = null;
        currentUserId = null;
        currentRole = null;
    }
}