package com.polyu.cmms.service;

import java.sql.SQLException;
import java.util.*;

/**
 * 房间服务类，提供房间相关的数据访问功能
 */
public class RoomService extends BaseService {
    private static RoomService instance;
    
    // 单例模式
    private RoomService() {}
    
    public static synchronized RoomService getInstance() {
        if (instance == null) {
            instance = new RoomService();
        }
        return instance;
    }
    
    /**
     * 添加房间记录
     * @param roomData 房间数据
     * @return 插入是否成功
     * @throws SQLException SQL异常
     */
    public boolean addRoom(Map<String, Object> roomData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO rooms (");
        StringBuilder valuesBuilder = new StringBuilder("VALUES (");
        List<Object> params = new ArrayList<>();
        
        // 添加必要字段
        if (roomData.containsKey("buildingId")) {
            sqlBuilder.append("building_id, ");
            valuesBuilder.append("?, ");
            params.add(roomData.get("buildingId"));
        }
        if (roomData.containsKey("name")) {
            sqlBuilder.append("name, ");
            valuesBuilder.append("?, ");
            params.add(roomData.get("name"));
        }
        if (roomData.containsKey("roomType")) {
            sqlBuilder.append("room_type, ");
            valuesBuilder.append("?, ");
            params.add(roomData.get("roomType"));
        }
        if (roomData.containsKey("capacity")) {
            sqlBuilder.append("capacity, ");
            valuesBuilder.append("?, ");
            params.add(roomData.get("capacity"));
        }
        if (roomData.containsKey("roomFeatures")) {
            sqlBuilder.append("room_features, ");
            valuesBuilder.append("?, ");
            params.add(roomData.get("roomFeatures"));
        }
        
        // 添加默认启用状态
        sqlBuilder.append("active_flag");
        valuesBuilder.append("'Y'");
        
        String sql = sqlBuilder.toString() + ") " + valuesBuilder.toString() + ")";
        int result = executeUpdate(sql, params.toArray());
        return result > 0;
    }
    
    /**
     * 更新房间信息
     * @param roomId 房间ID
     * @param updates 更新的字段和值
     * @return 更新是否成功
     * @throws SQLException SQL异常
     */
    public boolean updateRoom(int roomId, Map<String, Object> updates) throws SQLException {
        if (updates.isEmpty()) {
            return true;
        }
        
        StringBuilder sqlBuilder = new StringBuilder("UPDATE rooms SET ");
        List<Object> params = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String dbColumn = convertToDbColumn(entry.getKey());
            sqlBuilder.append(dbColumn).append(" = ?, ");
            params.add(entry.getValue());
        }
        
        sqlBuilder.setLength(sqlBuilder.length() - 2); // 移除最后一个逗号和空格
        sqlBuilder.append(" WHERE room_id = ?");
        params.add(roomId);
        
        int result = executeUpdate(sqlBuilder.toString(), params.toArray());
        return result > 0;
    }
    
    /**
     * 删除房间（软删除）
     * @param roomId 房间ID
     * @return 删除是否成功
     * @throws SQLException SQL异常
     */
    public boolean deleteRoom(int roomId) throws SQLException {
        String sql = "UPDATE rooms SET active_flag = 'N' WHERE room_id = ?";
        int result = executeUpdate(sql, roomId);
        return result > 0;
    }
    
    /**
     * 根据条件查询房间
     * @param conditions 查询条件
     * @return 房间列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> queryRooms(Map<String, Object> conditions) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM rooms WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (conditions != null) {
            // 转换Java属性名为数据库列名
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String dbColumn = convertToDbColumn(entry.getKey());
                sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                params.add(entry.getValue());
            }
        }
        
        return executeQuery(sqlBuilder.toString(), params.toArray());
    }
    
    /**
     * 分页查询房间
     * @param page 页码
     * @param pageSize 每页大小
     * @param conditions 查询条件
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     * @return 分页查询结果
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getRoomsByPage(int page, int pageSize, Map<String, Object> conditions, String sortField, String sortOrder) throws SQLException {
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 30;
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM rooms WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // 添加过滤条件
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String dbColumn = convertToDbColumn(entry.getKey());
                sqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                params.add(entry.getValue());
            }
        }
        
        // 添加排序
        if (sortField != null && !sortField.trim().isEmpty()) {
            String dbColumn = convertToDbColumn(sortField);
            String order = sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "DESC" : "ASC";
            sqlBuilder.append(" ORDER BY ").append(dbColumn).append(" ").append(order);
        } else {
            // 默认按ID排序
            sqlBuilder.append(" ORDER BY room_id ASC");
        }
        
        // 添加分页
        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        // 获取数据
        List<Map<String, Object>> data = executeQuery(sqlBuilder.toString(), params.toArray());
        
        // 获取总数
        StringBuilder countSqlBuilder = new StringBuilder("SELECT COUNT(*) FROM rooms WHERE 1=1");
        List<Object> countParams = new ArrayList<>();
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String dbColumn = convertToDbColumn(entry.getKey());
                countSqlBuilder.append(" AND ").append(dbColumn).append(" = ?");
                countParams.add(entry.getValue());
            }
        }
        
        List<Map<String, Object>> countResult = executeQuery(countSqlBuilder.toString(), countParams.toArray());
        int total = 0;
        
        // 安全获取总记录数，避免空指针异常
        if (countResult != null && !countResult.isEmpty()) {
            Map<String, Object> firstRow = countResult.get(0);
            if (firstRow != null) {
                Object countObj = firstRow.get("count");
                if (countObj instanceof Number) {
                    total = ((Number) countObj).intValue();
                } else if (countObj != null) {
                    try {
                        total = Integer.parseInt(countObj.toString());
                    } catch (NumberFormatException e) {
                        total = 0;
                    }
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        return result;
    }
    
    /**
     * 根据ID获取房间详情
     * @param roomId 房间ID
     * @return 房间详情
     * @throws SQLException SQL异常
     */
    public Map<String, Object> getRoomById(int roomId) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        List<Map<String, Object>> results = executeQuery(sql, roomId);
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * 根据建筑物ID获取房间列表
     * @param buildingId 建筑物ID
     * @return 房间列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> getRoomsByBuilding(int buildingId) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE building_id = ? AND active_flag = 'Y' ORDER BY name";
        return executeQuery(sql, buildingId);
    }
    
    /**
     * 根据房间类型获取房间列表
     * @param roomType 房间类型
     * @return 房间列表
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> getRoomsByType(String roomType) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_type = ? AND active_flag = 'Y' ORDER BY building_id, name";
        return executeQuery(sql, roomType);
    }
    
    /**
     * 将Java驼峰命名转换为数据库下划线命名
     * @param javaName Java属性名（驼峰命名）
     * @return 数据库列名（下划线命名）
     */
    private String convertToDbColumn(String javaName) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < javaName.length(); i++) {
            char c = javaName.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}