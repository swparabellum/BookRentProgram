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
    private boolean isMemberDeleted = false; // 회원 탈퇴 여부를 저장하는 변수 추가

    public Tabs(boolean isAdmin) {
        this.loggedInMember = LoginFrame.getLoggedInMember();
        this.isAdmin = isAdmin;
        setTitle("BBK 도서대여 시스템");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        MemberPanel memberPanel = new MemberPanel(loggedInMember, isAdmin, this);
        BookPanel bookPanel = new BookPanel(loggedInMember, isAdmin);
        RentalPanel rentalPanel = new RentalPanel(loggedInMember, isAdmin);

        tabbedPane.addTab("회원 관리", memberPanel);
        tabbedPane.addTab("도서 관리", bookPanel);
        tabbedPane.addTab("대여 관리", rentalPanel);

        add(tabbedPane, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (!isMemberDeleted) { // 회원 탈퇴가 아닌 경우에만 로그인 창을 띄움
                    SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
                }
            }
        });

        // 로그인 후 초기 데이터 로드
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
