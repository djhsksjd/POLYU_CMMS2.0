package com.polyu.cmms.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HtmlLogger {
    private static final String LOG_DIR = "logs";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String HTML_HEADER = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>系统操作日志 - %s</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                h1 { color: #333; }
                table { border-collapse: collapse; width: 100%%; margin-top: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #f2f2f2; }
                tr:nth-child(even) { background-color: #f9f9f9; }
                .ERROR { color: red; }
                .WARNING { color: orange; }
                .INFO { color: blue; }
                .SUCCESS { color: green; }
            </style>
        </head>
        <body>
            <h1>系统操作日志 - %s</h1>
            <table>
                <tr>
                    <th>时间</th>
                    <th>用户ID</th>
                    <th>角色</th>
                    <th>操作类型</th>
                    <th>操作描述</th>
                    <th>状态</th>
                    <th>IP地址</th>
                </tr>
    """;
    private static final String HTML_FOOTER = """
            </table>
        </body>
        </html>
    """;
    
    public enum LogLevel {
        INFO, WARNING, ERROR, SUCCESS
    }
    
    // 记录日志
    public static void log(int userId, String role, String operationType, String description, LogLevel level, String ipAddress) {
        String dateStr = DATE_FORMAT.format(new Date());
        String logFileName = LOG_DIR + "/log_" + dateStr + ".html";
        
        // 确保日志目录存在
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        File logFile = new File(logFileName);
        boolean isNewFile = !logFile.exists();
        
        try (FileWriter writer = new FileWriter(logFile, true)) {
            if (isNewFile) {
                // 新文件需要写入HTML头部
                writer.write(String.format(HTML_HEADER, dateStr, dateStr));
            } else {
                // 已有文件需要先移除HTML尾部，然后添加新记录，最后重新添加尾部
                String content = new String(java.nio.file.Files.readAllBytes(logFile.toPath()));
                int footerIndex = content.lastIndexOf(HTML_FOOTER);
                if (footerIndex > 0) {
                    java.nio.file.Files.write(logFile.toPath(), content.substring(0, footerIndex).getBytes());
                }
            }
            
            // 写入日志记录
            String timestamp = TIME_FORMAT.format(new Date());
            String logRow = String.format("""
                <tr>
                    <td>%s</td>
                    <td>%d</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td class="%s">%s</td>
                    <td>%s</td>
                </tr>
            """, timestamp, userId, role, operationType, description, level.name(), level.name(), ipAddress);
            
            writer.write(logRow);
            writer.write(HTML_FOOTER);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // 便捷方法：记录成功操作
    public static void logSuccess(int userId, String role, String operationType, String description) {
        log(userId, role, operationType, description, LogLevel.SUCCESS, "127.0.0.1");
    }
    
    // 便捷方法：记录错误操作
    public static void logError(int userId, String role, String operationType, String description) {
        log(userId, role, operationType, description, LogLevel.ERROR, "127.0.0.1");
    }
    
    // 便捷方法：记录信息
    public static void logInfo(int userId, String role, String operationType, String description) {
        log(userId, role, operationType, description, LogLevel.INFO, "127.0.0.1");
    }
    
    // 便捷方法：记录警告
    public static void logWarning(int userId, String role, String operationType, String description) {
        log(userId, role, operationType, description, LogLevel.WARNING, "127.0.0.1");
    }
}