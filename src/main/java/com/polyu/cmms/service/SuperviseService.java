package com.polyu.cmms.service;

import com.polyu.cmms.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class SuperviseService extends BaseService {
    
    /**
     * 将Java驼峰命名转换为数据库下划线命名
     * @param javaName Java属性名（驼峰命名）
     * @return 数据库列名（下划线命名）
     */
    private String convertToDbColumn(String javaName) {
        return javaName.replaceAll("([A-Z])", "_$1").toLowerCase();
    }
    private static SuperviseService instance;
    
    private SuperviseService() {}
    
    public static SuperviseService getInstance() {
        if (instance == null) {
            instance = new SuperviseService();
        }
        return instance;
    }
    
    /**
     * 从员工信息中获取角色ID
     */
    private int getRoleIdFromStaffInfo(Map<String, Object> staffInfo) {
        // 尝试多种可能的字段名格式
        Object roleIdObj = null;
        // 优先检查驼峰命名，因为BaseService的executeQuery方法会将数据库列名从下划线命名转换为Java驼峰命名
        if (staffInfo.containsKey("roleId")) {
            roleIdObj = staffInfo.get("roleId");
        } else if (staffInfo.containsKey("role_id")) {
            roleIdObj = staffInfo.get("roleId");
        }
        
        // 添加空值检查
        if (roleIdObj == null) {
            throw new IllegalArgumentException("员工信息中角色ID为空");
        }
        
        if (roleIdObj instanceof Number) {
            return ((Number) roleIdObj).intValue();
        } else if (roleIdObj instanceof String) {
            // 尝试将字符串转换为数字
            try {
                return Integer.parseInt((String) roleIdObj);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("员工信息中角色ID格式无效: " + roleIdObj);
            }
        }
        throw new IllegalArgumentException("无法从员工信息中获取有效的角色ID: " + roleIdObj);
    }
    
    /**
     * 验证角色层级关系是否符合三层结构规则
     * 规则：
     * - 行政官(role_id=1)可以监督中层经理(role_id=2)
     * - 中层经理(role_id=2)可以监督基层员工(role_id=3)
     * - 其他组合都不允许
     */
    private void validateRoleHierarchy(int supervisorRoleId, int subordinateRoleId) {
        if (supervisorRoleId == 1 && subordinateRoleId == 2) {
            // 行政官可以监督中层经理，符合规则
            return;
        } else if (supervisorRoleId == 2 && subordinateRoleId == 3) {
            // 中层经理可以监督基层员工，符合规则
            return;
        }
        
        // 不符合三层结构规则
        String supervisorRoleText = getRoleText(supervisorRoleId);
        String subordinateRoleText = getRoleText(subordinateRoleId);
        throw new IllegalArgumentException(
            "监督关系不符合三层结构规则：" + 
            supervisorRoleText + "(role_id=" + supervisorRoleId + ") 不能监督 " + 
            subordinateRoleText + "(role_id=" + subordinateRoleId + ")。\n" +
            "允许的监督关系：行政官(1)→中层经理(2)，中层经理(2)→基层员工(3)"
        );
    }
    
    /**
     * 根据角色ID获取角色文本描述
     */
    private String getRoleText(int roleId) {
        switch (roleId) {
            case 1:
                return "行政官";
            case 2:
                return "中层经理";
            case 3:
                return "基层员工";
            default:
                return "未知角色";
        }
    }
    
    // 创建监督关系，遵循三层结构规则
    public boolean createSupervise(int supervisorStaffId, int subordinateStaffId, Date startDate, Date endDate) {
        // 参数验证
        if (supervisorStaffId <= 0) {
            throw new IllegalArgumentException("上级员工ID必须大于0");
        }
        if (subordinateStaffId <= 0) {
            throw new IllegalArgumentException("下级员工ID必须大于0");
        }
        if (supervisorStaffId == subordinateStaffId) {
            throw new IllegalArgumentException("上级员工不能是自己");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("开始日期不能为空");
        }
        if (endDate != null && endDate.before(startDate)) {
            throw new IllegalArgumentException("结束日期不能早于开始日期");
        }
        
        // 验证员工是否存在并检查角色层级关系
        StaffService staffService = StaffService.getInstance();
        try {
            Map<String, Object> supervisorInfo = staffService.getStaffById(supervisorStaffId);
            if (supervisorInfo == null) {
                throw new IllegalArgumentException("指定的上级员工不存在");
            }
            
            Map<String, Object> subordinateInfo = staffService.getStaffById(subordinateStaffId);
            if (subordinateInfo == null) {
                throw new IllegalArgumentException("指定的下级员工不存在");
            }
            
            // 获取角色ID，遵循三层结构规则
            int supervisorRoleId = getRoleIdFromStaffInfo(supervisorInfo);
            int subordinateRoleId = getRoleIdFromStaffInfo(subordinateInfo);
            
            // 检查监督关系是否符合三层结构规则
            validateRoleHierarchy(supervisorRoleId, subordinateRoleId);
            
            System.out.println("创建监督关系: 上级ID=" + supervisorStaffId + "(角色ID=" + supervisorRoleId + "), " +
                             "下级ID=" + subordinateStaffId + "(角色ID=" + subordinateRoleId + ")");
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("验证员工信息失败", e);
        }
        
        // 检查是否已存在相同的监督关系（不考虑end_date）
        String checkSql = "SELECT COUNT(*) FROM supervise WHERE supervisor_staff_id = ? AND subordinate_staff_id = ? AND (end_date IS NULL OR end_date >= ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
            checkPstmt.setInt(1, supervisorStaffId);
            checkPstmt.setInt(2, subordinateStaffId);
            checkPstmt.setDate(3, new java.sql.Date(startDate.getTime()));
            
            try (ResultSet rs = checkPstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException("指定的监督关系已存在或时间重叠");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("检查监督关系失败", e);
        }
        
        String sql = "INSERT INTO supervise (supervisor_staff_id, subordinate_staff_id, start_date, end_date) VALUES (?, ?, ?, ?)";
        try {
            // 使用父类的executeUpdate方法
            int result = executeUpdate(sql, 
                supervisorStaffId, subordinateStaffId, 
                new java.sql.Date(startDate.getTime()), 
                endDate != null ? new java.sql.Date(endDate.getTime()) : null);
                
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("创建监督关系失败", e);
        }
    }
    
    // 根据ID获取监督关系

    // 根据ID获取监督关系
    public Map<String, Object> getSuperviseById(int superviseId) {
        if (superviseId <= 0) {
            throw new IllegalArgumentException("监督关系ID必须大于0");    
        }
        
        String sql = "SELECT * FROM supervise WHERE supervise_id = ?";
        try {
            List<Map<String, Object>> results = executeQuery(sql, superviseId);   
            if (!results.isEmpty()) {
                return mapResultSetToSupervise(results.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取监督关系失败", e);
        }
        return null;
    }
    
    // 从Map结果转换为驼峰命名的Map
    private Map<String, Object> mapResultSetToSupervise(Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        // 检查并添加所有必要的字段，同时转换为驼峰命名
        if (resultMap.containsKey("supervise_id")) {
            map.put(convertToJavaName("supervise_id"), resultMap.get("supervise_id"));
        }
        if (resultMap.containsKey("supervisor_staff_id")) {
            map.put(convertToJavaName("supervisor_staff_id"), resultMap.get("supervisor_staff_id"));
        }
        if (resultMap.containsKey("subordinate_staff_id")) {
            map.put(convertToJavaName("subordinate_staff_id"), resultMap.get("subordinate_staff_id"));
        }
        if (resultMap.containsKey("start_date")) {
            map.put(convertToJavaName("start_date"), resultMap.get("start_date"));
        }
        if (resultMap.containsKey("end_date")) {
            map.put(convertToJavaName("end_date"), resultMap.get("end_date"));
        }
        return map;
    }
    
    // 更新监督关系
    public boolean updateSupervise(int superviseId, Date endDate) {
        // 参数验证
        if (superviseId <= 0) {
            throw new IllegalArgumentException("监督关系ID必须大于0");    
        }
        
        // 先获取现有记录以验证日期
        Map<String, Object> supervise = getSuperviseById(superviseId);
        if (supervise == null) {
            throw new RuntimeException("未找到指定的监督关系记录");
        }
        
        Date startDate = (Date) supervise.get("start_date");
        if (endDate != null && endDate.before(startDate)) {
            throw new IllegalArgumentException("结束日期不能早于开始日期");
        }
        
        String sql = "UPDATE supervise SET end_date = ? WHERE supervise_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, endDate != null ? new java.sql.Date(endDate.getTime()) : null);
            pstmt.setInt(2, superviseId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("未找到指定的监督关系记录");
            }
            System.out.println("更新监督关系ID=" + superviseId + "，设置结束日期=" + endDate);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新监督关系失败", e);
        }
    }
    
    // 删除监督关系
    public boolean deleteSupervise(int superviseId) {
        if (superviseId <= 0) {
            throw new IllegalArgumentException("监督关系ID必须大于0");
        }
        
        String sql = "DELETE FROM supervise WHERE supervise_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, superviseId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("未找到指定的监督关系记录");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除监督关系失败", e);
        }
    }

    
      
    private static void testgetSubordinatesByStaffId() {
        SuperviseService superviseService = SuperviseService.getInstance();
        List<Map<String, Object>> subordinates = superviseService.getSubordinatesByStaffId(1);
        System.out.println(subordinates);
    }
    // 获取员工的所有下属
    public List<Map<String, Object>> getSubordinatesByStaffId(int supervisorStaffId) {
        if (supervisorStaffId <= 0) {
            throw new IllegalArgumentException("员工ID必须大于0");
        }
        
        String sql = "SELECT s.supervisor_staff_id, s.subordinate_staff_id, s.start_date, s.end_date, " +
                     "sf.staff_number, sf.first_name, sf.last_name, sf.role_id " +
                     "FROM supervise s " +
                     "JOIN staff sf ON s.subordinate_staff_id = sf.staff_id " +
                     "WHERE s.supervisor_staff_id = ? AND s.end_date IS NULL"; 
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supervisorStaffId);
            
            List<Map<String, Object>> subordinates = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> subordinateMap = new HashMap<>();
                    // 明确设置需要的字段
                    subordinateMap.put("supervisor_staff_id", rs.getInt("supervisor_staff_id"));
                    subordinateMap.put("subordinate_staff_id", rs.getInt("subordinate_staff_id"));
                    subordinateMap.put("start_date", rs.getDate("start_date"));
                    subordinateMap.put("end_date", rs.getDate("end_date"));
                    subordinateMap.put("staff_number", rs.getString("staff_number"));
                    subordinateMap.put("first_name", rs.getString("first_name"));
                    subordinateMap.put("last_name", rs.getString("last_name"));
                    subordinateMap.put("role_id", rs.getInt("role_id"));
                    subordinates.add(subordinateMap);
                }
                System.out.println("获取到的下属数量: " + subordinates.size());
            }
            return subordinates;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取员工下属失败", e);
        }
    }
    private static void testgetSupervisorsByStaffId() {
        SuperviseService superviseService = SuperviseService.getInstance();
        List<Map<String, Object>> supervisors = superviseService.getSupervisorsByStaffId(5);
        System.out.println(supervisors);
    }

    // 获取员工的所有上级
    public List<Map<String, Object>> getSupervisorsByStaffId(int subordinateStaffId) {
        if (subordinateStaffId <= 0) {
            throw new IllegalArgumentException("员工ID必须大于0");
        }
        
        String sql = "SELECT s.supervisor_staff_id, s.subordinate_staff_id, s.start_date, s.end_date, " +
                     "sf.staff_number, sf.first_name, sf.last_name, sf.role_id " +
                     "FROM supervise s " +
                     "JOIN staff sf ON s.supervisor_staff_id = sf.staff_id " +
                     "WHERE s.subordinate_staff_id = ? AND s.end_date IS NULL";        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subordinateStaffId);
            
            List<Map<String, Object>> supervisors = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> supervisorMap = new HashMap<>();
                    // 明确设置需要的字段
                    supervisorMap.put("supervisor_staff_id", rs.getInt("supervisor_staff_id"));
                    supervisorMap.put("subordinate_staff_id", rs.getInt("subordinate_staff_id"));
                    supervisorMap.put("start_date", rs.getDate("start_date"));
                    supervisorMap.put("end_date", rs.getDate("end_date"));
                    supervisorMap.put("staff_number", rs.getString("staff_number"));
                    supervisorMap.put("first_name", rs.getString("first_name"));
                    supervisorMap.put("last_name", rs.getString("last_name"));
                    supervisors.add(supervisorMap);
                }
            }
            return supervisors;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取员工上级失败", e);
        }
    }
    
    // 分页查询监督关系
    public Map<String, Object> getSupervisesByPage(int page, int pageSize, Map<String, Object> filters, String sortBy, String sortOrder) {
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 30;
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM supervise WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // 添加过滤条件
        if (filters != null) {
            if (filters.containsKey("supervisorStaffId")) {
                String dbColumn = convertToDbColumn("supervisorStaffId");
                sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                params.add(filters.get("supervisorStaffId"));
            }
            if (filters.containsKey("subordinateStaffId")) {
                String dbColumn = convertToDbColumn("subordinateStaffId");
                sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                params.add(filters.get("subordinateStaffId"));
            }
            if (filters.containsKey("activeOnly")) {
                if (Boolean.TRUE.equals(filters.get("activeOnly"))) {
                    sqlBuilder.append(" AND end_date IS NULL");
                }
            }
        }
        
        // 添加排序
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String validSortFields = "supervise_id,supervisor_staff_id,subordinate_staff_id,start_date,end_date";
            if (validSortFields.contains(sortBy)) {
                sqlBuilder.append(" ORDER BY " + sortBy + " ");
                sqlBuilder.append(sortOrder != null && sortOrder.equalsIgnoreCase("DESC") ? "DESC" : "ASC");
            }
        } else {
            sqlBuilder.append(" ORDER BY supervise_id ASC");
        }
        
        // 添加分页
        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        // 获取总记录数
        String countSql = "SELECT COUNT(*) FROM supervise WHERE 1=1";
        StringBuilder countSqlBuilder = new StringBuilder(countSql);
        if (filters != null) {
            if (filters.containsKey("supervisorStaffId")) {
                String dbColumn = convertToDbColumn("supervisorStaffId");
                countSqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
            }
            if (filters.containsKey("subordinateStaffId")) {
                String dbColumn = convertToDbColumn("subordinateStaffId");
                countSqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
            }
            if (filters.containsKey("activeOnly")) {
                if (Boolean.TRUE.equals(filters.get("activeOnly"))) {
                    countSqlBuilder.append(" AND end_date IS NULL");
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取总记录数
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement countPstmt = conn.prepareStatement(countSqlBuilder.toString())) {
                // 设置除分页外的参数
                for (int i = 0; i < params.size() - 2; i++) {
                    countPstmt.setObject(i + 1, params.get(i));
                }
                try (ResultSet countRs = countPstmt.executeQuery()) {
                    if (countRs.next()) {
                        result.put("total", countRs.getInt(1));
                    }
                }
            }
            
            // 获取分页数据
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
                // 设置所有参数
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                
                List<Map<String, Object>> supervises = new ArrayList<>();
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        supervises.add(mapResultSetToSupervise(rs));
                    }
                }
                result.put("data", supervises);
                result.put("page", page);
                result.put("pageSize", pageSize);
                // 安全获取total值并计算总页数，避免空指针异常
                int total = 0;
                Object totalObj = result.get("total");
                if (totalObj instanceof Number) {
                    total = ((Number) totalObj).intValue();
                } else if (totalObj instanceof String) {
                    try {
                        total = Integer.parseInt((String) totalObj);
                    } catch (NumberFormatException e) {
                        total = 0;
                    }
                }
                result.put("totalPages", (int) Math.ceil(total / (double) pageSize));
            }
            
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("分页查询监督关系失败", e);
        }
    }
    
    // 将数据库下划线命名转换为Java驼峰命名
    protected String convertToJavaName(String dbName) {
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < dbName.length(); i++) {
            char c = dbName.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }
    
    // 将ResultSet映射为Map
    private Map<String, Object> mapResultSetToSupervise(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        // 将下划线命名转换为驼峰命名
        map.put(convertToJavaName("supervise_id"), rs.getInt("supervise_id"));
        map.put(convertToJavaName("supervisor_staff_id"), rs.getInt("supervisor_staff_id"));
        map.put(convertToJavaName("subordinate_staff_id"), rs.getInt("subordinate_staff_id"));
        map.put(convertToJavaName("start_date"), rs.getDate("start_date"));
        map.put(convertToJavaName("end_date"), rs.getDate("end_date"));
        return map;
    }
    public static void main(String[] args) {
        testgetSupervisorsByStaffId();
        testgetSubordinatesByStaffId();
    }
}