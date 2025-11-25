package com.polyu.cmms.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportGenerationPanel extends JPanel {
    private JTextArea reportArea;
    
    public ReportGenerationPanel() {
        setLayout(new BorderLayout());
        
        // 报表类型选择面板
        JPanel selectionPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("报表类型"));
        
        JButton workerReportButton = new JButton("工人活动分布报表");
        JButton activityTypeReportButton = new JButton("活动类型分布报表");
        JButton buildingMaintenanceReportButton = new JButton("建筑物维护报表");
        JButton chemicalUsageReportButton = new JButton("化学品使用报表");
        
        workerReportButton.addActionListener(new ReportListener("worker"));
        activityTypeReportButton.addActionListener(new ReportListener("activityType"));
        buildingMaintenanceReportButton.addActionListener(new ReportListener("buildingMaintenance"));
        chemicalUsageReportButton.addActionListener(new ReportListener("chemicalUsage"));
        
        selectionPanel.add(workerReportButton);
        selectionPanel.add(activityTypeReportButton);
        selectionPanel.add(buildingMaintenanceReportButton);
        selectionPanel.add(chemicalUsageReportButton);
        
        // 报表显示区域
        reportArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportArea.setEditable(false);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel();
        JButton exportButton = new JButton("导出报表");
        JButton printButton = new JButton("打印报表");
        
        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);
        
        // 添加组件到面板
        add(new JLabel("报表生成", JLabel.CENTER), BorderLayout.NORTH);
        add(selectionPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private class ReportListener implements ActionListener {
        private String reportType;
        
        public ReportListener(String reportType) {
            this.reportType = reportType;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            generateReport(reportType);
        }
    }
    
    private void generateReport(String reportType) {
        StringBuilder report = new StringBuilder();
        report.append("校园维护与管理系统 - 报表\n\n");
        report.append("生成日期: " + new java.util.Date() + "\n\n");
        
        switch (reportType) {
            case "worker":
                report.append("=== 工人活动分布报表 ===\n\n");
                report.append("1. 张三 - 日常清洁: 15次\n");
                report.append("2. 李四 - 维修工作: 8次\n");
                report.append("3. 王五 - 紧急处理: 5次\n");
                break;
            case "activityType":
                report.append("=== 活动类型分布报表 ===\n\n");
                report.append("1. 清洁: 35次\n");
                report.append("2. 维修: 20次\n");
                report.append("3. 紧急处理: 10次\n");
                report.append("4. 预防性维护: 15次\n");
                break;
            case "buildingMaintenance":
                report.append("=== 建筑物维护报表 ===\n\n");
                report.append("1. 办公楼: 25次维护活动\n");
                report.append("2. 实验室: 18次维护活动\n");
                report.append("3. 图书馆: 12次维护活动\n");
                report.append("4. 学生宿舍: 30次维护活动\n");
                break;
            case "chemicalUsage":
                report.append("=== 化学品使用报表 ===\n\n");
                report.append("1. 清洁剂A: 使用20次，用于办公楼和图书馆\n");
                report.append("2. 消毒剂B: 使用15次，用于学生宿舍\n");
                report.append("3. 润滑油C: 使用8次，用于各类机械设备\n");
                break;
        }
        
        reportArea.setText(report.toString());
    }
}