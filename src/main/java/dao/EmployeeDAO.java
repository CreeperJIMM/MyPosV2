package dao;

import java.util.List;

import db.EmployeeDbManager;
import models.Employee;

/**
 * 使用者資料存取對象，負責與資料庫交互
 */
public class EmployeeDAO {
    private EmployeeDbManager dbManager;
    
    public EmployeeDAO() {
        dbManager = new EmployeeDbManager();
    }
    
    /**
     * 獲取所有使用者
     * @return 使用者列表
     */
    public List<Employee> getAllEmployees() {
        return dbManager.getAllEmployees();
    }
    
    /**
     * 根據ID獲取使用者
     * @param id 用戶名
     * @return 用戶對象，如果不存在返回null
     */
    public Employee getEmployeeById(int id) {
        return dbManager.getEmployeeById(id);
    }
    
    /**
     * 根據用戶名獲取使用者
     * @param username 用戶名
     * @return 用戶對象，如果不存在返回null
     */
    public Employee getEmployeeByUsername(String username) {
        return dbManager.getEmployeeByUsername(username);
    }
    
    /**
     * 驗證使用者登入
     * @param username 用戶名
     * @param password 密碼
     * @return 驗證成功返回用戶對象，失敗返回null
     */
    public Employee authenticate(String username, String password) {
        return dbManager.authenticateEmployee(username, password);
    }
    
    /**
     * 新增使用者
     * @param employee 要新增的使用者
     * @return 成功返回true，失敗返回false
     */
    public boolean addEmployee(Employee employee) {
        return dbManager.insertEmployee(employee);
    }
    
    /**
     * 更新使用者資料
     * @param employee 要更新的使用者
     * @return 成功返回true，失敗返回false
     */
    public boolean updateEmployee(Employee employee) {
        return dbManager.updateEmployee(employee);
    }
    
    /**
     * 刪除使用者
     * @param username 要刪除的使用者名稱
     * @return 成功返回true，失敗返回false
     */
    public boolean deleteEmployee(String username) {
        return dbManager.deleteEmployee(username);
    }
    
    /**
     * 關閉資料庫連接
     */
    public void closeConnection() {
        dbManager.close();
    }
}
