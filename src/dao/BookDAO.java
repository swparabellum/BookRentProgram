package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import db.DBConnection;
import dto.BookDTO;
import gui.BookDialog;

public class BookDAO {
    private static Connection conn;
    private static PreparedStatement pstmt;
    private static ResultSet rs;

    public BookDAO() {
        conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("���� ����");
            System.exit(0);
        }
        System.out.println("���� ����");
    }

    public static void dbClose() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        } catch (Exception e) {
            System.out.println(e + " Close Fail");
        }
    }

    public int bookListInsert(BookDialog book) {
        int result = 0;
        String bCode = book.getTf_bcode().getText();
        String bCategory = book.getTf_bcategory().getText();
        String bTitle = book.getTf_btitle().getText();
        String bWriter = book.getTf_bwriter().getText();
        String bPub = book.getTf_bpub().getText();
        String bYear = book.getTf_byear().getText();
        String bIntro = book.getTf_bintro().getText();

        if (bCode.isEmpty()) {
            book.getTf_bcode().requestFocus();
            JOptionPane.showMessageDialog(null, "���� �ڵ带 �Է��ϼ���.");
            return result;
        }
        if (bCategory.isEmpty()) {
            book.getTf_bcategory().requestFocus();
            JOptionPane.showMessageDialog(null, "���� �о߸� �Է��ϼ���.");
            return result;
        }
        if (bTitle.isEmpty()) {
            book.getTf_btitle().requestFocus();
            JOptionPane.showMessageDialog(null, "å������ �Է��ϼ���.");
            return result;
        }
        if (bWriter.isEmpty()) {
            book.getTf_bwriter().requestFocus();
            JOptionPane.showMessageDialog(null, "���ڸ� �Է��ϼ���.");
            return result;
        }
        if (bPub.isEmpty()) {
            book.getTf_bpub().requestFocus();
            JOptionPane.showMessageDialog(null, "���ǻ縦 �Է��ϼ���.");
            return result;
        }

        try {
            pstmt = conn.prepareStatement("INSERT INTO BOOK VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, bCode);
            pstmt.setString(2, bCategory);
            pstmt.setString(3, bTitle);
            pstmt.setString(4, bWriter);
            pstmt.setString(5, bPub);

            if (bYear.isEmpty()) {
                pstmt.setNull(6, Types.VARCHAR);
            } else {
                pstmt.setInt(6, Integer.parseInt(bYear));
            }

            if (bIntro.isEmpty()) {
                pstmt.setNull(7, Types.VARCHAR);
            } else {
                pstmt.setString(7, bIntro);
            }
            pstmt.setInt(8, 1);

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e + " Insert Fail");
        } finally {
            dbClose();
        }
        return result;
    }

    public int updateBook(BookDialog book) {
        int result = 0;
        String bCode = book.getTf_bcode().getText();
        String bCategory = book.getTf_bcategory().getText();
        String bTitle = book.getTf_btitle().getText();
        String bWriter = book.getTf_bwriter().getText();
        String bPub = book.getTf_bpub().getText();
        String bYear = book.getTf_byear().getText();
        String bIntro = book.getTf_bintro().getText();

        try {
            pstmt = conn.prepareStatement("UPDATE BOOK SET BCATEGORY=?, BTITLE=?, BWRITER=?, BPUB=?, BYEAR=?, BINTRO=? WHERE BCODE=?");
            pstmt.setString(1, bCategory);
            pstmt.setString(2, bTitle);
            pstmt.setString(3, bWriter);
            pstmt.setString(4, bPub);

            if (bYear.isEmpty()) {
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setInt(5, Integer.parseInt(bYear));
            }

            if (bIntro.isEmpty()) {
                pstmt.setNull(6, Types.VARCHAR);
            } else {
                pstmt.setString(6, bIntro);
            }
            pstmt.setString(7, bCode);

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e + " Update Fail");
        } finally {
            dbClose();
        }
        return result;
    }

    public void updateBookStatus(String bCode, int status) {
        String sqlUpdate = "UPDATE BOOK SET BRes=? WHERE BCode=?";
        try {
            pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setInt(1, status);
            pstmt.setString(2, bCode);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e + " Update Fail");
        } finally {
            dbClose();
        }
    }

    public void bookListDelete(String bCode) {
        int t = JOptionPane.showConfirmDialog(null, "å " + bCode + "��(��) �����Ͻðڽ��ϱ�?", "���� ����", JOptionPane.YES_NO_OPTION);
        if (t == 1)
            return;

        RentalDAO rentalDAO = new RentalDAO();
        

        String sqlDelete = "DELETE FROM BOOK WHERE BCode = ?";
        try {
            pstmt = conn.prepareStatement(sqlDelete);
            pstmt.setString(1, bCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " Delete Fail");
        } finally {
            dbClose();
        }
    }

    public boolean getBcodeByCheck(String bcode) {
        boolean result = true;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM BOOK WHERE BCode=?");
            pstmt.setString(1, bcode.trim());
            rs = pstmt.executeQuery();
            if (rs.next())
                result = false;
        } catch (SQLException e) {
            System.out.println(e + " BCodeCheck Fail");
        } finally {
            dbClose();
        }

        return result;
    }

    public List<BookDTO> getAllBooks() {
        List<BookDTO> books = new ArrayList<>();
        String query = "SELECT * FROM BOOK ORDER BY BCode";
        try {
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(new BookDTO(
                        rs.getString("BCode"),
                        rs.getString("BCategory"),
                        rs.getString("BTitle"),
                        rs.getString("BWriter"),
                        rs.getString("BPub"),
                        rs.getInt("BYear"),
                        rs.getString("BIntro"),
                        rs.getInt("BRes")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " SelectAll Fail");
        } finally {
            dbClose();
        }
        return books;
    }

    public BookDTO getBookByCode(String bCode) {
        BookDTO book = null;
        String query = "SELECT * FROM BOOK WHERE BCode = ?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, bCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                book = new BookDTO(
                        rs.getString("BCode"),
                        rs.getString("BCategory"),
                        rs.getString("BTitle"),
                        rs.getString("BWriter"),
                        rs.getString("BPub"),
                        rs.getInt("BYear"),
                        rs.getString("BIntro"),
                        rs.getInt("BRes")
                );
            }
        } catch (SQLException e) {
            System.out.println(e + " Select Fail");
        } finally {
            dbClose();
        }
        return book;
    }

    public List<BookDTO> searchBooks(String fieldName, String keyword) {
        List<BookDTO> books = new ArrayList<>();
        String sql;

     // ���� ����2: �˻� �ʵ���� �ѱۿ��� DB �ʵ������ ��ȯ
        switch (fieldName) {
        case "�о�": fieldName = "BCategory";
        	break;
        case "å����": fieldName = "BTitle";
        	break;
        case "����": fieldName = "BWriter";
            break;
        default:
            fieldName = "";
            break;
    }

        if (fieldName.isEmpty()) {
            sql = "SELECT * FROM BOOK WHERE BCode LIKE ? OR BCategory LIKE ? OR BTitle LIKE ? OR BWriter LIKE ? OR BPub LIKE ? OR BIntro LIKE ? ORDER BY BCODE";
        } else {
            sql = "SELECT * FROM BOOK WHERE " + fieldName + " LIKE ?";
        }

        try {
            pstmt = conn.prepareStatement(sql);
            if (fieldName.isEmpty()) {
                for (int i = 1; i <= 6; i++) {
                    pstmt.setString(i, "%" + keyword.trim() + "%");
                }
            } else {
                pstmt.setString(1, "%" + keyword.trim() + "%");
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(new BookDTO(
                        rs.getString("BCode"),
                        rs.getString("BCategory"),
                        rs.getString("BTitle"),
                        rs.getString("BWriter"),
                        rs.getString("BPub"),
                        rs.getInt("BYear"),
                        rs.getString("BIntro"),
                        rs.getInt("BRes")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " Search Fail");
        } finally {
            dbClose();
        }
        return books;
    }
}