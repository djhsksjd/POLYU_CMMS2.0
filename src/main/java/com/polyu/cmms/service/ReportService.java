package com.polyu.cmms.service;

import com.polyu.cmms.util.DatabaseUtil;
import com.polyu.cmms.util.DateUtils;
import com.polyu.cmms.util.StringUtils;
import com.polyu.cmms.util.HtmlLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class ReportService extends BaseService {

    // ==================== 工人活动分布报表 ====================
    public String generateWorkerActivityReport() {
        StringBuilder report = new StringBuilder();
        report.append("=================== 工人活动分布报表 ===================\n");
        report.append("生成日期：").append(DateUtils.format(new Date())).append("\n\n");

        // 修正后的 SQL 查询
        String sql = "SELECT " +
                "s.staff_id, " +
                "s.first_name, " +
                "s.last_name, " +
                "a.activity_type, " +
                "COUNT(a.activity_id) AS task_count, " +
                "SUM(TIMESTAMPDIFF(MINUTE, a.activity_datetime, a.actual_completion_datetime)) AS total_minutes " +
                "FROM staff s " +
                "LEFT JOIN activity a ON s.staff_id = a.created_by_staff_id AND a.status = 'completed' " +
                "GROUP BY s.staff_id, s.first_name, s.last_name, a.activity_type " +
                "ORDER BY s.first_name, s.last_name, task_count DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            Map<String, List<Map<String, Object>>> workerMap = new HashMap<>();

            while (rs.next()) {
                String workerName = rs.getString("first_name") + " " + rs.getString("last_name");

                // 处理可能为null的activity type（当LEFT JOIN没有匹配项时）
                String activityType = rs.getString("activity_type");
                if (activityType == null) {
                    activityType = "无记录";
                }

                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("type", activityType);
                taskInfo.put("count", rs.getInt("task_count"));
                taskInfo.put("minutes", rs.getInt("total_minutes"));

                workerMap.computeIfAbsent(workerName, k -> new ArrayList<>()).add(taskInfo);
            }

            // 遍历Map时正确处理entry
            for (Map.Entry<String, List<Map<String, Object>>> entry : workerMap.entrySet()) {
                String workerName = entry.getKey();
                List<Map<String, Object>> tasks = entry.getValue();

                report.append(workerName).append("\n");

                // 计算总任务数
                int totalTasks = tasks.stream()
                        .mapToInt(t -> (int) t.get("count"))
                        .sum();

                for (Map<String, Object> task : tasks) {
                    String type = (String) task.get("type");
                    int count = (int) task.get("count");
                    int minutes = (int) task.get("minutes");

                    // 避免除以零的错误
                    double ratio = totalTasks > 0 ? (double) count / totalTasks : 0.0;
                    double hours = minutes / 60.0;

                    // 改进格式，更加清晰易读
                    report.append(String.format("  活动类型：%s\n", type));
                    report.append(String.format("  任务次数：%d次（占比%.0f%%）\n", count, ratio * 100));
                    report.append(String.format("  累计耗时：%.1f小时\n", hours));
                    report.append("  -----------------------------------\n");
                }
                report.append("\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            HtmlLogger.error("ReportService.generateWorkerActivityReport", "生成工人活动分布报表失败", e);
            return "报表生成失败！请查看日志获取详细信息。";
        }
        
        // 添加报表结尾
        report.append("=================== 报表结束 ===================\n");
        return report.toString();
    }

    // ==================== 活动类型分布报表 ====================
    public String generateActivityTypeReport() {
        StringBuilder report = new StringBuilder();
        report.append("=================== 活动类型分布报表 ===================\n");
        report.append("生成日期：").append(DateUtils.format(new Date())).append("\n\n");

        // 修正后的 SQL 查询
        String sql = "SELECT " +
                "activity_type, " +
                "COUNT(*) AS total_tasks, " +  // 总任务数
                "SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) AS completed_tasks, " + // 完成任务数
                "SUM(CASE WHEN status != 'completed' THEN 1 ELSE 0 END) AS uncompleted_tasks " + // 未完成任务数
                "FROM activity " +
                "GROUP BY activity_type " +
                "ORDER BY total_tasks DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String type = rs.getString("activity_type");
                int total = rs.getInt("total_tasks");
                int completed = rs.getInt("completed_tasks");
                int uncompleted = rs.getInt("uncompleted_tasks");

                // 计算完成率，避免除以零
                double completionRate = total > 0 ? (double) completed / total : 0.0;

                report.append("-------------------\n");
                report.append("活动类型：").append(type).append("\n");
                report.append(String.format("  总任务数：%d次\n", total));
                report.append(String.format("  已完成：%d次\n", completed));
                report.append(String.format("  未完成：%d次\n", uncompleted));
                report.append(String.format("  完成率：%.1f%%\n\n", completionRate * 100));
            }

            // 处理无数据情况
            if (!rs.isBeforeFirst()) {
                report.append("暂无活动任务数据！\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            HtmlLogger.error("ReportService.generateWeeklyTrendReport", "生成周维护趋势报表失败", e);
            return "报表生成失败！请查看日志获取详细信息。";
        }
        
        // 添加报表结尾
        report.append("=================== 报表结束 ===================\n");
        return report.toString();
    }

    // ==================== 建筑物维护频次报表 ====================
//    public String generateBuildingMaintenanceReport() {
//        StringBuilder report = new StringBuilder();
//        report.append("=== 建筑物维护频次报表 ===\n");
//        report.append("生成日期：").append(new Date()).append("\n\n");
//
//        String sql = "SELECT " +
//                "b.building_id, b.building_code AS building_name, " +
//                "m.maintenance_type, " +
//                "COUNT(m.maintenance_id) AS maintenance_count, " +
//                "MAX(m.maintenance_time) AS last_maintenance_time " +
//                "FROM building b " +
//                "LEFT JOIN maintenance m ON b.building_id = m.building_id " +
//                "GROUP BY b.building_id, b.name, m.maintenance_type " +
//                "ORDER BY b.name, m.maintenance_type";
//
//        try (Connection conn = DatabaseUtil.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//
//            // 使用 Map 来模拟数据结构，键是建筑物名称
//            Map<String, List<Map<String, Object>>> buildingMap = new HashMap<>();
//
//            while (rs.next()) {
//                String buildingName = rs.getString("building_name");
//                if (buildingName == null) continue;
//
//                // 1. 读取数据库字段，并处理 NULL 值
//                String maintenanceType = rs.getString("maintenance_type");
//                // 如果维护类型为 NULL，说明该建筑物没有此类型的维护记录，我们给它一个明确的名称
//                if (maintenanceType == null) {
//                    maintenanceType = "综合维护"; // 或者 "无特定类型"，根据业务理解来定
//                }
//
//                int maintenanceCount = rs.getInt("maintenance_count");
//                Date lastMaintenanceTime = rs.getTimestamp("last_maintenance_time");
//
//                // 2. 将单条维护记录存入一个 Map
//                Map<String, Object> maintenanceRecord = new HashMap<>();
//                maintenanceRecord.put("type", maintenanceType);
//                maintenanceRecord.put("count", maintenanceCount);
//                maintenanceRecord.put("last_time", lastMaintenanceTime);
//
//                // 3. 将这条记录添加到对应的建筑物列表中
//                buildingMap.computeIfAbsent(buildingName, k -> new ArrayList<>()).add(maintenanceRecord);
//            }
//
//            // 4. 遍历 buildingMap，拼接报表文本
//            for (Map.Entry<String, List<Map<String, Object>>> entry : buildingMap.entrySet()) {
//                String buildingName = entry.getKey();
//                List<Map<String, Object>> maintenanceRecords = entry.getValue();
//
//                report.append(buildingName).append("\n");
//
//                for (Map<String, Object> record : maintenanceRecords) {
//                    // 从 Map 中获取数据，并进行类型转换
//                    String type = (String) record.get("type");
//                    int count = (int) record.get("count");
//                    Date lastTime = (Date) record.get("last_time");
//
//                    String lastTimeStr = (lastTime != null) ? DateUtils.format(lastTime) : "暂无记录";
//
//                    // 调用辅助方法生成警告信息
//                    String warning = getOverdueWarning(lastTime);
//
//                    report.append(String.format("  维护类型：%s\n", type));
                //report.append(String.format("  累计次数：%d次\n", count));
                //report.append(String.format("  最近维护：%s %s\n", lastTimeStr, warning));
                //report.append("  -----------------------------------\n");
//                }
//                report.append("\n"); // 每个建筑物后空一行，提升可读性
//            }
//
//        } catch (SQLException e) {
//            // 使用项目现有的日志工具记录错误
//           // HtmlLogger.error("生成建筑物维护频次报表失败：" + e.getMessage(), e);
//            return "报表生成失败！请查看日志获取详细信息。";
//        }
//
//        return report.toString();
//    }

    /**
     * 【辅助方法】计算维护记录是否超期，生成警告文本
     * @param lastMaintenanceTime 最近维护时间（可为 null）
     * @return 超期警告（空字符串表示未超期）
     */
    /**
     * 【辅助方法】计算维护记录是否超期，生成警告文本
     * @param lastMaintenanceTime 最近维护时间（可为 null）
     * @return 超期警告（空字符串表示未超期）
     */
    // 注意：方法声明末尾没有 "throws ParseException"
    private String getOverdueWarning(Date lastMaintenanceTime) {
        // 无维护时间 → 无警告
        if (lastMaintenanceTime == null) {
            return "";
        }

        // 假设超期阈值：7天
        long daysDiff = DateUtils.getDayDiff(lastMaintenanceTime, new Date());
        if (daysDiff > 7) {
            return "[超期警告] 已超" + daysDiff + "天";
        }
        
        // 未超期 → 无警告
        return "";
    }

    // ==================== 化学品使用消耗报表 ====================
    public String generateChemicalConsumptionReport() {
        StringBuilder report = new StringBuilder();
        report.append("=================== 化学品使用消耗报表 ===================\n");
        report.append("生成日期：").append(DateUtils.format(new Date())).append("\n\n");

        String sql = "SELECT " +
                "c.chemical_id, " +
                "c.name, " +
                "c.type, " +
                "COALESCE(SUM(ci.quantity), 0) AS current_stock, " + // 库存求和（仅有效库存）
                "COALESCE(COUNT(s.safety_check_id), 0) AS monthly_consumption " + // 本月检查次数（作为消耗次数）
                "FROM chemical c " +
                // 左连库存表（过滤有效库存）
                "LEFT JOIN chemical_inventory ci " +
                "ON c.chemical_id = ci.chemical_id " +
                "AND ci.active_flag = 1 " +
                // 左连安全检查表（过滤本月有效检查）
                "LEFT JOIN safety_check s " +
                "ON c.chemical_id = s.chemical_id " +
                "AND s.check_datetime >= DATE_FORMAT(NOW() ,'%Y-%m-01') " +
                // 过滤有效化学品
                "WHERE c.active_flag = 1 " +
                "GROUP BY c.chemical_id, c.name, c.type " +
                "ORDER BY current_stock ASC"; // 库存少的在前

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String name = rs.getString("name");
                String type = rs.getString("type");
                int currentStock = rs.getInt("current_stock");
                int monthlyConsumption = rs.getInt("monthly_consumption");

                // 改进库存状态判断，增加库存紧张状态
                String stockStatus;
                if (currentStock <= 0) {
                    stockStatus = "[警告] 库存不足"; 
                } else if (currentStock <= 5) { // 假设5单位以下为库存紧张
                    stockStatus = "[注意] 库存紧张"; 
                } else {
                    stockStatus = "库存充足"; 
                }

                report.append("-------------------\n");
                report.append("化学品名称：").append(name).append("\n");
                report.append("化学品类型：").append(type).append("\n");
                report.append(String.format("  本月使用次数：%d次\n", monthlyConsumption));
                report.append(String.format("  当前库存数量：%d单位\n", currentStock));
                report.append(String.format("  库存状态：%s\n\n", stockStatus));
            }

            if (!hasData) {
                report.append("暂无有效化学品使用消耗数据！\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            HtmlLogger.error("ReportService.generateActivityTypeReport", "生成活动类型分布报表失败", e);
            return "报表生成失败！请查看日志获取详细信息。";
        }
        
        // 添加报表结尾
        report.append("=================== 报表结束 ===================\n");
        return report.toString();
    }

    // ==================== 工人工作效率报表 ====================
    public String generateWorkerEfficiencyReport() {
        StringBuilder report = new StringBuilder();
        report.append("=================== 工人工作效率报表 ===================\n");
        report.append("生成日期：").append(DateUtils.format(new Date())).append("\n\n");

        String sql = "SELECT " +
                "s.staff_id, " +
                "CONCAT(s.first_name, ' ', s.last_name) AS worker_name, " +
                "COUNT(a.activity_id) AS total_tasks, " +
                "SUM(CASE WHEN a.status = 'completed' THEN 1 ELSE 0 END) AS completed_tasks, " +
                "AVG(CASE WHEN a.status = 'completed' THEN " +
                "TIMESTAMPDIFF(MINUTE, a.activity_datetime, a.actual_completion_datetime) " +
                "ELSE NULL END) AS avg_duration " +
                "FROM staff s " +
                "LEFT JOIN activity a ON s.staff_id = a.created_by_staff_id " +
                "GROUP BY s.staff_id, s.first_name, s.last_name " +
                "HAVING total_tasks > 0 " +
                "ORDER BY (completed_tasks / total_tasks) DESC, avg_duration ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            List<Map<String, Object>> workers = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> worker = new HashMap<>();
                worker.put("worker_name", rs.getString("worker_name"));
                worker.put("total_tasks", rs.getInt("total_tasks"));
                worker.put("completed_tasks", rs.getInt("completed_tasks"));

                // 正确处理 NULL 值
                double avgDuration;
                avgDuration = rs.getDouble("avg_duration");
                if (rs.wasNull()) {
                    avgDuration = 0.0; // 或者根据业务逻辑设置为其他默认值
                }
                worker.put("avg_duration", avgDuration);

                workers.add(worker);
            }

            // 计算整体平均指标
            int totalWorkers = workers.size();
            int totalAllTasks = 0;
            int totalAllCompleted = 0;
            double totalAllDuration = 0.0;
            int workersWithCompletedTasks = 0;
            
            for (Map<String, Object> worker : workers) {
                totalAllTasks += (int) worker.get("total_tasks");
                totalAllCompleted += (int) worker.get("completed_tasks");
                double duration = (double) worker.get("avg_duration");
                if (duration > 0) {
                    totalAllDuration += duration;
                    workersWithCompletedTasks++;
                }
            }
            
            double overallCompletionRate = totalAllTasks > 0 ? (double) totalAllCompleted / totalAllTasks : 0.0;
            double overallAvgDuration = workersWithCompletedTasks > 0 ? totalAllDuration / workersWithCompletedTasks : 0.0;
            
            // 添加整体统计信息
            report.append("===== 整体统计 =====\n");
            report.append(String.format("  统计工人：%d名\n", totalWorkers));
            report.append(String.format("  总任务数：%d次\n", totalAllTasks));
            report.append(String.format("  整体完成率：%.1f%%\n", overallCompletionRate * 100));
            report.append(String.format("  平均完成时长：%.1f分钟/任务\n\n", overallAvgDuration));
            
            // 输出个人排名
            report.append("===== 个人效率排名 =====\n\n");
            for (int i = 0; i < workers.size(); i++) {
                Map<String, Object> worker = workers.get(i);
                String workerName = (String) worker.get("worker_name");
                int totalTasks = (int) worker.get("total_tasks");
                int completedTasks = (int) worker.get("completed_tasks");
                double avgDuration = (double) worker.get("avg_duration");

                double completionRate = (double) completedTasks / totalTasks;

                report.append(String.format("----- 排名 %d -----\n", i + 1));
                report.append("  工人姓名：").append(workerName).append("\n");
                report.append(String.format("  任务总数：%d次\n", totalTasks));
                report.append(String.format("  已完成任务：%d次\n", completedTasks));
                report.append(String.format("  完成率：%.1f%%\n", completionRate * 100));
                report.append(String.format("  平均完成时长：%.1f分钟/任务\n\n", avgDuration));
            }

            if (workers.isEmpty()) {
                report.append("暂无员工任务数据！\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            HtmlLogger.error("ReportService.generateChemicalConsumptionReport", "生成化学品使用消耗报表失败", e);
            return "报表生成失败！请查看日志获取详细信息。";
        }
        
        // 添加报表结尾
        report.append("=================== 报表结束 ===================\n");
        return report.toString();
    }

    // ==================== 周维护趋势报表 ====================
    public String generateWeeklyTrendReport() {
        StringBuilder report = new StringBuilder();
        report.append("=================== 周维护趋势报表 ===================\n");
        report.append("统计期间：近7天");
        report.append("\n生成日期：").append(DateUtils.format(new Date())).append("\n\n");

        // 修正后的 SQL 查询（使用子查询）
        String sql = "SELECT " +
                "task_date, " +
                "SUM(type_count) AS total_tasks, " + // 总任务数 = 各类型任务数之和
                "GROUP_CONCAT( " +
                "CONCAT(activity_type, '(', type_count, ')') " +
                "ORDER BY activity_type " +
                "SEPARATOR '、' " +
                ") AS task_type_distribution " +
                "FROM ( " +
                // 子查询：先按日期和活动类型统计数量
                "SELECT " +
                "DATE(activity_datetime) AS task_date, " +
                "activity_type, " +
                "COUNT(activity_id) AS type_count " +
                "FROM activity " +
                "WHERE status = 'completed' " +
                "AND activity_datetime >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                "GROUP BY DATE(activity_datetime), activity_type " + // 按“天”和“类型”双重分组
                ") AS daily_type_summary " +
                "GROUP BY task_date " + // 主查询再按“天”分组
                "ORDER BY task_date ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            int previousTotal = -1;
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;

                Date taskDate = rs.getDate("task_date");
                int totalTasks = rs.getInt("total_tasks");
                String distribution = rs.getString("task_type_distribution");

                String trend = "";
                if (previousTotal != -1) {
                    if (totalTasks > previousTotal) {
                        trend = " (+" + (totalTasks - previousTotal) + ")";
                    } else if (totalTasks < previousTotal) {
                        trend = " (-" + (previousTotal - totalTasks) + ")";
                    }
                }
                previousTotal = totalTasks;

                report.append("-------------------\n");
                report.append("日期：").append(DateUtils.formatWithWeekday(taskDate)).append("\n");
                report.append(String.format("  当日总任务：%d次 %s\n", totalTasks, trend));
                report.append(String.format("  任务类型分布：%s\n\n", distribution != null ? distribution : "无任务类型数据"));
            }

            if (!hasData) {
                report.append("无数据：近7天内没有已完成的维护任务记录。\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            HtmlLogger.error("ReportService.generateWorkerEfficiencyReport", "生成工人工作效率报表失败", e);
            return "报表生成失败！请查看日志获取详细信息。";
        }
        
        // 添加报表结尾
        report.append("=================== 报表结束 ===================\n");
        return report.toString();
    }
}