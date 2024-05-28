package dto;
import java.util.Date;

public class RentalDTO {
    private int rNum;      // �뿩 ID
    private String id;     // ȸ�� ID
    private String name;   // ȸ�� �̸�
    private String bCode;  // ���� �ڵ�
    private String bTitle; // ���� ����
    private Date rentalDate; // �뿩 ��¥
    private Date returnDate; // �ݳ� ��¥
    private String tel;    // ȸ�� ��ȭ��ȣ
    private int rentState; // �뿩 ����

    public RentalDTO() {
    }

    public RentalDTO(int rNum, String id, String name, String bCode, String bTitle, Date rentalDate, Date returnDate, String tel, int rentState) {
        this.rNum = rNum;
        this.id = id;
        this.name = name;
        this.bCode = bCode;
        this.bTitle = bTitle;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.tel = tel;
        this.rentState = rentState;
    }

    // Getters and Setters
    public int getrNum() {
        return rNum;
    }

    public void setrNum(int rNum) {
        this.rNum = rNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getbCode() {
        return bCode;
    }

    public void setbCode(String bCode) {
        this.bCode = bCode;
    }

    public String getbTitle() {
        return bTitle;
    }

    public void setbTitle(String bTitle) {
        this.bTitle = bTitle;
    }

    public Date getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getRentState() {
        return rentState;
    }

    public void setRentState(int rentState) {
        this.rentState = rentState;
    }

    @Override
    public String toString() {
        return "RentalDTO [rNum=" + rNum + ", id=" + id + ", name=" + name + ", bCode=" + bCode + ", bTitle=" + bTitle
                + ", rentalDate=" + rentalDate + ", returnDate=" + returnDate + ", tel=" + tel + ", rentState=" + rentState + "]";
    }
}