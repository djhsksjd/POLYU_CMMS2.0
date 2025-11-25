package com.polyu.cmms.view;

import com.polyu.cmms.model.Activity;
import com.polyu.cmms.service.AuthService;
import com.polyu.cmms.util.HtmlLogger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.sql.SQLException;

public class ActivityManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Activity> activityList;
    private AuthService authService;
    
    public ActivityManagementPanel() {
        authService = AuthService.getInstance();
        setLayout(new BorderLayout());
        
        try {
            // 初始化示例数据
            activityList = new ArrayList<>();
            initializeSampleData();
            
            // 创建表格模型
            String[] columnNames = {"活动ID", "类型", "标题", "状态", "日期", "预计停机时间", "危害等级"};
            tableModel = new DefaultTableModel(columnNames, 0);
            
            // 填充表格数据
            fillTableData();
            
            // 创建表格
            table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            
            // 添加操作按钮面板
            JPanel buttonPanel = new JPanel();
            
            // 根据权限显示不同按钮
            if (authService.hasPermission("MANAGE_ACTIVITY")) {
            JButton createButton = new JButton("创建活动");
            JButton assignButton = new JButton("分配活动");
            JButton updateButton = new JButton("更新状态");
            
            createButton.addActionListener(e -> createActivity());
            assignButton.addActionListener(e -> assignActivity());
            updateButton.addActionListener(e -> updateActivityStatus());
            
            buttonPanel.add(createButton);
            buttonPanel.add(assignButton);
            buttonPanel.add(updateButton);
        }
        
        JButton viewDetailsButton = new JButton("查看详情");
        viewDetailsButton.addActionListener(e -> viewActivityDetails());
        buttonPanel.add(viewDetailsButton);
        
        // 添加组件到面板
        add(new JLabel("活动管理", JLabel.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        } catch (SQLException ex) {
            // 处理数据库异常
            JOptionPane.showMessageDialog(this, "初始化活动管理面板时发生数据库错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            HtmlLogger.logError(authService.getCurrentUserId(), authService.getCurrentRole(), 
                    "活动面板初始化", "初始化失败: " + ex.getMessage());
        }
    }


    
    private void initializeSampleData() {
        // 添加一些示例数据
        Date date = new java.util.Date();
        Date expectedDowntime = new java.util.Date();

        activityList.add(new Activity(1, "清洁", "清理", "办公楼3楼清洁", "计划中", 
                date, expectedDowntime, 1, null, 1, null, "低"));
        activityList.add(new Activity(2, "维修", "修复", "实验室窗户维修", "进行中", 
                date, expectedDowntime, 8, 2, null, 2, "中"));
        activityList.add(new Activity(3, "紧急处理", "处理", "操场积水清理", "已完成", 
                date, expectedDowntime, 2, 3, 1, 2, "低"));
    }
    
    private void fillTableData() {
        tableModel.setRowCount(0); // 清空表格
        
        for (Activity activity : activityList) {
            Object[] row = {
                activity.getActivityId(),
                activity.getActivityType(),
                activity.getTitle(),
                activity.getStatus(),
                activity.getDate(),
                activity.getExpectedDowntime(),
                activity.getHazardLevel()
            };
            tableModel.addRow(row);
        }
    }
    
    private void createActivity() {
        // 记录创建活动操作
        HtmlLogger.logInfo(authService.getCurrentUserId(), authService.getCurrentRole(), "创建活动", "用户尝试创建新活动");
        JOptionPane.showMessageDialog(this, "创建活动功能待实现", "信息", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void assignActivity() {
        // 记录分配活动操作
        HtmlLogger.logInfo(authService.getCurrentUserId(), authService.getCurrentRole(), "分配活动", "用户尝试分配活动");
        JOptionPane.showMessageDialog(this, "分配活动功能待实现", "信息", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateActivityStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // 记录更新活动状态操作
            int activityId = (int) tableModel.getValueAt(selectedRow, 0);
            HtmlLogger.logInfo(authService.getCurrentUserId(), authService.getCurrentRole(), "更新活动状态", "用户尝试更新活动ID=" + activityId + "的状态");
            JOptionPane.showMessageDialog(this, "更新活动状态功能待实现", "信息", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "请先选择一个活动", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void viewActivityDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // 记录查看活动详情操作
            int activityId = (int) tableModel.getValueAt(selectedRow, 0);
            HtmlLogger.logInfo(authService.getCurrentUserId(), authService.getCurrentRole(), "查看活动详情", "用户查看活动ID=" + activityId + "的详情");
            JOptionPane.showMessageDialog(this, "查看活动详情功能待实现", "信息", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "请先选择一个活动", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
}