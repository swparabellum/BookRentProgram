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
        setTitle("�α��� ȭ��");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());

        memberDAO = new MemberDAO();

        // ���Ϳ� �̹��� �߰�
        ImageIcon imageIcon = new ImageIcon("GGG.jpg");
        JLabel imageLabel = new JLabel(imageIcon);
        add(imageLabel, BorderLayout.CENTER);

        // ������ �α��� �� �߰�
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(50)); // ���� �߰�

        JLabel titleLabel = new JLabel("BBK");
        titleLabel.setFont(new Font("���ü", Font.BOLD, 70)); // ���ϴ� ��Ʈ�� ũ��� ����
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // ��� ����
        panel.add(titleLabel);

        JLabel titleLabel2 = new JLabel("���� �뿩 ����");
        titleLabel2.setFont(new Font("���ü", Font.BOLD, 30)); // ���ϴ� ��Ʈ�� ũ��� ����
        titleLabel2.setAlignmentX(Component.CENTER_ALIGNMENT); // ��� ����
        panel.add(titleLabel2);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(70)); // ���� �߰�

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.add(new JLabel("���̵�      :"));
        tf_id = new JTextField(15);
        idPanel.add(tf_id);
        panel.add(idPanel);

        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.add(new JLabel("��й�ȣ  :"));
        tf_pass = new JPasswordField(15);
        passPanel.add(tf_pass);
        panel.add(passPanel);

        loginButton = new JButton("�α���");
        registerButton = new JButton("ȸ������");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        panel.add(buttonPanel);    

        JLabel titleLabel3 = new JLabel("����");
        titleLabel3.setFont(new Font("���ü", Font.BOLD, 30)); // ���ϴ� ��Ʈ�� ũ��� ����
        titleLabel3.setAlignmentX(Component.CENTER_ALIGNMENT); // ��� ����
        panel.add(titleLabel3);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(5)); // ���� �߰�
        JLabel titleLabel4 = new JLabel("�輺��");
        titleLabel4.setFont(new Font("���ü", Font.BOLD, 20)); // ���ϴ� ��Ʈ�� ũ��� ����
        titleLabel4.setAlignmentX(Component.CENTER_ALIGNMENT); // ��� ����
        panel.add(titleLabel4);
        JLabel titleLabel5 = new JLabel("���¿�");
        titleLabel5.setFont(new Font("���ü", Font.BOLD, 20)); // ���ϴ� ��Ʈ�� ũ��� ����
        titleLabel5.setAlignmentX(Component.CENTER_ALIGNMENT); // ��� ����
        panel.add(titleLabel5);
        JLabel titleLabel6 = new JLabel("�����");
        titleLabel6.setFont(new Font("���ü", Font.BOLD, 20)); // ���ϴ� ��Ʈ�� ũ��� ����
        titleLabel6.setAlignmentX(Component.CENTER_ALIGNMENT); // ��� ����
        panel.add(titleLabel6);
        JLabel titleLabel7 = new JLabel("������");
        titleLabel7.setFont(new Font("���ü", Font.BOLD, 20)); // ���ϴ� ��Ʈ�� ũ��� ����
        titleLabel7.setAlignmentX(Component.CENTER_ALIGNMENT); // ��� ����
        panel.add(titleLabel7);
        JLabel titleLabel9 = new JLabel("�赿��");
        titleLabel9.setFont(new Font("���ü", Font.BOLD, 20)); // ���ϴ� ��Ʈ�� ũ��� ����
        titleLabel9.setAlignmentX(Component.CENTER_ALIGNMENT); // ��� ����
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
            JOptionPane.showMessageDialog(this, "���̵�� ��й�ȣ�� �Է��ϼ���.", "����", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MemberDTO member = memberDAO.getMember(id);
        if (member == null) {
            JOptionPane.showMessageDialog(this, "�������� �ʴ� ���̵��Դϴ�.", "����", JOptionPane.ERROR_MESSAGE);
        } else if (!member.getPass().equals(pass)) {
            JOptionPane.showMessageDialog(this, "��й�ȣ�� ��ġ���� �ʽ��ϴ�.", "����", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "�α��� ����!", "����", JOptionPane.INFORMATION_MESSAGE);
            boolean isAdmin = id.equals("admin");
            loggedInMember = member;
            new Tabs(isAdmin).setVisible(true);
            dispose();
        }
    }

  
}