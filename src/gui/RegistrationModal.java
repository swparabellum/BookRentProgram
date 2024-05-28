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

    // 수정 시작: 변경된 멤버 ID를 저장할 변수 추가
    private String updatedMemberId;
    // 수정 끝

    // 회원 수정용 생성자
    public RegistrationModal(JFrame parent, MemberDAO memberDAO, MemberDTO member, MemberPanel memberPanel, boolean isAdmin) {
        super(parent, "회원 정보 변경", true); // 모달 설정
        this.memberDAO = memberDAO;
        this.member = member;
        this.memberPanel = memberPanel;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                memberPanel.refreshData();
                // 수정 시작: 창이 닫힐 때 변경된 멤버를 선택
                if (updatedMemberId != null) {
                    memberPanel.selectMember(updatedMemberId);
                }
                // 수정 끝
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

    // 회원 가입용 생성자
    public RegistrationModal(JFrame parent, MemberDAO memberDAO) {
        super(parent, "회원 가입", true); // 모달 설정
        this.memberDAO = memberDAO;

        initialize();
    }

    // 기존 회원 수정용 생성자에서 isAdmin 매개변수 추가
    public RegistrationModal(JFrame parent, MemberDAO memberDAO, MemberDTO member, MemberPanel memberPanel) {
        this(parent, memberDAO, member, memberPanel, false);
    }

    private void initialize() {
        setResizable(false);
        setFont(new Font("고딕체", Font.BOLD, 15));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.white);
        setBounds(250, 230, 370, 440);

        lid = new JLabel("*아 이 디   : "); lpass1 = new JLabel("*비밀번호 : "); lpass2 = new JLabel("*비번확인 : ");
        lname = new JLabel("*이    름     : "); ljunin = new JLabel("*주민번호 : "); ltel = new JLabel("*전화번호 : ");
        laddr = new JLabel("*주    소     : "); lemail = new JLabel ("이 메 일   : "); ljob = new JLabel("직    업     : ");
        empty = new JLabel("* * * * * *"); empty2 = new JLabel("                                      "); empty3 = new JLabel("                ");

        idField = new JTextField(13); passField = new JPasswordField(13); passConfirmField = new JPasswordField(13);
        nameField = new JTextField(13); juminField1 = new JTextField(6); juminField2 = new JPasswordField(1);
        telField1 = new JTextField(3); telField2 = new JTextField(4); telField3 = new JTextField(4); 
        addrField = new JTextField(20);
        emailField = new JTextField(20);

        empty.setFont(new Font("고딕체", Font.BOLD, 12));
        JComponent dashLabel = new JLabel("-");
        dashLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashLabel.setFont(new Font("고딕체", Font.BOLD, 16));

        JComponent dashLabe2 = new JLabel("-");
        dashLabe2.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashLabe2.setFont(new Font("고딕체", Font.BOLD, 16));

        JComponent dashLabe3 = new JLabel("-");
        dashLabe3.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashLabe3.setFont(new Font("고딕체", Font.BOLD, 16));

        idCheckButton = new JButton("중복 확인"); registerButton = new JButton(" 회원 가입 "); cancelButton = new JButton("취 소");
        jobComboBox = new JComboBox<>(new String[]{"대학생", "직장인", "사업자", "청소년", "실업자", "학생", "기타"});
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
                dispose(); // 창을 닫음
            }
        });
        
        idField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    idCheckButton.doClick(); // idCheckButton 클릭 이벤트 호출
                    passField.requestFocus(); // passField로 포커스 이동
                }
            }
        });
        
        // 주민번호 입력 필드에 길이 제한 추가
        juminField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (juminField1.getText().length() >= 6) {
                    e.consume(); // 입력을 무효화하여 길이 제한
                }
            }
        });

        juminField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (juminField2.getText().length() >= 1) {
                    e.consume(); // 입력을 무효화하여 길이 제한
                }
            }
        });

        // 전화번호 입력 필드에 길이 제한 추가
        telField1.addKeyListener(new KeyAdapter() { // 전화번호 앞자리 길이 제한 추가
            @Override
            public void keyTyped(KeyEvent e) {
                if (telField1.getText().length() >= 3) {
                    e.consume(); // 입력을 무효화하여 길이 제한
                }
            }
        });

        telField2.addKeyListener(new KeyAdapter() { // 전화번호 중간자리 길이 제한 추가
            @Override
            public void keyTyped(KeyEvent e) {
                if (telField2.getText().length() >= 4) {
                    e.consume(); // 입력을 무효화하여 길이 제한
                }
            }
        });
        telField3.addKeyListener(new KeyAdapter() { // 전화번호 뒷자리 길이 제한 추가
            @Override
            public void keyTyped(KeyEvent e) {
                if (telField3.getText().length() >= 4) {
                    e.consume(); // 입력을 무효화하여 길이 제한
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
        registerButton.setText("회원 수정");

        idField.setEditable(false);
        nameField.setEditable(false);
        juminField1.setEditable(false);
        juminField2.setEditable(false);
        idCheckButton.setEnabled(false);

    }

    private void checkId() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (memberDAO.getMember(id) != null) {
            JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.", "오류", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "입력되지 않은 정보가 있습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!pass.equals(passConfirm)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tel = tel1 + "-" + tel2 + "-" + tel3;

        MemberDTO member = new MemberDTO(id, pass, name, jumin, tel, addr, email, job, new java.sql.Date(System.currentTimeMillis()));
        try {
            memberDAO.updateMember(member);
            JOptionPane.showMessageDialog(this, "회원수정이 완료되었습니다.");
            updatedMemberId = id; // 수정 시작: 변경된 멤버 ID 저장
            dispose();
            RentalPanel.getSearchButton().doClick();               
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "회원수정 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 수정 시작: 업데이트된 멤버 ID를 반환하는 메소드 추가
    public String getUpdatedMemberId() {
        return updatedMemberId;
    }
    // 수정 끝

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

        // 입력 필드의 길이를 검증하여 오류 메시지 표시
        if (id.isEmpty() || pass.isEmpty() || passConfirm.isEmpty() || name.isEmpty() || 
            jumin1.length() != 6 || jumin2.length() != 1 || 
            tel1.length() != 3 || tel2.length() != 4 || tel3.length() != 4) {
            JOptionPane.showMessageDialog(this, "입력되지 않은 정보가 있습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!pass.equals(passConfirm)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String jumin = jumin1 + "-" + jumin2;
        String tel = tel1 + "-" + tel2 + "-" + tel3;

        MemberDTO member = new MemberDTO(id, pass, name, jumin, tel, addr, email, job, new java.sql.Date(System.currentTimeMillis()));
        try {
            memberDAO.addMember(member);
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.");
            updatedMemberId = id; // 수정 시작: 추가된 회원 ID를 저장
            dispose();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "회원가입 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}