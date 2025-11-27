package com.polyu.cmms.util.createdata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateTable {
    public static void main(String[] args) {
        // 1. 数据库连接参数（从TiDB Cloud获取，替换密码！）
        String host = "gateway01.ap-southeast-1.prod.aws.tidbcloud.com";
        int port = 4000;
        String database = "test";
        String username = "3yZKtrYwuR4Coqh.root";
        String password = "p5e9zsWpB92ClYFW"; // 替换为你重置后的密码
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
            try{
                // 建表SQL
                String createRole = "-- 1. 角色表（基础表，无外键依赖）\n" +
                        "CREATE TABLE Role (\n" +
                        "    role_id INT AUTO_INCREMENT COMMENT '角色唯一标识',\n" +
                        "    role_name VARCHAR(50) NOT NULL COMMENT '角色名称（行政官/中层经理/基层员工）',\n" +
                    "    role_level INT NOT NULL COMMENT '角色层级（1=行政官，2=中层经理，3=基层员工）',\n" +
                    "    description VARCHAR(200) COMMENT '角色职责描述',\n" +
                    "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '角色启用状态（Y=启用，N=禁用）',\n" +
                    "    PRIMARY KEY (role_id),\n" +
                    "    UNIQUE KEY uk_role_name (role_name),\n" +
                    "    CHECK (role_name IN ('executive_officer', 'mid_level_manager', 'base_level_worker')),\n" +
                    "    CHECK (role_level IN (1, 2, 3)),\n" +
                    "    CHECK ((role_level=1 AND role_name='executive_officer') \n" +
                    "           OR (role_level=2 AND role_name='mid_level_manager') \n" +
                    "           OR (role_level=3 AND role_name='base_level_worker')),\n" +
                    "    CHECK (active_flag IN ('Y', 'N'))\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储员工角色信息，通过role_name与role_level强关联确保逻辑一致性';\n";
                    
                    stmt.executeUpdate(createRole);
                    System.out.println("✅ 表`Role`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Role`创建失败！");
                // e.printStackTrace();
            }
            try{
                // 创建系统配置表
                String createSystemLimits = "-- 2. 系统配置表（基础表，无外键依赖）\n" +
                        "CREATE TABLE SystemLimits (\n" +
                        "    system_limits_id INT AUTO_INCREMENT COMMENT '配置唯一标识',\n" +
                        "    max_mid_level_managers INT NOT NULL COMMENT '中层经理最大人数限制',\n" +
                    "    max_base_level_workers INT NOT NULL COMMENT '基层员工最大人数限制',\n" +
                    "    effective_date DATE NOT NULL COMMENT '配置生效日期',\n" +
                    "    PRIMARY KEY (system_limits_id),\n" +
                    "    CHECK (max_mid_level_managers >= 0),\n" +
                    "    CHECK (max_base_level_workers >= 0)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储员工人数限制配置，支持生效日期管理';\n";
            stmt.executeUpdate(createSystemLimits);
            System.out.println("✅ 表`SystemLimits`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`SystemLimits`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建地址表
                String createAddress = "-- 3. 地址表（基础表，无外键依赖，统一管理地址信息）\n" +
                        "CREATE TABLE Address (\n" +
                        "    address_id INT AUTO_INCREMENT COMMENT '地址唯一标识',\n" +
                        "    street VARCHAR(100) COMMENT '街道地址',\n" +
                    "    city VARCHAR(50) COMMENT '城市',\n" +
                    "    postal_code VARCHAR(20) COMMENT '邮政编码',\n" +
                    "    country VARCHAR(50) COMMENT '国家/地区',\n" +
                    "    detail VARCHAR(200) COMMENT '详细地址（如楼栋号、门牌号）',\n" +
                    "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '地址启用状态（Y=启用，N=禁用）',\n" +
                    "    PRIMARY KEY (address_id),\n" +
                    "    CHECK (active_flag IN ('Y', 'N'))\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '统一管理所有实体的地址信息，消除多表地址冗余';\n";
            stmt.executeUpdate(createAddress);
            System.out.println("✅ 表`Address`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Address`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建员工表
                String createStaff = "-- 4. 员工表（依赖Role表）\n" +
                        "CREATE TABLE Staff (\n" +
                        "    staff_id INT AUTO_INCREMENT COMMENT '员工唯一标识',\n" +
                        "    staff_number VARCHAR(20) NOT NULL COMMENT '员工工号',\n" +
                    "    first_name VARCHAR(50) NOT NULL COMMENT '名',\n" +
                    "    last_name VARCHAR(50) NOT NULL COMMENT '姓',\n" +
                    "    date_of_birth DATE COMMENT '出生日期（替代年龄字段）',\n" +
                    "    gender CHAR(1) COMMENT '性别（F=女，M=男，O=其他）',\n" +
                    "    role_id INT NOT NULL COMMENT '角色ID（关联Role表）',\n" +
                    "    email VARCHAR(100) COMMENT '电子邮箱',\n" +
                    "    phone VARCHAR(20) COMMENT '联系电话',\n" +
                    "    hire_date DATE NOT NULL COMMENT '入职日期',\n" +
                    "    emergency_contact VARCHAR(50) COMMENT '紧急联系人',\n" +
                    "    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',\n" +
                    "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '员工在职状态（Y=在职，N=离职）',\n" +
                    "    PRIMARY KEY (staff_id),\n" +
                    "    UNIQUE KEY uk_staff_number (staff_number),\n" +
                    "    UNIQUE KEY uk_staff_email (email),\n" +
                    "    UNIQUE KEY uk_staff_phone (phone),\n" +
                    "    FOREIGN KEY (role_id) REFERENCES Role(role_id) ON DELETE RESTRICT,\n" +
                    "    CHECK (gender IN ('F', 'M', 'O')),\n" +
                    "    CHECK (active_flag IN ('Y', 'N')),\n" +
                    "    INDEX idx_staff_role (role_id),\n" +
                    "    INDEX idx_staff_active_role (active_flag, role_id)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储员工基础信息，关联角色表实现权限层级管理';\n";
            stmt.executeUpdate(createStaff);
            System.out.println("✅ 表`Staff`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Staff`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建员工技能表
                String createSkill = "-- 5. 员工技能表（基础表，无外键依赖）\n" +
                        "CREATE TABLE Skill (\n" +
                        "    skill_id INT AUTO_INCREMENT COMMENT '技能唯一标识',\n" +
                    "    skill_name VARCHAR(50) NOT NULL COMMENT '技能名称（如机器人操作、化学品使用）',\n" +
                    "    description VARCHAR(200) COMMENT '技能描述（如清洁机器人操作、高风险化学品使用）',\n" +
                    "    PRIMARY KEY (skill_id),\n" +
                    "    UNIQUE KEY uk_skill_name (skill_name),\n" +
                    "    CHECK (skill_name IN ('robot_operation', 'chemical_use', 'electrical_repair',\n" +
                    "                          'mechanical_maintenance', 'cleaning_service', 'safety_inspection'))\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '标准化员工技能分类，支持技能枚举管理';\n";
            stmt.executeUpdate(createSkill);
            System.out.println("✅ 表`Skill`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Skill`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建员工-技能关联表
                String createStaffSkillMap = "-- 6. 员工-技能关联表（依赖Staff、Skill表）\n" +
                        "CREATE TABLE Staff_Skill_Map (\n" +
                        "    staff_id INT NOT NULL COMMENT '员工ID（关联Staff表）',\n" +
                    "    skill_id INT NOT NULL COMMENT '技能ID（关联Skill表）',\n" +
                    "    proficiency VARCHAR(20) NOT NULL COMMENT '技能熟练度（初级/中级/高级）',\n" +
                    "    PRIMARY KEY (staff_id, skill_id),\n" +
                    "    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id) ON DELETE CASCADE,\n" +
                    "    FOREIGN KEY (skill_id) REFERENCES Skill(skill_id) ON DELETE CASCADE,\n" +
                    "    CHECK (proficiency IN ('junior', 'intermediate', 'senior')),\n" +
                    "    INDEX idx_skill_staff (skill_id, staff_id)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '实现员工与技能的多对多关联，记录技能掌握程度';\n";
            stmt.executeUpdate(createStaffSkillMap);
            System.out.println("✅ 表`Staff_Skill_Map`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Staff_Skill_Map`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建监督关系表
                String createSupervise = "-- 7. 监督关系表（依赖Staff表，自引用）\n" +
                        "CREATE TABLE Supervise (\n" +
                        "    supervise_id INT AUTO_INCREMENT COMMENT '监督关系唯一标识',\n" +
                        "    supervisor_staff_id INT NOT NULL COMMENT '上级员工ID（关联Staff表）',\n" +
                    "    subordinate_staff_id INT NOT NULL COMMENT '下级员工ID（关联Staff表）',\n" +
                    "    start_date DATE NOT NULL COMMENT '监督关系开始日期',\n" +
                    "    end_date DATE COMMENT '监督关系结束日期（NULL=当前有效）',\n" +
                    "    PRIMARY KEY (supervise_id),\n" +
                    "    FOREIGN KEY (supervisor_staff_id) REFERENCES Staff(staff_id) ON DELETE CASCADE,\n" +
                    "    FOREIGN KEY (subordinate_staff_id) REFERENCES Staff(staff_id) ON DELETE CASCADE,\n" +
                    "    CHECK (supervisor_staff_id != subordinate_staff_id),\n" +
                    "    CHECK (end_date IS NULL OR end_date >= start_date),\n" +
                    "    UNIQUE KEY uk_supervisor_subordinate (supervisor_staff_id, subordinate_staff_id, end_date)\n" +
                    "        COMMENT '避免同一上下级重复建立有效关系',\n" +
                    "    INDEX idx_subordinate_supervisor (subordinate_staff_id, supervisor_staff_id)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储员工间的层级监督关系，支持关系生命周期管理';\n";
            stmt.executeUpdate(createSupervise);
            System.out.println("✅ 表`Supervise`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Supervise`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建外部公司表
                String createCompany = "-- 8. 外部公司表（依赖Address表）\n" +
                        "CREATE TABLE Company (\n" +
                        "    contractor_id INT AUTO_INCREMENT COMMENT '公司唯一标识',\n" +
                        "    contractor_code VARCHAR(20) NOT NULL COMMENT '公司编码',\n" +
                    "    name VARCHAR(100) NOT NULL COMMENT '公司名称',\n" +
                    "    contact_name VARCHAR(50) COMMENT '联系人姓名',\n" +
                    "    contract_quote DECIMAL(10,2) COMMENT '标准报价（元）',\n" +
                    "    email VARCHAR(100) COMMENT '公司邮箱',\n" +
                    "    phone VARCHAR(20) COMMENT '公司电话',\n" +
                    "    address_id INT COMMENT '地址ID（关联Address表）',\n" +
                    "    expertise VARCHAR(200) COMMENT '专业领域（如建筑维修、清洁服务）',\n" +
                    "    tax_id VARCHAR(50) COMMENT '税务登记号',\n" +
                    "    bank_account VARCHAR(100) COMMENT '银行账户',\n" +
                    "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '合作状态（Y=合作中，N=终止）',\n" +
                    "    PRIMARY KEY (contractor_id),\n" +
                    "    UNIQUE KEY uk_contractor_code (contractor_code),\n" +
                    "    UNIQUE KEY uk_company_email (email),\n" +
                    "    UNIQUE KEY uk_company_phone (phone),\n" +
                    "    UNIQUE KEY uk_company_taxid (tax_id),\n" +
                    "    FOREIGN KEY (address_id) REFERENCES Address(address_id) ON DELETE SET NULL,\n" +
                    "    CHECK (contract_quote >= 0),\n" +
                    "    CHECK (active_flag IN ('Y', 'N')),\n" +
                    "    INDEX idx_company_address (address_id)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储外包合作公司信息，关联地址表消除地址冗余';\n";
            stmt.executeUpdate(createCompany);
            System.out.println("✅ 表`Company`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Company`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建机器人表
                String createRobot = "-- 9. 机器人表（无外键依赖）\n" +
                "CREATE TABLE Robot (\n" +
                "    robot_id INT AUTO_INCREMENT COMMENT '机器人唯一标识',\n" +
                "    type VARCHAR(50) NOT NULL COMMENT '机器人类型（清洁机器人/维修机器人/检测机器人）',\n" +
            "    robot_capability VARCHAR(200) COMMENT '功能描述（如地面清扫、管道检测）',\n" +
            "    create_date DATE NOT NULL COMMENT '出厂日期',\n" + 
            "    last_maintained_date DATE COMMENT '最后维护日期',\n" +
            "    maintenance_cycle INT NOT NULL COMMENT '维护周期（天数）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '启用状态（Y=启用，N=停用）',\n" +
            "    PRIMARY KEY (robot_id),\n" +
            "    CHECK (type IN ('cleaning_robot', 'repair_robot', 'inspection_robot')),\n" +
            "    CHECK (maintenance_cycle >= 1),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_robot_active_type (active_flag, type)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储机器人基础信息，补充维护周期支撑定期维护提醒';\n";
            stmt.executeUpdate(createRobot);
            System.out.println("✅ 表`Robot`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Robot`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建机器人维护记录表（依赖Robot、Staff表）
                String createRobotMaintenance = "-- 10. 机器人维护记录表（依赖Robot、Staff表）\n" +
                "CREATE TABLE Robot_Maintenance (\n" +
                "    maintenance_id INT AUTO_INCREMENT COMMENT '维护记录唯一标识',\n" +
            "    robot_id INT NOT NULL COMMENT '机器人ID（关联Robot表）',\n" +
            "    maintenance_date DATE NOT NULL COMMENT '维护日期',\n" +
            "    maintenance_type VARCHAR(20) NOT NULL COMMENT '维护类型（日常维护/故障维修/大修）',\n" +
            "    content TEXT COMMENT '维护内容（如零件更换、系统升级）',\n" +  
            "    maintained_by_staff_id INT COMMENT '维护人员ID（关联Staff表）',\n" +
            "    cost DECIMAL(10,2) COMMENT '维护费用（元）',\n" +
            "    notes TEXT COMMENT '维护备注（如故障原因、使用建议）',\n" +
            "    PRIMARY KEY (maintenance_id),\n" +
            "    FOREIGN KEY (robot_id) REFERENCES Robot(robot_id) ON DELETE CASCADE,\n" +  
            "    FOREIGN KEY (maintained_by_staff_id) REFERENCES Staff(staff_id) ON DELETE SET NULL,\n" +
            "    CHECK (maintenance_type IN ('routine', 'fault', 'overhaul')),\n" +
            "    CHECK (cost IS NULL OR cost >= 0),\n" +
            "    INDEX idx_robot_maintenance (robot_id, maintenance_date),\n" +
            "    INDEX idx_maintenance_staff (maintained_by_staff_id)\n" +  
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '记录机器人全生命周期维护历史，支持维护成本统计与故障追溯';\n";
            stmt.executeUpdate(createRobotMaintenance);
            System.out.println("✅ 表`Robot_Maintenance`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Robot_Maintenance`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建外部区域表（依赖Address表）
                String createArea = "-- 11. 外部区域表（依赖Address表）\n" +
                "CREATE TABLE Area (\n" +
                "    area_id INT AUTO_INCREMENT COMMENT '区域唯一标识',\n" +
            "    area_type VARCHAR(50) NOT NULL COMMENT '区域类型（校园外部区域/临时作业区/其他）',\n" +
            "    description VARCHAR(200) COMMENT '区域描述（如校园东门外侧广场）',\n" +
            "    address_id INT COMMENT '地址ID（关联Address表）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '启用状态（Y=启用，N=停用）',\n" +
            "    PRIMARY KEY (area_id),\n" +
            "    CHECK (area_type IN ('campus_external', 'temporary_work_area', 'other')),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    FOREIGN KEY (address_id) REFERENCES Address(address_id) ON DELETE SET NULL,\n" +
            "    INDEX idx_area_address (address_id)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园外部或临时区域信息，关联地址表统一管理地址';\n";
            stmt.executeUpdate(createArea);
            System.out.println("✅ 表`Area`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Area`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建建筑物表（依赖Address、Staff表）
                String createBuildings = "-- 12. 建筑物表（依赖Address、Staff表）\n" +
                "CREATE TABLE Buildings (\n" +
                "    building_id INT AUTO_INCREMENT COMMENT '建筑物唯一标识',\n" +
            "    building_code VARCHAR(20) NOT NULL COMMENT '建筑物编码（如B1、行政楼）',\n" +
            "    construction_date DATE COMMENT '建造日期（替代使用年限字段）',\n" +
            "    address_id INT COMMENT '地址ID（关联Address表）',\n" +
            "    num_floors INT COMMENT '楼层数（含地下层，如-1=1层地下）',\n" +
            "    supervisor_staff_id INT COMMENT '负责经理ID（关联Staff表，中层经理）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=在用，N=停用）',\n" +
            "    PRIMARY KEY (building_id),\n" +
            "    UNIQUE KEY uk_building_code (building_code),\n" +
            "    FOREIGN KEY (address_id) REFERENCES Address(address_id) ON DELETE SET NULL,\n" +
            "    FOREIGN KEY (supervisor_staff_id) REFERENCES Staff(staff_id) ON DELETE SET NULL,\n" +
            "    CHECK (num_floors BETWEEN -5 AND 50),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    -- 确保负责人为中层经理\n" +
            "    CHECK (supervisor_staff_id IS NULL \n" +   
            "    OR (SELECT r.role_level FROM Staff s JOIN Role r ON s.role_id = r.role_id \n" +
            "    WHERE s.staff_id = supervisor_staff_id) = 2),\n" +
            "    INDEX idx_building_address (address_id),\n" +
            "    INDEX idx_building_supervisor (supervisor_staff_id)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园建筑物信息，关联地址表消除地址冗余';\n";
            stmt.executeUpdate(createBuildings);
            System.out.println("✅ 表`Buildings`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Buildings`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建楼层表（依赖Buildings表）
                String createLevels = "-- 13. 楼层表（依赖Buildings表）\n" +
                "CREATE TABLE Levels (\n" +
                "    building_id INT NOT NULL COMMENT '建筑物ID（关联Buildings表）',\n" +
            "    level_id INT AUTO_INCREMENT COMMENT '楼层唯一标识',\n" +
            "    level_number INT NOT NULL COMMENT '楼层号（如3=3楼，-1=地下1层）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=在用，N=停用）',\n" +
            "    PRIMARY KEY (building_id, level_id),\n" +
            "    UNIQUE KEY uk_building_level (building_id, level_number),\n" +
            "    FOREIGN KEY (building_id) REFERENCES Buildings(building_id) ON DELETE CASCADE,\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_level_number (building_id, level_number)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储建筑物楼层信息，复合主键确保同建筑内楼层唯一';\n";
            stmt.executeUpdate(createLevels);
            System.out.println("✅ 表`Levels`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Levels`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建房间表（依赖Buildings表）
                String createRooms = "-- 14. 房间表（依赖Buildings表）\n" +
                "CREATE TABLE Rooms (\n" +
                "    building_id INT NOT NULL COMMENT '建筑物ID（关联Buildings表）',\n" +
            "    room_id INT AUTO_INCREMENT COMMENT '房间唯一标识',\n" +
            "    name VARCHAR(50) NOT NULL COMMENT '房间名称（如302教室、设备间）',\n" +
            "    room_type VARCHAR(50) COMMENT '房间类型（如教室、办公室、实验室）',\n" +
            "    capacity INT COMMENT '容纳人数',\n" +
            "    room_features VARCHAR(200) COMMENT '房间特征（如空调、投影仪、通风系统）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=在用，N=停用）',\n" +
            "    PRIMARY KEY (building_id, room_id),\n" +
            "    UNIQUE KEY uk_building_room (building_id, name),\n" +
            "    FOREIGN KEY (building_id) REFERENCES Buildings(building_id) ON DELETE CASCADE,\n" +
            "    CHECK (capacity IS NULL OR capacity >= 0),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_room_type (building_id, room_type)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储建筑物内房间信息，同建筑内房间名称唯一';\n";
            stmt.executeUpdate(createRooms);
            System.out.println("✅ 表`Rooms`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Rooms`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建广场表（依赖Address表）
                String createSquares = "-- 15. 广场表（依赖Address表）\n" +
                "CREATE TABLE Squares (\n" +
            "    square_id INT AUTO_INCREMENT COMMENT '广场唯一标识',\n" +
            "    name VARCHAR(50) NOT NULL COMMENT '广场名称（如中心广场、西广场）',\n" +
            "    address_id INT COMMENT '地址ID（关联Address表）',\n" +
            "    capacity INT COMMENT '最大容纳人数',\n" +
            "    square_features VARCHAR(200) COMMENT '广场特征（如安全区域、服务设施）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=在用，N=停用）',\n" +
            "    PRIMARY KEY (square_id),\n" +
            "    UNIQUE KEY uk_square_name (name),\n" +
            "    FOREIGN KEY (address_id) REFERENCES Address(address_id) ON DELETE SET NULL,\n" +
            "    CHECK (capacity IS NULL OR capacity >= 0),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_square_address (address_id)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园广场信息，关联地址表统一管理地址';\n";
            stmt.executeUpdate(createSquares);
            System.out.println("✅ 表`Squares`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Squares`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建大门表（依赖Address表）
                String createGates = "-- 16. 大门表（依赖Address表）\n" +
                "CREATE TABLE Gates (\n" +
            "    gate_id INT AUTO_INCREMENT COMMENT '大门唯一标识',\n" +
            "    name VARCHAR(50) NOT NULL COMMENT '大门名称（如东门、南门、应急通道）',\n" +
            "    address_id INT COMMENT '地址ID（关联Address表）',\n" +
            "    flow_capacity INT COMMENT '通行容量（人/小时）',\n" +
            "    gate_features VARCHAR(200) COMMENT '大门特征（如安全区域、服务设施）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=启用，N=关闭）',\n" +
            "    PRIMARY KEY (gate_id),\n" +
            "    UNIQUE KEY uk_gate_name (name),\n" +
            "    FOREIGN KEY (address_id) REFERENCES Address(address_id) ON DELETE SET NULL,\n" +
            "    CHECK (flow_capacity IS NULL OR flow_capacity >= 0),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_gate_address (address_id)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园大门信息，关联地址表消除地址冗余';\n";
            stmt.executeUpdate(createGates);
            System.out.println("✅ 表`Gates`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Gates`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建食堂表（依赖Address表）
                String createCanteens = "-- 17. 食堂表（依赖Address表）\n" +
                "CREATE TABLE Canteen (\n" +
                "    canteen_id INT AUTO_INCREMENT COMMENT '食堂唯一标识',\n" +
            "    name VARCHAR(50) NOT NULL COMMENT '食堂名称（如第一食堂、清真食堂）',\n" +
            "    construction_date DATE COMMENT '建造日期（替代使用年限字段）',\n" +
            "    address_id INT COMMENT '地址ID（关联Address表）',\n" +
            "    capacity INT COMMENT '最大容纳人数',\n" +
            "    food_type VARCHAR(50) NOT NULL COMMENT '餐饮类型（中餐、西餐、混合）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '营业状态（Y=营业，N=停业）',\n" +
            "    PRIMARY KEY (canteen_id),\n" +
            "    UNIQUE KEY uk_canteen_name (name),\n" +
            "    FOREIGN KEY (address_id) REFERENCES Address(address_id) ON DELETE SET NULL,\n" +
            "    CHECK (capacity IS NULL OR capacity >= 0),\n" +
            "    CHECK (food_type IN ('Chinese', 'Western', 'Mixed')),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_canteen_address (address_id),\n" +
            "    INDEX idx_canteen_foodtype (food_type)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园食堂信息，关联地址表统一管理地址';\n";
            stmt.executeUpdate(createCanteens);
            System.out.println("✅ 表`Canteen`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Canteen`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建天气应急事件表（无外键依赖）
                String createWeatherEmergency = "-- 18. 天气应急事件表（无外键依赖）\n" +
                "CREATE TABLE Weather_Emergency (\n" +
                "    weather_id INT AUTO_INCREMENT COMMENT '天气事件唯一标识',\n" +
            "    name VARCHAR(100) NOT NULL COMMENT '事件名称（如2023年10月15日降雨）',\n" +
            "    type VARCHAR(50) NOT NULL COMMENT '事件类型（如降雨、大风、低温）',\n" +
            "    start_date DATETIME NOT NULL COMMENT '开始时间',\n" +
            "    end_date DATETIME NOT NULL COMMENT '结束时间',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '事件状态（Y=活跃，N=结束）',\n" +
            "    PRIMARY KEY (weather_id),\n" + 
            "    UNIQUE KEY uk_weather_name (name),\n" +
            "    CHECK (type IN ('rainfall', 'strong_wind', 'low_temperature', 'typhoon', 'blizzard', 'other')),\n" +
            "    CHECK (start_date <= end_date),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_weather_active (active_flag, start_date),\n" +
            "    INDEX idx_weather_type (type)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储天气应急事件信息，支持精准时间记录';\n";
            stmt.executeUpdate(createWeatherEmergency);
            System.out.println("✅ 表`Weather_Emergency`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Weather_Emergency`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建化学品表（无外键依赖）
                String createChemical = "-- 19. 化学品表（无外键依赖）\n" +
                "CREATE TABLE Chemical (\n" +
                "    chemical_id INT AUTO_INCREMENT COMMENT '化学品唯一标识',\n" +
            "    product_code VARCHAR(20) NOT NULL COMMENT '产品编码',\n" +
            "    name VARCHAR(100) NOT NULL COMMENT '化学品名称（如消毒水、清洁剂）',\n" +
            "    type VARCHAR(50) NOT NULL COMMENT '化学品类型（如消毒剂、溶剂、润滑剂）',\n" +
            "    manufacturer VARCHAR(100) COMMENT '生产厂商',\n" +
            "    msds_url VARCHAR(255) COMMENT '安全说明书URL',\n" +
            "    hazard_category VARCHAR(20) NOT NULL COMMENT '危险类别（低、中、高）',\n" +
            "    storage_requirements TEXT COMMENT '存储要求（如阴凉干燥、密封保存）',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '启用状态（Y=启用，N=禁用）',\n" +
            "    PRIMARY KEY (chemical_id),\n" +
            "    UNIQUE KEY uk_product_code (product_code),\n" +
            "    CHECK (type IN ('disinfectant', 'solvent', 'lubricant', 'detergent', 'other')),\n" +
            "    CHECK (hazard_category IN ('low', 'medium', 'high')),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_chemical_hazard (hazard_category, active_flag),\n" +
            "    INDEX idx_chemical_type (type)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储活动使用的化学品基础信息，支持安全管理';\n";
            stmt.executeUpdate(createChemical);
            System.out.println("✅ 表`Chemical`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Chemical`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建化学品库存表（依赖Chemical、Company表）
                String createChemicalInventory = "-- 20. 化学品库存表（依赖Chemical、Company表）\n" +
                "CREATE TABLE Chemical_Inventory (\n" +
                "    inventory_id INT AUTO_INCREMENT COMMENT '库存记录唯一标识',\n" +
            "    chemical_id INT NOT NULL COMMENT '化学品ID（关联Chemical表）',\n" +
            "    quantity DECIMAL(10,2) NOT NULL COMMENT '库存数量（单位：瓶/升/千克）',\n" +
            "    storage_location VARCHAR(100) NOT NULL COMMENT '存储位置（如化学品仓库A区）',\n" +
            "    purchase_date DATE NOT NULL COMMENT '采购日期',\n" +
            "    supplier_id INT COMMENT '供应商ID（关联Company表）',\n" +
            "    expiry_date DATE NOT NULL COMMENT '有效期',\n" +
            "    batch_number VARCHAR(50) COMMENT '采购批次号',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '库存状态（Y=可用，N=不可用）',\n" +
            "    PRIMARY KEY (inventory_id),\n" +
            "    FOREIGN KEY (chemical_id) REFERENCES Chemical(chemical_id) ON DELETE CASCADE,\n" +
            "    FOREIGN KEY (supplier_id) REFERENCES Company(contractor_id) ON DELETE SET NULL,\n" +   
            "    CHECK (quantity >= 0),\n" +
            "    CHECK (expiry_date >= purchase_date),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    -- 有效期到期自动置为不可用（可通过触发器实现，此处为约束）\n" +
            "    CHECK (active_flag = 'N' OR expiry_date >= CURRENT_DATE()),\n" +
            "    INDEX idx_inventory_chemical (chemical_id, active_flag),\n" +
            "    INDEX idx_inventory_expiry (expiry_date, active_flag),\n" +
            "    INDEX idx_inventory_supplier (supplier_id)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '跟踪化学品库存动态，支持库存预警与领用管理';\n";
            stmt.executeUpdate(createChemicalInventory);
            System.out.println("✅ 表`Chemical_Inventory`创建成功！");
            
            }catch (Exception e){
                System.err.println("❌ 表`Chemical_Inventory`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建活动表（依赖Staff、Weather_Emergency、Area、Buildings等表）
                String createActivity = "-- 21. 活动表（核心业务表，修复复合外键、状态约束、索引优化）\r\n" + //
                                        "CREATE TABLE Activity (\r\n" + //
                                        "    activity_id INT AUTO_INCREMENT COMMENT '活动唯一标识',\r\n" + //
                                        "    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型（清洁、维修、天气应急）',\r\n" + //
                                        "    title VARCHAR(150) NOT NULL COMMENT '活动标题（扩展长度适配复杂场景）',\r\n" + //
                                        "    description TEXT COMMENT '活动详情（如清洁范围、维修内容）',\r\n" + //
                                        "    status VARCHAR(20) NOT NULL DEFAULT 'planned' COMMENT '活动状态（计划中、进行中、已完成、取消）',\r\n" + //
                                        "    priority VARCHAR(20) NOT NULL DEFAULT 'medium' COMMENT '活动优先级（低、中、高）',\r\n" + //
                                        "    activity_datetime DATETIME NOT NULL COMMENT '活动执行时间',\r\n" + //
                                        "    expected_unavailable_duration DECIMAL(5,2) NOT NULL COMMENT '预计不可用时长（小时）',\r\n" + //
                                        "    actual_completion_datetime DATETIME COMMENT '实际完成时间（NULL=未完成）',\r\n" + //
                                        "    created_by_staff_id INT NOT NULL COMMENT '创建人ID（关联Staff表）',\r\n" + //
                                        "    weather_id INT COMMENT '关联天气事件ID（关联Weather_Emergency表）',\r\n" + //
                                        "    area_id INT COMMENT '关联外部区域ID（关联Area表）',\r\n" + //
                                        "    hazard_level VARCHAR(20) NOT NULL COMMENT '风险等级（低、中、高）',\r\n" + //
                                        "    facility_type VARCHAR(20) NOT NULL COMMENT '关联设施类型（建筑/房间/楼层/广场/大门/食堂/外部区域/无）',\r\n" + //
                                        "    building_id INT COMMENT '关联建筑物ID（关联Buildings表，复合外键组成部分）',\r\n" + //
                                        "    room_id INT COMMENT '关联房间ID（关联Rooms表，复合外键组成部分）',\r\n" + //
                                        "    level_id INT COMMENT '关联楼层ID（关联Levels表，复合外键组成部分）',\r\n" + //
                                        "    square_id INT COMMENT '关联广场ID（关联Squares表）',\r\n" + //
                                        "    gate_id INT COMMENT '关联大门ID（关联Gates表）',\r\n" + //
                                        "    canteen_id INT COMMENT '关联食堂ID（关联Canteen表）',\r\n" + //
                                        "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '活动状态（Y=有效，N=无效）',\r\n" + //
                                        "    PRIMARY KEY (activity_id),\r\n" + //
                                        "    -- 外键关联：修复复合外键，确保与复合主键表的关联一致性\r\n" + //
                                        "    FOREIGN KEY (created_by_staff_id) REFERENCES Staff(staff_id) ON DELETE RESTRICT,\r\n" + //
                                        "    FOREIGN KEY (weather_id) REFERENCES Weather_Emergency(weather_id) ON DELETE SET NULL,\r\n" + //
                                        "    FOREIGN KEY (area_id) REFERENCES Area(area_id) ON DELETE SET NULL,\r\n" + //
                                        "    FOREIGN KEY (building_id) REFERENCES Buildings(building_id) ON DELETE SET NULL,\r\n" + //
                                        "    FOREIGN KEY (square_id) REFERENCES Squares(square_id) ON DELETE SET NULL,\r\n" + //
                                        "    FOREIGN KEY (gate_id) REFERENCES Gates(gate_id) ON DELETE SET NULL,\r\n" + //
                                        "    FOREIGN KEY (canteen_id) REFERENCES Canteen(canteen_id) ON DELETE SET NULL,\r\n" + //
                                        "    -- 复合外键：关联 Rooms 表（building_id + room_id 对应目标表复合主键）\r\n" + //
                                        "    FOREIGN KEY (building_id, room_id) REFERENCES Rooms(building_id, room_id) ON DELETE SET NULL,\r\n" + //
                                        "    -- 复合外键：关联 Levels 表（building_id + level_id 对应目标表复合主键）\r\n" + //
                                        "    FOREIGN KEY (building_id, level_id) REFERENCES Levels(building_id, level_id) ON DELETE SET NULL,\r\n" + //
                                        "    -- 核心业务约束：确保数据逻辑一致性\r\n" + //
                                        "    CHECK (activity_type IN ('cleaning', 'repair', 'weather_response')),\r\n" + //
                                        "    CHECK (status IN ('planned', 'in_progress', 'completed', 'cancelled')),\r\n" + //
                                        "    CHECK (priority IN ('low', 'medium', 'high')),\r\n" + //
                                        "    CHECK (hazard_level IN ('low', 'medium', 'high')),\r\n" + //
                                        "    CHECK (facility_type IN ('building', 'room', 'level', 'square', 'gate', 'canteen', 'area', 'none')),\r\n" + //
                                        "    CHECK (expected_unavailable_duration >= 0),\r\n" + //
                                        "    -- 状态与实际完成时间联动约束：避免逻辑矛盾\r\n" + //
                                        "    CHECK (\r\n" + //
                                        "        (status = 'completed' AND actual_completion_datetime IS NOT NULL AND actual_completion_datetime >= activity_datetime)\r\n" + //
                                        "        OR (status IN ('planned', 'in_progress', 'cancelled') AND actual_completion_datetime IS NULL)\r\n" + //
                                        "    ),\r\n" + //
                                        "    -- 设施类型与关联字段严格匹配：非关联字段必须为空，无冗余\r\n" + //
                                        "    CHECK (\r\n" + //
                                        "        (facility_type = 'building' AND building_id IS NOT NULL \r\n" + //
                                        "         AND room_id IS NULL AND level_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL AND area_id IS NULL)\r\n" + //
                                        "        OR (facility_type = 'room' AND building_id IS NOT NULL AND room_id IS NOT NULL \r\n" + //
                                        "         AND level_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL AND area_id IS NULL)\r\n" + //
                                        "        OR (facility_type = 'level' AND building_id IS NOT NULL AND level_id IS NOT NULL \r\n" + //
                                        "         AND room_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL AND area_id IS NULL)\r\n" + //
                                        "        OR (facility_type = 'square' AND square_id IS NOT NULL \r\n" + //
                                        "         AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL AND area_id IS NULL)\r\n" + //
                                        "        OR (facility_type = 'gate' AND gate_id IS NOT NULL \r\n" + //
                                        "         AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL AND square_id IS NULL AND canteen_id IS NULL AND area_id IS NULL)\r\n" + //
                                        "        OR (facility_type = 'canteen' AND canteen_id IS NOT NULL \r\n" + //
                                        "         AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND area_id IS NULL)\r\n" + //
                                        "        OR (facility_type = 'area' AND area_id IS NOT NULL \r\n" + //
                                        "         AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL)\r\n" + //
                                        "        OR (facility_type = 'none' AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL \r\n" + //
                                        "         AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL AND area_id IS NULL)\r\n" + //
                                        "    ),\r\n" + //
                                        "    CHECK (active_flag IN ('Y', 'N')),\r\n" + //
                                        "    -- 优化索引：覆盖高频查询场景，减少回表\r\n" + //
                                        "    INDEX idx_activity_datetime_status_type (activity_datetime, status, activity_type) COMMENT '支持「时间段+状态+类型」高频查询',\r\n" + //
                                        "    INDEX idx_activity_facility (facility_type, building_id, room_id, level_id) COMMENT '支持「设施类型+关联设施」精准查询',\r\n" + //
                                        "    INDEX idx_activity_weather_status (weather_id, status) COMMENT '支持「天气事件+状态」应急活动查询',\r\n" + //
                                        "    INDEX idx_activity_creator_status (created_by_staff_id, status) COMMENT '支持「创建人+状态」活动管理查询',\r\n" + //
                                        "    INDEX idx_activity_hazard_status (hazard_level, status) COMMENT '支持「风险等级+状态」安全审计查询'\r\n" + //
                                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '校园维护活动核心表，修复复合外键关联、状态联动约束，适配多场景业务需求；高风险活动需通过触发器关联Safety_Check表';";
            stmt.executeUpdate(createActivity);
            System.out.println("✅ 表`Activity`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Activity`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建员工-活动关联表（依赖Staff、Activity表）
                String createWorksFor = "-- 22. 员工-活动关联表（依赖Staff、Activity表）\n" +
                "CREATE TABLE WORKS_FOR (\n" +
                "    works_for_id INT AUTO_INCREMENT COMMENT '关联唯一标识',\n" +
            "    staff_id INT NOT NULL COMMENT '员工ID（关联Staff表）',\n" +
            "    activity_id INT NOT NULL COMMENT '活动ID（关联Activity表）',\n" +
            "    activity_responsibility VARCHAR(200) NOT NULL COMMENT '员工在活动中的职责（如操作机器人、现场监督）',\n" +
            "    assigned_datetime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',\n" +
            "    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '关联状态（Y=有效，N=无效）',\n" +
            "    PRIMARY KEY (works_for_id),\n" +
            "    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id) ON DELETE CASCADE,\n" +
            "    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id) ON DELETE CASCADE,\n" +
            "    UNIQUE KEY uk_staff_activity (staff_id, activity_id),\n" +
            "    CHECK (active_flag IN ('Y', 'N')),\n" +
            "    INDEX idx_works_for_staff_activity (staff_id, activity_id, active_flag),\n" +
            "    INDEX idx_works_for_activity (activity_id, active_flag)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '实现员工与活动的多对多关联，记录分配时间与职责';\n";
            stmt.executeUpdate(createWorksFor);
            System.out.println("✅ 表`WORKS_FOR`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`WORKS_FOR`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建外包合同表（依赖Company、Activity表）
                String createContract = "-- 23. 外包合同表（依赖Company、Activity表）\n" +
                "CREATE TABLE Contract (\n" +
                "    contract_id INT AUTO_INCREMENT COMMENT '合同唯一标识',\n" +
            "    contractor_id INT NOT NULL COMMENT '外包公司ID（关联Company表）',\n" +
            "    activity_id INT NOT NULL COMMENT '关联活动ID（关联Activity表）',\n" +
            "    contract_date DATE NOT NULL COMMENT '合同签订日期',\n" +
            "    contract_amount DECIMAL(12,2) NOT NULL COMMENT '合同金额（元）',\n" +
            "    end_date DATE COMMENT '合同结束日期（NULL=未结束）',\n" +
            "    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '合同状态（生效中、已完成、已取消）',\n" +
            "    payment_terms VARCHAR(200) COMMENT '付款条款（如验收后30天付款）',\n" +
            "    notes TEXT COMMENT '合同备注（如验收标准、违约责任）',\n" +
            "    PRIMARY KEY (contract_id),\n" +
            "    FOREIGN KEY (contractor_id) REFERENCES Company(contractor_id) ON DELETE CASCADE,\n" +
            "    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id) ON DELETE CASCADE,\n" +
            "    UNIQUE KEY uk_contract_activity (activity_id),\n" +
            "    CHECK (contract_amount >= 0),\n" +
            "    CHECK (status IN ('active', 'completed', 'cancelled')),\n" +
            "    CHECK (end_date IS NULL OR contract_date <= end_date),\n" +
            "    INDEX idx_contract_company (contractor_id, status),\n" +
            "    INDEX idx_contract_date (contract_date),\n" +
            "    INDEX idx_contract_activity_status (activity_id, status)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储活动外包合同信息，确保一个活动仅对应一个外包合同';\n";
            stmt.executeUpdate(createContract);
            System.out.println("✅ 表`Contract`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Contract`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建机器人使用表（依赖Robot、Activity、Staff表）
            // 创建机器人使用表（依赖Robot、Activity、Staff表）
            String createRobotUsage = "-- 24. 机器人使用表（依赖Robot、Activity、Staff表）\n" +
            "CREATE TABLE Robot_Usage (\n" +
            "    robot_usage_id INT AUTO_INCREMENT COMMENT '使用记录唯一标识',\n" +
            "    robot_id INT NOT NULL COMMENT '机器人ID（关联Robot表）',\n" +
            "    activity_id INT NOT NULL COMMENT '活动ID（关联Activity表）',\n" +
            "    usage_datetime DATETIME NOT NULL COMMENT '使用时间',\n" +
            "    operator_staff_id INT COMMENT '操作人员ID（关联Staff表）',\n" +
            "    usage_duration DECIMAL(5,2) NOT NULL COMMENT '使用时长（小时）',\n" +
            "    usage_quantity INT NOT NULL COMMENT '使用次数（如启动次数）',\n" +
            "    notes TEXT COMMENT '使用备注（如故障记录、操作要点）',\n" +
            "    PRIMARY KEY (robot_usage_id),\n" +
            "    FOREIGN KEY (robot_id) REFERENCES Robot(robot_id) ON DELETE CASCADE,\n" +
            "    FOREIGN KEY (activity_id) REFERENCES Activity(activity_id) ON DELETE CASCADE,\n" +
            "    FOREIGN KEY (operator_staff_id) REFERENCES Staff(staff_id) ON DELETE SET NULL,\n" +
            "    -- 确保机器人处于启用状态\n" + 
            "    CHECK ((SELECT active_flag FROM Robot WHERE robot_id = Robot_Usage.robot_id) = 'Y'),\n" +
            "    CHECK (usage_duration >= 0),\n" +
            "    CHECK (usage_quantity >= 1),\n" +
            "    UNIQUE KEY uk_robot_activity_datetime (robot_id, activity_id, usage_datetime),\n" +
            "    INDEX idx_robot_usage_robot (robot_id, usage_datetime),\n" +
            "    INDEX idx_robot_usage_activity (activity_id, robot_id),\n" +
            "    INDEX idx_robot_usage_operator (operator_staff_id)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储机器人参与活动的使用记录，确保数据完整性';\n";
            stmt.executeUpdate(createRobotUsage);
            System.out.println("✅ 表`Robot_Usage`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Robot_Usage`创建失败！");
                e.printStackTrace();
            }
            try{
                // 创建化学品安全检查表（依赖Activity、Chemical、Staff表）
            String createSafetyCheck = "-- 25. 化学品安全检查表（依赖Activity、Chemical、Staff表）\n" +
            "CREATE TABLE Safety_Check (\n" +
            "    safety_check_id INT AUTO_INCREMENT COMMENT '检查记录唯一标识',\n" +
            "    activity_id INT NOT NULL COMMENT '活动ID（关联Activity表）',\n" +
            "    chemical_id INT NOT NULL COMMENT '化学品ID（关联Chemical表）',\n" +
            "    check_datetime DATETIME NOT NULL COMMENT '检查时间',\n" +
            "    checked_by_staff_id INT NOT NULL COMMENT '检查人ID（关联Staff表）',\n" +
            "    check_items TEXT NOT NULL COMMENT '检查项目明细（如浓度合规、存储合规）',\n" +
            "    check_result VARCHAR(20) NOT NULL COMMENT '检查结果（通过、未通过、待检查）',\n" +
            "    rectification_measures TEXT COMMENT '整改措施（如未通过时的处理方案）',\n" +
            "    notes TEXT COMMENT '检查备注（如环境条件、特殊情况）',\n" +
            "    INDEX idx_safety_check_activity (activity_id, check_result),\n" +
            "    INDEX idx_safety_check_chemical (chemical_id, check_datetime),\n" +
            "    INDEX idx_safety_check_staff (checked_by_staff_id, check_datetime)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储化学品在活动中使用的安全检查记录，支持合规追溯';\n";
            stmt.executeUpdate(createSafetyCheck);
            System.out.println("✅ 表`Safety_Check`创建成功！");
            }catch (Exception e){
                System.err.println("❌ 表`Safety_Check`创建失败！");
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.err.println("❌ 操作失败：" + e.getMessage());
            e.printStackTrace();
        }

    }

    
}
