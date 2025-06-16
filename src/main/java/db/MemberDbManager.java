package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import models.Member;

/**
 * 使用者資料表管理類別
 * 專門負責會員資料的 CRUD 操作
 */
public class MemberDbManager extends DatabaseConnection {
    
    
    /**
     * 插入新會員
     */
    public boolean insertMember(Member member) {
        String sql = "INSERT INTO members(name, sex, address, phone, birthday, create_date) VALUES(?, ?, ?, ?, ?, ?)";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getSex());
            pstmt.setString(3, member.getAddress());
            pstmt.setString(4, member.getPhone());
            pstmt.setString(5, member.getBirthday());
            pstmt.setString(6, date);       
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("插入使用者錯誤: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 更新會員資訊
     */
    public boolean updateMember(Member member) {
        String sql = "UPDATE members SET name = ?, sex = ?, address = ?, phone = ?, birthday = ? WHERE id = ?";
                
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) { 
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getSex());
            pstmt.setString(3, member.getAddress());
            pstmt.setString(4, member.getPhone());
            pstmt.setString(5, member.getBirthday());
            pstmt.setInt(6, member.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("更新使用者錯誤: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 刪除會員
     */
    public boolean deleteMember(String phone) {
        String sql = "DELETE FROM members WHERE phone = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("刪除使用者錯誤: " + e.getMessage());
            return false;
        }
    }
    
     /**
     * 根據ID獲取會員
     */
    public Member getMemberById(int id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                member.setSex(rs.getString("sex"));
                member.setAddress(rs.getString("address"));
                member.setPhone(rs.getString("phone"));
                member.setBirthday(rs.getString("birthday"));
                member.setCreateDate(rs.getString("create_date"));
                return member;
            }
        } catch (SQLException e) {
            System.out.println("獲取使用者錯誤: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 根據用戶名獲取會員
     */
    public Member getMemberByName(String name) {
        String sql = "SELECT * FROM members WHERE name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                member.setSex(rs.getString("sex"));
                member.setAddress(rs.getString("address"));
                member.setPhone(rs.getString("phone"));
                member.setBirthday(rs.getString("birthday"));
                member.setCreateDate(rs.getString("create_date"));
                return member;
            }
        } catch (SQLException e) {
            System.out.println("獲取使用者錯誤: " + e.getMessage());
        }
        return null;
    }
    
     /**
     * 根據手機號獲取會員
     */
    public Member getEmployeeByPhone(String name) {
        String sql = "SELECT * FROM members WHERE phone = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                member.setSex(rs.getString("sex"));
                member.setAddress(rs.getString("address"));
                member.setPhone(rs.getString("phone"));
                member.setBirthday(rs.getString("birthday"));
                member.setCreateDate(rs.getString("create_date"));
                return member;
            }
        } catch (SQLException e) {
            System.out.println("獲取使用者錯誤: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 獲取所有使用者
     */
    public List<Member> getAllMembers() {
        String sql = "SELECT * FROM members";
        List<Member> members = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                member.setSex(rs.getString("sex"));
                member.setAddress(rs.getString("address"));
                member.setPhone(rs.getString("phone"));
                member.setBirthday(rs.getString("birthday"));
                member.setCreateDate(rs.getString("create_date"));
                members.add(member);
            }
        } catch (SQLException e) {
            System.out.println("獲取所有使用者錯誤: " + e.getMessage());
        }
        return members;
    }
}
