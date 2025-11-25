package com.polyu.cmms.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QueryReportPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public QueryReportPanel() {
        setLayout(new BorderLayout());
        
        // 左侧导航面板
        JPanel navigationPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        navigationPanel.setBorder(BorderFactory.createTitledBorder("查询类型"));
        
        // 创建导航按钮
        JButton sqlQueryButton = new JButton("SQL查询");
        JButton buildingActivityButton = new JButton("建筑物活动查询");
        JButton staffActivityButton = new JButton("员工活动查询");
        JButton chemicalUsageButton = new JButton("化学物质使用查询");
        
        // 添加按钮监听器
        sqlQueryButton.addActionListener(new NavigationListener("sql"));
        buildingActivityButton.addActionListener(new NavigationListener("buildingActivity"));
        staffActivityButton.addActionListener(new NavigationListener("staffActivity"));
        chemicalUsageButton.addActionListener(new NavigationListener("chemicalUsage"));
        
        // 添加按钮到导航面板
        navigationPanel.add(sqlQueryButton);
        navigationPanel.add(buildingActivityButton);
        navigationPanel.add(staffActivityButton);
        navigationPanel.add(chemicalUsageButton);
        
        // 创建内容面板，使用CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // 添加各个查询面板
        contentPanel.add(new SqlQueryPanel(), "sql");
        contentPanel.add(new BuildingActivityPanel(), "buildingActivity");
        contentPanel.add(new GenericQueryPanel("员工活动查询"), "staffActivity");
        contentPanel.add(new GenericQueryPanel("化学物质使用查询"), "chemicalUsage");
        
        // 添加面板到主面板
        add(navigationPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private class NavigationListener implements ActionListener {
        private String panelName;
        
        public NavigationListener(String panelName) {
            this.panelName = panelName;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.show(contentPanel, panelName);
        }
    }
    
    // SQL查询面板
    private class SqlQueryPanel extends JPanel {
        public SqlQueryPanel() {
            setLayout(new BorderLayout());
            
            // SQL输入区域
            JTextArea sqlTextArea = new JTextArea(10, 50);
            JScrollPane scrollPane = new JScrollPane(sqlTextArea);
            
            // 结果区域
            JTextArea resultTextArea = new JTextArea(15, 50);
            JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
            resultTextArea.setEditable(false);
            
            // 按钮面板
            JPanel buttonPanel = new JPanel();
            JButton executeButton = new JButton("执行查询");
            JButton clearButton = new JButton("清空");
            
            buttonPanel.add(executeButton);
            buttonPanel.add(clearButton);
            
            // 添加组件到面板
            add(new JLabel("SQL查询", JLabel.CENTER), BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(resultScrollPane, BorderLayout.SOUTH);
            add(buttonPanel, BorderLayout.EAST);
        }
    }
    
    // 建筑物活动查询面板
    private class BuildingActivityPanel extends JPanel {
        public BuildingActivityPanel() {
            setLayout(new BorderLayout());
            
            // 查询条件面板
            JPanel criteriaPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            criteriaPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));
            
            criteriaPanel.add(new JLabel("建筑物:"));
            criteriaPanel.add(new JComboBox<>(new String[]{"全部", "办公楼", "实验室", "图书馆", "学生宿舍"}));
            
            criteriaPanel.add(new JLabel("开始日期:"));
            criteriaPanel.add(new JTextField());
            
            criteriaPanel.add(new JLabel("结束日期:"));
            criteriaPanel.add(new JTextField());
            
            criteriaPanel.add(new JLabel("是否含危险化学品:"));
            criteriaPanel.add(new JCheckBox());
            
            // 结果表格
            JTable table = new JTable();
            JScrollPane scrollPane = new JScrollPane(table);
            
            // 按钮面板
            JPanel buttonPanel = new JPanel();
            JButton queryButton = new JButton("查询");
            JButton exportButton = new JButton("导出结果");
            
            buttonPanel.add(queryButton);
            buttonPanel.add(exportButton);
            
            // 添加组件到面板
            add(new JLabel("建筑物活动查询", JLabel.CENTER), BorderLayout.NORTH);
            add(criteriaPanel, BorderLayout.WEST);
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }
    
    // 通用查询面板
    private class GenericQueryPanel extends JPanel {
        public GenericQueryPanel(String title) {
            setLayout(new BorderLayout());
            JLabel label = new JLabel(title, JLabel.CENTER);
            add(label, BorderLayout.CENTER);
        }
    }
}