package dao;

import app.MainMenuApp;
import java.util.List;

import db.MemberDbManager;
import models.Member;

/**
 * 使用者資料存取對象，負責與資料庫交互
 */
public class MemberDAO {
    private MemberDbManager dbManager;
    
    public MemberDAO() {
        dbManager = MainMenuApp.memberManger;
    }
    
    /**
     * 獲取所有會員
     * @return 會員列表
     */
    public List<Member> getAllMembers() {
        return dbManager.getAllMembers();
    }
    
    /**
     * 根據ID獲取會員
     * @param id 會員名
     * @return 會員對象，如果不存在返回null
     */
    public Member getMemberById(int id) {
        return dbManager.getMemberById(id);
    }
    
    /**
     * 根據會員名獲取會員
     * @param name 會員名
     * @return 會員對象，如果不存在返回null
     */
    public Member getMemberByName(String name) {
        return dbManager.getMemberByName(name);
    }
    
    /**
     * 根據手機號獲取會員
     * @param phone 會員手機
     * @return 會員對象，如果不存在返回null
     */
    public Member getMemberByPhone(String phone) {
        return dbManager.getEmployeeByPhone(phone);
    }
    
    /**
     * 新增使用者
     * @param member 要新增的使用者
     * @return 成功返回true，失敗返回false
     */
    public boolean addMember(Member member) {
        return dbManager.insertMember(member);
    }
    
    /**
     * 更新使用者資料
     * @param member 要更新的會員
     * @return 成功返回true，失敗返回false
     */
    public boolean updateMember(Member member) {
        return dbManager.updateMember(member);
    }
    
    /**
     * 刪除使用者
     * @param phone 要刪除的使用者手機號
     * @return 成功返回true，失敗返回false
     */
    public boolean deleteMember(String phone) {
        return dbManager.deleteMember(phone);
    }
    
    /**
     * 關閉資料庫連接
     */
    public void closeConnection() {
        dbManager.close();
    }
}
