/* Enter "USE {database};" to start exploring your data.
   Press Ctrl + I to try out AI-generated SQL queries or SQL rewrite using Chat2Query. */
use test1;
-- 1. 角色表（基础表，无外键依赖）
CREATE TABLE role (
    role_id INT AUTO_INCREMENT COMMENT '角色唯一标识',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称（行政官/中层经理/基层员工）',
    role_level INT NOT NULL COMMENT '角色层级（1=行政官，2=中层经理，3=基层员工）',
    description VARCHAR(200) COMMENT '角色职责描述',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '角色启用状态（Y=启用，N=禁用）',
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_role_name (role_name),
    CHECK (role_name IN ('executive_officer', 'mid_level_manager', 'base_level_worker')),
    CHECK (role_level IN (1, 2, 3)),
    CHECK ((role_level=1 AND role_name='executive_officer') 
           OR (role_level=2 AND role_name='mid_level_manager') 
           OR (role_level=3 AND role_name='base_level_worker')),
    CHECK (active_flag IN ('Y', 'N'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储员工角色信息，通过role_name与role_level强关联确保逻辑一致性';


CREATE TABLE system_limits (
    system_limits_id INT AUTO_INCREMENT COMMENT '配置唯一标识',
    max_mid_level_managers INT NOT NULL COMMENT '中层经理最大人数限制',
    max_base_level_workers INT NOT NULL COMMENT '基层员工最大人数限制',
    effective_date DATE NOT NULL COMMENT '配置生效日期',
    active_flag CHAR(1) NOT NULL DEFAULT 'N' COMMENT '配置激活状态（Y=激活，N=未激活）',
    PRIMARY KEY (system_limits_id),
    CHECK (max_mid_level_managers >= 0),
    CHECK (max_base_level_workers >= 0),
    CHECK (active_flag IN ('Y', 'N'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储员工人数限制配置，支持生效日期管理';

-- 补充触发器：确保同一时间仅一个激活配置（替代过滤索引）
DELIMITER //
CREATE TRIGGER trg_system_limits_active_unique_insert
BEFORE INSERT ON system_limits
FOR EACH ROW
BEGIN
    -- 插入时若设置为激活（Y），检查是否已有激活配置
    IF NEW.active_flag = 'Y' THEN
        IF EXISTS (SELECT 1 FROM system_limits WHERE active_flag = 'Y') THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '仅允许一个激活的系统配置，无法新增多个激活状态';
        END IF;
    END IF;
END //

CREATE TRIGGER trg_system_limits_active_unique_update
BEFORE UPDATE ON system_limits
FOR EACH ROW
BEGIN
    -- 更新时若设置为激活（Y），检查其他配置是否已激活
    IF NEW.active_flag = 'Y' THEN
        IF EXISTS (SELECT 1 FROM system_limits WHERE active_flag = 'Y' AND system_limits_id != NEW.system_limits_id) THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '仅允许一个激活的系统配置，请先禁用当前激活配置';
        END IF;
    END IF;
END //
DELIMITER ;

-- 3. 地址表（基础表，无外键依赖，统一管理地址信息）
CREATE TABLE address (
    address_id INT AUTO_INCREMENT COMMENT '地址唯一标识',
    street VARCHAR(100) COMMENT '街道地址',
    city VARCHAR(50) COMMENT '城市',
    postal_code VARCHAR(20) COMMENT '邮政编码',
    country VARCHAR(50) COMMENT '国家/地区',
    detail VARCHAR(200) COMMENT '详细地址（如楼栋号、门牌号）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '地址启用状态（Y=启用，N=禁用）',
    PRIMARY KEY (address_id),
    CHECK (active_flag IN ('Y', 'N'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '统一管理所有实体的地址信息，消除多表地址冗余';

-- 4. 员工表（依赖role表）
CREATE TABLE staff (
    staff_id INT AUTO_INCREMENT COMMENT '员工唯一标识',
    staff_number VARCHAR(20) NOT NULL COMMENT '员工工号',
    first_name VARCHAR(50) NOT NULL COMMENT '名',
    last_name VARCHAR(50) NOT NULL COMMENT '姓',
    date_of_birth DATE COMMENT '出生日期（替代年龄字段）',
    gender CHAR(1) COMMENT '性别（F=女，M=男，O=其他）',
    role_id INT NOT NULL COMMENT '角色ID（关联role表）',
    email VARCHAR(100) COMMENT '电子邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    hire_date DATE NOT NULL COMMENT '入职日期',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '员工在职状态（Y=在职，N=离职）',
    PRIMARY KEY (staff_id),
    UNIQUE KEY uk_staff_number (staff_number),
    UNIQUE KEY uk_staff_email (email),
    UNIQUE KEY uk_staff_phone (phone),
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE RESTRICT,
    CHECK (gender IN ('F', 'M', 'O')),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_staff_role (role_id),
    INDEX idx_staff_active_role (active_flag, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储员工基础信息，关联角色表实现权限层级管理';

-- 5. 员工技能表（基础表，无外键依赖）
CREATE TABLE skill (
    skill_id INT AUTO_INCREMENT COMMENT '技能唯一标识',
    skill_name VARCHAR(50) NOT NULL COMMENT '技能名称（如机器人操作、化学品使用）',
    description VARCHAR(200) COMMENT '技能描述（如清洁机器人操作、高风险化学品使用）',
    PRIMARY KEY (skill_id),
    UNIQUE KEY uk_skill_name (skill_name),
    CHECK (skill_name IN ('robot_operation', 'chemical_use', 'electrical_repair', 
                         'mechanical_maintenance', 'cleaning_service', 'safety_inspection'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '标准化员工技能分类，支持技能枚举管理';

-- 6. 员工-技能关联表（依赖staff、skill表）
CREATE TABLE staff_skill_map (
    staff_id INT NOT NULL COMMENT '员工ID（关联staff表）',
    skill_id INT NOT NULL COMMENT '技能ID（关联skill表）',
    proficiency VARCHAR(20) NOT NULL COMMENT '技能熟练度（初级/中级/高级）',
    PRIMARY KEY (staff_id, skill_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skill(skill_id) ON DELETE CASCADE,
    CHECK (proficiency IN ('junior', 'intermediate', 'senior')),
    INDEX idx_skill_staff (skill_id, staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '实现员工与技能的多对多关联，记录技能掌握程度';

-- 7. 监督关系表（依赖staff表，自引用）
CREATE TABLE supervise (
    supervise_id INT AUTO_INCREMENT COMMENT '监督关系唯一标识',
    supervisor_staff_id INT NOT NULL COMMENT '上级员工ID（关联staff表）',
    subordinate_staff_id INT NOT NULL COMMENT '下级员工ID（关联staff表）',
    start_date DATE NOT NULL COMMENT '监督关系开始日期',
    end_date DATE COMMENT '监督关系结束日期（NULL=当前有效）',
    PRIMARY KEY (supervise_id),
    FOREIGN KEY (supervisor_staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE,
    FOREIGN KEY (subordinate_staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE,
    CHECK (supervisor_staff_id != subordinate_staff_id),
    CHECK (end_date IS NULL OR end_date >= start_date),
    UNIQUE KEY uk_supervisor_subordinate (supervisor_staff_id, subordinate_staff_id, end_date)
        COMMENT '避免同一上下级重复建立有效关系',
    INDEX idx_subordinate_supervisor (subordinate_staff_id, supervisor_staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储员工间的层级监督关系，支持关系生命周期管理';

-- 8. 外部公司表（依赖address表）
CREATE TABLE company (
    contractor_id INT AUTO_INCREMENT COMMENT '公司唯一标识',
    contractor_code VARCHAR(20) NOT NULL COMMENT '公司编码',
    name VARCHAR(100) NOT NULL COMMENT '公司名称',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contract_quote DECIMAL(10,2) COMMENT '标准报价（元）',
    email VARCHAR(100) COMMENT '公司邮箱',
    phone VARCHAR(20) COMMENT '公司电话',
    address_id INT COMMENT '地址ID（关联address表）',
    expertise VARCHAR(200) COMMENT '专业领域（如建筑维修、清洁服务）',
    tax_id VARCHAR(50) COMMENT '税务登记号',
    bank_account VARCHAR(100) COMMENT '银行账户',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '合作状态（Y=合作中，N=终止）',
    PRIMARY KEY (contractor_id),
    UNIQUE KEY uk_contractor_code (contractor_code),
    UNIQUE KEY uk_company_email (email),
    UNIQUE KEY uk_company_phone (phone),
    UNIQUE KEY uk_company_taxid (tax_id),
    FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE SET NULL,
    CHECK (contract_quote >= 0),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_company_address (address_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储外包合作公司信息，关联地址表消除地址冗余';

-- 9. 机器人表（无外键依赖）
CREATE TABLE robot (
    robot_id INT AUTO_INCREMENT COMMENT '机器人唯一标识',
    type VARCHAR(50) NOT NULL COMMENT '机器人类型（清洁机器人/维修机器人/检测机器人）',
    robot_capability VARCHAR(200) COMMENT '功能描述（如地面清扫、管道检测）',
    create_date DATE NOT NULL COMMENT '出厂日期',
    last_maintained_date DATE COMMENT '最后维护日期',
    maintenance_cycle INT NOT NULL COMMENT '维护周期（天数）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '启用状态（Y=启用，N=停用）',
    PRIMARY KEY (robot_id),
    CHECK (type IN ('cleaning_robot', 'repair_robot', 'inspection_robot')),
    CHECK (maintenance_cycle >= 1),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_robot_active_type (active_flag, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储机器人基础信息，补充维护周期支撑定期维护提醒';

-- 10. 机器人维护记录表（依赖robot、staff表）
CREATE TABLE robot_maintenance (
    maintenance_id INT AUTO_INCREMENT COMMENT '维护记录唯一标识',
    robot_id INT NOT NULL COMMENT '机器人ID（关联robot表）',
    maintenance_date DATE NOT NULL COMMENT '维护日期',
    maintenance_type VARCHAR(20) NOT NULL COMMENT '维护类型（日常维护/故障维修/大修）',
    content TEXT COMMENT '维护内容（如零件更换、系统升级）',
    maintained_by_staff_id INT COMMENT '维护人员ID（关联staff表）',
    cost DECIMAL(10,2) COMMENT '维护费用（元）',
    notes TEXT COMMENT '维护备注（如故障原因、使用建议）',
    PRIMARY KEY (maintenance_id),
    FOREIGN KEY (robot_id) REFERENCES robot(robot_id) ON DELETE CASCADE,
    FOREIGN KEY (maintained_by_staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL,
    CHECK (maintenance_type IN ('routine', 'fault', 'overhaul')),
    CHECK (cost IS NULL OR cost >= 0),
    INDEX idx_robot_maintenance (robot_id, maintenance_date),
    INDEX idx_maintenance_staff (maintained_by_staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '记录机器人全生命周期维护历史，支持维护成本统计与故障追溯';

-- 11. 外部区域表（依赖address表）
CREATE TABLE area (
    area_id INT AUTO_INCREMENT COMMENT '区域唯一标识',
    area_type VARCHAR(50) NOT NULL COMMENT '区域类型（校园外部区域/临时作业区/其他）',
    description VARCHAR(200) COMMENT '区域描述（如校园东门外侧广场）',
    address_id INT COMMENT '地址ID（关联address表）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '启用状态（Y=启用，N=停用）',
    PRIMARY KEY (area_id),
    CHECK (area_type IN ('campus_external', 'temporary_work_area', 'other')),
    CHECK (active_flag IN ('Y', 'N')),
    FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE SET NULL,
    INDEX idx_area_address (address_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园外部或临时区域信息，关联地址表统一管理地址';

-- 12. 建筑物表（依赖address、staff表）
CREATE TABLE buildings (
    building_id INT AUTO_INCREMENT COMMENT '建筑物唯一标识',
    building_code VARCHAR(20) NOT NULL COMMENT '建筑物编码（如B1、行政楼）',
    construction_date DATE COMMENT '建造日期（替代使用年限字段）',
    address_id INT COMMENT '地址ID（关联address表）',
    num_floors INT COMMENT '楼层数（含地下层，如-1=1层地下）',
    supervisor_staff_id INT COMMENT '负责经理ID（关联staff表，中层经理）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=在用，N=停用）',
    PRIMARY KEY (building_id),
    UNIQUE KEY uk_building_code (building_code),
    FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE SET NULL,
    FOREIGN KEY (supervisor_staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL,
    CHECK (num_floors BETWEEN -5 AND 50),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_building_address (address_id),
    INDEX idx_building_supervisor (supervisor_staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园建筑物信息，关联地址表消除地址冗余';

-- 13. 楼层表（依赖buildings表）- 修复自增列异常
CREATE TABLE levels (
    level_id INT AUTO_INCREMENT COMMENT '楼层唯一标识（全局唯一）',
    building_id INT NOT NULL COMMENT '建筑物ID（关联buildings表）',
    level_number INT NOT NULL COMMENT '楼层号（如3=3楼，-1=地下1层）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=在用，N=停用）',
    PRIMARY KEY (level_id),
    UNIQUE KEY uk_building_level (building_id, level_number) COMMENT '确保同建筑内楼层唯一',
    FOREIGN KEY (building_id) REFERENCES buildings(building_id) ON DELETE CASCADE,
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_building_level_number (building_id, level_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储建筑物楼层信息';

-- 14. 房间表（依赖buildings表）- 修复自增列异常
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT COMMENT '房间唯一标识（全局唯一）',
    building_id INT NOT NULL COMMENT '建筑物ID（关联buildings表）',
    name VARCHAR(50) NOT NULL COMMENT '房间名称（如302教室、设备间）',
    room_type VARCHAR(50) COMMENT '房间类型（如教室、办公室、实验室）',
    capacity INT COMMENT '容纳人数',
    room_features VARCHAR(200) COMMENT '房间特征（如空调、投影仪、通风系统）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=在用，N=停用）',
    PRIMARY KEY (room_id),
    UNIQUE KEY uk_building_room (building_id, name) COMMENT '确保同建筑内房间名称唯一',
    FOREIGN KEY (building_id) REFERENCES buildings(building_id) ON DELETE CASCADE,
    CHECK (capacity IS NULL OR capacity >= 0),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_building_room_type (building_id, room_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储建筑物内房间信息，同建筑内房间名称唯一';

-- 15. 广场表（依赖address表）
CREATE TABLE squares (
    square_id INT AUTO_INCREMENT COMMENT '广场唯一标识',
    name VARCHAR(50) NOT NULL COMMENT '广场名称（如中心广场、西广场）',
    address_id INT COMMENT '地址ID（关联address表）',
    capacity INT COMMENT '最大容纳人数',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=在用，N=停用）',
    PRIMARY KEY (square_id),
    UNIQUE KEY uk_square_name (name),
    FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE SET NULL,
    CHECK (capacity IS NULL OR capacity >= 0),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_square_address (address_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园广场信息，关联地址表统一管理地址';

-- 16. 大门表（依赖address表）
CREATE TABLE gates (
    gate_id INT AUTO_INCREMENT COMMENT '大门唯一标识',
    name VARCHAR(50) NOT NULL COMMENT '大门名称（如东门、南门、应急通道）',
    address_id INT COMMENT '地址ID（关联address表）',
    flow_capacity INT COMMENT '通行容量（人/小时）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '使用状态（Y=启用，N=关闭）',
    PRIMARY KEY (gate_id),
    UNIQUE KEY uk_gate_name (name),
    FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE SET NULL,
    CHECK (flow_capacity IS NULL OR flow_capacity >= 0),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_gate_address (address_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园大门信息，关联地址表消除地址冗余';

-- 17. 食堂表（依赖address表）
CREATE TABLE canteen (
    canteen_id INT AUTO_INCREMENT COMMENT '食堂唯一标识',
    name VARCHAR(50) NOT NULL COMMENT '食堂名称（如第一食堂、清真食堂）',
    construction_date DATE COMMENT '建造日期（替代使用年限字段）',
    address_id INT COMMENT '地址ID（关联address表）',
    food_type VARCHAR(50) NOT NULL COMMENT '餐饮类型（中餐、西餐、混合）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '营业状态（Y=营业，N=停业）',
    PRIMARY KEY (canteen_id),
    UNIQUE KEY uk_canteen_name (name),
    FOREIGN KEY (address_id) REFERENCES address(address_id) ON DELETE SET NULL,
    CHECK (food_type IN ('Chinese', 'Western', 'Mixed')),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_canteen_address (address_id),
    INDEX idx_canteen_foodtype (food_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园食堂信息，关联地址表统一管理地址';

-- 18. 天气应急事件表（无外键依赖）
CREATE TABLE weather_emergency (
    weather_id INT AUTO_INCREMENT COMMENT '事件唯一标识',
    name VARCHAR(100) NOT NULL COMMENT '事件名称（如暴雨、台风、暴雪）',
    type VARCHAR(50) NOT NULL COMMENT '事件类型（如降雨、大风、低温）',
    start_date DATETIME NOT NULL COMMENT '开始时间',
    end_date DATETIME NOT NULL COMMENT '结束时间',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '事件状态（Y=活跃，N=结束）',
    PRIMARY KEY (weather_id),
    CHECK (type IN ('rainfall', 'strong_wind', 'low_temperature', 'typhoon', 'blizzard', 'other')),
    CHECK (start_date <= end_date),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_weather_active (active_flag, start_date),
    INDEX idx_weather_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储天气应急事件信息，支持精准时间记录';

-- 19. 化学品表（无外键依赖）
CREATE TABLE chemical (
    chemical_id INT AUTO_INCREMENT COMMENT '化学品唯一标识',
    product_code VARCHAR(20) NOT NULL COMMENT '产品编码',
    name VARCHAR(100) NOT NULL COMMENT '化学品名称（如消毒水、清洁剂）',
    type VARCHAR(50) NOT NULL COMMENT '化学品类型（如消毒剂、溶剂、润滑剂）',
    manufacturer VARCHAR(100) COMMENT '生产厂商',
    msds_url VARCHAR(255) COMMENT '安全说明书URL',
    hazard_category VARCHAR(20) NOT NULL COMMENT '危险类别（低、中、高）',
    storage_requirements TEXT COMMENT '存储要求（如阴凉干燥、密封保存）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '启用状态（Y=启用，N=禁用）',
    PRIMARY KEY (chemical_id),
    UNIQUE KEY uk_product_code (product_code),
    CHECK (type IN ('disinfectant', 'solvent', 'lubricant', 'detergent', 'other')),
    CHECK (hazard_category IN ('low', 'medium', 'high')),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_chemical_hazard (hazard_category, active_flag),
    INDEX idx_chemical_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储活动使用的化学品基础信息，支持安全管理';

-- 20. 化学品库存表（依赖chemical、company表）
CREATE TABLE chemical_inventory (
    inventory_id INT AUTO_INCREMENT COMMENT '库存记录唯一标识',
    chemical_id INT NOT NULL COMMENT '化学品ID（关联chemical表）',
    quantity DECIMAL(10,2) NOT NULL COMMENT '库存数量（单位：瓶/升/千克）',
    storage_location VARCHAR(100) NOT NULL COMMENT '存储位置（如化学品仓库A区）',
    purchase_date DATE NOT NULL COMMENT '采购日期',
    supplier_id INT COMMENT '供应商ID（关联company表）',
    expiry_date DATE NOT NULL COMMENT '有效期',
    batch_number VARCHAR(50) COMMENT '采购批次号',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '库存状态（Y=可用，N=不可用）',
    PRIMARY KEY (inventory_id),
    FOREIGN KEY (chemical_id) REFERENCES chemical(chemical_id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES company(contractor_id) ON DELETE SET NULL,
    CHECK (quantity >= 0),
    CHECK (expiry_date >= purchase_date),
    CHECK (active_flag IN ('Y', 'N')),
    -- 被动检查：新增/更新时确保未过期为可用
    CHECK (active_flag = 'N' OR expiry_date >= CURRENT_DATE()),
    INDEX idx_inventory_chemical (chemical_id, active_flag),
    INDEX idx_inventory_expiry (expiry_date, active_flag),
    INDEX idx_inventory_supplier (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '跟踪化学品库存动态，支持库存预警与领用管理';

-- 21. 活动表（依赖staff、weather_emergency、area、buildings等表）
CREATE TABLE activity (
    activity_id INT AUTO_INCREMENT COMMENT '活动唯一标识',
    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型（清洁、维修、天气应急）',
    title VARCHAR(100) NOT NULL COMMENT '活动标题（如3号楼3层清洁、东门维修）',
    description TEXT COMMENT '活动详情（如清洁范围、维修内容）',
    status VARCHAR(20) NOT NULL DEFAULT 'planned' COMMENT '活动状态（计划中、进行中、已完成、取消）',
    priority VARCHAR(20) NOT NULL DEFAULT 'medium' COMMENT '活动优先级（低、中、高）',
    activity_datetime DATETIME NOT NULL COMMENT '活动执行时间',
    expected_unavailable_duration DECIMAL(5,2) NOT NULL COMMENT '预计不可用时长（小时）',
    actual_completion_datetime DATETIME COMMENT '实际完成时间（NULL=未完成）',
    created_by_staff_id INT NOT NULL COMMENT '创建人ID（关联staff表）',
    weather_id INT COMMENT '关联天气事件ID（关联weather_emergency表）',
    area_id INT COMMENT '关联外部区域ID（关联area表）',
    hazard_level VARCHAR(20) NOT NULL COMMENT '风险等级（低、中、高）',
    facility_type VARCHAR(20) NOT NULL COMMENT '关联设施类型（建筑/房间/楼层/广场/大门/食堂/无）',
    building_id INT COMMENT '关联建筑物ID（关联buildings表）',
    room_id INT COMMENT '关联房间ID（关联rooms表）',
    level_id INT COMMENT '关联楼层ID（关联levels表）',
    square_id INT COMMENT '关联广场ID（关联squares表）',
    gate_id INT COMMENT '关联大门ID（关联gates表）',
    canteen_id INT COMMENT '关联食堂ID（关联canteen表）',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '活动状态（Y=有效，N=无效）',
    PRIMARY KEY (activity_id),
    FOREIGN KEY (created_by_staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT,
    FOREIGN KEY (weather_id) REFERENCES weather_emergency(weather_id) ON DELETE SET NULL,
    FOREIGN KEY (area_id) REFERENCES area(area_id) ON DELETE SET NULL,
    FOREIGN KEY (building_id) REFERENCES buildings(building_id) ON DELETE SET NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE SET NULL,
    FOREIGN KEY (level_id) REFERENCES levels(level_id) ON DELETE SET NULL,
    FOREIGN KEY (square_id) REFERENCES squares(square_id) ON DELETE SET NULL,
    FOREIGN KEY (gate_id) REFERENCES gates(gate_id) ON DELETE SET NULL,
    FOREIGN KEY (canteen_id) REFERENCES canteen(canteen_id) ON DELETE SET NULL,
    CHECK (activity_type IN ('cleaning', 'repair', 'weather_response')),
    CHECK (status IN ('planned', 'in_progress', 'completed', 'cancelled')),
    CHECK (priority IN ('low', 'medium', 'high')),
    CHECK (hazard_level IN ('low', 'medium', 'high')),
    CHECK (facility_type IN ('building', 'room', 'level', 'square', 'gate', 'canteen', 'none')),
    CHECK (expected_unavailable_duration >= 0),
    CHECK (actual_completion_datetime IS NULL OR actual_completion_datetime >= activity_datetime),
    CHECK (active_flag IN ('Y', 'N')),
    -- 确保设施类型与关联字段一致（仅一个设施字段非空）
    CHECK (
        (facility_type = 'building' AND building_id IS NOT NULL AND room_id IS NULL AND level_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL)
        OR (facility_type = 'room' AND room_id IS NOT NULL AND building_id IS NOT NULL AND level_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL)
        OR (facility_type = 'level' AND level_id IS NOT NULL AND building_id IS NOT NULL AND room_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL)
        OR (facility_type = 'square' AND square_id IS NOT NULL AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL)
        OR (facility_type = 'gate' AND gate_id IS NOT NULL AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL AND square_id IS NULL AND canteen_id IS NULL)
        OR (facility_type = 'canteen' AND canteen_id IS NOT NULL AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL AND square_id IS NULL AND gate_id IS NULL)
        OR (facility_type = 'none' AND building_id IS NULL AND room_id IS NULL AND level_id IS NULL AND square_id IS NULL AND gate_id IS NULL AND canteen_id IS NULL)
    ),
    INDEX idx_activity_status_priority (status, priority),
    INDEX idx_activity_datetime_type (activity_datetime, activity_type),
    INDEX idx_activity_facility (facility_type, building_id, room_id, level_id),
    INDEX idx_activity_creator (created_by_staff_id),
    INDEX idx_activity_weather (weather_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储校园维护活动核心信息，支持多场景活动管理';

-- 22. 员工-活动关联表（依赖staff、activity表）
CREATE TABLE works_for (
    works_for_id INT AUTO_INCREMENT COMMENT '关联唯一标识',
    staff_id INT NOT NULL COMMENT '员工ID（关联staff表）',
    activity_id INT NOT NULL COMMENT '活动ID（关联activity表）',
    activity_responsibility VARCHAR(200) NOT NULL COMMENT '员工在活动中的职责（如操作机器人、现场监督）',
    assigned_datetime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    active_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '关联状态（Y=有效，N=无效）',
    PRIMARY KEY (works_for_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE,
    UNIQUE KEY uk_staff_activity (staff_id, activity_id),
    CHECK (active_flag IN ('Y', 'N')),
    INDEX idx_works_for_staff_activity (staff_id, activity_id, active_flag),
    INDEX idx_works_for_activity (activity_id, active_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '实现员工与活动的多对多关联，记录分配时间与职责';

-- 23. 外包合同表（依赖company、activity表）
CREATE TABLE contract (
    contract_id INT AUTO_INCREMENT COMMENT '合同唯一标识',
    contractor_id INT NOT NULL COMMENT '合作公司ID（关联company表）',
    activity_id INT NOT NULL COMMENT '关联活动ID（关联activity表）',
    contract_date DATE NOT NULL COMMENT '合同签订日期',
    contract_amount DECIMAL(12,2) NOT NULL COMMENT '合同金额（元）',
    end_date DATE COMMENT '合同结束日期（NULL=未结束）',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '合同状态（生效中、已完成、已取消）',
    payment_terms VARCHAR(200) COMMENT '付款条款（如验收后30天付款）',
    notes TEXT COMMENT '合同备注（如验收标准、违约责任）',
    PRIMARY KEY (contract_id),
    FOREIGN KEY (contractor_id) REFERENCES company(contractor_id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE,
    UNIQUE KEY uk_contract_activity (activity_id),
    CHECK (contract_amount >= 0),
    CHECK (status IN ('active', 'completed', 'cancelled')),
    CHECK (end_date IS NULL OR contract_date <= end_date),
    INDEX idx_contract_company (contractor_id, status),
    INDEX idx_contract_date (contract_date),
    INDEX idx_contract_activity_status (activity_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储活动外包合同信息，确保一个活动仅对应一个外包合同';

-- 24. 机器人使用表（依赖robot、activity、staff表）
CREATE TABLE robot_usage (
    robot_usage_id INT AUTO_INCREMENT COMMENT '使用记录唯一标识',
    robot_id INT NOT NULL COMMENT '机器人ID（关联robot表）',
    activity_id INT NOT NULL COMMENT '活动ID（关联activity表）',
    usage_datetime DATETIME NOT NULL COMMENT '使用时间',
    operator_staff_id INT COMMENT '操作人员ID（关联staff表）',
    usage_duration DECIMAL(5,2) NOT NULL COMMENT '使用时长（小时）',
    usage_quantity INT NOT NULL COMMENT '使用次数（如启动次数）',
    notes TEXT COMMENT '使用备注（如故障记录、操作要点）',
    PRIMARY KEY (robot_usage_id),
    FOREIGN KEY (robot_id) REFERENCES robot(robot_id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE,
    FOREIGN KEY (operator_staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL,
    CHECK (usage_duration >= 0),
    CHECK (usage_quantity >= 1),
    UNIQUE KEY uk_robot_activity_datetime (robot_id, activity_id, usage_datetime),
    INDEX idx_robot_usage_robot (robot_id, usage_datetime),
    INDEX idx_robot_usage_activity (activity_id, robot_id),
    INDEX idx_robot_usage_operator (operator_staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储机器人参与活动的使用记录，确保数据完整性';

-- 25. 化学品安全检查表（依赖activity、chemical、staff表）
CREATE TABLE safety_check (
    safety_check_id INT AUTO_INCREMENT COMMENT '检查记录唯一标识',
    activity_id INT NOT NULL COMMENT '活动ID（关联activity表）',
    chemical_id INT NOT NULL COMMENT '化学品ID（关联chemical表）',
    check_datetime DATETIME NOT NULL COMMENT '检查时间',
    checked_by_staff_id INT NOT NULL COMMENT '检查人ID（关联staff表）',
    check_items TEXT NOT NULL COMMENT '检查项目明细（如浓度合规、存储合规）',
    check_result VARCHAR(20) NOT NULL COMMENT '检查结果（通过、未通过、待检查）',
    rectification_measures TEXT COMMENT '整改措施（如未通过时的处理方案）',
    notes TEXT COMMENT '检查备注（如环境条件、特殊情况）',
    PRIMARY KEY (safety_check_id),
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE,
    FOREIGN KEY (chemical_id) REFERENCES chemical(chemical_id) ON DELETE CASCADE,
    FOREIGN KEY (checked_by_staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE,
    CHECK (check_result IN ('passed', 'failed', 'pending')),
    UNIQUE KEY uk_activity_chemical_checkdatetime (activity_id, chemical_id, check_datetime),
    INDEX idx_safety_check_activity (activity_id, check_result),
    INDEX idx_safety_check_chemical (chemical_id, check_datetime),
    INDEX idx_safety_check_staff (checked_by_staff_id, check_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '存储化学品在活动中使用的安全检查记录，支持合规追溯';

-- ======================== 触发器（替换子查询CHECK约束）========================
DELIMITER //

-- 1. 监督关系表：验证上级角色层级低于下级
CREATE TRIGGER trg_supervise_role_level_check_insert
BEFORE INSERT ON supervise
FOR EACH ROW
BEGIN
    DECLARE supervisor_level INT;
    DECLARE subordinate_level INT;
    
    SELECT r.role_level INTO supervisor_level
    FROM staff s JOIN role r ON s.role_id = r.role_id
    WHERE s.staff_id = NEW.supervisor_staff_id;
    
    SELECT r.role_level INTO subordinate_level
    FROM staff s JOIN role r ON s.role_id = r.role_id
    WHERE s.staff_id = NEW.subordinate_staff_id;
    
    IF supervisor_level >= subordinate_level THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '上级角色层级必须低于下级（行政官→中层经理→基层员工）';
    END IF;
END //

CREATE TRIGGER trg_supervise_role_level_check_update
BEFORE UPDATE ON supervise
FOR EACH ROW
BEGIN
    DECLARE supervisor_level INT;
    DECLARE subordinate_level INT;
    
    SELECT r.role_level INTO supervisor_level
    FROM staff s JOIN role r ON s.role_id = r.role_id
    WHERE s.staff_id = NEW.supervisor_staff_id;
    
    SELECT r.role_level INTO subordinate_level
    FROM staff s JOIN role r ON s.role_id = r.role_id
    WHERE s.staff_id = NEW.subordinate_staff_id;
    
    IF supervisor_level >= subordinate_level THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '上级角色层级必须低于下级（行政官→中层经理→基层员工）';
    END IF;
END //

-- 2. 建筑物表：验证负责人为中层经理（role_level=2）
CREATE TRIGGER trg_building_supervisor_check_insert
BEFORE INSERT ON buildings
FOR EACH ROW
BEGIN
    DECLARE manager_level INT;
    IF NEW.supervisor_staff_id IS NOT NULL THEN
        SELECT r.role_level INTO manager_level
        FROM staff s JOIN role r ON s.role_id = r.role_id
        WHERE s.staff_id = NEW.supervisor_staff_id;
        
        IF manager_level != 2 THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '建筑物负责人必须是中层经理（role_level=2）';
        END IF;
    END IF;
END //

CREATE TRIGGER trg_building_supervisor_check_update
BEFORE UPDATE ON buildings
FOR EACH ROW
BEGIN
    DECLARE manager_level INT;
    IF NEW.supervisor_staff_id IS NOT NULL THEN
        SELECT r.role_level INTO manager_level
        FROM staff s JOIN role r ON s.role_id = r.role_id
        WHERE s.staff_id = NEW.supervisor_staff_id;
        
        IF manager_level != 2 THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '建筑物负责人必须是中层经理（role_level=2）';
        END IF;
    END IF;
END //

-- 3. 机器人使用表：验证机器人处于启用状态
CREATE TRIGGER trg_robot_usage_active_check_insert
BEFORE INSERT ON robot_usage
FOR EACH ROW
BEGIN
    DECLARE robot_status CHAR(1);
    SELECT active_flag INTO robot_status FROM robot WHERE robot_id = NEW.robot_id;
    IF robot_status != 'Y' THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '只能使用启用状态（active_flag=Y）的机器人';
    END IF;
END //

-- 4. 化学品安全检查表：验证化学品处于启用状态
CREATE TRIGGER trg_safety_check_chemical_active_insert
BEFORE INSERT ON safety_check
FOR EACH ROW
BEGIN
    DECLARE chemical_status CHAR(1);
    SELECT active_flag INTO chemical_status FROM chemical WHERE chemical_id = NEW.chemical_id;
    IF chemical_status != 'Y' THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '只能检查启用状态（active_flag=Y）的化学品';
    END IF;
END //

-- 5. 活动表：验证设施关联一致性（房间/楼层与建筑物匹配）
CREATE TRIGGER trg_activity_facility_consistency_insert
BEFORE INSERT ON activity
FOR EACH ROW
BEGIN
    DECLARE room_building_id INT;
    DECLARE level_building_id INT;
    
    -- 验证房间与建筑物一致性
    IF NEW.facility_type = 'room' THEN
        SELECT building_id INTO room_building_id FROM rooms WHERE room_id = NEW.room_id;
        IF room_building_id != NEW.building_id THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '房间所属建筑物与活动关联的建筑物不一致';
        END IF;
    END IF;
    
    -- 验证楼层与建筑物一致性
    IF NEW.facility_type = 'level' THEN
        SELECT building_id INTO level_building_id FROM levels WHERE level_id = NEW.level_id;
        IF level_building_id != NEW.building_id THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '楼层所属建筑物与活动关联的建筑物不一致';
        END IF;
    END IF;
END //

CREATE TRIGGER trg_activity_facility_consistency_update
BEFORE UPDATE ON activity
FOR EACH ROW
BEGIN
    DECLARE room_building_id INT;
    DECLARE level_building_id INT;
    
    -- 验证房间与建筑物一致性
    IF NEW.facility_type = 'room' THEN
        SELECT building_id INTO room_building_id FROM rooms WHERE room_id = NEW.room_id;
        IF room_building_id != NEW.building_id THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '房间所属建筑物与活动关联的建筑物不一致';
        END IF;
    END IF;
    
    -- 验证楼层与建筑物一致性
    IF NEW.facility_type = 'level' THEN
        SELECT building_id INTO level_building_id FROM levels WHERE level_id = NEW.level_id;
        IF level_building_id != NEW.building_id THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '楼层所属建筑物与活动关联的建筑物不一致';
        END IF;
    END IF;
END //

DELIMITER ;

-- ======================== 定时事件（自动维护数据状态）========================
-- 开启事件调度器（若未开启）
SET GLOBAL event_scheduler = ON;

-- 1. 每日凌晨1点：禁用过期化学品库存
CREATE EVENT evt_disable_expired_chemical
ON SCHEDULE EVERY 1 DAY STARTS '2025-01-01 01:00:00'
COMMENT '自动将过期的化学品库存置为不可用'
DO
UPDATE chemical_inventory
SET active_flag = 'N'
WHERE active_flag = 'Y' AND expiry_date < CURRENT_DATE();

-- 2. 每小时执行：更新天气应急事件状态
CREATE EVENT evt_update_weather_emergency_status
ON SCHEDULE EVERY 1 HOUR
COMMENT '自动将已结束的天气应急事件置为非活跃状态'
DO
UPDATE weather_emergency
SET active_flag = 'N'
WHERE active_flag = 'Y' AND end_date < CURRENT_TIMESTAMP();