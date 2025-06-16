package models;

/**
 * 代表系統使用者的模型類別
 */
public class Member {
    private int id;
    private String name;
    private String sex;
    private String address;
    private String phone;
    private String birthday;
    private String create_date;
    
    // 無參構造函數
    public Member() {
    }
    
    // 帶參數的構造函數
    public Member(String name, String sex, String address, String phone, String birthday) {
        this.name = name;
        this.sex = sex;
        this.address = address;
        this.phone = phone;
        this.birthday = birthday;
    }
    
    // 含ID的構造函數
    public Member(int id, String name, String sex, String address, String phone, String birthday, String create_date) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.address = address;
        this.phone = phone;
        this.birthday = birthday;
        this.create_date = create_date;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String username) {
        this.name = username;
    }
    
    public String getSex() {
        return sex;
    }
    
    public void setSex(String sex) {
        this.sex = sex;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    
    public String getCreateDate() {
        return create_date;
    }
    
    public void setCreateDate(String create_date) {
        this.create_date = create_date;
    }

    @Override
    public String toString() {
        return "Member{" + "id=" + id + ", name=" + name + ", phone=" + phone + '}';
    }
}
