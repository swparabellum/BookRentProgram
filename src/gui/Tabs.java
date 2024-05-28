package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import dto.MemberDTO;

public class Tabs extends JFrame {
    private JTabbedPane tabbedPane;
    private boolean isAdmin;
    private MemberDTO loggedInMember;
    private boolean isMemberDeleted = false; // ȸ�� Ż�� ���θ� �����ϴ� ���� �߰�

    public Tabs(boolean isAdmin) {
        this.loggedInMember = LoginFrame.getLoggedInMember();
        this.isAdmin = isAdmin;
        setTitle("BBK �����뿩 �ý���");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        MemberPanel memberPanel = new MemberPanel(loggedInMember, isAdmin, this);
        BookPanel bookPanel = new BookPanel(loggedInMember, isAdmin);
        RentalPanel rentalPanel = new RentalPanel(loggedInMember, isAdmin);

        tabbedPane.addTab("ȸ�� ����", memberPanel);
        tabbedPane.addTab("���� ����", bookPanel);
        tabbedPane.addTab("�뿩 ����", rentalPanel);

        add(tabbedPane, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (!isMemberDeleted) { // ȸ�� Ż�� �ƴ� ��쿡�� �α��� â�� ���
                    SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
                }
            }
        });

        // �α��� �� �ʱ� ������ �ε�
        SwingUtilities.invokeLater(() -> {
            rentalPanel.loadRentals();
        });
    }

    public void setMemberDeleted(boolean isMemberDeleted) {
        this.isMemberDeleted = isMemberDeleted;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Tabs(true).setVisible(true));
    }
}
