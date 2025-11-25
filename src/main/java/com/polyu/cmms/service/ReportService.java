package com.polyu.cmms.service;

import java.sql.SQLException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报告服务类，提供报告生成和导出功能
 */
public class ReportService extends BaseService {
    
    /**
     * 生成工人参与数量分析报告
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param activityTypes 活动类型列表
     * @return 报告数据
     * @throws SQLException SQL异常
     */
    public Map<String, Object> generateWorkerParticipationReport(Date startTime, Date endTime, List<String> activityTypes) throws SQLException {
        Map<String, Object> reportData = new java.util.HashMap<>();
        
        // 基本信息
        reportData.put("reportTitle", "校园各区域各类活动的工人参与数量分析");
        reportData.put("generationTime", new Date());
        reportData.put("startTime", startTime);
        reportData.put("endTime", endTime);
        reportData.put("activityTypes", activityTypes);
        
        // 生成查询条件
        StringBuilder sqlBuilder = new StringBuilder("""
            SELECT 
                a.activity_type,
                CASE 
                    WHEN a.facility_type = 'building' THEN b.building_code
                    WHEN a.facility_type = 'room' THEN r.name
                    WHEN a.facility_type = 'level' THEN CONCAT(b.building_code, '-', l.level_number)
                    WHEN a.facility_type = 'square' THEN s.name
                    WHEN a.facility_type = 'gate' THEN g.name
                    WHEN a.facility_type = 'canteen' THEN c.name
                    ELSE 'Other'
                END as area_name,
                COUNT(wfs.staff_id) as worker_count,
                AVG(DATEDIFF(a.actual_completion_datetime, a.activity_datetime)) as avg_duration_days
            FROM 
                activity a
            LEFT JOIN 
                works_for wfs ON a.activity_id = wfs.activity_id AND wfs.active_flag = 'Y'
            LEFT JOIN 
                buildings b ON a.building_id = b.building_id
            LEFT JOIN 
                rooms r ON a.room_id = r.room_id
            LEFT JOIN 
                levels l ON a.level_id = l.level_id
            LEFT JOIN 
                squares s ON a.square_id = s.square_id
            LEFT JOIN 
                gates g ON a.gate_id = g.gate_id
            LEFT JOIN 
                canteen c ON a.canteen_id = c.canteen_id
            WHERE 
                a.active_flag = 'Y' AND a.status = 'completed'
        """);
        
        List<Object> params = new java.util.ArrayList<>();
        
        // 添加活动类型条件
        if (!activityTypes.isEmpty()) {
            sqlBuilder.append(" AND a.activity_type IN (" + 
                String.join(",", java.util.Collections.nCopies(activityTypes.size(), "?")) + ")");
            params.addAll(activityTypes);
        }
        
        // 添加时间条件
        if (startTime != null) {
            sqlBuilder.append(" AND a.activity_datetime >= ?");
            params.add(startTime);
        }
        
        if (endTime != null) {
            sqlBuilder.append(" AND a.activity_datetime <= ?");
            params.add(endTime);
        }
        
        // 分组和排序
        sqlBuilder.append(" GROUP BY a.activity_type, area_name ORDER BY a.activity_type, worker_count DESC");
        
        // 执行查询
        List<Map<String, Object>> reportDetails = executeQuery(sqlBuilder.toString(), params.toArray());
        reportData.put("reportDetails", reportDetails);
        
        // 统计汇总信息
        String summarySql = """
            SELECT 
                a.activity_type,
                COUNT(DISTINCT a.activity_id) as activity_count,
                COUNT(DISTINCT wfs.staff_id) as unique_worker_count,
                SUM(COUNT(wfs.staff_id)) OVER (PARTITION BY a.activity_type) as total_participations
            FROM 
                activity a
            LEFT JOIN 
                works_for wfs ON a.activity_id = wfs.activity_id AND wfs.active_flag = 'Y'
            WHERE 
                a.active_flag = 'Y' AND a.status = 'completed'
        """;
        
        // 重新构建参数，用于汇总查询
        List<Object> summaryParams = new java.util.ArrayList<>();
        
        if (!activityTypes.isEmpty()) {
            summarySql += " AND a.activity_type IN (" + 
                String.join(",", java.util.Collections.nCopies(activityTypes.size(), "?")) + ")";
            summaryParams.addAll(activityTypes);
        }
        
        if (startTime != null) {
            summarySql += " AND a.activity_datetime >= ?";
            summaryParams.add(startTime);
        }
        
        if (endTime != null) {
            summarySql += " AND a.activity_datetime <= ?";
            summaryParams.add(endTime);
        }
        
        summarySql += " GROUP BY a.activity_type";
        
        List<Map<String, Object>> summaryData = executeQuery(summarySql, summaryParams.toArray());
        reportData.put("summaryData", summaryData);
        
        return reportData;
    }
    
    /**
     * 生成活动完成情况报告
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报告数据
     * @throws SQLException SQL异常
     */
    public Map<String, Object> generateActivityCompletionReport(Date startTime, Date endTime) throws SQLException {
        Map<String, Object> reportData = new java.util.HashMap<>();
        
        // 基本信息
        reportData.put("reportTitle", "活动完成情况报告");
        reportData.put("generationTime", new Date());
        reportData.put("startTime", startTime);
        reportData.put("endTime", endTime);
        
        // 活动状态统计
        String statusSql = """
            SELECT 
                status,
                COUNT(*) as count,
                ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM activity WHERE active_flag = 'Y' AND activity_datetime BETWEEN ? AND ?)), 2) as percentage
            FROM 
                activity
            WHERE 
                active_flag = 'Y' AND activity_datetime BETWEEN ? AND ?
            GROUP BY 
                status
            ORDER BY 
                count DESC
        """;
        
        Object[] statusParams = {startTime, endTime, startTime, endTime};
        List<Map<String, Object>> statusStats = executeQuery(statusSql, statusParams);
        reportData.put("statusStats", statusStats);
        
        // 按活动类型统计
        String typeSql = """
            SELECT 
                activity_type,
                COUNT(*) as count,
                SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_count,
                ROUND((SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) as completion_rate
            FROM 
                activity
            WHERE 
                active_flag = 'Y' AND activity_datetime BETWEEN ? AND ?
            GROUP BY 
                activity_type
            ORDER BY 
                count DESC
        """;
        
        Object[] typeParams = {startTime, endTime};
        List<Map<String, Object>> typeStats = executeQuery(typeSql, typeParams);
        reportData.put("typeStats", typeStats);
        
        return reportData;
    }
}