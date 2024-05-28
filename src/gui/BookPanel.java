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
    String[] columnNames = { "�����ڵ�", "�о�", "å����", "����", "���ǻ�", "���࿬��", "���ټҰ�", "�뿩����" };
    DefaultTableModel dt = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // ��� ���� ��Ȱ��ȭ�Ͽ� ����ڰ� ���� ������ �� ���� ��
        }
    };

    JTable jt = new JTable(dt);
    JScrollPane jsp = new JScrollPane(jt);
    JPanel p = new JPanel();
    String[] comboName = { "��ü", "�о�", "å����", "����" };

    JComboBox<String> combo = new JComboBox<>(comboName);
    JTextField jtf = new JTextField(20);
    static JButton search = new JButton("�˻�");
    JButton confirm = new JButton("�뿩");
    JButton insert = new JButton("���� �߰�");
    JButton update = new JButton("����");
    JButton delete = new JButton("����");
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
        
        updateBookStatusFromRentals(); // �߰�: rental ���̺� ��� ���� ���� ������Ʈ
        
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
    
 // �߰�: rental ���̺� ��� ���� ���� ������Ʈ �޼ҵ�
    private void updateBookStatusFromRentals() {
        RentalDAO rentalDAO = new RentalDAO();
        List<RentalDTO> rentals = rentalDAO.getAllRentals();
        BookDAO bookDAO = new BookDAO();

        for (RentalDTO rental : rentals) {
            if (rental.getRentState() == 0) { // �뿩���� ���
                bookDAO.updateBookStatus(rental.getbCode(), 0); // �뿩������ ����
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
                JOptionPane.showMessageDialog(this, "�뿩�� ������ �����ϼ���.");
            } else {
                String bCode = (String) jt.getValueAt(row, 0);
                String bTitle = (String) jt.getValueAt(row, 2);

                RentalDAO rentalDAO = new RentalDAO();
				boolean isRented = rentalDAO.isBookRented(bCode);
				if (isRented) {
					JOptionPane.showMessageDialog(this, "�̹� �뿩���� �����Դϴ�.");
				} else {
					int confirm = JOptionPane.showConfirmDialog(this, "' " + bTitle + " '" + "��(��) �뿩�Ͻðڽ��ϱ�?", "�뿩 Ȯ��", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						rentalDAO.addRental(new RentalDTO(0, member.getId(), member.getName(), bCode, bTitle, new Date(),
								new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), member.getTel(), 0));
						updateBookStatus(bCode, 0);
						JOptionPane.showMessageDialog(this, "' " + bTitle + " '" + "��(��) �뿩�Ǿ����ϴ�.");
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
             String keyword = jtf.getText().trim(); // �Է°��� ������ �� ���� ����

             // �Է°��� ��� ���� �� ��ü ����� ǥ��
             if (keyword.isEmpty()) {
                 getAllBooks();
                 return;
             }

             List<BookDTO> books = bDAO.searchBooks(fieldName, keyword); // searchBooks �޼ҵ��� ��ȯ���� ������ ����
             if (books.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "�ش� �˻� ����� �����ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
                 getAllBooks(); // �˻� ����� ������ ��ü ���� ����� ������
                 jtf.setText(""); // �˻� �ؽ�Ʈ �ʵ带 �ʱ�ȭ
             } else {
                 displayBooks(books); // �˻� ����� ������ �ش� ����� ������
             }

        } else if (e.getSource() == insert) {
            // ���� ����: �߰��� ���� ����
            BookDialog bookDialog = new BookDialog(this, "�߰�");
            bookDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    String addedBookCode = bookDialog.getAddedBookCode();
                    System.out.println("�߰��� ���� �ڵ�� �� ���� �õ�: " + addedBookCode);
                    if (addedBookCode != null && !addedBookCode.isEmpty()) {
                        selectBook(addedBookCode); // �߰��� ������ �����մϴ�.
                    }
                }
            });
            bookDialog.setVisible(true);
            // ���� ��
        } else if (e.getSource() == update) {
            int row = jt.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "������ ������ �����ϼ���.");
            } else {
                String bCode = (String) jt.getValueAt(row, 0);
                String bTitle = (String) jt.getValueAt(row, 2);
                RentalDAO rentalDAO = new RentalDAO();
                boolean isRented = rentalDAO.isBookRented(bCode);

                if (isRented) {
                    JOptionPane.showMessageDialog(this, "�뿩������ ������ �� �����ϴ�.");
                } else {
                    BookDialog bookDialog = new BookDialog(this, "����", bCode);
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
                JOptionPane.showMessageDialog(this, "������ ������ �����ϼ���.");
            } else {
                String bCode = (String) jt.getValueAt(row, 0);
                RentalDAO rentalDAO = new RentalDAO();
                boolean isRented = rentalDAO.isBookRented(bCode);

                if (isRented) {
                    JOptionPane.showMessageDialog(this, "�뿩������ ������ �� �����ϴ�.");
                } else {
                	rentalDAO.deleteRentalsByBookCode(bCode); // �ݳ� �Ϸ�� �뿩 ��� ����
                    bDAO.bookListDelete(bCode);
                    String fieldName = combo.getSelectedItem().toString();
                    String keyword = jtf.getText();

                    if (fieldName.equals("��ü")) {
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
        System.out.println("���̺� ������ ���ŵ�. �� ��: " + jt.getRowCount()); // ���̺� ������ ���� Ȯ��
    }
    public void sortBooks() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(dt);
        jt.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // ù ��° ���� �������� �������� ����
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
                    book.getBPub(), book.getBYear(), book.getBIntro(), book.getBRes() == 1 ? "�뿩 ����" : "�뿩��" });
        }
    }

    public void updateBookStatus(String bCode, int status) {
        bDAO.updateBookStatus(bCode, status);
    }

    // �߰��� ������ �����ϴ� �޼ҵ�
    public void selectBook(String bCode) {
        System.out.println("���� �õ� ���� ���� �ڵ�: " + bCode);
        for (int i = 0; i < jt.getRowCount(); i++) {
            if (jt.getValueAt(i, 0).equals(bCode)) {
                jt.setRowSelectionInterval(i, i);
                System.out.println("���� �ڵ� ��ġ: " + bCode + " - �� ���õ�");
                break;
            }
        }
    }
}