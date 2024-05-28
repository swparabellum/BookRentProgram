package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.BookDAO;
import dao.RentalDAO;
import dto.BookDTO;

public class BookListDialog extends JDialog implements ActionListener {
    private JComboBox<String> comboBox;
    private JTextField textField;
    private JButton searchButton;
    private JButton selectButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;
    private RentalPanel rentalPanel;

    public BookListDialog(JFrame parent, RentalPanel rentalPanel) {
        super(parent, "å ���", true);
        this.rentalPanel = rentalPanel;
        this.bookDAO = new BookDAO();

        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        String[] options = {"��ü", "�о�", "å����", "����"};
        comboBox = new JComboBox<>(options);
        controlPanel.add(comboBox);

        textField = new JTextField(20);
        controlPanel.add(textField);

        searchButton = new JButton("�˻�");
        controlPanel.add(searchButton);
        searchButton.addActionListener(this);

        selectButton = new JButton("����");
        controlPanel.add(selectButton);
        selectButton.addActionListener(this);

        add(controlPanel, BorderLayout.SOUTH);

        String[] columnNames = {"�����ڵ�", "�о�", "å����", "����", "���ǻ�", "���࿬��", "���ټҰ�", "�뿩����"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        loadData();

        setSize(800, 600);
        setLocationRelativeTo(parent);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchButton.doClick();
                textField.requestFocus();
            }
        });
    }

    public void loadData() {
        List<BookDTO> books = bookDAO.getAllBooks();
        displayBooks(books);
    }

    private void displayBooks(List<BookDTO> books) {
        tableModel.setRowCount(0);
        for (BookDTO book : books) {
            tableModel.addRow(new Object[]{
                book.getBCode(),
                book.getBCategory(),
                book.getBTitle(),
                book.getBWriter(),
                book.getBPub(),
                book.getBYear(),
                book.getBIntro(),
                book.getBRes() == 1 ? "�뿩 ����" : "�뿩��"
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton || e.getSource() == textField) {
            String fieldName = comboBox.getSelectedItem().toString();
            String keyword = textField.getText().trim();

            if (keyword.isEmpty()) {
                loadData();
                return;
            }

            try {
                List<BookDTO> books;
                if (fieldName.equals("��ü")) {
                    books = bookDAO.searchBooks("", keyword);
                } else {
                    books = bookDAO.searchBooks(fieldName, keyword);
                }

                if (books.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "�ش� �˻� ����� �����ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    textField.setText("");
                } else {
                    displayBooks(books);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "�˻� �� ���� �߻�: " + ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == selectButton) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "�뿩�� ������ �����ϼ���.");
            } else {
            	 String bookCode = (String) table.getValueAt(row, 0);
            	 RentalDAO rentalDAO = new RentalDAO();
 				 boolean isRented = rentalDAO.isBookRented(bookCode);
 				 if (isRented) {
 					 JOptionPane.showMessageDialog(this, "�̹� �뿩���� �����Դϴ�.");
 				 } else {
                 boolean rentalSuccess = rentalPanel.rentBook(bookCode);
                 if (rentalSuccess) {
                     dispose();
                 }
                 // ���� ����: ���� �� ��� â�� ���� �ʰ� ����
                
 				 }
            }
        }
    }
}
