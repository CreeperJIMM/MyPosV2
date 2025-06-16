package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Employee;

/**
 * 使用者資料表管理類別
 * 專門負責使用者資料的 CRUD 操作
 */
public class EmployeeDbManager extends DatabaseConnection {
    
    
    /**
     * 插入新使用者
     */
    public boolean insertEmployee(Employee employee) {
        String sql = "INSERT INTO employees(username, password, role, department) VALUES(?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getUsername());
            pstmt.setString(2, employee.getPassword());
            pstmt.setString(3, employee.getRole());
            pstmt.setString(4, employee.getDepartment());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("插入使用者錯誤: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 更新使用者資訊
     */
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET username = ?, password = ?, role = ?, department = ? WHERE id = ?";
                
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getUsername());
            pstmt.setString(2, employee.getPassword());
            pstmt.setString(3, employee.getRole());
            pstmt.setString(4, employee.getDepartment());
            pstmt.setInt(5, employee.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("更新使用者錯誤: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 刪除使用者
     */
    public boolean deleteEmployee(String username) {
        String sql = "DELETE FROM employees WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("刪除使用者錯誤: " + e.getMessage());
            return false;
        }
    }
    
     /**
     * 根據ID獲取使用者
     */
    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setUsername(rs.getString("username"));
                employee.setPassword(rs.getString("password"));
                employee.setRole(rs.getString("role"));
                employee.setDepartment(rs.getString("department"));
                return employee;
            }
        } catch (SQLException e) {
            System.out.println("獲取使用者錯誤: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 根據用戶名獲取使用者
     */
    public Employee getEmployeeByUsername(String username) {
        String sql = "SELECT * FROM employees WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setUsername(rs.getString("username"));
                employee.setPassword(rs.getString("password"));
                employee.setRole(rs.getString("role"));
                employee.setDepartment(rs.getString("department"));
                return employee;
            }
        } catch (SQLException e) {
            System.out.println("獲取使用者錯誤: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 獲取所有使用者
     */
    public List<Employee> getAllEmployees() {
        String sql = "SELECT * FROM employees";
        List<Employee> employees = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setUsername(rs.getString("username"));
                employee.setPassword(rs.getString("password"));
                employee.setRole(rs.getString("role"));
                employee.setDepartment(rs.getString("department"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.out.println("獲取所有使用者錯誤: " + e.getMessage());
        }
        return employees;
    }
    
    /**
     * 驗證使用者登入
     */
    public Employee authenticateEmployee(String username, String password) {
        String sql = "SELECT * FROM employees WHERE username = ? AND password = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setUsername(rs.getString("username"));
                employee.setPassword(rs.getString("password"));
                employee.setRole(rs.getString("role"));
                employee.setDepartment(rs.getString("department"));
                return employee;
            }
        } catch (SQLException e) {
            System.out.println("使用者認證錯誤: " + e.getMessage());
        }
        return null;
    }
}
