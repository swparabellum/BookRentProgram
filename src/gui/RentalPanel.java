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
	private JButton returnButton; // �ݳ� ��ư �߰�
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
            options = new String[] { "��ü", "�̸�", "���̵�", "å ����" };
        } else {
            options = new String[] { "��ü", "å ����" };
        }
		comboBox = new JComboBox<>(options);
		controlPanel.add(comboBox);

		textField = new JTextField(20);
		controlPanel.add(textField);

		searchButton = new JButton("�˻�");
		controlPanel.add(searchButton);
		searchButton.addActionListener(this);

		confirmButton = new JButton("�뿩");
		controlPanel.add(confirmButton);
		confirmButton.addActionListener(this);

		returnButton = new JButton("�ݳ�");
		controlPanel.add(returnButton);
		returnButton.addActionListener(this);

		deleteButton = new JButton("����");
		controlPanel.add(deleteButton);
		deleteButton.addActionListener(this);

		this.add(controlPanel, BorderLayout.SOUTH);

		if (isAdmin) {
		String[] columnNames = { "ȸ�� ID", "ȸ�� �̸�", "���� �ڵ�", "å ����", "�뿩 ��¥", "�ݳ� ��¥", "��ȭ��ȣ", "�뿩 ����" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // ��� ���� ��Ȱ��ȭ�Ͽ� ����ڰ� ���� ������ �� ���� ��
			}
		};
	} else {
        String[] columnNames = { "���� �ڵ�", "å ����", "�뿩 ��¥", "�ݳ� ��¥", "��ȭ��ȣ", "�뿩 ����" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // ��� ���� ��Ȱ��ȭ�Ͽ� ����ڰ� ���� ������ �� ���� ��
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
		
		// �����ڿ� ȸ���� ��� ����ȭ
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
	        loadData(); // ���⿡ �޼ҵ� �߰�
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
					rental.getRentState() == 0 ? "�뿩��" : "�ݳ��Ϸ�" });
			} else {
	            tableModel.addRow(new Object[] { rental.getbCode(), rental.getbTitle(), rental.getRentalDate(), rental.getReturnDate(), rental.getTel(),
	                    rental.getRentState() == 0 ? "�뿩��" : "�ݳ��Ϸ�" });
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
	                case "��ü":
	                    rentals = rentalDAO.searchRentalsAllFields(keyword);
	                    break;
	                case "�̸�":
	                    fieldName = "r.NAME";
	                    rentals = rentalDAO.searchRentals(fieldName, keyword);
	                    break;
	                case "���̵�":
	                    fieldName = "r.ID";
	                    rentals = rentalDAO.searchRentals(fieldName, keyword);
	                    break;
	                case "å ����":
	                    fieldName = "r.BTITLE";
	                    rentals = rentalDAO.searchRentals(fieldName, keyword);
	                    break;
	                default:
	                    rentals = new ArrayList<>();
	                    break;
	            }
	        } else {
	            if (fieldName.equals("��ü")) {
	                rentals = rentalDAO.searchRentalsByUserIdAllFields(member.getId(), keyword);
	            } else {
	                switch (fieldName) {
	                    case "�̸�":
	                        fieldName = "r.NAME";
	                        break;
	                    case "���̵�":
	                        fieldName = "r.ID";
	                        break;
	                    case "å ����":
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
	    
        // ���� ��

	        // ���� ��

	        try {
				if (rentals.isEmpty()) { // ���� ����: �˻� ����� ���� �� �˸� �޽��� �߰�
	                JOptionPane.showMessageDialog(this, "�ش� �˻� ����� �����ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
	                loadData();
	                textField.setText("");
	            } else {
	                displayRentals(rentals);
	            } // ���� ��
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "�˻� �� ���� �߻�: " + ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
			}
	    } else if (e.getSource() == returnButton) {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(this, "�ݳ��� �뿩 ������ �����ϼ���.");
	        } else {
	            String id;
	            String bCode;
	            String rentStateStr;
	            if (isAdmin) {
	                id = (String) table.getValueAt(selectedRow, 0); // �������� ��� ���̺��� ID ������
	                bCode = (String) table.getValueAt(selectedRow, 2);
	                rentStateStr = (String) table.getValueAt(selectedRow, 7);
	            } else {
	                id = member.getId().trim(); // ȸ���� ��� ���� �α��ε� ȸ���� ID ���
	                bCode = (String) table.getValueAt(selectedRow, 0);
	                rentStateStr = (String) table.getValueAt(selectedRow, 5);
	            }
	            
	            int rentState;
	            if (rentStateStr.equals("�뿩��")) {
	                rentState = 0;
	            } else {
	                JOptionPane.showMessageDialog(this, "�̹� �ݳ��Ǿ����ϴ�.");
	                return;
	            }

	            int newRentState = (rentState == 0) ? 1 : 0;
	            String returnBook = (String) table.getValueAt(selectedRow, isAdmin ? 3 : 1);
	            returnBook = "' " + returnBook + " '" + " ��(��) �ݳ��Ͻðڽ��ϱ�?";
	            String cancelReturn = (String) table.getValueAt(selectedRow, isAdmin ? 3 : 1);
	            cancelReturn = "' " + cancelReturn + " '" + " ��(��) �ݳ�����Ͻðڽ��ϱ�?";
	            int confirm = JOptionPane.showConfirmDialog(this, (newRentState == 1) ? returnBook : cancelReturn,
	                    (newRentState == 1) ? "�ݳ��Ϸ�" : "�ݳ����", JOptionPane.YES_NO_OPTION);
	            if (confirm == JOptionPane.YES_OPTION) {
	                try {
	                    rentalDAO.updateRentalState(id, bCode, 1); // 1: �ݳ��Ϸ�
	                    BookDAO bookDAO = new BookDAO();
	                    bookDAO.updateBookStatus(bCode, 1); // 1: �뿩 ����
	                    loadData();
	                    JOptionPane.showMessageDialog(this, "�ݳ��Ǿ����ϴ�.");
	                    BookPanel.getsearch().doClick();
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(this, "�ݳ� ����: " + ex.getMessage(), "����",
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
	        JOptionPane.showMessageDialog(this, "������ �뿩 ������ �����ϼ���.");
	    } else {
	        String memberId;
	        if (isAdmin) {
	            memberId = (String) table.getValueAt(selectedRow, 0); // �������� ��� ���̺��� ID ������
	        } else {
	            memberId = member.getId().trim(); // ȸ���� ��� ���� �α��ε� ȸ���� ID ���
	        }

	        String bookCode = isAdmin ? (String) table.getValueAt(selectedRow, 2) : (String) table.getValueAt(selectedRow, 0);
	        String rentalState = isAdmin ? (String) table.getValueAt(selectedRow, 7) : (String) table.getValueAt(selectedRow, 5);

	        if (rentalState.equals("�뿩��")) {
	            JOptionPane.showMessageDialog(this, "�ݳ� �� �������ּ���");
	            return;
	        } else if (rentalState.equals("�ݳ��Ϸ�")) {
	            String delBook = (String) table.getValueAt(selectedRow, isAdmin ? 3 : 1);
	            delBook = "' " + delBook + " '" + "�� �뿩 ������ �����Ͻðڽ��ϱ�?";
	            int confirm = JOptionPane.showConfirmDialog(this, delBook, "���� Ȯ��", JOptionPane.YES_NO_OPTION);

	            if (confirm == JOptionPane.YES_OPTION) {
	                try {
	                    rentalDAO.deleteRental(memberId, bookCode);
	                    JOptionPane.showMessageDialog(this, "�뿩 ������ �����Ǿ����ϴ�.");
	                    loadData();
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(this, "���� �� ���� �߻�: " + ex.getMessage(), "����",
	                            JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        }
	    }
	}
	}

	public boolean rentBook(String bookCode) {
		String memberId = JOptionPane.showInputDialog(this, "ȸ�� ID�� �Է��ϼ���:", "ȸ�� ID �Է�", JOptionPane.PLAIN_MESSAGE);
		if (memberId == null || memberId.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "ȸ�� ID�� �Է��ϼ���.", "����", JOptionPane.ERROR_MESSAGE);
			return false; // ���� �� false ��ȯ
		}
		RentalDAO rentalDAO = new RentalDAO();
		MemberDAO memberDAO = new MemberDAO();
		MemberDTO member = memberDAO.getMember(memberId.trim());

		if (member == null) {
			JOptionPane.showMessageDialog(this, "�������� �ʴ� ȸ�� ID�Դϴ�.", "����", JOptionPane.ERROR_MESSAGE);
			return false; 
		}

		if (rentalDAO.isBookRented(bookCode)) {
			JOptionPane.showMessageDialog(this, "�̹� �뿩���� �����Դϴ�.", "����", JOptionPane.ERROR_MESSAGE);
			return false; 
		}

		String bookTitle = bookDAO.getBookByCode(bookCode).getBTitle();
		rentalDAO.addRental(new RentalDTO(0, member.getId(), member.getName(), bookCode, bookTitle, new Date(),
				new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), member.getTel(), 0));
		bookDAO.updateBookStatus(bookCode, 0);
		JOptionPane.showMessageDialog(this, bookTitle + "��(��) �뿩�Ǿ����ϴ�.");
		BookPanel.getsearch().doClick();
		loadData();
		return true; // ���� �� true ��ȯ
	}
}