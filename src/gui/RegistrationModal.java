package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.MemberDAO;
import dao.RentalDAO;
import dto.MemberDTO;

public class RegistrationModal extends JDialog {
    private MemberDAO memberDAO;
    private MemberDTO member;
    private MemberPanel memberPanel;

    private JLabel lid, lpass1, lpass2, lname, ljunin, ltel, laddr, lemail, ljob, empty, empty2, empty3;
    private JTextField idField, nameField, juminField1, telField1, telField2, telField3, addrField, emailField;
    private JPasswordField passField, passConfirmField, juminField2;
    private JButton registerButton, idCheckButton, cancelButton;
    private JComboBox<String> jobComboBox;

    // ���� ����: ����� ��� ID�� ������ ���� �߰�
    private String updatedMemberId;
    // ���� ��

    // ȸ�� ������ ������
    public RegistrationModal(JFrame parent, MemberDAO memberDAO, MemberDTO member, MemberPanel memberPanel, boolean isAdmin) {
        super(parent, "ȸ�� ���� ����", true); // ��� ����
        this.memberDAO = memberDAO;
        this.member = member;
        this.memberPanel = memberPanel;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                memberPanel.refreshData();
                // ���� ����: â�� ���� �� ����� ����� ����
                if (updatedMemberId != null) {
                    memberPanel.selectMember(updatedMemberId);
                }
                // ���� ��
            }
        });

        initialize();
        populateFields(member);
        
        idField.setText(member.getId());
        passField.setText(member.getPass());
        passConfirmField.setText(member.getPass());
        nameField.setText(member.getName());
        juminField1.setText(member.getJumin().split("-")[0]);
        juminField2.setText(member.getJumin().split("-")[1]);
        telField1.setText(member.getTel().split("-")[0]);
        telField2.setText(member.getTel().split("-")[1]);
        telField3.setText(member.getTel().split("-")[2]);
        addrField.setText(member.getAddr());
        emailField.setText(member.getEmail());
        jobComboBox.setSelectedItem(member.getJob());
        
        if (isAdmin) {
            passField.setEditable(false);
            passConfirmField.setEditable(false);
        }
    }

    // ȸ�� ���Կ� ������
    public RegistrationModal(JFrame parent, MemberDAO memberDAO) {
        super(parent, "ȸ�� ����", true); // ��� ����
        this.memberDAO = memberDAO;

        initialize();
    }

    // ���� ȸ�� ������ �����ڿ��� isAdmin �Ű����� �߰�
    public RegistrationModal(JFrame parent, MemberDAO memberDAO, MemberDTO member, MemberPanel memberPanel) {
        this(parent, memberDAO, member, memberPanel, false);
    }

    private void initialize() {
        setResizable(false);
        setFont(new Font("���ü", Font.BOLD, 15));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.white);
        setBounds(250, 230, 370, 440);

        lid = new JLabel("*�� �� ��   : "); lpass1 = new JLabel("*��й�ȣ : "); lpass2 = new JLabel("*���Ȯ�� : ");
        lname = new JLabel("*��    ��     : "); ljunin = new JLabel("*�ֹι�ȣ : "); ltel = new JLabel("*��ȭ��ȣ : ");
        laddr = new JLabel("*��    ��     : "); lemail = new JLabel ("�� �� ��   : "); ljob = new JLabel("��    ��     : ");
        empty = new JLabel("* * * * * *"); empty2 = new JLabel("                                      "); empty3 = new JLabel("                ");

        idField = new JTextField(13); passField = new JPasswordField(13); passConfirmField = new JPasswordField(13);
        nameField = new JTextField(13); juminField1 = new JTextField(6); juminField2 = new JPasswordField(1);
        telField1 = new JTextField(3); telField2 = new JTextField(4); telField3 = new JTextField(4); 
        addrField = new JTextField(20);
        emailField = new JTextField(20);

        empty.setFont(new Font("���ü", Font.BOLD, 12));
        JComponent dashLabel = new JLabel("-");
        dashLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashLabel.setFont(new Font("���ü", Font.BOLD, 16));

        JComponent dashLabe2 = new JLabel("-");
        dashLabe2.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashLabe2.setFont(new Font("���ü", Font.BOLD, 16));

        JComponent dashLabe3 = new JLabel("-");
        dashLabe3.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashLabe3.setFont(new Font("���ü", Font.BOLD, 16));

        idCheckButton = new JButton("�ߺ� Ȯ��"); registerButton = new JButton(" ȸ�� ���� "); cancelButton = new JButton("�� ��");
        jobComboBox = new JComboBox<>(new String[]{"���л�", "������", "�����", "û�ҳ�", "�Ǿ���", "�л�", "��Ÿ"});
        jobComboBox.setPreferredSize(new Dimension(225, 30)); 

        Panel idPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        idPanel.add(lid); idPanel.add(idField); idPanel.add(idCheckButton);

        Panel pass1Panel = new Panel(new FlowLayout(FlowLayout.LEFT));
        pass1Panel.add(lpass1); pass1Panel.add(passField);

        Panel pass2Panel = new Panel(new FlowLayout(FlowLayout.LEFT));
        pass2Panel.add(lpass2); pass2Panel.add(passConfirmField);

        Panel namePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(lname); namePanel.add(nameField);

        Panel juminPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        juminPanel.add(ljunin);
        juminPanel.add(juminField1);
        juminPanel.add(dashLabel); 
        juminPanel.add(juminField2);
        juminPanel.add(empty);

        Panel telPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        telPanel.add(ltel); 
        telPanel.add(telField1); 
        telPanel.add(dashLabe2); 
        telPanel.add(telField2); 
        telPanel.add(dashLabe3); 
        telPanel.add(telField3);

        Panel addrPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        addrPanel.add(laddr); addrPanel.add(addrField);

        Panel emailPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.add(lemail); emailPanel.add(emailField);

        Panel jobPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        jobPanel.add(ljob); jobPanel.add(jobComboBox);

        Panel registerPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        registerPanel.add(empty2); registerPanel.add(registerButton); registerPanel.add(empty3); registerPanel.add(cancelButton);

        add(idPanel); add(pass1Panel); add(pass2Panel); add(namePanel); add(juminPanel); add(telPanel); add(addrPanel); add(emailPanel); add(jobPanel);
        add(registerPanel);

        setLocationRelativeTo(null);
        idCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkId(); 
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (member != null) {
                    update();
                } else {
                    register();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // â�� ����
            }
        });
        
        idField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    idCheckButton.doClick(); // idCheckButton Ŭ�� �̺�Ʈ ȣ��
                    passField.requestFocus(); // passField�� ��Ŀ�� �̵�
                }
            }
        });
        
        // �ֹι�ȣ �Է� �ʵ忡 ���� ���� �߰�
        juminField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (juminField1.getText().length() >= 6) {
                    e.consume(); // �Է��� ��ȿȭ�Ͽ� ���� ����
                }
            }
        });

        juminField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (juminField2.getText().length() >= 1) {
                    e.consume(); // �Է��� ��ȿȭ�Ͽ� ���� ����
                }
            }
        });

        // ��ȭ��ȣ �Է� �ʵ忡 ���� ���� �߰�
        telField1.addKeyListener(new KeyAdapter() { // ��ȭ��ȣ ���ڸ� ���� ���� �߰�
            @Override
            public void keyTyped(KeyEvent e) {
                if (telField1.getText().length() >= 3) {
                    e.consume(); // �Է��� ��ȿȭ�Ͽ� ���� ����
                }
            }
        });

        telField2.addKeyListener(new KeyAdapter() { // ��ȭ��ȣ �߰��ڸ� ���� ���� �߰�
            @Override
            public void keyTyped(KeyEvent e) {
                if (telField2.getText().length() >= 4) {
                    e.consume(); // �Է��� ��ȿȭ�Ͽ� ���� ����
                }
            }
        });
        telField3.addKeyListener(new KeyAdapter() { // ��ȭ��ȣ ���ڸ� ���� ���� �߰�
            @Override
            public void keyTyped(KeyEvent e) {
                if (telField3.getText().length() >= 4) {
                    e.consume(); // �Է��� ��ȿȭ�Ͽ� ���� ����
                }
            }
        });
    }
    
    public JButton getRegisterButton() {
        return registerButton;
    }
    
    private void populateFields(MemberDTO member) {
        idField.setText(member.getId());
        nameField.setText(member.getName());
        addrField.setText(member.getAddr());
        emailField.setText(member.getEmail());
        registerButton.setText("ȸ�� ����");

        idField.setEditable(false);
        nameField.setEditable(false);
        juminField1.setEditable(false);
        juminField2.setEditable(false);
        idCheckButton.setEnabled(false);

    }

    private void checkId() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "���̵� �Է��ϼ���.", "����", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (memberDAO.getMember(id) != null) {
            JOptionPane.showMessageDialog(this, "�̹� �����ϴ� ���̵��Դϴ�.", "����", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "��� ������ ���̵��Դϴ�.", "����", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void update() {
    	String id = idField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String passConfirm = new String(passConfirmField.getPassword()).trim();
        String name = nameField.getText().trim();
        String jumin = juminField1.getText().trim() + "-" + juminField2.getText().trim(); 
        String tel1 = telField1.getText().trim();
        String tel2 = telField2.getText().trim();
        String tel3 = telField3.getText().trim();
        String addr = addrField.getText().trim();
        String email = emailField.getText().trim();
        String job = (String) jobComboBox.getSelectedItem();

        if (id.isEmpty() || pass.isEmpty() || passConfirm.isEmpty() || name.isEmpty() || 
                juminField1.getText().length() != 6 || juminField2.getText().length() != 1 || 
                tel1.length() != 3 || tel2.length() != 4 || tel3.length() != 4 || addr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "�Էµ��� ���� ������ �ֽ��ϴ�.", "����", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!pass.equals(passConfirm)) {
                JOptionPane.showMessageDialog(this, "��й�ȣ�� ��ġ���� �ʽ��ϴ�.", "����", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tel = tel1 + "-" + tel2 + "-" + tel3;

        MemberDTO member = new MemberDTO(id, pass, name, jumin, tel, addr, email, job, new java.sql.Date(System.currentTimeMillis()));
        try {
            memberDAO.updateMember(member);
            JOptionPane.showMessageDialog(this, "ȸ�������� �Ϸ�Ǿ����ϴ�.");
            updatedMemberId = id; // ���� ����: ����� ��� ID ����
            dispose();
            RentalPanel.getSearchButton().doClick();               
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "ȸ������ �� ���� �߻�: " + ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ���� ����: ������Ʈ�� ��� ID�� ��ȯ�ϴ� �޼ҵ� �߰�
    public String getUpdatedMemberId() {
        return updatedMemberId;
    }
    // ���� ��

    private void register() {
        String id = idField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String passConfirm = new String(passConfirmField.getPassword()).trim();
        String name = nameField.getText().trim();
        String jumin1 = juminField1.getText().trim();
        String jumin2 = juminField2.getText().trim();
        String tel1 = telField1.getText().trim();
        String tel2 = telField2.getText().trim();
        String tel3 = telField3.getText().trim();
        String addr = addrField.getText().trim();
        String email = emailField.getText().trim();
        String job = (String) jobComboBox.getSelectedItem();

        // �Է� �ʵ��� ���̸� �����Ͽ� ���� �޽��� ǥ��
        if (id.isEmpty() || pass.isEmpty() || passConfirm.isEmpty() || name.isEmpty() || 
            jumin1.length() != 6 || jumin2.length() != 1 || 
            tel1.length() != 3 || tel2.length() != 4 || tel3.length() != 4) {
            JOptionPane.showMessageDialog(this, "�Էµ��� ���� ������ �ֽ��ϴ�.", "����", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!pass.equals(passConfirm)) {
            JOptionPane.showMessageDialog(this, "��й�ȣ�� ��ġ���� �ʽ��ϴ�.", "����", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String jumin = jumin1 + "-" + jumin2;
        String tel = tel1 + "-" + tel2 + "-" + tel3;

        MemberDTO member = new MemberDTO(id, pass, name, jumin, tel, addr, email, job, new java.sql.Date(System.currentTimeMillis()));
        try {
            memberDAO.addMember(member);
            JOptionPane.showMessageDialog(this, "ȸ�������� �Ϸ�Ǿ����ϴ�.");
            updatedMemberId = id; // ���� ����: �߰��� ȸ�� ID�� ����
            dispose();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "ȸ������ �� ���� �߻�: " + ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
        }
    }
}