package dao;

import db.DBConnection;
import dto.RentalDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    public RentalDAO() {
        conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("연결 실패");
            System.exit(0);
        }
        System.out.println("연결 성공");
    }

    public void dbClose() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        } catch (Exception e) {
            System.out.println(e + " Close Fail");
        }
    }
    public int updateRental(RentalDTO rental) {
        String query = "UPDATE rental SET NAME = ?, BTITLE = ?, TEL = ? WHERE RNUM = ?";
        int result = 0;
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, rental.getName());
            pstmt.setString(2, rental.getbTitle());
            pstmt.setString(3, rental.getTel());
            pstmt.setInt(4, rental.getrNum());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " Update Rental Fail");
        } finally {
            dbClose();
        }
        return result;
    }

    public RentalDTO getRental(String id, String bCode) {
        RentalDTO rental = null;
        String query = "SELECT r.*, m.NAME, m.TEL, b.BTITLE " +
                "FROM rental r " +
                "JOIN member m ON r.ID = m.ID " +
                "JOIN book b ON r.BCODE = b.BCODE " +
                "WHERE r.ID = ? AND r.BCODE = ?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            pstmt.setString(2, bCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                rental = new RentalDTO(
                    rs.getInt("RNUM"),
                    rs.getString("ID"),
                    rs.getString("NAME"),
                    rs.getString("BCODE"),
                    rs.getString("BTITLE"),
                    rs.getDate("RENTDATE"),
                    rs.getDate("RETURNDATE"),
                    rs.getString("TEL"),
                    rs.getInt("RENTSTATE")
                );
            }
        } catch (SQLException e) {
            System.out.println(e + " Get Rental Fail");
        } finally {
            dbClose();
        }
        return rental;
    }


    public void addRental(RentalDTO rental) {
        String query = "INSERT INTO rental (RNUM, ID, NAME, BCODE, BTITLE, RENTDATE, RETURNDATE, TEL, RENTSTATE) VALUES (rental_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE, ?, ?, ?)";

        try {
            pstmt = conn.prepareStatement(query, new String[]{"RNUM"});
            pstmt.setString(1, rental.getId());
            pstmt.setString(2, rental.getName());
            pstmt.setString(3, rental.getbCode());
            pstmt.setString(4, rental.getbTitle());
           // pstmt.setDate(5, new java.sql.Date(rental.getRentalDate().getTime()));
            pstmt.setDate(5, rental.getReturnDate() != null ? new java.sql.Date(rental.getReturnDate().getTime()) : null);
            pstmt.setString(6, rental.getTel());
            pstmt.setInt(7, rental.getRentState());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    rental.setrNum(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e + " Add Rental Fail");
        } finally {
            dbClose();
        }
    }

    public void updateRentalState(String id, String bCode, int state) {
        String query = "UPDATE rental SET RENTSTATE = ? WHERE ID = ? AND BCODE = ?";

        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, state);
            pstmt.setString(2, id);
            pstmt.setString(3, bCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " Update Rental State Fail");
        } finally {
            dbClose();
        }
    }

    public List<RentalDTO> getAllRentals() {
        List<RentalDTO> rentals = new ArrayList<>();
        String query = "SELECT r.*, m.NAME, m.TEL, b.BTITLE " +
                "FROM rental r " +
                "JOIN member m ON r.ID = m.ID " +
                "JOIN book b ON r.BCODE = b.BCODE " +
                "ORDER BY r.RENTSTATE ASC, r.RENTDATE DESC"; // 수정된 부분

        try {
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                rentals.add(new RentalDTO(
                    rs.getInt("RNUM"),
                    rs.getString("ID"),
                    rs.getString("NAME"),
                    rs.getString("BCODE"),
                    rs.getString("BTITLE"),
                    rs.getDate("RENTDATE"),
                    rs.getDate("RETURNDATE"),
                    rs.getString("TEL"),
                    rs.getInt("RENTSTATE")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " Get All Rentals Fail");
        } finally {
            dbClose();
        }
        return rentals;
    }
 // 회원의 ID와 키워드를 사용하여 검색하는 메소드 수정
    public List<RentalDTO> searchRentalsByUserId(String userId, String fieldName, String keyword) {
        List<RentalDTO> rentals = new ArrayList<>();
        String query = "SELECT r.*, m.NAME, m.TEL, b.BTITLE " +
                       "FROM rental r " +
                       "JOIN member m ON r.ID = m.ID " +
                       "JOIN book b ON r.BCODE = b.BCODE " +
                       "WHERE " + (fieldName.equals("r.BTITLE") ? "b.BTITLE" : "r." + fieldName) + " LIKE ? " +
                       "AND r.ID = ? " +
                       "ORDER BY r.RENTSTATE, r.RENTDATE DESC"; // 수정된 부분

        // 수정 끝
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                rentals.add(new RentalDTO(
                    rs.getInt("RNUM"),
                    rs.getString("ID"),
                    rs.getString("NAME"),
                    rs.getString("BCODE"),
                    rs.getString("BTITLE"),
                    rs.getDate("RENTDATE"),
                    rs.getDate("RETURNDATE"),
                    rs.getString("TEL"),
                    rs.getInt("RENTSTATE")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " Search Rentals By User ID Fail");
        } finally {
            dbClose();
        }
        return rentals;
    }

    // 회원의 ID와 키워드를 사용하여 전체 필드를 검색하는 메소드 수정
    public List<RentalDTO> searchRentalsByUserIdAllFields(String userId, String keyword) {
        List<RentalDTO> rentals = new ArrayList<>();
        // 수정 시작
        String query = "SELECT r.*, m.NAME, m.TEL, b.BTITLE " +
                "FROM rental r " +
                "JOIN member m ON r.ID = m.ID " +
                "JOIN book b ON r.BCODE = b.BCODE " +
                "WHERE (r.ID LIKE ? OR m.NAME LIKE ? OR r.BCODE LIKE ? OR b.BTITLE LIKE ?) " +
                "AND r.ID = ? " +
                "ORDER BY r.RENTSTATE, r.RENTDATE DESC"; // 수정된 부분
        // 수정 끝
        try {
            pstmt = conn.prepareStatement(query);
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            pstmt.setString(3, searchKeyword);
            pstmt.setString(4, searchKeyword);
            pstmt.setString(5, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                rentals.add(new RentalDTO(
                    rs.getInt("RNUM"),
                    rs.getString("ID"),
                    rs.getString("NAME"),
                    rs.getString("BCODE"),
                    rs.getString("BTITLE"),
                    rs.getDate("RENTDATE"),
                    rs.getDate("RETURNDATE"),
                    rs.getString("TEL"),
                    rs.getInt("RENTSTATE")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " Search Rentals By User ID All Fields Fail");
        } finally {
            dbClose();
        }
        return rentals;
    }
 // 수정 시작: 전체 필드 검색을 위한 메소드 추가
    public List<RentalDTO> searchRentalsAllFields(String keyword) {
        List<RentalDTO> rentalList = new ArrayList<>();
        String sql = "SELECT r.*, m.NAME, m.TEL, b.BTITLE FROM rental r " +
                "JOIN member m ON r.ID = m.ID " +
                "JOIN book b ON r.BCODE = b.BCODE " +
                "WHERE r.ID LIKE ? OR " +
                "m.NAME LIKE ? OR " +
                "r.BCODE LIKE ? OR " +
                "b.BTITLE LIKE ? OR " +
                "r.RENTDATE LIKE ? OR " +
                "r.RETURNDATE LIKE ? OR " +
                "m.TEL LIKE ? OR " +
                "r.RENTSTATE LIKE ? " +
                "ORDER BY r.RENTSTATE, r.RENTDATE DESC "; // 수정된 부분

        try {
            pstmt = conn.prepareStatement(sql);
            String searchKeyword = "%" + keyword + "%";
            for (int i = 1; i <= 8; i++) {
                pstmt.setString(i, searchKeyword);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                rentalList.add(new RentalDTO(
                    rs.getInt("RNUM"),
                    rs.getString("ID"),
                    rs.getString("NAME"),
                    rs.getString("BCODE"),
                    rs.getString("BTITLE"),
                    rs.getDate("RENTDATE"),
                    rs.getDate("RETURNDATE"),
                    rs.getString("TEL"),
                    rs.getInt("RENTSTATE")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " Search Rentals All Fields Fail");
        } finally {
            dbClose();
        }
        return rentalList;
    }
    // 수정 끝

    public List<RentalDTO> searchRentals(String fieldName, String keyword) {
        List<RentalDTO> rentals = new ArrayList<>();
        String sql;

        if (fieldName.isEmpty()) {
        	sql = "SELECT r.*, m.NAME, m.TEL, b.BTITLE " +
                    "FROM rental r " +
                    "JOIN member m ON r.ID = m.ID " +
                    "JOIN book b ON r.BCODE = b.BCODE " +
                    "WHERE r.RNUM LIKE ? OR " +
                    "r.ID LIKE ? OR " +
                    "m.NAME LIKE ? OR " +
                    "r.BCODE LIKE ? OR " +
                    "b.BTITLE LIKE ? OR " +
                    "r.RENTDATE LIKE ? OR " +
                    "r.RETURNDATE LIKE ? OR " +
                    "m.TEL LIKE ? OR " +
                    "r.RENTSTATE LIKE ? " +
                    "ORDER BY r.RENTSTATE, r.RENTDATE DESC"; // 수정된 부분
        	
        } else {
        	 sql = "SELECT r.*, m.NAME, m.TEL, b.BTITLE " +
                     "FROM rental r " +
                     "JOIN member m ON r.ID = m.ID " +
                     "JOIN book b ON r.BCODE = b.BCODE " +
                     "WHERE " + fieldName + " LIKE ? " +
                     "ORDER BY r.RENTSTATE, r.RENTDATE DESC"; // 수정된 부분
        }

        try {
            pstmt = conn.prepareStatement(sql);
            if (fieldName.isEmpty()) {
                for (int i = 1; i <= 9; i++) {
                    pstmt.setString(i, "%" + keyword.trim() + "%");
                }
            } else {
                pstmt.setString(1, "%" + keyword.trim() + "%");
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                rentals.add(new RentalDTO(
                    rs.getInt("RNUM"),
                    rs.getString("ID"),
                    rs.getString("NAME"),
                    rs.getString("BCODE"),
                    rs.getString("BTITLE"),
                    rs.getDate("RENTDATE"),
                    rs.getDate("RETURNDATE"),
                    rs.getString("TEL"),
                    rs.getInt("RENTSTATE")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " Search Rentals Fail");
        } finally {
            dbClose();
        }

        return rentals;
    }

    public boolean isBookRented(String bCode) {
        String query = "SELECT * FROM rental WHERE BCODE = ? AND RENTSTATE = 0"; //0: 대여중

        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, bCode);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e + " Check Book Rented Fail");
        } finally {
            dbClose();
        }
        return false;
    }

    public List<RentalDTO> getRentalsByUserId(String userId) {
        List<RentalDTO> rentals = new ArrayList<>();
        String query = "SELECT * FROM rental WHERE ID = ? ORDER BY RENTSTATE ASC, RENTDATE DESC";

        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                rentals.add(new RentalDTO(
                    rs.getInt("RNUM"),
                    rs.getString("ID"),
                    rs.getString("NAME"),
                    rs.getString("BCODE"),
                    rs.getString("BTITLE"),
                    rs.getDate("RENTDATE"),
                    rs.getDate("RETURNDATE"),
                    rs.getString("TEL"),
                    rs.getInt("RENTSTATE")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e + " Get Rentals By User ID Fail");
        } finally {
            dbClose();
        }
        return rentals;
    }
    
    public boolean hasRentedBooks(String userId) {
        String query = "SELECT COUNT(*) AS count FROM rental WHERE ID = ? AND RENTSTATE = 0";
        boolean hasRentedBooks = false;

        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                hasRentedBooks = rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println(e + " Check Rented Books Fail");
        } finally {
            dbClose();
        }
        return hasRentedBooks;
    }
    public void deleteRentalsByUserId(String userId) {
        String query = "DELETE FROM rental WHERE ID = ?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " Delete Rentals By User ID Fail");
        } finally {
            dbClose();
        }
    }

    public void deleteRental(String id, String bCode) {
        String query = "DELETE FROM rental WHERE ID = ? AND BCODE = ?";

        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            pstmt.setString(2, bCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " Delete Rental Fail");
        } finally {
            dbClose();}
        }
        // 추가된 deleteRentalsByBookCode 메소드
        public void deleteRentalsByBookCode(String bCode) {
            String query = "DELETE FROM rental WHERE BCODE = ? AND RENTSTATE = 1"; // RENTSTATE 1: 반납완료
            try {
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, bCode);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e + " Delete Rentals By Book Code Fail");
            } finally {
                dbClose();
            }
        }
    }