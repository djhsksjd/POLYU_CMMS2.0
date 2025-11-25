package com.polyu.cmms.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataManagementPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public DataManagementPanel() {
        setLayout(new BorderLayout());
        
        // 左侧导航面板
        JPanel navigationPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        navigationPanel.setBorder(BorderFactory.createTitledBorder("数据实体"));
        
        // 创建导航按钮
        // JButton staffButton = new JButton("员工信息");
        JButton buildingButton = new JButton("建筑物信息");
        JButton roomButton = new JButton("房间信息");
        JButton activityButton = new JButton("活动信息");
        JButton companyButton = new JButton("外包公司");
        JButton chemicalButton = new JButton("化学物质");
        
        // 添加按钮监听器
        // staffButton.addActionListener(new NavigationListener("staff"));
        buildingButton.addActionListener(new NavigationListener("building"));
        roomButton.addActionListener(new NavigationListener("room"));
        activityButton.addActionListener(new NavigationListener("activity"));
        companyButton.addActionListener(new NavigationListener("company"));
        chemicalButton.addActionListener(new NavigationListener("chemical"));
        
        // 添加按钮到导航面板
        // navigationPanel.add(staffButton);
        navigationPanel.add(buildingButton);
        navigationPanel.add(roomButton);
        navigationPanel.add(activityButton);
        navigationPanel.add(companyButton);
        navigationPanel.add(chemicalButton);
        
        // 创建内容面板，使用CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // 添加各个数据实体的管理面板
        // contentPanel.add(new StaffDataPanel(), "staff");
        contentPanel.add(new GenericDataPanel("建筑物"), "building");
        contentPanel.add(new GenericDataPanel("房间"), "room");
        contentPanel.add(new GenericDataPanel("活动"), "activity");
        contentPanel.add(new GenericDataPanel("外包公司"), "company");
        contentPanel.add(new GenericDataPanel("化学物质"), "chemical");
        
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
    
    // 通用数据管理面板
    private class GenericDataPanel extends JPanel {
        public GenericDataPanel(String entityName) {
            setLayout(new BorderLayout());
            
            JLabel label = new JLabel("" + entityName + "数据管理界面", JLabel.CENTER);
            label.setFont(new Font("宋体", Font.PLAIN, 18));
            
            add(label, BorderLayout.CENTER);
            
            // 添加操作按钮面板
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("添加");
            JButton updateButton = new JButton("更新");
            JButton deleteButton = new JButton("删除");
            JButton batchAddButton = new JButton("批量添加");
            
            buttonPanel.add(addButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(batchAddButton);
            
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }
}