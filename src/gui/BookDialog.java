package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dao.BookDAO;
import dto.BookDTO;

public class BookDialog extends JDialog implements ActionListener {
    private String addedBookCode; // �߰��� ���� �ڵ带 ������ ���� �߰�
    JPanel pl = new JPanel(new GridLayout(7, 1));
    JPanel ptf = new JPanel(new GridLayout(7, 1));
    JPanel ps = new JPanel();

    JLabel label_bcode = new JLabel("*�����ڵ�");
    JLabel label_bcategory = new JLabel("*��  ��");
    JLabel label_btitle = new JLabel("*å����");
    JLabel label_bwriter = new JLabel("*��  ��");
    JLabel label_bpub = new JLabel("*���ǻ�");
    JLabel label_byear = new JLabel("���࿬��");
    JLabel label_bintro = new JLabel("���ټҰ�");

    JTextField tf_bcode = new JTextField();
    JTextField tf_bcategory = new JTextField();
    JTextField tf_btitle = new JTextField();
    JTextField tf_bwriter = new JTextField();
    JTextField tf_bpub = new JTextField();
    JTextField tf_byear = new JTextField();
    JTextField tf_bintro = new JTextField();

    JButton confirm = new JButton("�� ��");
    JButton reset = new JButton("�� ��");
    BookPanel me;

    JPanel bcodeCkp = new JPanel(new BorderLayout());
    JButton bcodeBtn = new JButton("�ߺ�Ȯ��");
    BookDAO bDAO = new BookDAO();

    public BookDialog(BookPanel me, String str) {
        this.me = me;

        pl.add(label_bcode);
        pl.add(label_bcategory);
        pl.add(label_btitle);
        pl.add(label_bwriter);
        pl.add(label_bpub);
        pl.add(label_byear);
        pl.add(label_bintro);

        bcodeCkp.add(tf_bcode, "Center");
        bcodeCkp.add(bcodeBtn, "East");

        ptf.add(bcodeCkp);
        ptf.add(tf_bcategory);
        ptf.add(tf_btitle);
        ptf.add(tf_bwriter);
        ptf.add(tf_bpub);
        ptf.add(tf_byear);
        ptf.add(tf_bintro);

        ps.add(confirm);
        ps.add(reset);

        add(pl, "West");
        add(ptf, "Center");
        add(ps, "South");

        setBounds(750, 265, 400, 500);
        setVisible(true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        confirm.addActionListener(this);
        reset.addActionListener(this);
        bcodeBtn.addActionListener(this);
        setLocationRelativeTo(null);
    }

    public BookDialog(BookPanel me, String str, String bCode) {
        this(me, str);

        BookDTO book = bDAO.getBookByCode(bCode);
        if (book != null) {
            tf_bcode.setText(book.getBCode());
            tf_bcategory.setText(book.getBCategory());
            tf_btitle.setText(book.getBTitle());
            tf_bwriter.setText(book.getBWriter());
            tf_bpub.setText(book.getBPub());
            tf_byear.setText(String.valueOf(book.getBYear()));
            tf_bintro.setText(book.getBIntro());

            tf_bcode.setEditable(false);
            confirm.setText("����");
        }
    }

    public JTextField getTf_bcode() {
        return tf_bcode;
    }

    public JTextField getTf_bcategory() {
        return tf_bcategory;
    }

    public JTextField getTf_btitle() {
        return tf_btitle;
    }

    public JTextField getTf_bwriter() {
        return tf_bwriter;
    }

    public JTextField getTf_bpub() {
        return tf_bpub;
    }

    public JTextField getTf_byear() {
        return tf_byear;
    }

    public JTextField getTf_bintro() {
        return tf_bintro;
    }

    public String getAddedBookCode() {
        return addedBookCode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String btnLabel = e.getActionCommand();
        String fieldName = me.combo.getSelectedItem().toString();
		String keyword = me.jtf.getText().trim(); // �Է°��� ������ �� ���� ����
        if (btnLabel.equals("�� ��")) {
            addedBookCode = tf_bcode.getText(); // �߰��� ���� �ڵ带 ����
            System.out.println("�߰��� ���� �ڵ�: " + addedBookCode); // tf_bcode �� Ȯ��
            if (bDAO.bookListInsert(this) > 0) {
                JOptionPane.showMessageDialog(this, tf_bcode.getText() + " å�� �߰��Ǿ����ϴ�.");
                if (fieldName.equals("��ü")) {
					me.getAllBooks();
				} else {
					List<BookDTO> books = bDAO.searchBooks(fieldName, keyword);
					me.displayBooks(books);
				}
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "å�� �߰����� �ʾҽ��ϴ�.");
            }
        } else if (btnLabel.equals("����")) {
            String fieldName1 = me.combo.getSelectedItem().toString();
            String keyword1 = me.jtf.getText();
            int row = me.jt.getSelectedRow();
            // ���� ���� ���� �߰�
            if (bDAO.updateBook(this) > 0) {
                JOptionPane.showMessageDialog(this, tf_bcode.getText() + " å�� �����Ǿ����ϴ�.");
                dispose();
                me.getAllBooks(); // ��ü ������ �ٽ� �ҷ���
                me.sortBooks(); // ���̺� �����͸� ����
                me.selectBook(tf_bcode.getText()); // ������ ������ �ٽ� ����
                

            } else {
                JOptionPane.showMessageDialog(this, "å�� �������� �ʾҽ��ϴ�.");
            }
        } else if (btnLabel.equals("�� ��")) {
            dispose();
        } else if (btnLabel.equals("�ߺ�Ȯ��")) {
            if (tf_bcode.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(this, "�����ڵ� �Է�");
                tf_bcode.requestFocus();
            } else {
                if (bDAO.getBcodeByCheck(tf_bcode.getText())) {
                    JOptionPane.showMessageDialog(this, tf_bcode.getText() + "�� ��밡��");
                } else {
                    JOptionPane.showMessageDialog(this, tf_bcode.getText() + "�� �ߺ�!");
                    tf_bcode.setText("");
                    tf_bcode.requestFocus();
                }
            }
        }
    }
}