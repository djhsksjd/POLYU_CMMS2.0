package com.polyu.cmms.test;

import com.polyu.cmms.model.Activity;
import com.polyu.cmms.service.ActivityService;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ActivityService测试类，用于验证活动相关功能
 */
public class ActivityServiceTest {

    // 用于日期字符串和Date对象之间的转换
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("============= 开始执行 ActivityService 测试 =============");
        // 调用各个测试方法
        testQueryActivitiesByPage();
        testQueryActivitiesWithConditions();
        testQueryCleaningActivities();
        testCountWorkersByAreaAndActivity();
        System.out.println("============= ActivityService 测试执行完毕 =============");
    }

    /**
     * 测试分页查询活动
     */
    public static void testQueryActivitiesByPage() {
        System.out.println("\n[测试1] 分页查询所有活动...");
        ActivityService activityService = ActivityService.getInstance();

        try {
            // 查询第1页，每页10条记录
            int page = 1;
            int pageSize = 10;
            List<Map<String, Object>> activities = activityService.queryActivitiesByPage(page, pageSize, null);

            System.out.println("第 " + page + " 页活动数据:");
            printActivities(activities);

        } catch (SQLException e) {
            System.err.println("分页查询活动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试带条件查询活动
     */
    public static void testQueryActivitiesWithConditions() {
        System.out.println("\n[测试2] 根据条件查询活动...");
        ActivityService activityService = ActivityService.getInstance();

        try {
            // 构建查询条件：例如查询状态为 'scheduled' 的活动
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("status", "scheduled"); // 注意：这里使用Java驼峰命名

            List<Map<String, Object>> activities = activityService.queryActivities(conditions);

            System.out.println("状态为 'scheduled' 的活动数量: " + activities.size());
            printActivities(activities);

        } catch (SQLException e) {
            System.err.println("条件查询活动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试查询清洁类活动
     */
    public static void testQueryCleaningActivities() {
        System.out.println("\n[测试3] 查询特定时间段和类型的清洁活动...");
        ActivityService activityService = ActivityService.getInstance();

        try {
            // 准备测试数据
            Date startTime = sdf.parse("2024-01-01 00:00:00");
            Date endTime = sdf.parse("2024-12-31 23:59:59");
            Integer buildingId = 1; // 假设存在ID为1的建筑物
            List<String> activityTypes = Arrays.asList("cleaning", "deep_cleaning"); // 假设这两种活动类型

            List<Map<String, Object>> activities = activityService.queryCleaningActivities(startTime, endTime, buildingId, activityTypes);

            System.out.println("在 " + sdf.format(startTime) + " 至 " + sdf.format(endTime) + " 期间，");
            System.out.println("建筑物 " + buildingId + " 的 '" + activityTypes + "' 类型活动数量: " + activities.size());
            printActivities(activities);

        } catch (ParseException e) {
            System.err.println("日期格式解析错误: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("查询清洁活动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试统计各区域活动的工人参与数量
     */
    public static void testCountWorkersByAreaAndActivity() {
        System.out.println("\n[测试4] 统计各区域各类活动的工人参与数量...");
        ActivityService activityService = ActivityService.getInstance();

        try {
            // 准备一个时间范围，例如过去一年
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            Date startTime = cal.getTime();
            Date endTime = new Date(); // 当前时间

            List<Map<String, Object>> countResults = activityService.countWorkersByAreaAndActivity(startTime, endTime);

            System.out.println("统计 " + sdf.format(startTime) + " 至 " + sdf.format(endTime) + " 期间的数据:");
            if (countResults.isEmpty()) {
                System.out.println("没有找到符合条件的统计数据。");
            } else {
                System.out.printf("%-20s %-20s %s%n", "活动类型", "区域名称", "参与工人数");
                System.out.println("------------------------------------------------------------");
                for (Map<String, Object> result : countResults) {
                    String activityType = (String) result.get("activityType");
                    String areaName = (String) result.get("area_name");
                    Number workerCount = (Number) result.get("worker_count");
                    System.out.printf("%-20s %-20s %d%n", activityType, areaName, workerCount.intValue());
                }
            }

        } catch (SQLException e) {
            System.err.println("统计工人参与数量失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 辅助方法：打印活动列表信息
     * @param activities 活动数据列表
     */
    private static void printActivities(List<Map<String, Object>> activities) {
        if (activities == null || activities.isEmpty()) {
            System.out.println(" -> 未查询到任何活动。");
            return;
        }

        System.out.println(" -> 查询到 " + activities.size() + " 条活动记录:");
        for (Map<String, Object> activity : activities) {
            System.out.println("    [ID: " + activity.get("activityId") +
                    ", 标题: " + activity.get("title") +
                    ", 类型: " + activity.get("activityType") +
                    ", 状态: " + activity.get("status") +
                    ", 时间: " + activity.get("activity_datetime") + "]");
        }
    }
}