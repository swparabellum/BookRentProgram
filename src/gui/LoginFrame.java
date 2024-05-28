package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.MemberDAO;
import dto.MemberDTO;

public class LoginFrame extends JFrame {
    private JTextField tf_id;
    private JPasswordField tf_pass;
    private JButton loginButton, registerButton;
    private MemberDAO memberDAO;
    private static MemberDTO loggedInMember;

    public static MemberDTO getLoggedInMember() {
        return loggedInMember;
    }

    public LoginFrame() {
        setTitle("로그인 화면");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());

        memberDAO = new MemberDAO();

        // 센터에 이미지 추가
        ImageIcon imageIcon = new ImageIcon("GGG.jpg");
        JLabel imageLabel = new JLabel(imageIcon);
        add(imageLabel, BorderLayout.CENTER);

        // 우측에 로그인 폼 추가
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(50)); // 여백 추가

        JLabel titleLabel = new JLabel("BBK");
        titleLabel.setFont(new Font("고딕체", Font.BOLD, 70)); // 원하는 폰트와 크기로 설정
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(titleLabel);

        JLabel titleLabel2 = new JLabel("도서 대여 서비스");
        titleLabel2.setFont(new Font("고딕체", Font.BOLD, 30)); // 원하는 폰트와 크기로 설정
        titleLabel2.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(titleLabel2);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(70)); // 여백 추가

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.add(new JLabel("아이디      :"));
        tf_id = new JTextField(15);
        idPanel.add(tf_id);
        panel.add(idPanel);

        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.add(new JLabel("비밀번호  :"));
        tf_pass = new JPasswordField(15);
        passPanel.add(tf_pass);
        panel.add(passPanel);

        loginButton = new JButton("로그인");
        registerButton = new JButton("회원가입");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        panel.add(buttonPanel);    

        JLabel titleLabel3 = new JLabel("팀원");
        titleLabel3.setFont(new Font("고딕체", Font.BOLD, 30)); // 원하는 폰트와 크기로 설정
        titleLabel3.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(titleLabel3);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(5)); // 여백 추가
        JLabel titleLabel4 = new JLabel("김성엽");
        titleLabel4.setFont(new Font("고딕체", Font.BOLD, 20)); // 원하는 폰트와 크기로 설정
        titleLabel4.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(titleLabel4);
        JLabel titleLabel5 = new JLabel("강승우");
        titleLabel5.setFont(new Font("고딕체", Font.BOLD, 20)); // 원하는 폰트와 크기로 설정
        titleLabel5.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(titleLabel5);
        JLabel titleLabel6 = new JLabel("김시은");
        titleLabel6.setFont(new Font("고딕체", Font.BOLD, 20)); // 원하는 폰트와 크기로 설정
        titleLabel6.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(titleLabel6);
        JLabel titleLabel7 = new JLabel("신정현");
        titleLabel7.setFont(new Font("고딕체", Font.BOLD, 20)); // 원하는 폰트와 크기로 설정
        titleLabel7.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(titleLabel7);
        JLabel titleLabel9 = new JLabel("김동규");
        titleLabel9.setFont(new Font("고딕체", Font.BOLD, 20)); // 원하는 폰트와 크기로 설정
        titleLabel9.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(titleLabel9);

        add(panel, BorderLayout.EAST);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RegistrationModal registrationFrame = new RegistrationModal(LoginFrame.this, memberDAO);
                registrationFrame.setVisible(true);
            }
        });

        tf_pass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void login() {
        String id = tf_id.getText().trim();
        String pass = new String(tf_pass.getPassword()).trim();

        if (id.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MemberDTO member = memberDAO.getMember(id);
        if (member == null) {
            JOptionPane.showMessageDialog(this, "존재하지 않는 아이디입니다.", "오류", JOptionPane.ERROR_MESSAGE);
        } else if (!member.getPass().equals(pass)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "로그인 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
            boolean isAdmin = id.equals("admin");
            loggedInMember = member;
            new Tabs(isAdmin).setVisible(true);
            dispose();
        }
    }

  
}