package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import dao.BookDAO;
import dao.MemberDAO;
import dao.RentalDAO;
import dto.MemberDTO;
import dto.RentalDTO;

public class RentalPanel extends JPanel implements ActionListener {
	private JComboBox<String> comboBox;
	private JTextField textField;
	private static JButton searchButton;
	private JButton confirmButton;
	private JButton returnButton; // 반납 버튼 추가
	private JButton deleteButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private RentalDAO rentalDAO;
	private BookDAO bookDAO;
	private MemberDTO member;
	private boolean isAdmin;

	public RentalPanel(MemberDTO member, boolean isAdmin) {
		this.isAdmin = isAdmin;
		this.member = member;

		this.setLayout(new BorderLayout());
		rentalDAO = new RentalDAO();
		bookDAO = new BookDAO();

		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		String[] options;
		if (isAdmin) {
            options = new String[] { "전체", "이름", "아이디", "책 제목" };
        } else {
            options = new String[] { "전체", "책 제목" };
        }
		comboBox = new JComboBox<>(options);
		controlPanel.add(comboBox);

		textField = new JTextField(20);
		controlPanel.add(textField);

		searchButton = new JButton("검색");
		controlPanel.add(searchButton);
		searchButton.addActionListener(this);

		confirmButton = new JButton("대여");
		controlPanel.add(confirmButton);
		confirmButton.addActionListener(this);

		returnButton = new JButton("반납");
		controlPanel.add(returnButton);
		returnButton.addActionListener(this);

		deleteButton = new JButton("삭제");
		controlPanel.add(deleteButton);
		deleteButton.addActionListener(this);

		this.add(controlPanel, BorderLayout.SOUTH);

		if (isAdmin) {
		String[] columnNames = { "회원 ID", "회원 이름", "도서 코드", "책 제목", "대여 날짜", "반납 날짜", "전화번호", "대여 상태" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // 모든 셀을 비활성화하여 사용자가 직접 수정할 수 없게 함
			}
		};
	} else {
        String[] columnNames = { "도서 코드", "책 제목", "대여 날짜", "반납 날짜", "전화번호", "대여 상태" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 모든 셀을 비활성화하여 사용자가 직접 수정할 수 없게 함
            }
        };
    }


		table = new JTable(tableModel);

		JScrollPane tableScrollPane = new JScrollPane(table);
		this.add(tableScrollPane, BorderLayout.CENTER);

		loadData();

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchButton.doClick();
				textField.requestFocus();
			}
		});
		
		// 관리자와 회원의 기능 차별화
	    if (isAdmin) {
	        loadData();
	    } else {
	        displayMember(member);
	        confirmButton.setVisible(false);
	    }
	    }
	    
	    private void displayMember(MemberDTO member2) {
			// TODO Auto-generated method stub
			
	    }
	    public void triggerSearch() {
	        searchButton.doClick();
	    }
	    
	public void loadData() {
		List<RentalDTO> rentals = isAdmin ? rentalDAO.getAllRentals() : rentalDAO.getRentalsByUserId(member.getId());
		displayRentals(rentals);
	}
	
	  public void loadRentals() {
	        loadData(); // 여기에 메소드 추가
	    }
	
	 
	public static JButton getSearchButton() {
		return searchButton;
	}

	private void displayRentals(List<RentalDTO> rentals) {
		tableModel.setRowCount(0);
		for (RentalDTO rental : rentals) {
			if (isAdmin) {
			tableModel.addRow(new Object[] { rental.getId(), rental.getName(), rental.getbCode(), rental.getbTitle(),
					rental.getRentalDate(), rental.getReturnDate(), rental.getTel(),
					rental.getRentState() == 0 ? "대여중" : "반납완료" });
			} else {
	            tableModel.addRow(new Object[] { rental.getbCode(), rental.getbTitle(), rental.getRentalDate(), rental.getReturnDate(), rental.getTel(),
	                    rental.getRentState() == 0 ? "대여중" : "반납완료" });
	        }
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == searchButton) {
	        String fieldName = comboBox.getSelectedItem().toString();
	        String keyword = textField.getText();

	        List<RentalDTO> rentals;
	        if (isAdmin) {
	            switch (fieldName) {
	                case "전체":
	                    rentals = rentalDAO.searchRentalsAllFields(keyword);
	                    break;
	                case "이름":
	                    fieldName = "r.NAME";
	                    rentals = rentalDAO.searchRentals(fieldName, keyword);
	                    break;
	                case "아이디":
	                    fieldName = "r.ID";
	                    rentals = rentalDAO.searchRentals(fieldName, keyword);
	                    break;
	                case "책 제목":
	                    fieldName = "r.BTITLE";
	                    rentals = rentalDAO.searchRentals(fieldName, keyword);
	                    break;
	                default:
	                    rentals = new ArrayList<>();
	                    break;
	            }
	        } else {
	            if (fieldName.equals("전체")) {
	                rentals = rentalDAO.searchRentalsByUserIdAllFields(member.getId(), keyword);
	            } else {
	                switch (fieldName) {
	                    case "이름":
	                        fieldName = "r.NAME";
	                        break;
	                    case "아이디":
	                        fieldName = "r.ID";
	                        break;
	                    case "책 제목":
	                        fieldName = "r.BTITLE";
	                        break;
	                    default:
	                        fieldName = "";
	                        break;
	                }
	                rentals = rentalDAO.searchRentalsByUserId(member.getId(), fieldName, keyword);
	            }
	        }

	        if (rentals.isEmpty()) {
	            
	            loadData();
	            textField.setText("");
	        } else {
	            displayRentals(rentals);
	        }
	    
        // 수정 끝

	        // 수정 끝

	        try {
				if (rentals.isEmpty()) { // 수정 시작: 검색 결과가 없을 시 알림 메시지 추가
	                JOptionPane.showMessageDialog(this, "해당 검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
	                loadData();
	                textField.setText("");
	            } else {
	                displayRentals(rentals);
	            } // 수정 끝
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "검색 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
			}
	    } else if (e.getSource() == returnButton) {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(this, "반납할 대여 정보를 선택하세요.");
	        } else {
	            String id;
	            String bCode;
	            String rentStateStr;
	            if (isAdmin) {
	                id = (String) table.getValueAt(selectedRow, 0); // 관리자일 경우 테이블에서 ID 가져옴
	                bCode = (String) table.getValueAt(selectedRow, 2);
	                rentStateStr = (String) table.getValueAt(selectedRow, 7);
	            } else {
	                id = member.getId().trim(); // 회원일 경우 현재 로그인된 회원의 ID 사용
	                bCode = (String) table.getValueAt(selectedRow, 0);
	                rentStateStr = (String) table.getValueAt(selectedRow, 5);
	            }
	            
	            int rentState;
	            if (rentStateStr.equals("대여중")) {
	                rentState = 0;
	            } else {
	                JOptionPane.showMessageDialog(this, "이미 반납되었습니다.");
	                return;
	            }

	            int newRentState = (rentState == 0) ? 1 : 0;
	            String returnBook = (String) table.getValueAt(selectedRow, isAdmin ? 3 : 1);
	            returnBook = "' " + returnBook + " '" + " 을(를) 반납하시겠습니까?";
	            String cancelReturn = (String) table.getValueAt(selectedRow, isAdmin ? 3 : 1);
	            cancelReturn = "' " + cancelReturn + " '" + " 을(를) 반납취소하시겠습니까?";
	            int confirm = JOptionPane.showConfirmDialog(this, (newRentState == 1) ? returnBook : cancelReturn,
	                    (newRentState == 1) ? "반납완료" : "반납취소", JOptionPane.YES_NO_OPTION);
	            if (confirm == JOptionPane.YES_OPTION) {
	                try {
	                    rentalDAO.updateRentalState(id, bCode, 1); // 1: 반납완료
	                    BookDAO bookDAO = new BookDAO();
	                    bookDAO.updateBookStatus(bCode, 1); // 1: 대여 가능
	                    loadData();
	                    JOptionPane.showMessageDialog(this, "반납되었습니다.");
	                    BookPanel.getsearch().doClick();
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(this, "반납 실패: " + ex.getMessage(), "오류",
	                            JOptionPane.ERROR_MESSAGE);
	                }
	            }
	            table.setRowSelectionInterval(selectedRow, selectedRow);
	        }
	    
			
			
	} else if (e.getSource() == confirmButton) {
	    new BookListDialog((JFrame) SwingUtilities.getWindowAncestor(this), this).setVisible(true);
	} else if (e.getSource() == deleteButton) {
	    int selectedRow = table.getSelectedRow();
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(this, "삭제할 대여 정보를 선택하세요.");
	    } else {
	        String memberId;
	        if (isAdmin) {
	            memberId = (String) table.getValueAt(selectedRow, 0); // 관리자일 경우 테이블에서 ID 가져옴
	        } else {
	            memberId = member.getId().trim(); // 회원일 경우 현재 로그인된 회원의 ID 사용
	        }

	        String bookCode = isAdmin ? (String) table.getValueAt(selectedRow, 2) : (String) table.getValueAt(selectedRow, 0);
	        String rentalState = isAdmin ? (String) table.getValueAt(selectedRow, 7) : (String) table.getValueAt(selectedRow, 5);

	        if (rentalState.equals("대여중")) {
	            JOptionPane.showMessageDialog(this, "반납 후 삭제해주세요");
	            return;
	        } else if (rentalState.equals("반납완료")) {
	            String delBook = (String) table.getValueAt(selectedRow, isAdmin ? 3 : 1);
	            delBook = "' " + delBook + " '" + "의 대여 정보를 삭제하시겠습니까?";
	            int confirm = JOptionPane.showConfirmDialog(this, delBook, "삭제 확인", JOptionPane.YES_NO_OPTION);

	            if (confirm == JOptionPane.YES_OPTION) {
	                try {
	                    rentalDAO.deleteRental(memberId, bookCode);
	                    JOptionPane.showMessageDialog(this, "대여 정보가 삭제되었습니다.");
	                    loadData();
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(this, "삭제 중 오류 발생: " + ex.getMessage(), "오류",
	                            JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        }
	    }
	}
	}

	public boolean rentBook(String bookCode) {
		String memberId = JOptionPane.showInputDialog(this, "회원 ID를 입력하세요:", "회원 ID 입력", JOptionPane.PLAIN_MESSAGE);
		if (memberId == null || memberId.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "회원 ID를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
			return false; // 실패 시 false 반환
		}
		RentalDAO rentalDAO = new RentalDAO();
		MemberDAO memberDAO = new MemberDAO();
		MemberDTO member = memberDAO.getMember(memberId.trim());

		if (member == null) {
			JOptionPane.showMessageDialog(this, "존재하지 않는 회원 ID입니다.", "오류", JOptionPane.ERROR_MESSAGE);
			return false; 
		}

		if (rentalDAO.isBookRented(bookCode)) {
			JOptionPane.showMessageDialog(this, "이미 대여중인 도서입니다.", "오류", JOptionPane.ERROR_MESSAGE);
			return false; 
		}

		String bookTitle = bookDAO.getBookByCode(bookCode).getBTitle();
		rentalDAO.addRental(new RentalDTO(0, member.getId(), member.getName(), bookCode, bookTitle, new Date(),
				new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), member.getTel(), 0));
		bookDAO.updateBookStatus(bookCode, 0);
		JOptionPane.showMessageDialog(this, bookTitle + "이(가) 대여되었습니다.");
		BookPanel.getsearch().doClick();
		loadData();
		return true; // 성공 시 true 반환
	}
}