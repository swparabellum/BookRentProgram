package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import db.DBConnection;
import dto.MemberDTO;

public class MemberDAO {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    public MemberDAO() {
        conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("���� ����");
            System.exit(0);
        }
        System.out.println("���� ����");
    }

    public void dbClose() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        } catch (Exception e) {
            System.out.println(e + " Close Fail");
        }
    }

    // Add a member
    public void addMember(MemberDTO member) {
        String query = "INSERT INTO MEMBER (ID, PASS, NAME, JUMIN, TEL, ADDR, EMAIL, JOB, JOINDATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUNC(SYSDATE))";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPass());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getJumin());
            pstmt.setString(5, member.getTel());
            pstmt.setString(6, member.getAddr());
            pstmt.setString(7, member.getEmail());
            pstmt.setString(8, member.getJob());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " Add Member Fail");
        } finally {
            dbClose();
        }
    }

    // Get a member by ID
    public MemberDTO getMember(String id) {
        MemberDTO member = null;
        String query = "SELECT * FROM MEMBER WHERE ID = ?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                member = new MemberDTO(
                    rs.getString("ID"),
                    rs.getString("PASS"),
                    rs.getString("NAME"),
                    rs.getString("JUMIN"),
                    rs.getString("TEL"),
                    rs.getString("ADDR"),
                    rs.getString("EMAIL"),
                    rs.getString("JOB"),
                    rs.getDate("JOINDATE")
                );
            }
        } catch (SQLException e) {
            System.out.println(e + " Get Member Fail");
        } finally {
            dbClose();
        }
        return member;
    }

    // Update a member
    public void updateMember(MemberDTO member) {
        String query = "UPDATE MEMBER SET PASS = ?, NAME = ?, TEL = ?, ADDR = ?, EMAIL = ?, JOB = ? WHERE ID = ?";
        String rentalQuery = "UPDATE RENTAL r SET r.tel = (SELECT m.tel FROM MEMBER m WHERE m.ID = r.id)";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, member.getPass());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getTel());
            pstmt.setString(4, member.getAddr());
            pstmt.setString(5, member.getEmail());
            pstmt.setString(6, member.getJob());
            pstmt.setString(7, member.getId());
            pstmt.executeUpdate();
            
            
            pstmt = conn.prepareStatement(rentalQuery);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e + " Update Member Fail");
        } finally {
            dbClose();
        }
    }
    
    

    public void deleteMember(String id) {
        RentalDAO rentalDAO = new RentalDAO();

        // �뿩���� ������ �ִ��� Ȯ��
        if (rentalDAO.hasRentedBooks(id)) {
            JOptionPane.showMessageDialog(null, "���� �뿩���� ������ �־� Ż���� �� �����ϴ�.", "Ż�� �Ұ�", JOptionPane.WARNING_MESSAGE);
            return; // Ȯ�� ��ư�� ������ �޼ҵ� ����
        }

        // �뿩���� ������ ���� ��� �뿩 ��� ���� �� ȸ�� ����
        rentalDAO.deleteRentalsByUserId(id); // ȸ�� ���� ���� �뿩 ��� ����

        String query = "DELETE FROM MEMBER WHERE ID = ?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e + " Delete Member Fail");
        } finally {
            dbClose();
        }
    }

    // Get all members
    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> members = new ArrayList<>();
        // ���� ����: �������� ��������, �̸� ������������ ����
        String query = "SELECT * FROM MEMBER ORDER BY JOINDATE DESC, NAME ASC";
        // ���� ��
        try {
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(new MemberDTO(
                    rs.getString("ID"),
                    rs.getString("PASS"),
                    rs.getString("NAME"),
                    rs.getString("JUMIN"),
                    rs.getString("TEL"),
                    rs.getString("ADDR"),
                    rs.getString("EMAIL"),
                    rs.getString("JOB"),
                    rs.getDate("JOINDATE")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " Get All Members Fail");
        } finally {
            dbClose();
        }
        return members;
    }

    // Search members by field and keyword
    public List<MemberDTO> searchMembers(String fieldName, String keyword) {
        List<MemberDTO> members = new ArrayList<>();
        String SQL;

        if (fieldName.isEmpty()) {
            SQL = "SELECT * FROM member WHERE ID LIKE ? OR NAME LIKE ? OR TEL LIKE ? OR ADDR LIKE ? OR EMAIL LIKE ? OR JOB LIKE ?";
        } else {
            SQL = "SELECT * FROM member WHERE " + fieldName + " LIKE ?";
        }

        try {
            pstmt = conn.prepareStatement(SQL);

            if (fieldName.isEmpty()) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
                pstmt.setString(3, "%" + keyword + "%");
                pstmt.setString(4, "%" + keyword + "%");
                pstmt.setString(5, "%" + keyword + "%");
                pstmt.setString(6, "%" + keyword + "%");
            } else {
                pstmt.setString(1, "%" + keyword + "%");
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                MemberDTO member = new MemberDTO(
                    rs.getString("ID"),
                    rs.getString("PASS"),
                    rs.getString("NAME"),
                    rs.getString("JUMIN"),
                    rs.getString("TEL"),
                    rs.getString("ADDR"),
                    rs.getString("EMAIL"),
                    rs.getString("JOB"),
                    rs.getDate("JOINDATE")
                );
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return members;
    }
}
