package com.polyu.cmms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// import java.sql.Statement;

public class DatabaseUtil {
    // TiDB Cloud数据库连接配置（根据用户提供的新配置）
    private static final String URL = "jdbc:mysql://3yZKtrYwuR4Coqh.root:i2jo6bBHviADptG6@gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/test2?sslMode=VERIFY_IDENTITY";
    private static final String USERNAME = "3yZKtrYwuR4Coqh.root";
    private static final String PASSWORD = "i2jo6bBHviADptG6"; // 使用用户提供的密码
    private static Connection connection = null;
    
    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // 加载MySQL驱动
                Class.forName("com.mysql.cj.jdbc.Driver");
                // 建立连接
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // 设置事务隔离级别为READ COMMITTED
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                // // 初始化数据库表（如果需要）
                // initializeDatabase();
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL驱动未找到", e);
            }
        }
        return connection;
    }
    
    // 关闭数据库连接
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 初始化数据库表
    // private static void initializeDatabase() {
        // try (Statement stmt = connection.createStatement()) {
        //     // 创建用户角色表
        //     stmt.executeUpdate("""
        //         CREATE TABLE IF NOT EXISTS user_roles (
        //             role_id INT PRIMARY KEY AUTO_INCREMENT,
        //             role_name VARCHAR(100) NOT NULL UNIQUE,
        //             description VARCHAR(255)
        //         )
        //     """);
            
        //     // 创建员工表
        //     stmt.executeUpdate("""
        //         CREATE TABLE IF NOT EXISTS staff (
        //             staff_id INT PRIMARY KEY AUTO_INCREMENT,
        //             staff_number VARCHAR(50) NOT NULL UNIQUE,
        //             first_name VARCHAR(100) NOT NULL,
        //             last_name VARCHAR(100) NOT NULL,
        //             gender VARCHAR(10),
        //             date_of_birth DATE,
        //             phone_number VARCHAR(20),
        //             email VARCHAR(100),
        //             id_card_number VARCHAR(50),
        //             hire_date DATE,
        //             department VARCHAR(100),
        //             role_id INT,
        //             address_id INT,
        //             active_flag VARCHAR(10),
        //             FOREIGN KEY (role_id) REFERENCES user_roles(role_id)
        //         )
        //     """);
            
        //     // 创建用户表
        //     stmt.executeUpdate("""
        //         CREATE TABLE IF NOT EXISTS users (
        //             user_id INT PRIMARY KEY AUTO_INCREMENT,
        //             staff_id INT,
        //             username VARCHAR(100) NOT NULL UNIQUE,
        //             password VARCHAR(255) NOT NULL,
        //             role_id INT,
        //             FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
        //             FOREIGN KEY (role_id) REFERENCES user_roles(role_id)
        //         )
        //     """);
            
        //     // 创建权限表
        //     stmt.executeUpdate("""
        //         CREATE TABLE IF NOT EXISTS permissions (
        //             permission_id INT PRIMARY KEY AUTO_INCREMENT,
        //             permission_name VARCHAR(100) NOT NULL UNIQUE,
        //             description VARCHAR(255)
        //         )
        //     """);
            
        //     // 创建角色-权限关联表
        //     stmt.executeUpdate("""
        //         CREATE TABLE IF NOT EXISTS role_permissions (
        //             role_id INT,
        //             permission_id INT,
        //             PRIMARY KEY (role_id, permission_id),
        //             FOREIGN KEY (role_id) REFERENCES user_roles(role_id),
        //             FOREIGN KEY (permission_id) REFERENCES permissions(permission_id)
        //         )
        //     """);
            
        //     // 初始化基础角色
        //     stmt.executeUpdate("INSERT INTO user_roles (role_name, description) VALUES ('管理员', '系统管理员，拥有所有权限') ON DUPLICATE KEY UPDATE role_name=role_name");
        //     stmt.executeUpdate("INSERT INTO user_roles (role_name, description) VALUES ('中层管理', '部门管理者，可以管理本部门人员和活动') ON DUPLICATE KEY UPDATE role_name=role_name");
        //     stmt.executeUpdate("INSERT INTO user_roles (role_name, description) VALUES ('基层工人', '普通工作人员，只能查看和执行分配的任务') ON DUPLICATE KEY UPDATE role_name=role_name");
            
        //     // 初始化基础权限
        //     stmt.executeUpdate("INSERT INTO permissions (permission_name, description) VALUES ('VIEW_STAFF', '查看员工信息') ON DUPLICATE KEY UPDATE permission_name=permission_name");
        //     stmt.executeUpdate("INSERT INTO permissions (permission_name, description) VALUES ('MANAGE_STAFF', '管理员工信息') ON DUPLICATE KEY UPDATE permission_name=permission_name");
        //     stmt.executeUpdate("INSERT INTO permissions (permission_name, description) VALUES ('VIEW_ACTIVITY', '查看活动信息') ON DUPLICATE KEY UPDATE permission_name=permission_name");
        //     stmt.executeUpdate("INSERT INTO permissions (permission_name, description) VALUES ('MANAGE_ACTIVITY', '管理活动信息') ON DUPLICATE KEY UPDATE permission_name=permission_name");
        //     stmt.executeUpdate("INSERT INTO permissions (permission_name, description) VALUES ('VIEW_REPORT', '查看报表') ON DUPLICATE KEY UPDATE permission_name=permission_name");
        //     stmt.executeUpdate("INSERT INTO permissions (permission_name, description) VALUES ('GENERATE_REPORT', '生成报表') ON DUPLICATE KEY UPDATE permission_name=permission_name");
        //     stmt.executeUpdate("INSERT INTO permissions (permission_name, description) VALUES ('ADMINISTER_SYSTEM', '系统管理权限') ON DUPLICATE KEY UPDATE permission_name=permission_name");
            
        //     // 为角色分配权限
        //     stmt.executeUpdate("INSERT INTO role_permissions SELECT 1, p.permission_id FROM permissions p LEFT JOIN role_permissions rp ON p.permission_id = rp.permission_id AND rp.role_id = 1 WHERE rp.permission_id IS NULL"); // 管理员拥有所有权限
            
        //     // 中层管理权限
        //     stmt.executeUpdate("INSERT INTO role_permissions SELECT 2, p.permission_id FROM permissions p LEFT JOIN role_permissions rp ON p.permission_id = rp.permission_id AND rp.role_id = 2 WHERE p.permission_name IN ('VIEW_STAFF', 'VIEW_ACTIVITY', 'MANAGE_ACTIVITY', 'VIEW_REPORT') AND rp.permission_id IS NULL");
            
        //     // 基层工人权限
        //     stmt.executeUpdate("INSERT INTO role_permissions SELECT 3, p.permission_id FROM permissions p LEFT JOIN role_permissions rp ON p.permission_id = rp.permission_id AND rp.role_id = 3 WHERE p.permission_name IN ('VIEW_STAFF', 'VIEW_ACTIVITY') AND rp.permission_id IS NULL");
            
        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }
    // }
}