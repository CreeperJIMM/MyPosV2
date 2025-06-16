package models;

/**
 * 代表系統使用者的模型類別
 */
public class Employee {
    private int id;
    private String department;
    private String username;
    private String password;
    private String role;
    
    // 無參構造函數
    public Employee() {
    }
    
    // 帶參數的構造函數
    public Employee(String username, String password, String role, String department) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
    }
    
    // 含ID的構造函數
    public Employee(int id, String username, String password, String role, String department) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String toString() {
        return "Employee{" + "id=" + id + ", username=" + username + ", role=" + role + '}';
    }
}
