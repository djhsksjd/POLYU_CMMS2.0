package com.polyu.cmms.service;

import com.polyu.cmms.model.Staff;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 员工服务类，提供员工相关的数据访问功能
 */
public class StaffService extends BaseService {
    private static StaffService instance;
    
    // 单例模式
    private StaffService() {}
    
    public static synchronized StaffService getInstance() {
        if (instance == null) {
            instance = new StaffService();
        }
        return instance;
    }
    
    /**
     * 添加单条员工记录
     * @param staff 员工对象
     * @return 插入是否成功
     * @throws SQLException SQL异常
     */
    public boolean addStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO staff (staff_number, first_name, last_name, age, gender, role, email, phone, hire_date, responsibility, active_flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int result = executeUpdate(sql, 
            staff.getStaffNumber(), staff.getFirstName(), staff.getLastName(), 
            staff.getAge(), staff.getGender(), staff.getRole(), 
            staff.getEmail(), staff.getPhone(), staff.getHireDate(), 
            staff.getResponsibility(), "Y");
        return result > 0;
    }
    
    /**
     * 批量添加员工记录
     * @param staffList 员工列表
     * @return 插入是否成功
     * @throws SQLException SQL异常
     */
    public boolean batchAddStaff(List<Staff> staffList) throws SQLException {
        String sql = "INSERT INTO staff (staff_number, first_name, last_name, age, gender, role, email, phone, hire_date, responsibility, active_flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        List<Object[]> paramsList = new java.util.ArrayList<>();
        for (Staff staff : staffList) {
            Object[] params = {
                staff.getStaffNumber(), staff.getFirstName(), staff.getLastName(),
                staff.getAge(), staff.getGender(), staff.getRole(),
                staff.getEmail(), staff.getPhone(), staff.getHireDate(),
                staff.getResponsibility(), "Y"
            };
            paramsList.add(params);
        }
        
        int[] results = executeBatch(sql, paramsList);
        return results.length == staffList.size();
    }
    
    /**
     * 更新员工信息
     * @param staffId 员工ID
     * @param updates 更新的字段和值
     * @return 更新是否成功
     * @throws SQLException SQL异常
     */
    public boolean updateStaff(int staffId, Map<String, Object> updates) throws SQLException {
        if (updates.isEmpty()) {
            return true;
        }
        
        StringBuilder sqlBuilder = new StringBuilder("UPDATE staff SET ");
        List<Object> params = new java.util.ArrayList<>();
        
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            sqlBuilder.append(entry.getKey()).append(" = ?, ");
            params.add(entry.getValue());
        }
        
        sqlBuilder.setLength(sqlBuilder.length() - 2); // 移除最后一个逗号和空格
        sqlBuilder.append(" WHERE staff_id = ?");
        params.add(staffId);
        
        int result = executeUpdate(sqlBuilder.toString(), params.toArray());
        return result > 0;
    }
    
    /**
     * 删除员工
     * @param staffId 员工ID
     * @return 删除是否成功
     * @throws SQLException SQL异常
     */
    public boolean deleteStaff(int staffId) throws SQLException {
        String sql = "UPDATE staff SET active_flag = 'N' WHERE staff_id = ?";
        int result = executeUpdate(sql, staffId);
        return result > 0;
    }
    
    /**
     * 按条件查询员工
     * @param conditions 查询条件
     * @return 员工列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> queryStaff(Map<String, Object> conditions) throws SQLException {
        // 直接构建SQL，避免动态列名问题
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM staff WHERE active_flag = 'Y'");
        List<Object> params = new java.util.ArrayList<>();
        
        if (conditions.containsKey("activeFlag")) {
            // 已经在基础SQL中设置了active_flag = 'Y'
        }
        
        if (conditions.containsKey("roleId") && conditions.get("roleId") instanceof List) {
            List<?> roleIdList = (List<?>) conditions.get("roleId");
            if (!roleIdList.isEmpty()) {
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < roleIdList.size(); i++) {
                    if (i > 0) {
                        placeholders.append(", ");
                    }
                    placeholders.append("?");
                    params.add(roleIdList.get(i));
                }
                sqlBuilder.append(" AND role_id IN (").append(placeholders).append(")");
            }
        }
        
        // 处理其他可能的条件
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            String key = entry.getKey();
            if ("activeFlag".equals(key) || "roleId".equals(key)) {
                continue; // 已经处理过的条件
            }
            // 其他条件暂时不处理，避免引入新的问题
        }
        
        return executeQuery(sqlBuilder.toString(), params.toArray());
    }
    
    /**
     * 分页查询员工
     * @param page 页码
     * @param pageSize 每页大小
     * @param conditions 查询条件
     * @return 分页查询结果，包含data、totalPages和total字段
     * @throws SQLException SQL异常
     */
    // 将Java驼峰命名转换为数据库下划线命名
    private String convertToDbColumn(String javaName) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < javaName.length(); i++) {
            char c = javaName.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append('_');
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }
    
    public Map<String, Object> getStaffByPage(int page, int pageSize, Map<String, Object> conditions, String sortField, String sortOrder) throws SQLException {
        // 获取总数
        int total = getStaffCount(conditions);
        int totalPages = (total + pageSize - 1) / pageSize; // 计算总页数
        
        // 查询数据
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM staff WHERE active_flag = 'Y'");
        List<Object> params = new java.util.ArrayList<>();
        
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            // 将Java驼峰命名转换为数据库下划线命名
            String dbColumn = convertToDbColumn(entry.getKey());
            sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
            params.add(entry.getValue());
        }
        
        // 添加排序
        if (sortField != null && !sortField.isEmpty()) {
            // 对排序字段也进行列名转换
            String dbSortField = convertToDbColumn(sortField);
            sqlBuilder.append(" ORDER BY ").append(dbSortField);
            if (sortOrder != null && "desc".equalsIgnoreCase(sortOrder)) {
                sqlBuilder.append(" DESC");
            } else {
                sqlBuilder.append(" ASC");
            }
        }
        
        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        List<Map<String, Object>> staffList = executeQuery(sqlBuilder.toString(), params.toArray());
        
        // 构建返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("data", staffList);
        result.put("totalPages", totalPages);
        result.put("total", total);
        
        return result;
    }
    
    public List<Map<String, Object>> queryStaffByPage(int page, int pageSize, Map<String, Object> conditions) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM staff WHERE active_flag = 'Y'");
        List<Object> params = new java.util.ArrayList<>();
        
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            // 将Java驼峰命名转换为数据库下划线命名
            String dbColumn = convertToDbColumn(entry.getKey());
            sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
            params.add(entry.getValue());
        }
        
        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        return executeQuery(sqlBuilder.toString(), params.toArray());
    }
    
    /**
     * 获取员工总数
     * @param conditions 查询条件
     * @return 员工总数
     * @throws SQLException SQL异常
     */
    public int getStaffCount(Map<String, Object> conditions) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) as count FROM staff WHERE active_flag = 'Y'");
        List<Object> params = new java.util.ArrayList<>();
        
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            // 将Java驼峰命名转换为数据库下划线命名
            String dbColumn = convertToDbColumn(entry.getKey());
            sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
            params.add(entry.getValue());
        }
        
        List<Map<String, Object>> results = executeQuery(sqlBuilder.toString(), params.toArray());
        if (results == null || results.isEmpty()) {
            return 0;
        }
        
        Map<String, Object> firstRow = results.get(0);
        if (firstRow == null) {
            return 0;
        }
        
        Object countObj = firstRow.get("count");
        if (countObj instanceof Number) {
            return ((Number) countObj).intValue();
        } else if (countObj != null) {
            try {
                return Integer.parseInt(countObj.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * 按角色统计员工数量
     * @return 角色统计数据，key为角色ID，value为数量
     * @throws SQLException SQL异常
     */
    public Map<Integer, Integer> getStaffCountByRole() throws SQLException {
        String sql = "SELECT role_id, COUNT(*) as count FROM staff WHERE active_flag = 'Y' GROUP BY role_id";
        List<Map<String, Object>> results = executeQuery(sql);
        
        Map<Integer, Integer> roleCountMap = new java.util.HashMap<>();
        for (Map<String, Object> result : results) {
            // 安全获取整数值，避免空指针异常
            Integer roleId = null;
            Integer count = null;
            
            // 尝试从不同的键名获取roleId（考虑数据库列名可能为role_id）
            Object roleIdObj = result.get("roleId");
            if (roleIdObj == null) {
                roleIdObj = result.get("role_id");
            }
            
            // 尝试从不同的键名获取count
            Object countObj = result.get("count");
            
            // 安全转换为Integer
            if (roleIdObj instanceof Number) {
                roleId = ((Number) roleIdObj).intValue();
            } else if (roleIdObj != null) {
                try {
                    roleId = Integer.parseInt(roleIdObj.toString());
                } catch (NumberFormatException e) {
                    roleId = null;
                }
            }
            
            if (countObj instanceof Number) {
                count = ((Number) countObj).intValue();
            } else if (countObj != null) {
                try {
                    count = Integer.parseInt(countObj.toString());
                } catch (NumberFormatException e) {
                    count = null;
                }
            }
            
            // 只有当两个值都有效时才添加到映射中
            if (roleId != null && count != null) {
                roleCountMap.put(roleId, count);
            }
        }
        return roleCountMap;
    }
    
    /**
     * 按性别统计员工数量
     * @return 性别统计数据，key为性别，value为数量
     * @throws SQLException SQL异常
     */
    public Map<String, Integer> getStaffCountByGender() throws SQLException {
        String sql = "SELECT gender, COUNT(*) as count FROM staff WHERE active_flag = 'Y' GROUP BY gender";
        List<Map<String, Object>> results = executeQuery(sql);
        
        Map<String, Integer> genderCountMap = new java.util.HashMap<>();
        for (Map<String, Object> result : results) {
            // 安全获取gender值
            String gender = null;
            Object genderObj = result.get("gender");
            if (genderObj instanceof String) {
                gender = (String) genderObj;
            } else if (genderObj != null) {
                gender = genderObj.toString();
            }
            
            // 安全获取count值
            Integer count = null;
            Object countObj = result.get("count");
            if (countObj instanceof Number) {
                count = ((Number) countObj).intValue();
            } else if (countObj != null) {
                try {
                    count = Integer.parseInt(countObj.toString());
                } catch (NumberFormatException e) {
                    count = null;
                }
            }
            
            // 只有当两个值都有效时才添加到映射中
            if (gender != null && count != null) {
                genderCountMap.put(gender, count);
            }
        }
        return genderCountMap;
    }
    
    /**
     * 获取总员工数量
     * @return 员工总数
     * @throws SQLException SQL异常
     */
    public int getTotalStaffCount() throws SQLException {
        return getStaffCount(new java.util.HashMap<>());
    }
    
    /**
     * 根据ID获取员工信息
     * @param staffId 员工ID
     * @return 员工信息
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getStaffById(int staffId) throws SQLException {
        String sql = "SELECT * FROM staff WHERE staff_id = ? AND active_flag = 'Y'";
        List<Map<String, Object>> results = executeQuery(sql, staffId);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
    
    /**
     * 创建员工（匹配StaffManagementPanel中的调用）
     * @return 创建是否成功
     * @throws SQLException SQL异常
     */
    public boolean createStaff(String staffNumber, String firstName, String lastName, String gender, java.util.Date dateOfBirth, 
                              String phone, String email, java.util.Date hireDate, int roleId, 
                              String emergencyContact, String emergencyPhone) throws SQLException {
        // 构建SQL语句，只包含staff表中存在的字段
        String sql = "INSERT INTO staff (staff_number, first_name, last_name, gender, date_of_birth, phone, email, " +
                    "hire_date, role_id, emergency_contact, emergency_phone, active_flag) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        int result = executeUpdate(sql, 
            staffNumber, firstName, lastName, gender, dateOfBirth, 
            phone, email, hireDate, roleId, emergencyContact, emergencyPhone, "Y"); // 默认设置为激活状态
        
        return result > 0;
    }
    
    /**
     * 更新员工信息（匹配StaffManagementPanel的调用）
     * @param staffId 员工ID
     * @param 其他参数 员工信息字段
     * @return 更新是否成功
     * @throws SQLException SQL异常
     */
    public boolean updateStaff(int staffId, String staffNumber, String firstName, String lastName, String gender, 
                              java.util.Date dateOfBirth, String phone, String email, java.util.Date hireDate, 
                              int roleId, String emergencyContact, String emergencyPhone, String activeFlag) throws SQLException {
        // 构建SQL语句，只包含staff表中存在的字段
        String sql = "UPDATE staff SET staff_number = ?, first_name = ?, last_name = ?, gender = ?, " +
                    "date_of_birth = ?, phone = ?, email = ?, hire_date = ?, " +
                    "role_id = ?, emergency_contact = ?, emergency_phone = ?, active_flag = ? " +
                    "WHERE staff_id = ?";
        
        int result = executeUpdate(sql, 
            staffNumber, firstName, lastName, gender, dateOfBirth, 
            phone, email, hireDate, roleId, emergencyContact, emergencyPhone, activeFlag, staffId);
        
        return result > 0;
    }
}