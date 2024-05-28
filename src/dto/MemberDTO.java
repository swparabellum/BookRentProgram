package dto;
import java.sql.Date;

public class MemberDTO {
    private String id;
    private String pass;
    private String name;
    private String jumin;
    private String tel;
    private String addr;
    private String email;
    private String job;
    private Date joinDate;

    public MemberDTO(String id, String pass, String name, String jumin, String tel, String addr, String email, String job, Date joinDate) {
        this.id = id;
        this.pass = pass;
        this.name = name;
        this.jumin = jumin;
        this.tel = tel;
        this.addr = addr;
        this.email = email;
        this.job = job;
        this.joinDate = joinDate;
    }

    public MemberDTO() {}

    @Override
    public String toString() {
        return "MemberDTO [id=" + id + ", pass=" + pass + ", name=" + name + ", jumin=" + jumin + ", tel=" + tel + ", addr=" + addr + ", email=" + email + ", job=" + job + ", joinDate=" + joinDate + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJumin() {
        return jumin;
    }

    public void setJumin(String jumin) {
        this.jumin = jumin;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
}