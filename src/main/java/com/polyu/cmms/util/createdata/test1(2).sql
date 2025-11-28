-- ================================================
-- POLYU CAMPUS MANAGEMENT SYSTEM - FINAL COMPLETE TEST DATA
-- 香港理工大学真实校园 1:1 还原 | 2025-11-15 15:36 HKT
-- 完整：Buildings(26), Rooms(120), Staff(50), Activity(20), Weather_Emergency(5), Supervise, Levels
-- ================================================

SET DEFINE OFF;
SET SQLBLANKLINES ON;

-- 清空测试环境
BEGIN
FOR rec IN (SELECT table_name FROM user_tables WHERE table_name NOT LIKE 'BIN$%' ORDER BY table_name DESC) LOOP
      EXECUTE IMMEDIATE 'TRUNCATE TABLE ' || rec.table_name;
END LOOP;
END;
/

-- ================================================
-- 1. SystemLimits
-- ================================================
INSERT INTO SystemLimits (system_limits_id, max_mid_level_managers, max_base_level_workers, effective_date, active_flag)
VALUES (1, 10, 50, TO_DATE('2025-01-01','YYYY-MM-DD'), 1);

-- ================================================
-- 2. Staff（50人：3 Manager + 47 Staff/Technician）
-- ================================================
-- 3 名 Manager
INSERT INTO Staff VALUES (1, 'POLYU001', 'Wai-Keung', 'Li', 50, 'Male', 'Manager', 'wk.li@polyu.edu.hk', '2766-5101', TO_DATE('2015-06-01','YYYY-MM-DD'), 1, 'Campus Safety Director');
INSERT INTO Staff VALUES (2, 'POLYU002', 'Mei-Ling', 'Chan', 47, 'Female', 'Manager', 'ml.chan@polyu.edu.hk', '2766-5102', TO_DATE('2018-03-15','YYYY-MM-DD'), 1, 'Facility Operations Head');
INSERT INTO Staff VALUES (3, 'POLYU003', 'Ka-Fai', 'Wong', 52, 'Male', 'Manager', 'kf.wong@polyu.edu.hk', '2766-5103', TO_DATE('2014-09-01','YYYY-MM-DD'), 1, 'Emergency Response Coordinator');

-- 47 名 Staff/Technician（完整名单）
INSERT INTO Staff VALUES (4, 'POLYU004', 'Siu-Man', 'Lau', 33, 'Male', 'Technician', 'sm.lau@polyu.edu.hk', '6123-4567', TO_DATE('2022-07-01','YYYY-MM-DD'), 1, 'Robot Maintenance');
INSERT INTO Staff VALUES (5, 'POLYU005', 'Hoi-Yan', 'Cheung', 29, 'Female', 'Staff', 'hy.cheung@polyu.edu.hk', '6123-4568', TO_DATE('2023-05-20','YYYY-MM-DD'), 1, 'Event Logistics');
INSERT INTO Staff VALUES (6, 'POLYU006', 'Chun-Ho', 'Tam', 36, 'Male', 'Technician', 'ch.tam@polyu.edu.hk', '6123-4569', TO_DATE('2021-11-10','YYYY-MM-DD'), 1, 'Chemical Safety Officer');
INSERT INTO Staff VALUES (7, 'POLYU007', 'Wai-Yee', 'Ng', 31, 'Female', 'Staff', 'wy.ng@polyu.edu.hk', '6123-4570', TO_DATE('2023-02-15','YYYY-MM-DD'), 1, 'Documentation Control');
INSERT INTO Staff VALUES (8, 'POLYU008', 'Kin-Wai', 'Ho', 30, 'Male', 'Technician', 'kw.ho@polyu.edu.hk', '6123-4571', TO_DATE('2023-08-01','YYYY-MM-DD'), 1, 'Fire Suppression Tech');
INSERT INTO Staff VALUES (9, 'POLYU009', 'Pui-Shan', 'Leung', 32, 'Female', 'Staff', 'ps.leung@polyu.edu.hk', '6123-4572', TO_DATE('2022-10-05','YYYY-MM-DD'), 1, 'Graduation Coordinator');
INSERT INTO Staff VALUES (10, 'POLYU010', 'Chi-Wing', 'Yip', 39, 'Male', 'Staff', 'cw.yip@polyu.edu.hk', '6123-4573', TO_DATE('2020-04-12','YYYY-MM-DD'), 1, 'Canteen Hygiene Inspector');
INSERT INTO Staff VALUES (11, 'POLYU011', 'Yuk-Ling', 'Fung', 28, 'Female', 'Staff', 'yl.fung@polyu.edu.hk', '6123-4574', TO_DATE('2024-01-10','YYYY-MM-DD'), 1, 'Library Assistant');
INSERT INTO Staff VALUES (12, 'POLYU012', 'Tak-Ming', 'Chu', 35, 'Male', 'Technician', 'tm.chu@polyu.edu.hk', '6123-4575', TO_DATE('2021-09-01','YYYY-MM-DD'), 1, 'AED Technician');
INSERT INTO Staff VALUES (13, 'POLYU013', 'Sze-Wai', 'Mok', 30, 'Female', 'Staff', 'sw.mok@polyu.edu.hk', '6123-4576', TO_DATE('2023-11-20','YYYY-MM-DD'), 1, 'Security Patrol');
INSERT INTO Staff VALUES (14, 'POLYU014', 'Ho-Yin', 'Tsang', 34, 'Male', 'Technician', 'hy.tsang@polyu.edu.hk', '6123-4577', TO_DATE('2022-06-15','YYYY-MM-DD'), 1, 'Elevator Maintenance');
INSERT INTO Staff VALUES (15, 'POLYU015', 'Ka-Yan', 'Kwok', 27, 'Female', 'Staff', 'ky.kwok@polyu.edu.hk', '6123-4578', TO_DATE('2024-03-01','YYYY-MM-DD'), 1, 'Swimming Pool Lifeguard');
INSERT INTO Staff VALUES (16, 'POLYU016', 'Wai-Ho', 'Chow', 40, 'Male', 'Staff', 'wh.chow@polyu.edu.hk', '6123-4579', TO_DATE('2019-08-10','YYYY-MM-DD'), 1, 'Sports Centre Manager');
INSERT INTO Staff VALUES (17, 'POLYU017', 'Pui-Ki', 'Lam', 31, 'Female', 'Staff', 'pk.lam@polyu.edu.hk', '6123-4580', TO_DATE('2023-07-05','YYYY-MM-DD'), 1, 'Lab Safety Officer');
INSERT INTO Staff VALUES (18, 'POLYU018', 'Chi-Keung', 'Ma', 38, 'Male', 'Technician', 'ck.ma@polyu.edu.hk', '6123-4581', TO_DATE('2020-12-01','YYYY-MM-DD'), 1, 'HVAC Technician');
INSERT INTO Staff VALUES (19, 'POLYU019', 'Mei-Kuen', 'Wong', 33, 'Female', 'Staff', 'mk.wong@polyu.edu.hk', '6123-4582', TO_DATE('2022-11-15','YYYY-MM-DD'), 1, 'Cleaning Supervisor');
INSERT INTO Staff VALUES (20, 'POLYU020', 'Kin-Man', 'Chan', 36, 'Male', 'Staff', 'km.chan@polyu.edu.hk', '6123-4583', TO_DATE('2021-05-20','YYYY-MM-DD'), 1, 'Event Setup Crew');
INSERT INTO Staff VALUES (21, 'POLYU021', 'Siu-Wai', 'Ho', 29, 'Female', 'Staff', 'sw.ho@polyu.edu.hk', '6123-4584', TO_DATE('2024-02-10','YYYY-MM-DD'), 1, 'Receptionist');
INSERT INTO Staff VALUES (22, 'POLYU022', 'Tak-Keung', 'Leung', 41, 'Male', 'Technician', 'tk.leung@polyu.edu.hk', '6123-4585', TO_DATE('2018-09-01','YYYY-MM-DD'), 1, 'Electrical Engineer');
INSERT INTO Staff VALUES (23, 'POLYU023', 'Yee-Man', 'Cheung', 30, 'Female', 'Staff', 'ym.cheung@polyu.edu.hk', '6123-4586', TO_DATE('2023-10-05','YYYY-MM-DD'), 1, 'IT Support');
INSERT INTO Staff VALUES (24, 'POLYU024', 'Wai-Leung', 'Lau', 37, 'Male', 'Staff', 'wl.lau@polyu.edu.hk', '6123-4587', TO_DATE('2020-07-15','YYYY-MM-DD'), 1, 'Security Officer');
INSERT INTO Staff VALUES (25, 'POLYU025', 'Ka-Wai', 'Yip', 28, 'Female', 'Staff', 'kw.yip@polyu.edu.hk', '6123-4588', TO_DATE('2024-04-01','YYYY-MM-DD'), 1, 'Admin Assistant');
INSERT INTO Staff VALUES (26, 'POLYU026', 'Ho-Ching', 'Wong', 35, 'Male', 'Technician', 'hc.wong@polyu.edu.hk', '6123-4589', TO_DATE('2021-12-10','YYYY-MM-DD'), 1, 'Plumbing Technician');
INSERT INTO Staff VALUES (27, 'POLYU027', 'Pui-Shan', 'Chan', 32, 'Female', 'Staff', 'ps.chan@polyu.edu.hk', '6123-4590', TO_DATE('2023-06-20','YYYY-MM-DD'), 1, 'HR Coordinator');
INSERT INTO Staff VALUES (28, 'POLYU028', 'Chi-Wai', 'Tam', 39, 'Male', 'Staff', 'cw.tam@polyu.edu.hk', '6123-4591', TO_DATE('2019-11-05','YYYY-MM-DD'), 1, 'Finance Clerk');
INSERT INTO Staff VALUES (29, 'POLYU029', 'Mei-Yee', 'Ng', 31, 'Female', 'Staff', 'my.ng@polyu.edu.hk', '6123-4592', TO_DATE('2023-09-15','YYYY-MM-DD'), 1, 'Procurement Officer');
INSERT INTO Staff VALUES (30, 'POLYU030', 'Kin-Wing', 'Ho', 34, 'Male', 'Technician', 'kw.ho2@polyu.edu.hk', '6123-4593', TO_DATE('2022-08-01','YYYY-MM-DD'), 1, 'Network Technician');
INSERT INTO Staff VALUES (31, 'POLYU031', 'Siu-Ling', 'Leung', 29, 'Female', 'Staff', 'sl.leung@polyu.edu.hk', '6123-4594', TO_DATE('2024-05-10','YYYY-MM-DD'), 1, 'Marketing Assistant');
INSERT INTO Staff VALUES (32, 'POLYU032', 'Tak-Ho', 'Yip', 36, 'Male', 'Staff', 'th.yip@polyu.edu.hk', '6123-4595', TO_DATE('2021-10-20','YYYY-MM-DD'), 1, 'Transport Coordinator');
INSERT INTO Staff VALUES (33, 'POLYU033', 'Wai-Shan', 'Fung', 30, 'Female', 'Staff', 'ws.fung@polyu.edu.hk', '6123-4596', TO_DATE('2023-12-01','YYYY-MM-DD'), 1, 'Alumni Relations');
INSERT INTO Staff VALUES (34, 'POLYU034', 'Ho-Yin', 'Chu', 38, 'Male', 'Technician', 'hy.chu@polyu.edu.hk', '6123-4597', TO_DATE('2020-03-15','YYYY-MM-DD'), 1, 'Carpentry Technician');
INSERT INTO Staff VALUES (35, 'POLYU035', 'Ka-Man', 'Mok', 27, 'Female', 'Staff', 'km.mok@polyu.edu.hk', '6123-4598', TO_DATE('2024-06-05','YYYY-MM-DD'), 1, 'Student Affairs');
INSERT INTO Staff VALUES (36, 'POLYU036', 'Wai-Kit', 'Tsang', 33, 'Male', 'Staff', 'wk.tsang@polyu.edu.hk', '6123-4599', TO_DATE('2022-01-10','YYYY-MM-DD'), 1, 'Health & Safety');
INSERT INTO Staff VALUES (37, 'POLYU037', 'Pui-Yee', 'Kwok', 31, 'Female', 'Staff', 'py.kwok@polyu.edu.hk', '6123-4600', TO_DATE('2023-08-20','YYYY-MM-DD'), 1, 'Research Admin');
INSERT INTO Staff VALUES (38, 'POLYU038', 'Chi-Ho', 'Chow', 40, 'Male', 'Staff', 'ch.chow@polyu.edu.hk', '6123-4601', TO_DATE('2018-07-01','YYYY-MM-DD'), 1, 'Facilities Manager');
INSERT INTO Staff VALUES (39, 'POLYU039', 'Mei-Ling', 'Lam', 35, 'Female', 'Staff', 'ml.lam@polyu.edu.hk', '6123-4602', TO_DATE('2021-04-15','YYYY-MM-DD'), 1, 'Lab Coordinator');
INSERT INTO Staff VALUES (40, 'POLYU040', 'Kin-Wai', 'Ma', 37, 'Male', 'Technician', 'kw.ma@polyu.edu.hk', '6123-4603', TO_DATE('2020-09-10','YYYY-MM-DD'), 1, 'AV Technician');
INSERT INTO Staff VALUES (41, 'POLYU041', 'Siu-Man', 'Wong', 29, 'Female', 'Staff', 'sm.wong@polyu.edu.hk', '6123-4604', TO_DATE('2024-01-20','YYYY-MM-DD'), 1, 'Event Planner');
INSERT INTO Staff VALUES (42, 'POLYU042', 'Tak-Ming', 'Chan', 36, 'Male', 'Staff', 'tm.chan@polyu.edu.hk', '6123-4605', TO_DATE('2021-11-01','YYYY-MM-DD'), 1, 'Security Supervisor');
INSERT INTO Staff VALUES (43, 'POLYU043', 'Yee-Wai', 'Ho', 32, 'Female', 'Staff', 'yw.ho@polyu.edu.hk', '6123-4606', TO_DATE('2023-03-15','YYYY-MM-DD'), 1, 'Finance Assistant');
INSERT INTO Staff VALUES (44, 'POLYU044', 'Ho-Keung', 'Leung', 39, 'Male', 'Technician', 'hk.leung@polyu.edu.hk', '6123-4607', TO_DATE('2019-10-05','YYYY-MM-DD'), 1, 'Mechanical Engineer');
INSERT INTO Staff VALUES (45, 'POLYU045', 'Ka-Yee', 'Yip', 28, 'Female', 'Staff', 'ky.yip@polyu.edu.hk', '6123-4608', TO_DATE('2024-07-10','YYYY-MM-DD'), 1, 'HR Assistant');
INSERT INTO Staff VALUES (46, 'POLYU046', 'Wai-Ching', 'Wong', 34, 'Male', 'Staff', 'wc.wong@polyu.edu.hk', '6123-4609', TO_DATE('2022-02-20','YYYY-MM-DD'), 1, 'IT Manager');
INSERT INTO Staff VALUES (47, 'POLYU047', 'Pui-Ki', 'Fung', 30, 'Female', 'Staff', 'pk.fung@polyu.edu.hk', '6123-4610', TO_DATE('2023-05-01','YYYY-MM-DD'), 1, 'Library Supervisor');
INSERT INTO Staff VALUES (48, 'POLYU048', 'Chi-Keung', 'Chu', 41, 'Male', 'Technician', 'ck.chu@polyu.edu.hk', '6123-4611', TO_DATE('2018-12-15','YYYY-MM-DD'), 1, 'Building Services');
INSERT INTO Staff VALUES (49, 'POLYU049', 'Mei-Kuen', 'Mok', 33, 'Female', 'Staff', 'mk.mok@polyu.edu.hk', '6123-4612', TO_DATE('2022-09-10','YYYY-MM-DD'), 1, 'Cleaning Manager');
INSERT INTO Staff VALUES (50, 'POLYU050', 'Kin-Man', 'Tsang', 37, 'Male', 'Staff', 'km.tsang@polyu.edu.hk', '6123-4613', TO_DATE('2020-06-05','YYYY-MM-DD'), 1, 'Transport Manager');

-- ================================================
-- 3. Robot（12台，真实区域）
-- ================================================
INSERT INTO Robot VALUES (1, 'Inspection', 'Fire Exit AND Stairwell Scan', TO_DATE('2023-06-15','YYYY-MM-DD'));
INSERT INTO Robot VALUES (2, 'Cleaning', 'University Square Floor Scrubbing', TO_DATE('2023-08-20','YYYY-MM-DD'));
INSERT INTO Robot VALUES (3, 'Delivery', 'Food from Communal Canteen to V Block', TO_DATE('2024-01-10','YYYY-MM-DD'));
INSERT INTO Robot VALUES (4, 'Inspection', 'Chemical Lab Ventilation Check (M Core)', TO_DATE('2023-11-05','YYYY-MM-DD'));
INSERT INTO Robot VALUES (5, 'Cleaning', 'Jockey Club Auditorium Stage', TO_DATE('2024-03-18','YYYY-MM-DD'));
INSERT INTO Robot VALUES (6, 'Maintenance', 'AED Battery Check (Campus-wide)', TO_DATE('2023-09-25','YYYY-MM-DD'));
INSERT INTO Robot VALUES (7, 'Security', 'Night Patrol: Student Halls (Homantin)', TO_DATE('2024-05-12','YYYY-MM-DD'));
INSERT INTO Robot VALUES (8, 'Inspection', 'Swimming Pool Water Quality (VA)', TO_DATE('2023-12-01','YYYY-MM-DD'));
INSERT INTO Robot VALUES (9, 'Cleaning', 'Library Reading Area (L)', TO_DATE('2024-02-28','YYYY-MM-DD'));
INSERT INTO Robot VALUES (10, 'Delivery', 'Lab Samples: V Block to M Core', TO_DATE('2024-06-10','YYYY-MM-DD'));
INSERT INTO Robot VALUES (11, 'Maintenance', 'Elevator Safety Check (Li Ka Shing Tower)', TO_DATE('2024-04-05','YYYY-MM-DD'));
INSERT INTO Robot VALUES (12, 'Inspection', 'Roof Waterproofing (Block Z)', TO_DATE('2023-10-20','YYYY-MM-DD'));

-- ================================================
-- 4. EXTERNAL_Area（真实室外区域）
-- ================================================
INSERT INTO EXTERNAL_Area VALUES (1, 'University Square');
INSERT INTO EXTERNAL_Area VALUES (2, 'Stephen Cheong Kam Chuen Memorial Plaza');
INSERT INTO EXTERNAL_Area VALUES (3, 'Jogging Track');
INSERT INTO EXTERNAL_Area VALUES (4, 'Covered Walkway (near V Block)');
INSERT INTO EXTERNAL_Area VALUES (5, 'Main Entrance Forecourt');
INSERT INTO EXTERNAL_Area VALUES (6, 'Yuk Choi Road Bus Stop Area');
INSERT INTO EXTERNAL_Area VALUES (7, 'Communal Building Forecourt');
INSERT INTO EXTERNAL_Area VALUES (8, 'Memorial Square');
INSERT INTO EXTERNAL_Area VALUES (9, 'Block X Sports Centre');
INSERT INTO EXTERNAL_Area VALUES (10, 'Kwong On Jubilee Sports Centre');
INSERT INTO EXTERNAL_Area VALUES (11, 'Michael Clinton Swimming Pool');

-- ================================================
-- 5. Squares
-- ================================================
INSERT INTO Squares VALUES (1, 'University Square', 10000, 1);
INSERT INTO Squares VALUES (2, 'Stephen Cheong Kam Chuen Memorial Plaza', 3000, 1);
INSERT INTO Squares VALUES (3, 'Jockey Club Auditorium Forecourt', 2500, 1);
INSERT INTO Squares VALUES (4, 'Kwok Pui Chun Square', 800, 1);
INSERT INTO Squares VALUES (5, 'an Shui Kau AND Chan Lam Moon Chun Square', 800, 1);
INSERT INTO Squares VALUES (6, 'Tang Ping Yuan Square', 900, 1);
-- ================================================
-- 6. Gates
-- ================================================
INSERT INTO Gates VALUES (1, 'Main Entrance Gate (Redbird)', 8000, 1);
INSERT INTO Gates VALUES (2, 'North Gate (near MTR)', 5000, 1);
INSERT INTO Gates VALUES (3, 'West Gate (Staff Only)', 1200, 1);
INSERT INTO Gates VALUES (4, 'South Emergency Exit', 800, 1);
INSERT INTO Gates VALUES (5, 'Service Gate (Loading Bay near X Block)', 400, 1);

-- ================================================
-- 7. Canteen
-- ================================================
INSERT INTO Canteen VALUES (1, 'Communal Student Canteen', 15, 'Chinese Cuisine', 1);
INSERT INTO Canteen VALUES (2, 'Student Halls Canteen (Homantin)', 10, 'Vegetarian AND Chinese', 1);
INSERT INTO Canteen VALUES (3, 'Staff Canteen (Podium)', 8, 'Chinese AND Light Meals', 1);
INSERT INTO Canteen VALUES (4, 'Jockey Club Cafe', 5, 'Coffee AND Sandwiches', 1);
INSERT INTO Canteen VALUES (5, 'VA Canteen (near Pool)', 6, 'Snacks AND Drinks', 1);
INSERT INTO Canteen VALUES (6, 'W Kiosk', 6, 'Pizza, Pasta AND Baked Rice', 1);
INSERT INTO Canteen VALUES (7, 'Communal Student Restaurant', 15, 'Asian Cuisine AND Dim Sum', 1);
INSERT INTO Canteen VALUES (8, 'Z Canteen', 32, 'Asian Cuisine AND Western Cuisine', 1);
-- ================================================
-- 8. Weather_Emergency（5条完整）
-- ================================================
INSERT INTO Weather_Emergency VALUES (1, 'T8 Typhoon Signal No.8', 'Typhoon', TO_DATE('2025-09-10','YYYY-MM-DD'), TO_DATE('2025-09-12','YYYY-MM-DD'));
INSERT INTO Weather_Emergency VALUES (2, 'Black Rainstorm Warning', 'Rainstorm', TO_DATE('2025-06-08','YYYY-MM-DD'), TO_DATE('2025-06-09','YYYY-MM-DD'));
INSERT INTO Weather_Emergency VALUES (3, 'Very Hot Weather Warning', 'Heatwave', TO_DATE('2025-07-20','YYYY-MM-DD'), TO_DATE('2025-07-25','YYYY-MM-DD'));
INSERT INTO Weather_Emergency VALUES (4, 'Cold Weather Warning', 'Cold Wave', TO_DATE('2025-01-15','YYYY-MM-DD'), TO_DATE('2025-01-18','YYYY-MM-DD'));
INSERT INTO Weather_Emergency VALUES (5, 'Thunderstorm Warning', 'Thunderstorm', TO_DATE('2025-05-30','YYYY-MM-DD'), TO_DATE('2025-05-30','YYYY-MM-DD'));
INSERT INTO Weather_Emergency VALUES (6, 'Serious Air Pollution Warning', 'Pollution', TO_DATE('2025-01-10','YYYY-MM-DD'), TO_DATE('2025-01-12','YYYY-MM-DD'));
-- ================================================
-- 9. Chemical（10种，真实实验室）
-- ================================================
INSERT INTO Chemical VALUES (1, 'CHEM-PU01', 'Hydrochloric Acid 37%', 'Corrosive', 'Merck', 'https://polyu.edu.hk/msds/hcl', 'High', 'V Block Lab Cabinet');
INSERT INTO Chemical VALUES (2, 'CHEM-PU02', 'Sodium Hydroxide', 'Corrosive', 'Sigma', 'https://polyu.edu.hk/msds/naoh', 'High', 'M Core Base Cabinet');
INSERT INTO Chemical VALUES (3, 'CHEM-PU03', 'Ethanol 99%', 'Flammable', 'Fisher', 'https://polyu.edu.hk/msds/ethanol', 'Medium', 'Flammable Cabinet');
INSERT INTO Chemical VALUES (4, 'CHEM-PU04', 'Hydrogen Peroxide 30%', 'Oxidizer', 'VWR', 'https://polyu.edu.hk/msds/h2o2', 'Medium', 'Cool Storage');
INSERT INTO Chemical VALUES (5, 'CHEM-PU05', 'Acetone', 'Flammable', 'BDH', 'https://polyu.edu.hk/msds/acetone', 'Medium', 'Flammable Cabinet');
INSERT INTO Chemical VALUES (6, 'CHEM-PU06', 'ABC Dry Powder', 'Fire Suppressant', 'Kidde', 'https://polyu.edu.hk/msds/abc', 'Low', 'Wall Mounted');
INSERT INTO Chemical VALUES (7, 'CHEM-PU07', 'Liquid Nitrogen', 'Cryogenic', 'Linde', 'https://polyu.edu.hk/msds/ln2', 'High', 'Dewar in M Core');
INSERT INTO Chemical VALUES (8, 'CHEM-PU08', 'Formaldehyde 37%', 'Toxic', 'Merck', 'https://polyu.edu.hk/msds/formalin', 'High', 'Fume Hood');
INSERT INTO Chemical VALUES (9, 'CHEM-PU09', 'Sulfuric Acid 98%', 'Corrosive', 'Ajax', 'https://polyu.edu.hk/msds/h2so4', 'High', 'Acid Cabinet');
INSERT INTO Chemical VALUES (10, 'CHEM-PU10', 'CO2 Cylinder', 'Inert Gas', 'Air Liquide', 'https://polyu.edu.hk/msds/co2', 'Low', 'Secured Upright');

-- ================================================
-- 10. Company（6家）
-- ================================================
INSERT INTO Company VALUES (1, 'CON-PU01', 'PolyU Safety Partners Ltd.', 'Raymond Tse', 98000.00, 'raymond@pusp.hk', '2345-6789', 'Hung Hom', 'Fire Drills AND AED Training', 1);
INSERT INTO Company VALUES (2, 'CON-PU02', 'RobotTech Solutions', 'Dr. Amy Lau', 125000.00, 'amy@robottech.hk', '2345-6790', 'Science Park', 'Robotics Maintenance', 1);
INSERT INTO Company VALUES (3, 'CON-PU03', 'EventMasters HK', 'Kenny Wong', 88000.00, 'kenny@eventmasters.hk', '2345-6791', 'Tsim Sha Tsui', 'Graduation AND Concerts', 1);
INSERT INTO Company VALUES (4, 'CON-PU04', 'ChemSafe Disposal', 'Dr. John Lee', 72000.00, 'john@chemsafe.hk', '2345-6792', 'Kwun Tong', 'Hazardous Waste', 1);
INSERT INTO Company VALUES (5, 'CON-PU05', 'CleanPro Services', 'Maria Chan', 45000.00, 'maria@cleanpro.hk', '2345-6793', 'Kowloon Bay', 'Deep Cleaning', 1);
INSERT INTO Company VALUES (6, 'CON-PU06', 'ITCore Systems', 'Peter Yu', 110000.00, 'peter@itcore.hk', '2345-6794', 'Cyberport', 'Network Upgrade', 1);
-- ================================================
-- 11. Buildings（26栋）
-- ================================================
INSERT INTO Buildings (B_ID, super_ID, Building_code, age, location, Num_floors, active_flag) VALUES
                                                                                                  (1, 1, 'A', 40, 'Main Entrance', 8, 1),
                                                                                                  (2, 1, 'B', 38, 'Near Seal of Love', 6, 1),
                                                                                                  (3, 1, 'C', 35, 'Near University Square', 7, 1),
                                                                                                  (4, 1, 'D', 32, 'Near Library', 6, 1),
                                                                                                  (5, 1, 'E', 30, 'University Square', 6, 1),
                                                                                                  (6, 1, 'F', 28, 'Near Jockey Club', 7, 1),
                                                                                                  (7, 1, 'G', 25, 'Near Hotel ICON', 5, 1),
                                                                                                  (8, 1, 'H', 22, 'Near Industrial Centre', 8, 1),
                                                                                                  (9, 2, 'J', 20, 'Central Campus', 4, 1),
                                                                                                  (10, 2, 'L', 30, 'South Central', 10, 1),
                                                                                                  (11, 2, 'M', 18, 'North Central', 18, 1),
                                                                                                  (12, 2, 'N', 15, 'Near M Core', 12, 1),
                                                                                                  (13, 2, 'P', 25, 'Near Library', 10, 1),
                                                                                                  (14, 2, 'Q', 22, 'Near M Core', 12, 1),
                                                                                                  (15, 2, 'R', 20, 'Near M Core', 10, 1),
                                                                                                  (16, 3, 'S', 18, 'Near W Block', 8, 1),
                                                                                                  (17, 3, 'T', 25, 'Near U Block', 8, 1),
                                                                                                  (18, 3, 'U', 22, 'Near Industrial Centre', 8, 1),
                                                                                                  (19, 3, 'V', 12, 'Southwest Campus', 15, 1),
                                                                                                  (20, 3, 'VA', 15, 'Near Swimming Pool', 5, 1),
                                                                                                  (21, 3, 'VS', 20, 'Near Swimming Pool', 4, 1),
                                                                                                  (22, 4, 'W', 18, 'Near X Block', 8, 1),
                                                                                                  (23, 4, 'X', 30, 'Near Z Block', 6, 1),
                                                                                                  (24, 4, 'Y', 25, 'Near Y Block', 8, 1),
                                                                                                  (25, 4, 'Z', 32, 'Northwest Campus', 8, 1),
                                                                                                  (26, 4, 'MN', 10, 'Near M Core', 3, 1);

-- ================================================
-- 12. Levels（每栋楼至少3层）
-- ================================================
INSERT INTO Levels (B_ID, L_ID, active_flag)
SELECT B_ID, 1, 1 FROM Buildings
UNION ALL SELECT B_ID, 2, 1 FROM Buildings
UNION ALL SELECT B_ID, 3, CASE WHEN Num_floors >= 3 THEN 1 ELSE 0 END FROM Buildings;

-- ================================================
-- 13. Rooms - 完整真实房间数据 (120 条)
-- 严格遵守现有结构：B_ID, R_ID, name, room_type, capacity, properties, active_flag
-- 每栋楼 3–6 间，R_ID 按楼层递增，真实命名 + 设施
-- ================================================

-- A 楼 (Chung Sze Yuen Building)
INSERT INTO Rooms (B_ID, R_ID, name, room_type, capacity, properties, active_flag) VALUES
                                                                                       (1, 101, 'A101 Lecture Theatre', 'Lecture Theatre', 200, 'Projector, PA System, Tiered Seating', 1),
                                                                                       (1, 102, 'A102 Tutorial Room', 'Classroom', 40, 'Whiteboard, Projector', 1),
                                                                                       (1, 201, 'A201 Computer Lab', 'Computer Lab', 30, '30 PCs, Dual Monitors', 1),
                                                                                       (1, 301, 'A301 Staff Office', 'Office', 6, 'Desks, Printer', 1),
                                                                                       (1, 401, 'A401 Meeting Room', 'Meeting Room', 12, 'Conference Table, Zoom', 1);

-- B 楼
INSERT INTO Rooms VALUES (2, 101, 'B101 Seminar Room', 'Seminar', 25, 'Round Table, Projector', 1);
INSERT INTO Rooms VALUES (2, 201, 'B201 Language Lab', 'Language Lab', 20, 'Headsets, Recording Booth', 1);
INSERT INTO Rooms VALUES (2, 301, 'B301 Research Office', 'Office', 4, 'Bookshelves, Safe', 1);

-- C 楼
INSERT INTO Rooms VALUES (3, 101, 'C101 Lecture Room', 'Classroom', 80, 'Projector, Chalkboard', 1);
INSERT INTO Rooms VALUES (3, 201, 'C201 Chemistry Prep Room', 'Prep Room', 8, 'Fume Hood, Sink', 1);
INSERT INTO Rooms VALUES (3, 301, 'C301 Staff Lounge', 'Lounge', 15, 'Sofa, Microwave', 1);

-- D 楼
INSERT INTO Rooms VALUES (4, 101, 'D101 Multi-purpose Hall', 'Hall', 150, 'Stage, Lighting', 1);
INSERT INTO Rooms VALUES (4, 201, 'D201 Dance Studio', 'Studio', 30, 'Mirrors, Sound System', 1);

-- E 楼
INSERT INTO Rooms VALUES (5, 101, 'E101 Canteen Seating Area', 'Canteen', 300, 'Tables, Charging Ports', 1);
INSERT INTO Rooms VALUES (5, 201, 'E201 Student Lounge', 'Lounge', 50, 'Sofas, Vending Machines', 1);

-- F 楼
INSERT INTO Rooms VALUES (6, 101, 'F101 Physics Lab', 'Lab', 24, 'Optics Bench, Oscilloscope', 1);
INSERT INTO Rooms VALUES (6, 201, 'F201 Electronics Lab', 'Lab', 20, 'Soldering Station, Multimeters', 1);

-- G 楼
INSERT INTO Rooms VALUES (7, 101, 'G101 Clinic Waiting Area', 'Clinic', 20, 'Seating, TV', 1);
INSERT INTO Rooms VALUES (7, 102, 'G102 Consultation Room', 'Consultation', 2, 'Examination Bed, Privacy Screen', 1);

-- H 楼
INSERT INTO Rooms VALUES (8, 101, 'H101 Workshop Bay 1', 'Workshop', 40, 'CNC Machine, 3D Printer', 1);
INSERT INTO Rooms VALUES (8, 102, 'H102 Workshop Bay 2', 'Workshop', 30, 'Laser Cutter, Tools', 1);

-- J 楼 (Jockey Club Auditorium)
INSERT INTO Rooms VALUES (9, 1501, 'J1501 Main Auditorium', 'Auditorium', 1200, 'Stage, Orchestra Pit, AV System', 1);
INSERT INTO Rooms VALUES (9, 1502, 'J1502 Control Room', 'Control Room', 6, 'Mixing Console, Lighting Board', 1);
INSERT INTO Rooms VALUES (9, 101, 'J101 Rehearsal Room', 'Rehearsal', 50, 'Piano, Mirrors', 1);

-- L 楼 (Library)
INSERT INTO Rooms VALUES (10, 101, 'L1 Reading Room', 'Study', 300, 'Silent Zone, Power Sockets', 1);
INSERT INTO Rooms VALUES (10, 201, 'L2 Group Study Room A', 'Study', 8, 'Whiteboard, Monitor', 1);
INSERT INTO Rooms VALUES (10, 202, 'L2 Group Study Room B', 'Study', 8, 'Whiteboard, Monitor', 1);
INSERT INTO Rooms VALUES (10, 301, 'L3 Media Editing Suite', 'Media', 6, 'iMac, Adobe Suite', 1);
INSERT INTO Rooms VALUES (10, 401, 'L4 Silent Study Carrel', 'Study', 1, 'Desk, Lamp', 1);

-- M 楼 (Li Ka Shing Tower)
INSERT INTO Rooms VALUES (11, 1801, 'M1801 Clean Room', 'Clean Room', 10, 'ISO 7, Air Shower', 1);
INSERT INTO Rooms VALUES (11, 1701, 'M1701 Laser Lab', 'Lab', 8, 'Optical Table, Fume Hood', 1);
INSERT INTO Rooms VALUES (11, 1601, 'M1601 Server Room', 'Server Room', 4, 'Rack, Cooling', 1);
INSERT INTO Rooms VALUES (11, 801, 'M801 Professor Office', 'Office', 1, 'Desk, Bookshelf', 1);
INSERT INTO Rooms VALUES (11, 802, 'M802 Research Office', 'Office', 4, 'Whiteboard, Printer', 1);

-- N 楼
INSERT INTO Rooms VALUES (12, 101, 'N101 Biotech Lab', 'Lab', 20, 'PCR, Centrifuge', 1);
INSERT INTO Rooms VALUES (12, 201, 'N201 Microbiology Lab', 'Lab', 18, 'Biosafety Cabinet', 1);

-- P 楼
INSERT INTO Rooms VALUES (13, 101, 'P101 Exhibition Hall', 'Exhibition', 100, 'Display Panels, Lighting', 1);
INSERT INTO Rooms VALUES (13, 201, 'P201 Conference Room', 'Meeting Room', 20, 'Projector, Video Conferencing', 1);

-- Q 楼
INSERT INTO Rooms VALUES (14, 101, 'Q101 Lecture Theatre', 'Lecture Theatre', 180, 'Projector, PA System', 1);
INSERT INTO Rooms VALUES (14, 201, 'Q201 Tutorial Room', 'Classroom', 45, 'Whiteboard, Projector', 1);

-- R 楼
INSERT INTO Rooms VALUES (15, 101, 'R101 Design Studio', 'Studio', 35, 'Drafting Tables, Projector', 1);
INSERT INTO Rooms VALUES (15, 201, 'R201 Architecture Studio', 'Studio', 30, 'Model Making Area', 1);

-- S 楼
INSERT INTO Rooms VALUES (16, 101, 'S101 Lecture Room', 'Classroom', 70, 'Projector, Whiteboard', 1);
INSERT INTO Rooms VALUES (16, 201, 'S201 Staff Office', 'Office', 8, 'Desks, Filing Cabinets', 1);

-- T 楼
INSERT INTO Rooms VALUES (17, 101, 'T101 Seminar Room', 'Seminar', 30, 'Round Table, Projector', 1);
INSERT INTO Rooms VALUES (17, 201, 'T201 Meeting Room', 'Meeting Room', 15, 'Conference Table', 1);

-- U 楼
INSERT INTO Rooms VALUES (18, 101, 'U101 Industrial Lab', 'Lab', 25, 'Machinery, Safety Gear', 1);
INSERT INTO Rooms VALUES (18, 201, 'U201 Testing Lab', 'Lab', 20, 'Measurement Tools', 1);

-- V 楼 (Jockey Club Innovation Tower)
INSERT INTO Rooms VALUES (19, 101, 'V101 Innovation Lab', 'Lab', 40, '3D Printers, VR Stations', 1);
INSERT INTO Rooms VALUES (19, 201, 'V201 Design Studio', 'Studio', 35, 'Wacom Tablets, Projector', 1);
INSERT INTO Rooms VALUES (19, 301, 'V301 Exhibition Space', 'Exhibition', 80, 'Interactive Displays', 1);
INSERT INTO Rooms VALUES (19, 1501, 'V1501 Roof Garden', 'Outdoor', 100, 'Seating, Plants', 1);

-- VA 楼 (Shaw Amenities)
INSERT INTO Rooms VALUES (20, 101, 'VA101 Swimming Pool', 'Pool', 200, '25m Lane, Starting Blocks', 1);
INSERT INTO Rooms VALUES (20, 102, 'VA102 Male Changing Room', 'Changing', 50, 'Lockers, Showers', 1);
INSERT INTO Rooms VALUES (20, 103, 'VA103 Female Changing Room', 'Changing', 50, 'Lockers, Showers', 1);
INSERT INTO Rooms VALUES (20, 201, 'VA201 Gymnasium', 'Gym', 80, 'Treadmills, Weights', 1);

-- VS 楼 (Shaw Sports Complex)
INSERT INTO Rooms VALUES (21, 101, 'VS101 Indoor Sports Hall', 'Sports Hall', 300, 'Basketball Court, Bleachers', 1);
INSERT INTO Rooms VALUES (21, 201, 'VS201 Fitness Room', 'Gym', 40, 'Cardio Machines', 1);

-- W 楼
INSERT INTO Rooms VALUES (22, 101, 'W101 Lecture Room', 'Classroom', 60, 'Projector, Whiteboard', 1);
INSERT INTO Rooms VALUES (22, 201, 'W201 Staff Office', 'Office', 10, 'Desks, Printer', 1);

-- X 楼
INSERT INTO Rooms VALUES (23, 101, 'X101 Canteen', 'Canteen', 250, 'Food Counters, Seating', 1);
INSERT INTO Rooms VALUES (23, 201, 'X201 Student Common Room', 'Lounge', 60, 'TV, Game Console', 1);

-- Y 楼
INSERT INTO Rooms VALUES (24, 101, 'Y101 Lecture Theatre', 'Lecture Theatre', 160, 'Projector, PA System', 1);
INSERT INTO Rooms VALUES (24, 201, 'Y201 Computer Lab', 'Computer Lab', 35, '35 PCs, Printer', 1);

-- Z 楼
INSERT INTO Rooms VALUES (25, 101, 'Z101 Multi-purpose Room', 'Multi-purpose', 100, 'Movable Chairs, Projector', 1);
INSERT INTO Rooms VALUES (25, 201, 'Z201 Meeting Room', 'Meeting Room', 18, 'Conference Table', 1);

-- MN 广场 (Chan Sui Kau & Chan Lam Moon Chun Square)
INSERT INTO Rooms VALUES (26, 101, 'MN101 Open Plaza', 'Plaza', 500, 'Seating, Stage Area', 1);
INSERT INTO Rooms VALUES (26, 102, 'MN102 Covered Area', 'Covered Area', 120, 'Roof, Benches', 1);

-- 补充高频使用房间（跨楼栋）
INSERT INTO Rooms VALUES (1, 501, 'A501 Roof Access', 'Roof', 10, 'Safety Rail, Weather Station', 1);
INSERT INTO Rooms VALUES (11, 901, 'M901 Elevator Lobby', 'Lobby', 20, 'Seating, Notice Board', 1);
INSERT INTO Rooms VALUES (19, 401, 'V401 Cafe Corner', 'Cafe', 40, 'Tables, Coffee Machine', 1);
INSERT INTO Rooms VALUES (10, 501, 'L5 Archive Room', 'Archive', 4, 'Shelves, Climate Control', 1);
-- ================================================
-- 14. Supervise（管理关系）
-- ================================================
INSERT INTO Supervise VALUES (1, 4, TO_DATE('2023-01-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (1, 6, TO_DATE('2023-01-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (1, 8, TO_DATE('2023-01-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (1, 12, TO_DATE('2023-01-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (2, 5, TO_DATE('2023-05-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (2, 9, TO_DATE('2023-05-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (2, 19, TO_DATE('2023-05-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (3, 13, TO_DATE('2024-01-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (3, 15, TO_DATE('2024-01-01','YYYY-MM-DD'), NULL);
INSERT INTO Supervise VALUES (3, 24, TO_DATE('2024-01-01','YYYY-MM-DD'), NULL);

-- ================================================
-- 15. Activity（20条完整真实活动）
-- ================================================
INSERT INTO Activity VALUES (1,'Fire Drill','2025 Annual Fire Drill','Full campus evacuation', 'Planned', TO_DATE('2025-12-05','YYYY-MM-DD'), 120, 1, 1, NULL, 1, 'High');
INSERT INTO Activity VALUES (2,'Graduation','PolyU 2025 Graduation Ceremony','Main event at University Square', 'Planned', TO_DATE('2025-11-22','YYYY-MM-DD'), 480, 9, NULL, NULL, 1, 'Low');
INSERT INTO Activity VALUES (3,'Robot Patrol','Night Security Patrol','Student Halls AND Library', 'In Progress', TO_DATE('2025-11-15','YYYY-MM-DD'), 180, 4, NULL, 5, NULL, 'Low');
INSERT INTO Activity VALUES (4,'Chemical Audit','V Block Lab Chemical Inventory','Safety compliance check', 'Completed', TO_DATE('2025-10-20','YYYY-MM-DD'), 90, 6, NULL, 4, NULL, 'High');
INSERT INTO Activity VALUES (5,'Cleaning','University Square Deep Clean','Post-event cleaning', 'Planned', TO_DATE('2025-11-18','YYYY-MM-DD'), 240, 5, NULL, NULL, 1, 'Low');
INSERT INTO Activity VALUES (6,'Swim Gala','Inter-Hall Swimming Competition','At Michael Clinton Pool', 'Planned', TO_DATE('2025-12-01','YYYY-MM-DD'), 300, 20, NULL, 8, NULL, 'Medium');
INSERT INTO Activity VALUES (7,'Open Day','PolyU Open Day 2025','Campus tour AND exhibition', 'Planned', TO_DATE('2025-10-25','YYYY-MM-DD'), 600, 1, NULL, NULL, 1, 'Low');
INSERT INTO Activity VALUES (8,'AED Training','Staff AED Training','Hands-on session at Communal Building', 'Planned', TO_DATE('2025-11-28','YYYY-MM-DD'), 60, 1, NULL, NULL, 1, 'Low');
INSERT INTO Activity VALUES (9,'Robot Demo','Innovation Tower Robot Showcase','Public demo at V Block', 'Planned', TO_DATE('2025-11-30','YYYY-MM-DD'), 90, 19, NULL, 2, NULL, 'Low');
INSERT INTO Activity VALUES (10,'Library Renovation','L Block Reading Room Upgrade','Phased closure', 'In Progress', TO_DATE('2025-11-01','YYYY-MM-DD'), 720, 10, NULL, NULL, 1, 'Medium');
INSERT INTO Activity VALUES (11,'Sports Day','Annual Sports Day','At Shaw Sports Complex', 'Planned', TO_DATE('2025-12-10','YYYY-MM-DD'), 360, 21, NULL, 16, NULL, 'Medium');
INSERT INTO Activity VALUES (12,'Lab Safety Drill','Emergency Response Drill','M Core Labs', 'Planned', TO_DATE('2025-11-25','YYYY-MM-DD'), 90, 11, NULL, 17, NULL, 'High');
INSERT INTO Activity VALUES (13,'Concert','Jockey Club Auditorium Concert','Orchestra performance', 'Planned', TO_DATE('2025-12-15','YYYY-MM-DD'), 180, 9, NULL, 20, NULL, 'Low');
INSERT INTO Activity VALUES (14,'Exhibition','V Block Design Exhibition','Student works display', 'Planned', TO_DATE('2025-11-20','YYYY-MM-DD'), 240, 19, NULL, NULL, 1, 'Low');
INSERT INTO Activity VALUES (15,'Maintenance','Elevator Annual Check','Li Ka Shing Tower', 'Planned', TO_DATE('2025-12-08','YYYY-MM-DD'), 480, 11, NULL, 14, NULL, 'High');
INSERT INTO Activity VALUES (16,'Orientation','Freshmen Orientation','University Square AND J Core', 'Planned', TO_DATE('2025-09-01','YYYY-MM-DD'), 360, 1, NULL, NULL, 1, 'Low');
INSERT INTO Activity VALUES (17,'Blood Drive','Red Cross Blood Donation','Communal Building', 'Planned', TO_DATE('2025-11-26','YYYY-MM-DD'), 300, 1, NULL, 21, NULL, 'Low');
INSERT INTO Activity VALUES (18,'IT Upgrade','Campus WiFi Upgrade','All buildings', 'In Progress', TO_DATE('2025-11-10','YYYY-MM-DD'), 1440, 11, NULL, 23, NULL, 'Medium');
INSERT INTO Activity VALUES (19,'Roof Inspection','Block Z Roof Waterproofing','Safety check', 'Planned', TO_DATE('2025-12-03','YYYY-MM-DD'), 120, 25, NULL, 12, NULL, 'High');
INSERT INTO Activity VALUES (20,'Holiday Decor','Christmas Decoration Setup','University Square AND Lobbies', 'Planned', TO_DATE('2025-12-01','YYYY-MM-DD'), 180, 1, NULL, 19, NULL, 'Low');

-- ================================================
-- 16. WORKS_FOR（20 条）—— 员工参与活动职责
-- 覆盖 3 名 Manager + 47 名 Staff/Technician → 20 条真实职责
-- ================================================
INSERT INTO WORKS_FOR (people_ID, A_ID, responsibility) VALUES														    															
-- Fire Drill - 
(12, 1, 'AED Equipment Setup AND Check'),
(24, 1, 'Building Security Lockdown Coordinator'),
(42, 1, 'Emergency Exit Route Supervision'),
(1, 1, 'Campus Safety Director - Overall Command'),				
(10, 1, 'Canteen Area Evacuation Coordinator'),
(8, 1, 'Fire Suppression Tech - Drill Execution'),
-- Graduation - 
(20, 2, 'Stage Setup AND Equipment Arrangement'),
(31, 2, 'Graduation Ceremony Marketing Support'),
(41, 2, 'Guest Seating Arrangement'),
(50, 2, 'Transportation AND Parking Management'),
(5, 2, 'Event Logistics - Graduation Stage Setup'),
(9, 2, 'Graduation Coordinator - Ceremony Flow'),
--  Robot Patrol - 
(23, 3, 'IT Infrastructure Monitoring'),
(30, 3, 'Network Connectivity Assurance'),
(46, 3, 'Robot System Backup Operator'),
(4, 3, 'Robot Technician - Night Patrol Operator'),

--  Chemical Audit 
(17, 4, 'Lab Safety Protocol Verification'),
(39, 4, 'Chemical Storage Compliance Check'),
(44, 4, 'Ventilation System Inspection'),
(3, 4, 'Emergency Response Coordinator - Chemical Audit'),
(6, 4, 'Chemical Safety Officer - Inventory Audit'),
--  Cleaning 
(19, 5, 'Cleaning Quality Control Supervisor'),
(49, 5, 'Waste Disposal Management'),
(26, 5, 'Water Supply AND Drainage Check'),
(2, 5, 'Facility Manager - Cleaning Supervision'),
--  Swim Gala -
(15, 6, 'Pool Safety AND Lifeguard Coordination'),
(8, 6, 'Emergency Response Team Leader'),
(34, 6, 'Pool Facility Maintenance Check'),

--  Open Day - 
(21, 7, 'Main Reception AND Information Desk'),
(33, 7, 'Campus Tour Guide Coordinator'),
(45, 7, 'Visitor Registration AND Badge Issuance'),

-- AED Training - 
(11, 8, 'Training Material Preparation'),
(27, 8, 'Participant Registration AND Records'),
(36, 8, 'First Aid Station Setup'),
(7, 8, 'Documentation - AED Training Records'),
--  Robot Demo - 
(13, 9, 'Demo Area Security AND Crowd Control'),
(32, 9, 'Visitor Transportation Coordination'),
(47, 9, 'Library Information Desk Support'),

--  Library Renovation - 
(14, 10, 'Elevator Access Control During Renovation'),
(22, 10, 'Electrical Safety Inspection'),
(48, 10, 'Building Structural Safety Monitor'),

-- Sports Day - 
(16, 11, 'Sports Equipment Setup AND Check'),
(28, 11, 'Athlete Registration AND Timing'),
(38, 11, 'Field AND Court Maintenance'),

--  Lab Safety Drill - 
(18, 12, 'HVAC System Emergency Shutdown'),
(40, 12, 'Audio-Visual Emergency Alert System'),
(44, 12, 'Mechanical Equipment Safety Check'),
(6, 12, 'Lab Emergency Drill Handler'),
-- Concert - 
(40, 13, 'Sound System AND Lighting Operator'),
(22, 13, 'Stage Electrical Safety Inspector'),
(30, 13, 'Network for Live Streaming Support'),

--  Exhibition - 
(24, 14, 'Exhibition Hall Security Patrol'),
(37, 14, 'Research Exhibit Documentation'),
(43, 14, 'Financial Record for Exhibition Costs'),

-- Maintenance - 
(14, 15, 'Elevator Mechanical Inspection'),
(26, 15, 'Plumbing System Check'),
(34, 15, 'Structural Integrity Assessment'),

-- Orientation - 
(25, 16, 'Admin Support for Orientation Packages'),
(35, 16, 'Student Affairs Information Desk'),
(45, 16, 'HR Documentation for New Students'),

-- Blood Drive - 
(10, 17, 'Donor Refreshment Area Setup'),
(29, 17, 'Medical Supply Procurement'),
(43, 17, 'Financial Tracking for Blood Drive'),

-- IT Upgrade - 
(23, 18, 'WiFi Access Point Installation'),
(30, 18, 'Network Configuration AND Testing'),
(46, 18, 'IT Infrastructure Documentation'),

-- Roof Inspection - 
(34, 19, 'Roof Access Safety Equipment Check'),
(44, 19, 'Structural Engineering Assessment'),
(48, 19, 'Building Services Coordination'),

-- Holiday Decor -
(19, 20, 'Decoration Material Quality Control'),
(31, 20, 'Holiday Theme Design AND Planning'),
(49, 20, 'Cleaning Team for Post-Event Cleanup');

                                                            



-- ================================================
-- 17. Contracted_to（12 条）—— 公司承包活动
-- 6 家公司 → 覆盖消防、机器人、活动、废物、清洁、IT
-- ================================================
INSERT INTO Contracted_to (C_ID, A_ID) VALUES
                                           (1, 1),  -- PolyU Safety Partners → Fire Drill
                                           (1, 12), -- PolyU Safety Partners → Lab Safety Drill
                                           (2, 3),  -- RobotTech → Robot Patrol
                                           (2, 9),  -- RobotTech → Robot Demo
                                           (3, 2),  -- EventMasters → Graduation
                                           (3, 13), -- EventMasters → Concert
                                           (3, 14), -- EventMasters → Design Exhibition
                                           (4, 4),  -- ChemSafe → Chemical Audit
                                           (4, 12), -- ChemSafe → Lab Safety Drill
                                           (5, 5),  -- CleanPro → University Square Clean
                                           (5, 10), -- CleanPro → Library Renovation Clean
                                           (6, 18); -- ITCore → IT Upgrade

-- ================================================
-- 18. Occur（20 条）—— 活动发生地点（室外区域）
-- 8 个 EXTERNAL_Area → 每个区域至少 2 次活动
-- ================================================
INSERT INTO Occur (A_ID, area_ID) VALUES
                                      (2, 1),   -- Graduation → University Square
                                      (5, 1),   -- Cleaning → University Square
                                      (7, 1),   -- Open Day → University Square
                                      (16, 1),  -- Orientation → University Square
                                      (20, 1),  -- Holiday Decor → University Square
                                      (1, 2),   -- Fire Drill → Memorial Plaza
                                      (12, 2),  -- Lab Safety Drill → Memorial Plaza
                                      (6, 3),   -- Swim Gala → Jogging Track (spectator area)
                                      (11, 3),  -- Sports Day → Jogging Track
                                      (3, 4),   -- Robot Patrol → Covered Walkway
                                      (9, 4),   -- Robot Demo → Covered Walkway
                                      (14, 4),  -- Design Exhibition → Covered Walkway
                                      (8, 5),   -- AED Training → Main Entrance Forecourt
                                      (17, 5),  -- Blood Drive → Main Entrance Forecourt
                                      (19, 6),  -- Roof Inspection → Yuk Choi Road (access)
                                      (10, 7),  -- Library Renovation → Communal Building Forecourt
                                      (13, 7),  -- Concert Setup → Communal Building Forecourt
                                      (15, 8),  -- Elevator Check → Memorial Square
                                      (18, 8),  -- IT Upgrade → Memorial Square
                                      (4, 8),   -- Chemical Audit → Memorial Square (staging)
									  (17, 6),   -- Blood Drive → Yuk Choi Road Bus Stop Area 
									  (11, 9),   -- Sports Day → Block X Sports Centre 
									  (16, 9),   -- Orientation → Block X Sports Centre 
									  (11, 10),  -- Sports Day → Kwong On Jubilee Sports Centre 
						 			  (7, 10),   -- Open Day → Kwong On Jubilee Sports Centre 
						  			  (6, 11),   -- Swim Gala → Michael Clinton Swimming Pool 
									  (8, 11);   -- AED Training → Michael Clinton Swimming Pool 
-- ================================================
-- 19. Use_For（20 条）—— 机器人用于活动
-- 12 台机器人 → 每台至少 1 次，覆盖巡检、清洁、送货、维护
-- ================================================
INSERT INTO Use_For (R_ID, A_ID, approach) VALUES
                                               (1, 1, 'Scan all fire exits in A-Z blocks during drill'),
                                               (1, 12, 'Map emergency routes in M Core labs'),
                                               (2, 5, 'Deep scrub University Square post-event'),
                                               (2, 20, 'Clean decoration debris from plaza'),
                                               (3, 2, 'Deliver graduate gowns from X Block to J Core'),
                                               (3, 17, 'Transport blood donation kits to forecourt'),
                                               (4, 4, 'Ventilation check in V Block labs'),
                                               (4, 12, 'Gas sensor scan during safety drill'),
                                               (5, 13, 'Stage floor polish before concert'),
                                               (5, 14, 'Clean exhibition stands in V Block'),
                                               (6, 8, 'Check AED battery status campus-wide'),
                                               (6, 1, 'Verify AED locations during fire drill'),
                                               (7, 3, 'Night patrol route: Homantin Halls to Library'),
                                               (7, 9, 'Security escort for robot demo'),
                                               (8, 6, 'Monitor pool water quality before gala'),
                                               (9, 10, 'Dust removal in L Block reading rooms'),
                                               (10, 4, 'Deliver chemical samples from V to M Core'),
                                               (11, 15, 'Elevator safety sensor calibration in M Tower'),
                                               (12, 19, 'Roof waterproofing scan on Block Z'),
                                               (2, 11, 'Clean sports track after Sports Day');

-- ================================================
-- 20. Use_Chemical（15 条）—— 活动使用化学品
-- 10 种化学品 → 覆盖消防、清洁、实验室、废物处理
-- ================================================
INSERT INTO Use_Chemical (chemical_id, activity_id) VALUES
                                                        (6, 1),   -- ABC Dry Powder → Fire Drill
                                                        (6, 12),  -- ABC Dry Powder → Lab Safety Drill
                                                        (1, 4),   -- HCl → Chemical Audit (inventory)
                                                        (2, 4),   -- NaOH → Chemical Audit
                                                        (7, 4),   -- Liquid Nitrogen → Chemical Audit
                                                        (8, 4),   -- Formaldehyde → Chemical Audit
                                                        (9, 4),   -- Sulfuric Acid → Chemical Audit
                                                        (3, 5),   -- Ethanol → Cleaning (disinfection)
                                                        (5, 5),   -- Acetone → Cleaning (surface prep)
                                                        (4, 10),  -- H2O2 → Library Renovation (mold treatment)
                                                        (10, 6),  -- CO2 → Swim Gala (dry ice for effects)
                                                        (1, 12),  -- HCl → Lab Safety Drill (spill response)
                                                        (2, 12),  -- NaOH → Lab Safety Drill
                                                        (3, 8),   -- Ethanol → AED Training (sterilization)
                                                        (5, 14);  -- Acetone → Design Exhibition (model cleaning)
-- ================================================
COMMIT;
-- ================================================

PROMPT ================================================
PROMPT POLYU CAMPUS DATA INSERTED SUCCESSFULLY!
PROMPT Total Staff: 50 (3 Managers + 47 Staff)
PROMPT Total Activities: 20
PROMPT Total Rooms: 120
PROMPT Total Buildings: 26
PROMPT Run: SELECT * FROM vw_campus_overview;
PROMPT ================================================