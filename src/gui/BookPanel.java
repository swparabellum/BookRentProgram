package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import dao.BookDAO;
import dao.RentalDAO;
import dto.BookDTO;
import dto.MemberDTO;
import dto.RentalDTO;

public class BookPanel extends JPanel implements ActionListener {
    String[] columnNames = { "도서코드", "분야", "책제목", "저자", "출판사", "발행연도", "한줄소개", "대여여부" };
    DefaultTableModel dt = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // 모든 셀을 비활성화하여 사용자가 직접 수정할 수 없게 함
        }
    };

    JTable jt = new JTable(dt);
    JScrollPane jsp = new JScrollPane(jt);
    JPanel p = new JPanel();
    String[] comboName = { "전체", "분야", "책제목", "저자" };

    JComboBox<String> combo = new JComboBox<>(comboName);
    JTextField jtf = new JTextField(20);
    static JButton search = new JButton("검색");
    JButton confirm = new JButton("대여");
    JButton insert = new JButton("도서 추가");
    JButton update = new JButton("수정");
    JButton delete = new JButton("삭제");
    BookDAO bDAO = new BookDAO();
    private boolean isAdmin;
    private MemberDTO member;

    public BookPanel(MemberDTO member, boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.member = member;

        this.setLayout(new BorderLayout());
        p.add(combo);
        p.add(jtf);
        p.add(search);
        p.add(confirm);
        p.add(insert);
        p.add(update);
        p.add(delete);
        add(jsp, "Center");
        add(p, "South");
        setVisible(true);
        insert.addActionListener(this);
        update.addActionListener(this);
        search.addActionListener(this);
        confirm.addActionListener(this);
        delete.addActionListener(this);
        
        updateBookStatusFromRentals(); // 추가: rental 테이블 기반 도서 상태 업데이트
        
        getAllBooks();

        if (!isAdmin) {
            insert.setVisible(false);
            update.setVisible(false);
            delete.setVisible(false);
        } else {
            confirm.setVisible(false);
        }

        jtf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                search.doClick();
                jtf.requestFocus();
            }
        });
    }
    
 // 추가: rental 테이블 기반 도서 상태 업데이트 메소드
    private void updateBookStatusFromRentals() {
        RentalDAO rentalDAO = new RentalDAO();
        List<RentalDTO> rentals = rentalDAO.getAllRentals();
        BookDAO bookDAO = new BookDAO();

        for (RentalDTO rental : rentals) {
            if (rental.getRentState() == 0) { // 대여중인 경우
                bookDAO.updateBookStatus(rental.getbCode(), 0); // 대여중으로 설정
            }
        }
    }

    public static JButton getsearch() {
        return search;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirm) {
            int row = jt.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "대여할 도서를 선택하세요.");
            } else {
                String bCode = (String) jt.getValueAt(row, 0);
                String bTitle = (String) jt.getValueAt(row, 2);

                RentalDAO rentalDAO = new RentalDAO();
				boolean isRented = rentalDAO.isBookRented(bCode);
				if (isRented) {
					JOptionPane.showMessageDialog(this, "이미 대여중인 도서입니다.");
				} else {
					int confirm = JOptionPane.showConfirmDialog(this, "' " + bTitle + " '" + "을(를) 대여하시겠습니까?", "대여 확인", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						rentalDAO.addRental(new RentalDTO(0, member.getId(), member.getName(), bCode, bTitle, new Date(),
								new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), member.getTel(), 0));
						updateBookStatus(bCode, 0);
						JOptionPane.showMessageDialog(this, "' " + bTitle + " '" + "이(가) 대여되었습니다.");
						search.doClick();
						RentalPanel.getSearchButton().doClick();
					}
					
				}
                String fieldName = combo.getSelectedItem().toString();
                String keyword = jtf.getText();

                List<BookDTO> books = bDAO.searchBooks(fieldName, keyword);
                displayBooks(books);
                jt.setRowSelectionInterval(row, row);
            }

        } else if (e.getSource() == search) {
        	 String fieldName = combo.getSelectedItem().toString();
             String keyword = jtf.getText().trim(); // 입력값을 가져올 때 공백 제거

             // 입력값이 비어 있을 때 전체 목록을 표시
             if (keyword.isEmpty()) {
                 getAllBooks();
                 return;
             }

             List<BookDTO> books = bDAO.searchBooks(fieldName, keyword); // searchBooks 메소드의 반환값을 변수에 저장
             if (books.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "해당 검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                 getAllBooks(); // 검색 결과가 없으면 전체 도서 목록을 보여줌
                 jtf.setText(""); // 검색 텍스트 필드를 초기화
             } else {
                 displayBooks(books); // 검색 결과가 있으면 해당 결과를 보여줌
             }

        } else if (e.getSource() == insert) {
            // 수정 시작: 추가된 행을 선택
            BookDialog bookDialog = new BookDialog(this, "추가");
            bookDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    String addedBookCode = bookDialog.getAddedBookCode();
                    System.out.println("추가된 도서 코드로 행 선택 시도: " + addedBookCode);
                    if (addedBookCode != null && !addedBookCode.isEmpty()) {
                        selectBook(addedBookCode); // 추가된 도서를 선택합니다.
                    }
                }
            });
            bookDialog.setVisible(true);
            // 수정 끝
        } else if (e.getSource() == update) {
            int row = jt.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "수정할 도서를 선택하세요.");
            } else {
                String bCode = (String) jt.getValueAt(row, 0);
                String bTitle = (String) jt.getValueAt(row, 2);
                RentalDAO rentalDAO = new RentalDAO();
                boolean isRented = rentalDAO.isBookRented(bCode);

                if (isRented) {
                    JOptionPane.showMessageDialog(this, "대여중으로 수정할 수 없습니다.");
                } else {
                    BookDialog bookDialog = new BookDialog(this, "수정", bCode);
                    bookDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            searchBooks(combo.getSelectedItem().toString(), jtf.getText());
                            selectBook(bCode);
                        }
                    });
                    bookDialog.setVisible(true);
                }
            }
        } else if (e.getSource() == delete) {
            int row = jt.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "삭제할 도서를 선택하세요.");
            } else {
                String bCode = (String) jt.getValueAt(row, 0);
                RentalDAO rentalDAO = new RentalDAO();
                boolean isRented = rentalDAO.isBookRented(bCode);

                if (isRented) {
                    JOptionPane.showMessageDialog(this, "대여중으로 삭제할 수 없습니다.");
                } else {
                	rentalDAO.deleteRentalsByBookCode(bCode); // 반납 완료된 대여 기록 삭제
                    bDAO.bookListDelete(bCode);
                    String fieldName = combo.getSelectedItem().toString();
                    String keyword = jtf.getText();

                    if (fieldName.equals("전체")) {
                        getAllBooks();
                    } else {
                        List<BookDTO> books = bDAO.searchBooks(fieldName, keyword);
                        displayBooks(books);
                    }
                    jt.setRowSelectionInterval(row, row);
                }
            }
        }
    }

    public void getAllBooks() {
        List<BookDTO> books = bDAO.getAllBooks();
        displayBooks(books);
        System.out.println("테이블 데이터 갱신됨. 행 수: " + jt.getRowCount()); // 테이블 데이터 갱신 확인
    }
    public void sortBooks() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(dt);
        jt.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // 첫 번째 열을 기준으로 오름차순 정렬
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    public void searchBooks(String fieldName, String keyword) {
        List<BookDTO> books = bDAO.searchBooks(fieldName, keyword);
        displayBooks(books);
    }

    public void displayBooks(List<BookDTO> books) {
        dt.setRowCount(0);
        for (BookDTO book : books) {
            dt.addRow(new Object[] { book.getBCode(), book.getBCategory(), book.getBTitle(), book.getBWriter(),
                    book.getBPub(), book.getBYear(), book.getBIntro(), book.getBRes() == 1 ? "대여 가능" : "대여중" });
        }
    }

    public void updateBookStatus(String bCode, int status) {
        bDAO.updateBookStatus(bCode, status);
    }

    // 추가된 도서를 선택하는 메소드
    public void selectBook(String bCode) {
        System.out.println("선택 시도 중인 도서 코드: " + bCode);
        for (int i = 0; i < jt.getRowCount(); i++) {
            if (jt.getValueAt(i, 0).equals(bCode)) {
                jt.setRowSelectionInterval(i, i);
                System.out.println("도서 코드 일치: " + bCode + " - 행 선택됨");
                break;
            }
        }
    }
}