package com.polyu.cmms.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDataPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<com.polyu.cmms.model.Staff> staffList;
    
    public StaffDataPanel() {
        setLayout(new BorderLayout());
        
        // 初始化示例数据
        staffList = new ArrayList<>();
        initializeSampleData();
        
        // 创建表格模型
        String[] columnNames = {"ID", "员工号", "姓名", "年龄", "性别", "角色", "邮箱", "电话", "入职日期", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0);
        
        // 填充表格数据
        fillTableData();
        
        // 创建表格
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // 添加操作按钮面板
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加员工");
        JButton updateButton = new JButton("更新员工");
        JButton deleteButton = new JButton("删除员工");
        JButton batchAddButton = new JButton("批量添加");
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(batchAddButton);
        
        // 添加组件到面板
        add(new JLabel("员工信息管理", JLabel.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void initializeSampleData() {
        // 添加一些示例数据
        staffList.add(new com.polyu.cmms.model.Staff(1, "EMP001", "张三", "张", 35, "男", "执行官", 
                "zhangsan@example.com", "13800138001", new java.util.Date(), true, "总负责人"));
        staffList.add(new com.polyu.cmms.model.Staff(2, "EMP002", "李四", "李", 42, "男", "中层管理", 
                "lisi@example.com", "13800138002", new java.util.Date(), true, "建筑物管理"));
        staffList.add(new com.polyu.cmms.model.Staff(3, "EMP003", "王五", "王", 28, "男", "基层工人", 
                "wangwu@example.com", "13800138003", new java.util.Date(), true, "日常清洁"));
    }
    
    private void fillTableData() {
        tableModel.setRowCount(0); // 清空表格
        
        for (com.polyu.cmms.model.Staff staff : staffList) {
            Object[] row = {
                staff.getStaffId(),
                staff.getStaffNumber(),
                staff.getLastName() + staff.getFirstName(),
                staff.getAge(),
                staff.getGender(),
                staff.getRole(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getHireDate(),
                staff.isActiveFlag() ? "活跃" : "非活跃"
            };
            tableModel.addRow(row);
        }
    }
}