package com.polyu.cmms.view;

import com.polyu.cmms.service.AuthService;
import com.polyu.cmms.util.HtmlLogger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    protected MainFrame parent;
    
    public LoginPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel("校园维护与管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // 登录表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("用户名:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("密码:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("登录");
        JButton resetButton = new JButton("重置");
        
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        
        // 添加登录监听器
        loginButton.addActionListener(new LoginListener());
        resetButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });
        
        // 添加面板到主面板
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginPanel.this, "用户名和密码不能为空", "登录失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            AuthService authService = AuthService.getInstance();
            try {
                if (authService.login(username, password)) {
                    // 登录成功，记录日志
                    HtmlLogger.logSuccess(authService.getCurrentUserId(), authService.getCurrentRole(), "用户登录", "用户" + username + "登录成功");
                    
                    // 显示主界面并根据权限调整
                    // 由于系统已取消登录功能，此处代码已不再使用
                } else {
                // 登录失败，记录日志
                HtmlLogger.logError(-1, "未知用户", "用户登录", "用户" + username + "登录失败：员工编号不存在或密码错误");
                JOptionPane.showMessageDialog(LoginPanel.this, "员工编号不存在或密码错误\n提示：用户名使用员工编号，密码固定为123", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
            } catch (Exception ex) {
                // 处理数据库异常
                HtmlLogger.logError(-1, "未知用户", "用户登录", "用户" + username + "登录失败：数据库错误 - " + ex.getMessage());
                JOptionPane.showMessageDialog(LoginPanel.this, "数据库错误：" + ex.getMessage(), "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}