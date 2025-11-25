package com.polyu.cmms.util.createdata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectDB {
    public static void main(String[] args) {
        // 1. 数据库连接参数（从TiDB Cloud获取，替换密码！）
        String host = "gateway01.ap-southeast-1.prod.aws.tidbcloud.com";
        int port = 4000;
        String database = "test1";
        String username = "3yZKtrYwuR4Coqh.root";
        String password = "PGzKU7mGSEDy7CKt"; // 替换为你重置后的密码
        // CA证书路径（resources目录下的相对路径，无需写全路径）
        String caPath = ConnectDB.class.getClassLoader().getResource("cert/isrgrootx1.pem").getPath();

        // 2. 构造JDBC URL（Windows路径自动兼容，无需转义）
        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?sslMode=VERIFY_IDENTITY&sslCa=%s",
                host, port, database, caPath
        );

        
        // 3. 连接数据库并创建表
        try (
                // 自动关闭连接（try-with-resources语法，无需手动close）
                Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
                Statement stmt = conn.createStatement()
        ) {
            System.out.println("✅ 数据库连接成功！");

            // 建表SQL（创建users表，包含id、name、age字段）
            String createTableSql = "CREATE TABLE IF NOT EXISTS `users` (" +
                    "`id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID'," +
                    "`name` VARCHAR(50) NOT NULL COMMENT '用户名'," +
                    "`age` INT COMMENT '用户年龄'" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            String addingData = "INSERT INTO `users` (`name`, `age`) VALUES ('张三', 25);";
            stmt.executeUpdate(createTableSql);
            stmt.executeUpdate(addingData);
            System.out.println("✅ 表`users`创建成功！");
            System.out.println("✅ 数据插入成功！");

        } catch (Exception e) {
            System.err.println("❌ 操作失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}