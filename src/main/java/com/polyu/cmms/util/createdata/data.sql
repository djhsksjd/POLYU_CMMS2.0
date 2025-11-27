/* Enter "USE {database};" to start exploring your data.
   Press Ctrl + I to try out AI-generated SQL queries or SQL rewrite using Chat2Query. */
USE test;
-- 1. 角色表（Role）- 3条（覆盖所有角色）
INSERT INTO Role (role_name, role_level, description, active_flag)
VALUES 
('executive_officer', 1, '负责校园整体运维管理、政策制定', 'Y'),
('mid_level_manager', 2, '负责具体建筑物/区域运维管理、人员调度', 'Y'),
('base_level_worker', 3, '执行清洁、维修、设备操作等具体工作', 'Y');

-- 2. 系统配置表（SystemLimits）- 12条（覆盖不同生效日期、激活状态）
INSERT INTO SystemLimits (max_mid_level_managers, max_base_level_workers, effective_date)
VALUES 
(10, 50, '2024-01-01'),
(12, 60, '2024-07-01'),
(15, 70, '2025-01-01'),
(12, 65, '2025-07-01'),
(14, 68, '2023-01-01'),
(11, 55, '2023-07-01'),
(13, 62, '2026-01-01'),
(12, 60, '2026-07-01'),
(10, 58, '2022-01-01'),
(9, 52, '2022-07-01'),
(15, 72, '2027-01-01'),
(16, 75, '2027-07-01');

-- 3. 地址表（Address）- 15条（覆盖不同区域、详细地址）
INSERT INTO Address (street, city, postal_code, country, detail, active_flag)
VALUES 
('Central Street', 'Beijing', '100001', 'China', 'No.123, Building A', 'Y'),
('West Avenue', 'Beijing', '100002', 'China', 'No.45, Industrial Park', 'Y'),
('East Road', 'Shanghai', '200001', 'China', 'No.67, Commercial District', 'Y'),
('South Street', 'Guangzhou', '510000', 'China', 'No.89, Residential Area', 'Y'),
('North Boulevard', 'Shenzhen', '518000', 'China', 'No.10, High-tech Zone', 'Y'),
('Lake Road', 'Chengdu', '610000', 'China', 'No.11, Lakeside Community', 'Y'),
('Mountain Avenue', 'Chongqing', '400000', 'China', 'No.13, Hilltop Area', 'Y'),
('River Street', 'Wuhan', '430000', 'China', 'No.15, Riverside Building', 'Y'),
('Park Road', 'Nanjing', '210000', 'China', 'No.17, Park North Gate', 'Y'),
('Garden Avenue', 'Hangzhou', '310000', 'China', 'No.19, Garden District', 'Y'),
('Forest Street', 'Qingdao', '266000', 'China', 'No.21, Coastal Zone', 'Y'),
('Ocean Road', 'Xiamen', '361000', 'China', 'No.23, Seaside Building', 'Y'),
('Desert Avenue', 'Urumqi', '830000', 'China', 'No.25, Urban Center', 'Y'),
('Snow Road', 'Harbin', '150000', 'China', 'No.27, Ice City District', 'Y'),
('Cloud Street', 'Kunming', '650000', 'China', 'No.29, Plateau Area', 'Y');

-- 4. 员工技能表（Skill）- 12条（覆盖不同技能类型）
INSERT INTO Skill (skill_name, description)
VALUES 
('robot_operation', '清洁/维修/检测机器人操作与基础故障排查'),
('chemical_use', '低/中/高风险化学品规范使用与存储'),
('electrical_repair', '建筑物电路、照明设备维修与维护'),
('mechanical_maintenance', '机械设备、管道系统维修与保养'),
('cleaning_service', '室内外清洁、消毒、垃圾处理'),
('safety_inspection', '活动安全风险评估与合规检查'),
('emergency_response', '天气应急、设备故障应急处理'),
('building_inspection', '建筑物结构、设施日常巡检'),
('inventory_management', '化学品、耗材库存管理与领用登记'),
('contract_management', '外包合同洽谈、执行与验收'),
('robot_maintenance', '机器人定期维护、零件更换与系统升级'),
('hazardous_waste_disposal', '危险废弃物分类与合规处置');

-- 5. 机器人表（Robot）- 15条（覆盖不同类型、维护周期）
INSERT INTO Robot (type, robot_capability, create_date, last_maintained_date, maintenance_cycle, active_flag)
VALUES 
('cleaning_robot', '地面清扫、吸尘、拖地一体化', '2022-01-15', '2024-10-20', 30, 'Y'),
('repair_robot', '管道检测、小型零件更换', '2022-03-20', '2024-10-15', 60, 'Y'),
('inspection_robot', '建筑物外墙、高空设施检测', '2022-05-10', '2024-10-10', 45, 'Y'),
('cleaning_robot', '室内甲醛净化、空气消毒', '2022-07-05', '2024-10-25', 30, 'Y'),
('repair_robot', '电路故障定位、应急修复', '2022-09-12', '2024-10-05', 60, 'Y'),
('inspection_robot', '消防设施、安全通道巡检', '2022-11-18', '2024-10-30', 45, 'Y'),
('cleaning_robot', '广场、操场大面积清扫', '2023-01-20', '2024-10-18', 25, 'Y'),
('repair_robot', '空调系统、通风设备维修', '2023-03-15', '2024-10-12', 50, 'Y'),
('inspection_robot', '化学品仓库环境、安全合规检测', '2023-05-22', '2024-10-08', 40, 'Y'),
('cleaning_robot', '食堂、餐厅油污清洁与消毒', '2023-07-10', '2024-10-22', 20, 'Y'),
('repair_robot', '大门门禁系统、通行设备维修', '2023-09-05', '2024-10-03', 55, 'Y'),
('inspection_robot', '楼层、房间设施完好度巡检', '2023-11-12', '2024-10-28', 35, 'Y'),
('cleaning_robot', '地下车库清洁、排水系统清理', '2024-01-08', '2024-10-24', 28, 'Y'),
('repair_robot', '广场照明、景观设施维修', '2024-03-10', '2024-10-16', 48, 'Y'),
('inspection_robot', '应急通道、疏散标识巡检', '2024-05-15', '2024-10-01', 38, 'Y');

-- 6. 天气应急事件表（Weather_Emergency）- 10条（覆盖不同事件类型）
INSERT INTO Weather_Emergency (name, type, start_date, end_date, active_flag)
VALUES 
('2024年北京暴雨', 'rainfall', '2024-07-20 08:00:00', '2024-07-22 12:00:00', 'N'),
('2024年台风“杜苏芮”', 'typhoon', '2024-08-01 14:00:00', '2024-08-03 09:00:00', 'N'),
('2024年北方寒潮', 'low_temperature', '2024-11-10 00:00:00', '2024-11-15 18:00:00', 'N'),
('2024年上海大风', 'strong_wind', '2024-06-15 10:00:00', '2024-06-15 16:00:00', 'N'),
('2024年广州暴雨', 'rainfall', '2024-05-25 09:00:00', '2024-05-26 11:00:00', 'N'),
('2024年深圳暴雪', 'blizzard', '2024-02-05 03:00:00', '2024-02-06 07:00:00', 'N'),
('2025年预期暴雨', 'rainfall', '2025-06-01 00:00:00', '2025-06-03 23:59:59', 'Y'),
('2025年预期台风', 'typhoon', '2025-07-15 00:00:00', '2025-07-18 23:59:59', 'Y'),
('2025年预期寒潮', 'low_temperature', '2025-12-01 00:00:00', '2025-12-07 23:59:59', 'Y'),
('2025年预期大风', 'strong_wind', '2025-04-10 00:00:00', '2025-04-12 23:59:59', 'Y');

-- 7. 化学品表（Chemical）- 18条（覆盖不同类型、危险等级）
INSERT INTO Chemical (product_code, name, type, manufacturer, msds_url, hazard_category, storage_requirements, active_flag)
VALUES 
('CHEM-001', '医用酒精（75%）', 'disinfectant', 'Beijing Medical Supplies Co.', 'https://msds.example.com/chem001', 'medium', '阴凉干燥、密封保存，远离火源', 'Y'),
('CHEM-002', '工业清洁剂', 'detergent', 'Shanghai Cleaning Products Co.', 'https://msds.example.com/chem002', 'low', '常温存储、避免阳光直射', 'Y'),
('CHEM-003', '机械润滑油', 'lubricant', 'Guangzhou Machinery Co.', 'https://msds.example.com/chem003', 'low', '密封保存、远离高温', 'Y'),
('CHEM-004', '强氧化剂', 'other', 'Shenzhen Chemical Co.', 'https://msds.example.com/chem004', 'high', '阴凉通风、单独存储，避免与还原剂接触', 'Y'),
('CHEM-005', '甲醛消毒液', 'disinfectant', 'Chengdu Chemical Supplies Co.', 'https://msds.example.com/chem005', 'high', '密封避光、通风存储，佩戴防护装备', 'Y'),
('CHEM-006', '玻璃清洁剂', 'detergent', 'Chongqing Daily Supplies Co.', 'https://msds.example.com/chem006', 'low', '常温存储、避免儿童接触', 'Y'),
('CHEM-007', '电路清洗剂', 'solvent', 'Wuhan Electronic Co.', 'https://msds.example.com/chem007', 'medium', '密封保存、远离火源与静电', 'Y'),
('CHEM-008', '管道疏通剂', 'detergent', 'Nanjing Hardware Co.', 'https://msds.example.com/chem008', 'medium', '常温存储、避免与酸性物质混用', 'Y'),
('CHEM-009', '防锈剂', 'lubricant', 'Hangzhou Metal Co.', 'https://msds.example.com/chem009', 'low', '密封保存、防潮', 'Y'),
('CHEM-010', '高浓度消毒液', 'disinfectant', 'Qingdao Sanitary Co.', 'https://msds.example.com/chem010', 'high', '专业存储区、双人双锁管理', 'Y'),
('CHEM-011', '橡胶溶剂', 'solvent', 'Xiamen Chemical Co.', 'https://msds.example.com/chem011', 'medium', '通风存储、远离火源', 'Y'),
('CHEM-012', '地板蜡', 'detergent', 'Urumqi Daily Co.', 'https://msds.example.com/chem012', 'low', '常温存储、避免阳光直射', 'Y'),
('CHEM-013', '金属清洁剂', 'detergent', 'Harbin Hardware Co.', 'https://msds.example.com/chem013', 'low', '常温存储、密封保存', 'Y'),
('CHEM-014', '实验室酒精（95%）', 'solvent', 'Kunming Laboratory Supplies Co.', 'https://msds.example.com/chem014', 'medium', '阴凉干燥、远离火源，专业区域存储', 'Y'),
('CHEM-015', '除垢剂', 'detergent', 'Beijing Daily Co.', 'https://msds.example.com/chem015', 'low', '常温存储、避免与碱性物质混用', 'Y'),
('CHEM-016', '工业溶剂', 'solvent', 'Shanghai Industrial Co.', 'https://msds.example.com/chem016', 'high', '防爆存储区、通风良好，佩戴防护装备', 'Y'),
('CHEM-017', '植物消毒剂', 'disinfectant', 'Guangzhou Green Co.', 'https://msds.example.com/chem017', 'low', '常温存储、开封后尽快使用', 'Y'),
('CHEM-018', '机械除锈剂', 'other', 'Shenzhen Hardware Co.', 'https://msds.example.com/chem018', 'medium', '通风存储、避免皮肤接触', 'Y');

-- #################################### 关联表数据（依赖基础表）####################################
-- 8. 员工表（Staff）- 20条（覆盖不同角色、联系方式）
INSERT INTO Staff (staff_number, first_name, last_name, date_of_birth, gender, role_id, email, phone, hire_date, emergency_contact, emergency_phone, active_flag)
VALUES 
('EMP-001', 'Zhang', 'San', '1980-01-15', 'M', 1, 'zhang.san@cmms.com', '13800138001', '2010-03-01', 'Li Si', '13900139001', 'Y'),
('EMP-002', 'Li', 'Si', '1985-03-20', 'M', 2, 'li.si@cmms.com', '13800138002', '2012-05-10', 'Wang Wu', '13900139002', 'Y'),
('EMP-003', 'Wang', 'Wu', '1990-05-10', 'F', 2, 'wang.wu@cmms.com', '13800138003', '2014-07-15', 'Zhao Liu', '13900139003', 'Y'),
('EMP-004', 'Zhao', 'Liu', '1995-07-05', 'M', 3, 'zhao.liu@cmms.com', '13800138004', '2016-09-20', 'Chen Qi', '13900139004', 'Y'),
('EMP-005', 'Chen', 'Qi', '1992-09-12', 'F', 3, 'chen.qi@cmms.com', '13800138005', '2015-11-01', 'Yang Ba', '13900139005', 'Y'),
('EMP-006', 'Yang', 'Ba', '1988-11-18', 'M', 3, 'yang.ba@cmms.com', '13800138006', '2013-01-10', 'Huang Jiu', '13900139006', 'Y'),
('EMP-007', 'Huang', 'Jiu', '1993-01-20', 'F', 3, 'huang.jiu@cmms.com', '13800138007', '2017-03-15', 'Zhou Shi', '13900139007', 'Y'),
('EMP-008', 'Zhou', 'Shi', '1987-03-15', 'M', 2, 'zhou.shi@cmms.com', '13800138008', '2011-05-20', 'Wu Bai', '13900139008', 'Y'),
('EMP-009', 'Wu', 'Bai', '1994-05-22', 'F', 3, 'wu.bai@cmms.com', '13800138009', '2018-07-25', 'Xu Shan', '13900139009', 'Y'),
('EMP-010', 'Xu', 'Shan', '1991-07-10', 'M', 3, 'xu.shan@cmms.com', '13800138010', '2016-09-30', 'Hu Ya', '13900139010', 'Y'),
('EMP-011', 'Hu', 'Ya', '1989-09-05', 'F', 3, 'hu.ya@cmms.com', '13800138011', '2014-11-05', 'Guo Jia', '13900139011', 'Y'),
('EMP-012', 'Guo', 'Jia', '1996-11-12', 'M', 3, 'guo.jia@cmms.com', '13800138012', '2019-01-10', 'He Min', '13900139012', 'Y'),
('EMP-013', 'He', 'Min', '1986-01-08', 'F', 2, 'he.min@cmms.com', '13800138013', '2010-03-15', 'Lin Tao', '13900139013', 'Y'),
('EMP-014', 'Lin', 'Tao', '1993-03-10', 'M', 3, 'lin.tao@cmms.com', '13800138014', '2020-05-15', 'Zheng Hua', '13900139014', 'Y'),
('EMP-015', 'Zheng', 'Hua', '1990-05-15', 'F', 3, 'zheng.hua@cmms.com', '13800138015', '2018-07-20', 'Xie Jun', '13900139015', 'Y'),
('EMP-016', 'Xie', 'Jun', '1984-07-20', 'M', 2, 'xie.jun@cmms.com', '13800138016', '2009-09-01', 'Wang Hong', '13900139016', 'Y'),
('EMP-017', 'Wang', 'Hong', '1992-09-25', 'F', 3, 'wang.hong@cmms.com', '13800138017', '2021-01-25', 'Chen Ming', '13900139017', 'Y'),
('EMP-018', 'Chen', 'Ming', '1995-11-30', 'M', 3, 'chen.ming@cmms.com', '13800138018', '2022-03-10', 'Li Ying', '13900139018', 'Y'),
('EMP-019', 'Li', 'Ying', '1983-12-05', 'F', 2, 'li.ying@cmms.com', '13800138019', '2008-05-15', 'Zhang Wei', '13900139019', 'Y'),
('EMP-020', 'Zhang', 'Wei', '1994-02-10', 'M', 3, 'zhang.wei@cmms.com', '13800138020', '2023-07-01', 'Liu Fang', '13900139020', 'Y');

-- 9. 外部公司表（Company）- 15条（覆盖不同专业领域）
INSERT INTO Company (contractor_code, name, contact_name, contract_quote, email, phone, address_id, expertise, tax_id, bank_account, active_flag)
VALUES 
('COM-001', 'Beijing Construction Co.', 'Li Ming', 1500.00, 'contact@beijing-construction.com', '010-12345678', 1, '建筑维修、结构加固', '91110000MA0001', '11001010200012345678', 'Y'),
('COM-002', 'Shanghai Cleaning Service Co.', 'Wang Hong', 800.00, 'contact@shanghai-cleaning.com', '021-87654321', 2, '校园清洁、垃圾清运', '91310000MA0002', '11001010200087654321', 'Y'),
('COM-003', 'Guangzhou Mechanical Maintenance Co.', 'Zhao Wei', 1200.00, 'contact@guangzhou-mech.com', '020-13579246', 3, '机械设备维修、管道养护', '91440100MA0003', '11001010200013579246', 'Y'),
('COM-004', 'Shenzhen High-tech Co.', 'Chen Fang', 2000.00, 'contact@shenzhen-hightech.com', '0755-24681357', 4, '机器人维护、智能设备调试', '91440300MA0004', '11001010200024681357', 'Y'),
('COM-005', 'Chengdu Sanitary Co.', 'Yang Tao', 750.00, 'contact@chengdu-sanitary.com', '028-11223344', 5, '消毒服务、环境检测', '91510100MA0005', '11001010200011223344', 'Y'),
('COM-006', 'Chongqing Road Co.', 'Huang Qiang', 1600.00, 'contact@chongqing-road.com', '023-55667788', 6, '道路维修、广场养护', '91500000MA0006', '11001010200055667788', 'Y'),
('COM-007', 'Wuhan Electrical Co.', 'Zhou Min', 1300.00, 'contact@wuhan-electrical.com', '027-99887766', 7, '电路维修、照明设备安装', '91420100MA0007', '11001010200099887766', 'Y'),
('COM-008', 'Nanjing Gardening Co.', 'Wu Jie', 900.00, 'contact@nanjing-gardening.com', '025-33445566', 8, '绿化养护、景观设施维护', '91320100MA0008', '11001010200033445566', 'Y'),
('COM-009', 'Hangzhou Environmental Co.', 'Xu Feng', 1100.00, 'contact@hangzhou-env.com', '0571-66778899', 9, '环保检测、废弃物处理', '91330100MA0009', '11001010200066778899', 'Y'),
('COM-010', 'Qingdao Coastal Co.', 'Hu Li', 1400.00, 'contact@qingdao-coastal.com', '0532-88990011', 10, '海岸设施维修、防腐蚀处理', '91370200MA0010', '11001010200088990011', 'Y'),
('COM-011', 'Xiamen Seaside Co.', 'Guo Hong', 1000.00, 'contact@xiamen-seaside.com', '0592-22334455', 11, '海水淡化设备维护、海滨清洁', '91350200MA0011', '11001010200022334455', 'Y'),
('COM-012', 'Urumqi Engineering Co.', 'Xie Lin', 1800.00, 'contact@urumqi-eng.com', '0991-33445566', 12, '戈壁地区设施维修、保温工程', '91650100MA0012', '11001010200033445566', 'Y'),
('COM-013', 'Harbin Ice Co.', 'Lin Qiang', 1700.00, 'contact@harbin-ice.com', '0451-44556677', 13, '冰雪设施维护、防冻工程', '91230100MA0013', '11001010200044556677', 'Y'),
('COM-014', 'Kunming Plateau Co.', 'Zheng Hua', 950.00, 'contact@kunming-plateau.com', '0871-55667788', 14, '高原设施维护、绿化养护', '91530100MA0014', '11001010200055667788', 'Y'),
('COM-015', 'Beijing Emergency Co.', 'Wang Tao', 2200.00, 'contact@beijing-emergency.com', '010-66778899', 15, '应急救援、灾害后修复', '91110000MA0015', '11001010200066778899', 'Y');

-- 10. 建筑物表（Buildings）- 12条（覆盖不同楼层数、负责人）
INSERT INTO Buildings (building_code, construction_date, address_id, num_floors, supervisor_staff_id, active_flag)
VALUES 
('B-001', '2000-01-15', 1, 10, 2, 'Y'), -- 负责人：Li Si（中层经理，staff_id=2）
('B-002', '2005-03-20', 2, 8, 3, 'Y'), -- 负责人：Wang Wu（中层经理，staff_id=3）
('B-003', '2010-05-10', 3, 12, 8, 'Y'), -- 负责人：Zhou Shi（中层经理，staff_id=8）
('B-004', '2015-07-05', 4, 6, 13, 'Y'), -- 负责人：He Min（中层经理，staff_id=13）
('B-005', '2008-09-12', 5, 15, 16, 'Y'), -- 负责人：Xie Jun（中层经理，staff_id=16）
('B-006', '2012-11-18', 6, 7, 19, 'Y'), -- 负责人：Li Ying（中层经理，staff_id=19）
('B-007', '2018-01-20', 7, 9, 2, 'Y'), -- 负责人：Li Si（中层经理，staff_id=2）
('B-008', '2003-03-15', 8, 5, 3, 'Y'), -- 负责人：Wang Wu（中层经理，staff_id=3）
('B-009', '2013-05-22', 9, 11, 8, 'Y'), -- 负责人：Zhou Shi（中层经理，staff_id=8）
('B-010', '2007-07-10', 10, 4, 13, 'Y'), -- 负责人：He Min（中层经理，staff_id=13）
('B-011', '2016-09-05', 11, 8, 16, 'Y'), -- 负责人：Xie Jun（中层经理，staff_id=16）
('B-012', '2011-11-12', 12, 10, 19, 'Y'); -- 负责人：Li Ying（中层经理，staff_id=19）

-- 11. 楼层表（Levels）- 18条（每个建筑物1-2个楼层）
INSERT INTO Levels (building_id, level_number, active_flag)
VALUES 
(1, 1, 'Y'), (1, 2, 'Y'), (1, 3, 'Y'), -- 建筑物1：3层
(2, 1, 'Y'), (2, 2, 'Y'), -- 建筑物2：2层
(3, 1, 'Y'), (3, 2, 'Y'), (3, 3, 'Y'), (3, 4, 'Y'), -- 建筑物3：4层
(4, 1, 'Y'), (4, 2, 'Y'), -- 建筑物4：2层
(5, 1, 'Y'), (5, 2, 'Y'), (5, 3, 'Y'), -- 建筑物5：3层
(6, 1, 'Y'), (6, 2, 'Y'), -- 建筑物6：2层
(7, 1, 'Y'), (7, 2, 'Y'); -- 建筑物7：2层
SELECT * FROM Levels;
-- 12. 房间表（Rooms）- 20条（每个建筑物2-3个房间）
INSERT INTO Rooms (building_id, name, room_type, capacity, room_features, active_flag)
VALUES 
(1, '101教室', 'classroom', 50, '空调、投影仪、黑板', 'Y'),
(1, '102办公室', 'office', 10, '空调、电脑、打印机', 'Y'),
(1, '201实验室', 'laboratory', 20, '通风系统、实验台、仪器设备', 'Y'),
(2, '101会议室', 'meeting_room', 30, '空调、投影仪、音响', 'Y'),
(2, '102设备间', 'equipment_room', 5, '配电设备、空调主机', 'Y'),
(3, '101食堂', 'canteen', 100, '餐桌、厨房设备、消毒设备', 'Y'),
(3, '201办公室', 'office', 8, '空调、电脑、文件柜', 'Y'),
(3, '301教室', 'classroom', 40, '空调、投影仪、多媒体', 'Y'),
(4, '101维修间', 'maintenance_room', 6, '工具、备件、工作台', 'Y'),
(4, '102仓库', 'warehouse', 20, '货架、叉车、通风设备', 'Y'),
(5, '101大厅', 'hall', 200, '照明、监控、电梯', 'Y'),
(5, '201办公室', 'office', 12, '空调、电脑、会议桌', 'Y'),
(5, '301活动室', 'activity_room', 80, '音响、舞台、座椅', 'Y'),
(6, '101门卫室', 'guard_room', 2, '监控、通讯设备', 'Y'),
(6, '102休息室', 'rest_room', 8, '沙发、饮水机、电视', 'Y'),
(7, '101实验室', 'laboratory', 25, '实验设备、通风系统', 'Y'),
(7, '201办公室', 'office', 10, '空调、电脑、打印机', 'Y'),
(8, '101教室', 'classroom', 45, '空调、投影仪、黑板', 'Y'),
(8, '102办公室', 'office', 9, '空调、电脑、文件柜', 'Y'),
(9, '101会议室', 'meeting_room', 25, '空调、投影仪、白板', 'Y');

-- 13. 广场表（Squares）- 10条（覆盖不同名称、容量）
INSERT INTO Squares (name, address_id, capacity, active_flag)
VALUES 
('中心广场', 1, 500, 'Y'),
('西广场', 2, 300, 'Y'),
('东广场', 3, 250, 'Y'),
('南广场', 4, 400, 'Y'),
('北广场', 5, 350, 'Y'),
('湖畔广场', 6, 200, 'Y'),
('山顶广场', 7, 150, 'Y'),
('江滨广场', 8, 450, 'Y'),
('公园广场', 9, 300, 'Y'),
('花园广场', 10, 280, 'Y');

-- 14. 大门表（Gates）- 12条（覆盖不同名称、通行容量）
INSERT INTO Gates (name, address_id, flow_capacity, active_flag)
VALUES 
('东门', 1, 200, 'Y'),
('西门', 2, 150, 'Y'),
('南门', 3, 250, 'Y'),
('北门', 4, 180, 'Y'),
('应急东门', 5, 100, 'Y'),
('应急西门', 6, 80, 'Y'),
('贵宾门', 7, 50, 'Y'),
('货物门', 8, 120, 'Y'),
('学生门', 9, 300, 'Y'),
('教职工门', 10, 100, 'Y'),
('后门', 11, 80, 'Y'),
('侧门', 12, 60, 'Y');

-- 15. 食堂表（Canteen）- 10条（覆盖不同餐饮类型）
INSERT INTO Canteen (name, construction_date, address_id, food_type, active_flag)
VALUES 
('第一食堂', '2000-01-15', 1, 'Chinese', 'Y'),
('第二食堂', '2005-03-20', 2, 'Western', 'Y'),
('第三食堂', '2010-05-10', 3, 'Mixed', 'Y'),
('清真食堂', '2015-07-05', 4, 'Chinese', 'Y'),
('教职工食堂', '2008-09-12', 5, 'Mixed', 'Y'),
('学生食堂', '2012-11-18', 6, 'Chinese', 'Y'),
('湖畔食堂', '2018-01-20', 7, 'Western', 'Y'),
('山顶食堂', '2003-03-15', 8, 'Mixed', 'Y'),
('江滨食堂', '2013-05-22', 9, 'Chinese', 'Y'),
('公园食堂', '2007-07-10', 10, 'Western', 'Y');

-- 16. 外部区域表（Area）- 10条（覆盖不同区域类型）
INSERT INTO Area (area_type, description, address_id, active_flag)
VALUES 
('campus_external', '校园东门外侧广场', 1, 'Y'),
('campus_external', '校园西门外侧道路', 2, 'Y'),
('temporary_work_area', 'B1号楼施工临时区域', 3, 'Y'),
('temporary_work_area', '中心广场维修临时区域', 4, 'Y'),
('campus_external', '校园南门外侧停车场', 5, 'Y'),
('campus_external', '校园北门外侧绿化带', 6, 'Y'),
('temporary_work_area', '食堂改造临时区域', 7, 'Y'),
('other', '校园垃圾处理站区域', 8, 'Y'),
('other', '化学品仓库外部缓冲区', 9, 'Y'),
('temporary_work_area', '大门维修临时区域', 10, 'Y');

-- #################################### 业务关联表数据（依赖基础表/关联表）####################################
-- 17. 员工-技能关联表（Staff_Skill_Map）- 20条（每个员工1-2个技能）
INSERT INTO Staff_Skill_Map (staff_id, skill_id, proficiency)
VALUES 
(4, 1, 'intermediate'), (4, 5, 'senior'), -- 员工4：机器人操作、清洁服务
(5, 2, 'junior'), (5, 10, 'intermediate'), -- 员工5：化学品使用、合同管理
(6, 3, 'senior'), (6, 4, 'intermediate'), -- 员工6：电气维修、机械维护
(7, 5, 'intermediate'), (7, 7, 'junior'), -- 员工7：清洁服务、应急响应
(8, 6, 'senior'), (8, 11, 'intermediate'), -- 员工8：安全检查、机器人维护
(9, 1, 'junior'), (9, 9, 'intermediate'), -- 员工9：机器人操作、库存管理
(10, 2, 'intermediate'), (10, 12, 'senior'), -- 员工10：化学品使用、危险废弃物处置
(11, 3, 'intermediate'), (11, 7, 'junior'), -- 员工11：电气维修、应急响应
(12, 4, 'senior'), (12, 8, 'intermediate'), -- 员工12：机械维护、管道疏通
(13, 6, 'intermediate'), (13, 10, 'senior'); -- 员工13：安全检查、合同管理

-- 18. 监督关系表（Supervise）- 15条（上级为中层/行政官，下级为基层/中层）
INSERT INTO Supervise (supervisor_staff_id, subordinate_staff_id, start_date, end_date)
VALUES 
(2, 4, '2016-09-20', NULL), (2, 5, '2015-11-01', NULL), (2, 6, '2013-01-10', NULL), -- 中层经理2（Li Si）监督基层员工4-6
(3, 7, '2017-03-15', NULL), (3, 8, '2011-05-20', NULL), (3, 9, '2018-07-25', NULL), -- 中层经理3（Wang Wu）监督基层员工7-9
(8, 10, '2016-09-30', NULL), (8, 11, '2014-11-05', NULL), (8, 12, '2019-01-10', NULL), -- 中层经理8（Zhou Shi）监督基层员工10-12
(13, 14, '2020-05-15', NULL), (13, 15, '2018-07-20', NULL), -- 中层经理13（He Min）监督基层员工14-15
(16, 17, '2021-01-25', NULL), (16, 18, '2022-03-10', NULL), -- 中层经理16（Xie Jun）监督基层员工17-18
(1, 2, '2012-05-10', NULL), (1, 3, '2014-07-15', NULL); -- 行政官1（Zhang San）监督中层经理2-3

-- 19. 化学品库存表（Chemical_Inventory）- 18条（每个化学品1条库存）
INSERT INTO Chemical_Inventory (chemical_id, quantity, storage_location, purchase_date, supplier_id, expiry_date, batch_number, active_flag)
VALUES 
(1, 50.00, '化学品仓库A区', '2024-01-15', 5, '2025-01-15', 'B20240115', 'Y'),
(2, 100.00, '化学品仓库B区', '2024-02-20', 2, '2026-02-20', 'B20240220', 'Y'),
(3, 80.00, '化学品仓库C区', '2024-03-10', 3, '2027-03-10', 'B20240310', 'Y'),
(4, 30.00, '危险化学品专区', '2024-04-05', 4, '2025-04-05', 'B20240405', 'Y'),
(5, 20.00, '危险化学品专区', '2024-05-12', 5, '2025-05-12', 'B20240512', 'Y'),
(6, 90.00, '化学品仓库B区', '2024-06-18', 6, '2026-06-18', 'B20240618', 'Y'),
(7, 40.00, '化学品仓库A区', '2024-07-22', 7, '2025-07-22', 'B20240722', 'Y'),
(8, 60.00, '化学品仓库C区', '2024-08-30', 8, '2026-08-30', 'B20240830', 'Y'),
(9, 70.00, '化学品仓库B区', '2024-09-15', 9, '2027-09-15', 'B20240915', 'Y'),
(10, 15.00, '危险化学品专区', '2024-10-01', 10, '2025-10-01', 'B20241001', 'Y'),
(11, 45.00, '化学品仓库A区', '2024-10-10', 11, '2025-10-10', 'B20241010', 'Y'),
(12, 85.00, '化学品仓库C区', '2024-10-15', 12, '2026-10-15', 'B20241015', 'Y'),
(13, 55.00, '化学品仓库B区', '2024-10-20', 13, '2027-10-20', 'B20241020', 'Y'),
(14, 35.00, '化学品仓库A区', '2024-10-25', 14, '2025-10-25', 'B20241025', 'Y'),
(15, 65.00, '化学品仓库C区', '2024-10-30', 15, '2026-10-30', 'B20241030', 'Y'),
(16, 10.00, '危险化学品专区', '2024-11-05', 4, '2025-11-05', 'B20241105', 'Y'),
(17, 75.00, '化学品仓库B区', '2024-11-10', 5, '2026-11-10', 'B20241110', 'Y'),
(18, 25.00, '化学品仓库A区', '2024-11-15', 3, '2025-11-15', 'B20241115', 'Y');

-- 验证 Levels 表中 (3,2) 存在
SELECT * FROM Levels;
-- 验证所有 Rooms 表关联组合存在（以第1条为例）
SELECT * FROM Rooms WHERE building_id=1 AND room_id=1;
-- 验证所有 Gates/Canteen 关联存在（以第5条为例）
SELECT * FROM Gates WHERE gate_id=2;

-- 20. 活动表（Activity）- 20条（终极修复外键冲突，确保所有关联组合存在）
INSERT INTO Activity (activity_type, title, description, status, priority, activity_datetime, expected_unavailable_duration, actual_completion_datetime, created_by_staff_id, weather_id, area_id, hazard_level, facility_type, building_id, room_id, level_id, square_id, gate_id, canteen_id, active_flag)
VALUES 
-- 清洁活动
('cleaning', 'B-001号楼101教室清洁消毒', '日常清洁+酒精消毒，覆盖桌面、地面、门窗', 'completed', 'medium', '2024-10-01 08:00:00', 2.00, '2024-10-01 10:00:00', 4, NULL, NULL, 'low', 'room', 1, 1, NULL, NULL, NULL, NULL, 'Y'),
('cleaning', '中心广场大面积清扫', '清扫落叶、垃圾，冲洗地面', 'completed', 'low', '2024-10-02 09:00:00', 3.00, '2024-10-02 12:00:00', 5, NULL, NULL, 'low', 'square', NULL, NULL, NULL, 1, NULL, NULL, 'Y'),
('cleaning', '东门外侧广场台风后清洁', '清理台风过后的树枝、垃圾、积水', 'completed', 'high', '2024-08-02 10:00:00', 4.00, '2024-08-02 14:00:00', 6, 2, 1, 'medium', 'area', NULL, NULL, NULL, NULL, NULL, NULL, 'Y'),
-- 维修活动
('repair', 'B-002号楼102设备间电路维修', '修复配电设备故障，更换老化线路', 'completed', 'high', '2024-10-03 14:00:00', 2.50, '2024-10-03 16:30:00', 7, NULL, NULL, 'medium', 'room', 2, 5, NULL, NULL, NULL, NULL, 'Y'),
('repair', '西门门禁系统维修', '修复门禁识别故障，测试通行功能', 'in_progress', 'medium', '2024-10-10 09:00:00', 1.50, NULL, 8, NULL, NULL, 'low', 'gate', NULL, NULL, NULL, NULL, 2, NULL, 'Y'),
-- 关键修复：将 level_id=3 改为 2（Levels表中确认存在 (3,2)）
('repair', 'B-003号楼空调系统维修', '维修2层空调制冷故障，更换滤网', 'planned', 'high', '2024-11-01 13:00:00', 3.00, NULL, 9, NULL, NULL, 'medium', 'level', 3, NULL, 6, NULL, NULL, NULL, 'Y'),
-- 应急活动
('weather_response', '暴雨后B-005号楼排水系统清理', '清理地下室积水，疏通排水管道', 'completed', 'high', '2024-07-21 09:00:00', 5.00, '2024-07-21 14:00:00', 10, 1, NULL, 'high', 'building', 5, NULL, NULL, NULL, NULL, NULL, 'Y'),
('weather_response', '寒潮前校园水管防冻处理', '对裸露水管进行保温包裹，检查防冻措施', 'planned', 'high', '2025-12-02 08:00:00', 6.00, NULL, 11, 9, NULL, 'medium', 'building', NULL, NULL, NULL, NULL, NULL, NULL, 'Y'),
-- 其他活动
('cleaning', '第一食堂日常清洁消毒', '餐桌、地面、厨房区域清洁消毒', 'completed', 'medium', '2024-10-04 15:00:00', 2.00, '2024-10-04 17:00:00', 12, NULL, NULL, 'low', 'canteen', NULL, NULL, NULL, NULL, NULL, 1, 'Y'),
('repair', 'B-004号楼101维修间工具整理', '维修工具分类存放，检查工具完好度', 'completed', 'low', '2024-10-05 10:00:00', 1.00, '2024-10-05 11:00:00', 13, NULL, NULL, 'low', 'room', 4, 9, NULL, NULL, NULL, NULL, 'Y'),
('cleaning', '化学品仓库外部缓冲区清洁', '清理杂物，检查安全通道', 'in_progress', 'medium', '2024-10-15 14:00:00', 1.50, NULL, 14, NULL, 9, 'medium', 'area', NULL, NULL, NULL, NULL, NULL, NULL, 'Y'),
('repair', '中心广场照明设施维修', '更换损坏路灯，测试照明效果', 'planned', 'medium', '2024-11-05 16:00:00', 2.00, NULL, 15, NULL, NULL, 'low', 'square', NULL, NULL, NULL, 1, NULL, NULL, 'Y'),
('weather_response', '大风前户外设施加固', '加固广场遮阳棚、广告牌等户外设施', 'completed', 'high', '2024-06-14 15:00:00', 3.00, '2024-06-14 18:00:00', 16, 4, NULL, 'medium', 'square', NULL, NULL, NULL, 1, NULL, NULL, 'Y'),
('cleaning', 'B-006号楼101门卫室清洁', '日常清洁，整理值班记录', 'completed', 'low', '2024-10-06 08:30:00', 0.50, '2024-10-06 09:00:00', 17, NULL, NULL, 'low', 'room', 6, 14, NULL, NULL, NULL, NULL, 'Y'),
('repair', 'B-007号楼201办公室电脑维修', '修复电脑无法开机故障，数据备份', 'in_progress', 'medium', '2024-10-20 10:30:00', 2.00, NULL, 18, NULL, NULL, 'low', 'room', 7, 17, NULL, NULL, NULL, NULL, 'Y'),
('weather_response', '暴雪后校园道路除雪', '清理主要通道积雪，撒融雪剂', 'completed', 'high', '2024-02-05 08:00:00', 4.00, '2024-02-05 12:00:00', 19, 6, NULL, 'high', 'area', NULL, NULL, NULL, NULL, NULL, NULL, 'Y'),
('cleaning', 'B-008号楼101教室清洁', '桌面、地面清洁，黑板擦拭', 'planned', 'low', '2024-11-10 09:00:00', 1.00, NULL, 4, NULL, NULL, 'low', 'room', 8, 18, NULL, NULL, NULL, NULL, 'Y'),
('repair', '南门通行设备维修', '修复道闸无法升降故障', 'planned', 'high', '2024-11-08 14:00:00', 2.00, NULL, 5, NULL, NULL, 'medium', 'gate', NULL, NULL, NULL, NULL, 3, NULL, 'Y'),
('weather_response', '预期台风前应急物资准备', '准备沙袋、抽水机、急救物资等', 'planned', 'high', '2025-07-14 10:00:00', 3.00, NULL, 6, 8, NULL, 'medium', 'building', 5, NULL, NULL, NULL, NULL, NULL, 'Y'),
('cleaning', 'B-009号楼101会议室清洁', '会前清洁，布置会议场地', 'completed', 'medium', '2024-10-08 13:00:00', 1.00, '2024-10-08 14:00:00', 7, NULL, NULL, 'low', 'room', 9, 20, NULL, NULL, NULL, NULL, 'Y');

-- 21. 员工-活动关联表（WORKS_FOR）- 20条（每个活动1-2个员工）
INSERT INTO WORKS_FOR (staff_id, activity_id, activity_responsibility, assigned_datetime, active_flag)
VALUES 
(4, 1, '执行教室清洁消毒，操作清洁设备', '2024-09-30 10:00:00', 'Y'),
(5, 1, '准备消毒化学品，监督清洁质量', '2024-09-30 10:00:00', 'Y'),
(6, 2, '操作清扫机器人，清理广场垃圾', '2024-10-01 09:00:00', 'Y'),
(7, 3, '清理树枝、积水，设置警示标识', '2024-08-01 15:00:00', 'Y'),
(8, 4, '电路故障排查，更换老化线路', '2024-10-02 10:00:00', 'Y'),
(9, 5, '门禁设备拆卸、维修、测试', '2024-10-09 09:00:00', 'Y'),
(10, 6, '空调系统检测，更换滤网', '2024-10-25 14:00:00', 'Y'),
(11, 7, '操作抽水机，清理地下室积水', '2024-07-20 16:00:00', 'Y'),
(12, 7, '疏通排水管道，检查管道完好度', '2024-07-20 16:00:00', 'Y'),
(13, 8, '水管保温包裹，防冻措施检查', '2025-11-25 10:00:00', 'Y'),
(14, 9, '食堂区域清洁，消毒设备操作', '2024-10-03 14:00:00', 'Y'),
(15, 10, '工具分类存放，完好度检查', '2024-10-04 09:00:00', 'Y'),
(16, 11, '缓冲区清洁，安全通道检查', '2024-10-14 15:00:00', 'Y'),
(17, 12, '路灯拆卸、更换、照明测试', '2024-11-04 10:00:00', 'Y'),
(18, 13, '遮阳棚加固，广告牌检查', '2024-06-13 14:00:00', 'Y'),
(4, 14, '门卫室清洁，值班记录整理', '2024-10-05 08:00:00', 'Y'),
(5, 15, '电脑故障排查，数据备份', '2024-10-19 10:00:00', 'Y'),
(6, 16, '操作除雪设备，撒融雪剂', '2024-02-04 16:00:00', 'Y'),
(7, 17, '教室清洁，黑板擦拭', '2024-11-09 09:00:00', 'Y'),
(8, 18, '道闸设备维修，升降测试', '2024-11-07 14:00:00', 'Y');

-- 22. 外包合同表（Contract）- 10条（每个合同关联一个活动）
INSERT INTO Contract (contractor_id, activity_id, contract_date, contract_amount, end_date, status, payment_terms, notes)
VALUES 
(2, 2, '2024-09-25', 800.00, '2024-10-03', 'completed', '验收后30天付款', '清洁范围：中心广场全部区域'),
(3, 4, '2024-10-01', 1200.00, '2024-10-04', 'completed', '验收后15天付款', '维修内容：设备间电路故障修复'),
(4, 6, '2024-10-20', 2000.00, NULL, 'active', '维修完成后付款50%，质保3个月后付剩余50%', '维修内容：空调系统制冷故障修复'),
(5, 9, '2024-10-02', 750.00, '2024-10-05', 'completed', '验收后20天付款', '清洁消毒范围：第一食堂全区域'),
(1, 13, '2024-06-10', 1500.00, '2024-06-15', 'completed', '加固完成后一次性付款', '加固设施：广场遮阳棚、广告牌'),
(6, 16, '2024-02-03', 1600.00, '2024-02-06', 'completed', '除雪完成后付款', '除雪范围：校园主要通道'),
(7, 18, '2024-11-05', 1300.00, NULL, 'active', '维修验收后30天付款', '维修内容：南门道闸升降故障'),
(8, 12, '2024-10-30', 900.00, NULL, 'active', '照明设施维修完成后付款', '维修内容：中心广场路灯更换'),
(9, 11, '2024-10-12', 1100.00, NULL, 'active', '清洁验收后25天付款', '清洁范围：化学品仓库外部缓冲区'),
(10, 7, '2024-07-18', 1400.00, '2024-07-22', 'completed', '排水清理完成后付款', '清理内容：B-005号楼地下室积水');
 