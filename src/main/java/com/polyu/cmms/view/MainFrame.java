package com.polyu.cmms.view;

import com.polyu.cmms.service.AuthService;
import com.polyu.cmms.util.HtmlLogger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    
    public MainFrame() {
        setTitle("校园维护与管理系统");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 自定义关闭操作
        setLocationRelativeTo(null);
        
        // 添加窗口关闭监听器
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
        
        // 直接创建并显示主界面
        createMainInterface();
        add(tabbedPane);
    }
    
    // 创建主界面
    private void createMainInterface() {
        tabbedPane = new JTabbedPane();
        
        // 添加各个功能面板（根据权限）
        AuthService authService = AuthService.getInstance();
        
        try {
            // 数据管理面板 - 需要MANAGE_STAFF权限
            if (authService.hasPermission("MANAGE_STAFF")) {
                tabbedPane.addTab("数据管理", new DataManagementPanel());
            }
            
            // 活动管理面板 - 需要MANAGE_ACTIVITY权限
            if (authService.hasPermission("MANAGE_ACTIVITY")) {
                tabbedPane.addTab("活动管理", new ActivityManagementPanel());
            } else if (authService.hasPermission("VIEW_ACTIVITY")) {
                // 如果只有查看权限，添加受限版本
                tabbedPane.addTab("活动查看", new ActivityViewPanel());
            }
            
            // 人员管理面板 - 需要MANAGE_STAFF权限
            if (authService.hasPermission("MANAGE_STAFF")) {
                tabbedPane.addTab("人员管理", new StaffManagementPanel());
            } else if (authService.hasPermission("VIEW_STAFF")) {
                // 如果只有查看权限，添加受限版本
                tabbedPane.addTab("人员查看", new StaffViewPanel());
            }
            
            // 查询与报告面板 - 需要VIEW_REPORT权限
            if (authService.hasPermission("VIEW_REPORT")) {
                tabbedPane.addTab("查询与报告", new QueryReportPanel());
            }
            
            // 报表生成面板 - 需要GENERATE_REPORT权限
            if (authService.hasPermission("GENERATE_REPORT")) {
                tabbedPane.addTab("报表生成", new ReportGenerationPanel());
            }
        } catch (Exception ex) {
            // 处理数据库异常
            JOptionPane.showMessageDialog(this, "权限检查时发生数据库错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            // 确保userId不为null，避免空指针异常
            Integer userId = authService.getCurrentUserId();
            String role = authService.getCurrentRole();
            HtmlLogger.logError(userId != null ? userId : -1, role != null ? role : "未知角色", 
                    "权限检查", "权限检查失败: " + ex.getMessage());
        }
        
        // 添加用户信息菜单
        JMenuBar menuBar = new JMenuBar();
        JMenu userMenu = new JMenu("用户: " + authService.getCurrentRole());
        menuBar.add(userMenu);
        setJMenuBar(menuBar);
    }
    

    
    // 处理退出
    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要退出系统吗？", "退出确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AuthService authService = AuthService.getInstance();
            // 记录系统退出日志
            HtmlLogger.logInfo(authService.getCurrentUserId(), authService.getCurrentRole(), "系统退出", "用户退出系统");
            System.exit(0);
        }
    }
    
    // 为只有查看权限的用户创建简化的活动查看面板
    private class ActivityViewPanel extends JPanel {
        public ActivityViewPanel() {
            setLayout(new BorderLayout());
            JLabel label = new JLabel("活动信息查看（查看权限）", JLabel.CENTER);
            add(label, BorderLayout.CENTER);
        }
    }
    
    // 为只有查看权限的用户创建简化的人员查看面板
    private class StaffViewPanel extends JPanel {
        public StaffViewPanel() {
            setLayout(new BorderLayout());
            JLabel label = new JLabel("人员信息查看（查看权限）", JLabel.CENTER);
            add(label, BorderLayout.CENTER);
        }
    }
}