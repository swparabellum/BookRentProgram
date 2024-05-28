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
    private String addedBookCode; // 추가된 도서 코드를 저장할 변수 추가
    JPanel pl = new JPanel(new GridLayout(7, 1));
    JPanel ptf = new JPanel(new GridLayout(7, 1));
    JPanel ps = new JPanel();

    JLabel label_bcode = new JLabel("*도서코드");
    JLabel label_bcategory = new JLabel("*분  야");
    JLabel label_btitle = new JLabel("*책제목");
    JLabel label_bwriter = new JLabel("*저  자");
    JLabel label_bpub = new JLabel("*출판사");
    JLabel label_byear = new JLabel("발행연도");
    JLabel label_bintro = new JLabel("한줄소개");

    JTextField tf_bcode = new JTextField();
    JTextField tf_bcategory = new JTextField();
    JTextField tf_btitle = new JTextField();
    JTextField tf_bwriter = new JTextField();
    JTextField tf_bpub = new JTextField();
    JTextField tf_byear = new JTextField();
    JTextField tf_bintro = new JTextField();

    JButton confirm = new JButton("추 가");
    JButton reset = new JButton("취 소");
    BookPanel me;

    JPanel bcodeCkp = new JPanel(new BorderLayout());
    JButton bcodeBtn = new JButton("중복확인");
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
            confirm.setText("수정");
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
		String keyword = me.jtf.getText().trim(); // 입력값을 가져올 때 공백 제거
        if (btnLabel.equals("추 가")) {
            addedBookCode = tf_bcode.getText(); // 추가된 도서 코드를 저장
            System.out.println("추가된 도서 코드: " + addedBookCode); // tf_bcode 값 확인
            if (bDAO.bookListInsert(this) > 0) {
                JOptionPane.showMessageDialog(this, tf_bcode.getText() + " 책이 추가되었습니다.");
                if (fieldName.equals("전체")) {
					me.getAllBooks();
				} else {
					List<BookDTO> books = bDAO.searchBooks(fieldName, keyword);
					me.displayBooks(books);
				}
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "책이 추가되지 않았습니다.");
            }
        } else if (btnLabel.equals("수정")) {
            String fieldName1 = me.combo.getSelectedItem().toString();
            String keyword1 = me.jtf.getText();
            int row = me.jt.getSelectedRow();
            // 도서 수정 로직 추가
            if (bDAO.updateBook(this) > 0) {
                JOptionPane.showMessageDialog(this, tf_bcode.getText() + " 책이 수정되었습니다.");
                dispose();
                me.getAllBooks(); // 전체 도서를 다시 불러옴
                me.sortBooks(); // 테이블 데이터를 정렬
                me.selectBook(tf_bcode.getText()); // 수정된 도서를 다시 선택
                

            } else {
                JOptionPane.showMessageDialog(this, "책이 수정되지 않았습니다.");
            }
        } else if (btnLabel.equals("취 소")) {
            dispose();
        } else if (btnLabel.equals("중복확인")) {
            if (tf_bcode.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(this, "도서코드 입력");
                tf_bcode.requestFocus();
            } else {
                if (bDAO.getBcodeByCheck(tf_bcode.getText())) {
                    JOptionPane.showMessageDialog(this, tf_bcode.getText() + "는 사용가능");
                } else {
                    JOptionPane.showMessageDialog(this, tf_bcode.getText() + "는 중복!");
                    tf_bcode.setText("");
                    tf_bcode.requestFocus();
                }
            }
        }
    }
}