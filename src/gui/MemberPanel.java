package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import dao.MemberDAO;
import dao.RentalDAO;
import dto.MemberDTO;

public class MemberPanel extends JPanel implements ActionListener {
    private boolean isAdmin;
    private MemberDTO member;
    private JButton updateButton;
    private JButton deleteButton;
    private static JButton searchButton;
    private JButton addButton;
    private JComboBox<String> comboBox;
    private JTextField textField;
    private JTable table;
    private DefaultTableModel tableModel;
    private MemberDAO memberDAO;
    private Tabs parentTabs; // Tabs 인스턴스를 저장하는 변수 추가

    public MemberPanel(MemberDTO member, boolean isAdmin, Tabs parentTabs) {
        this.isAdmin = isAdmin;
        this.member = member;
        this.memberDAO = new MemberDAO();
        this.parentTabs = parentTabs; // Tabs 인스턴스 저장
        this.setLayout(new BorderLayout());
        
        

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        String[] searchOptions = {"전체", "아이디", "이름"};
        comboBox = new JComboBox<>(searchOptions);
        controlPanel.add(comboBox);
        
        textField = new JTextField(20);
        controlPanel.add(textField);
        
        searchButton = new JButton("검색");
        controlPanel.add(searchButton);
        searchButton.addActionListener(this);
        
        updateButton = new JButton("정보 변경");
        controlPanel.add(updateButton);
        updateButton.addActionListener(this);
        
        deleteButton = new JButton("회원 탈퇴");
        controlPanel.add(deleteButton);
        deleteButton.addActionListener(this);

        addButton = new JButton("회원 등록");
        controlPanel.add(addButton);
        addButton.addActionListener(this);
        
        this.add(controlPanel, BorderLayout.SOUTH);
        
        String[] columnNames = {"ID", "이름", "전화번호", "주소", "이메일", "직업", "가입일자"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 모든 셀을 비활성화하여 사용자가 직접 수정할 수 없게 함
            }
        };
        
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        this.add(tableScrollPane, BorderLayout.CENTER);

        // 관리자와 회원의 기능 차별화
        if (isAdmin) {
            loadData();
        } else {
            displayMember(member);
            searchButton.setVisible(false);
            addButton.setVisible(false);
            comboBox.setVisible(false);
            textField.setVisible(false);
            
        }

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchButton.doClick();
                textField.requestFocus();
            }
        });
    }

    private void loadData() {
        try {
            List<MemberDTO> members = memberDAO.getAllMembers();
            displayMembers(members);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터를 로드하는 도중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayMembers(List<MemberDTO> members) {
        tableModel.setRowCount(0);
        for (MemberDTO member : members) {
            tableModel.addRow(new Object[]{
                member.getId(),
                member.getName(),
                member.getTel(),
                member.getAddr(),
                member.getEmail(),
                member.getJob(),
                member.getJoinDate().toString()
            });
        }
    }

    private void displayMember(MemberDTO member) {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{
            member.getId(),
            member.getName(),
            member.getTel(),
            member.getAddr(),
            member.getEmail(),
            member.getJob(),
            member.getJoinDate().toString()
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == searchButton) {
            String fieldName = comboBox.getSelectedItem().toString();
            String keyword = textField.getText().trim(); // 입력값을 가져올 때 공백 제거

            // 입력값이 비어 있을 때 전체 목록을 표시
            if (keyword.isEmpty()) {
                loadData();
                return;
            }

            // 검색 필드 이름 설정
            switch (fieldName) {
                case "전체":
                    fieldName = ""; // "전체" 옵션일 때는 모든 필드를 검색하도록 처리
                    break;
                case "아이디":
                    fieldName = "ID";
                    break;
                case "이름":
                    fieldName = "NAME";
                    break;
                default:
                    fieldName = ""; // 예외적으로 설정된 필드가 아닌 경우 빈 문자열로 설정
                    break;
            }

            try {
                List<MemberDTO> members = memberDAO.searchMembers(fieldName, keyword);
                if (members.isEmpty()) {
                    
                    loadData(); // 검색 결과가 없으면 전체 회원 목록을 보여줌
                    textField.setText(""); // 검색 텍스트 필드를 초기화
                } else {
                    displayMembers(members);
                }
            } catch (Exception es) {
                es.printStackTrace();
                JOptionPane.showMessageDialog(this, "검색 중 오류 발생: " + es.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }

            try {
                List<MemberDTO> members = memberDAO.searchMembers(fieldName, keyword);
                if (members.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "해당 검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // 검색 결과가 없으면 전체 회원 목록을 보여줌
                    textField.setText(""); // 검색 텍스트 필드를 초기화
                } else {
                    displayMembers(members);
                }
            } catch (Exception es) {
                es.printStackTrace();
                JOptionPane.showMessageDialog(this, "검색 중 오류 발생: " + es.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        
    
        } else if (e.getSource() == updateButton) {
            int selectedRow = table.getSelectedRow();
            String selectedId = null;

            // 선택된 행이 있는 경우에만 해당 회원의 ID를 가져옴
            if (selectedRow != -1) {
                selectedId = (String) table.getValueAt(selectedRow, 0);
            }
            
            if (isAdmin) {
                // 관리자로 로그인한 경우
                if (selectedRow == -1) {
                    // 선택된 회원이 없는 경우 메시지 표시
                    JOptionPane.showMessageDialog(this, "수정할 회원을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    MemberDTO selectedMember = memberDAO.getMember(selectedId);
                    if (selectedMember != null) {
                        // 선택된 회원의 정보 변경 창 열기
                        RegistrationModal regFrame = new RegistrationModal((JFrame) SwingUtilities.getWindowAncestor(this), memberDAO, selectedMember, this, isAdmin);
                        regFrame.setVisible(true);
                        
                        // 수정 시작: 정보 변경 후 해당 회원을 선택
                        regFrame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                String updatedMemberId = regFrame.getUpdatedMemberId();
                                if (updatedMemberId != null) {
                                    selectMember(updatedMemberId);
                                }
                            }
                        });
                        // 수정 끝
                    }
                }
            } else {
                // 사용자로 로그인한 경우
                if (selectedRow == -1 || member.getId().equals(selectedId)) {
                    // 선택된 회원이 없거나 선택된 회원이 현재 로그인한 사용자와 같은 경우
                    MemberDTO selectedMember = memberDAO.getMember(member.getId()); // 현재 로그인한 사용자의 정보를 가져옴
                    if (selectedMember != null) {
                        // 현재 로그인한 사용자의 정보 변경 창 열기
                        RegistrationModal regFrame = new RegistrationModal((JFrame) SwingUtilities.getWindowAncestor(this), memberDAO, selectedMember, this);
                        regFrame.setVisible(true);
                        
                        // 수정 시작: 정보 변경 후 해당 회원을 선택
                        regFrame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                String updatedMemberId = regFrame.getUpdatedMemberId();
                                if (updatedMemberId != null) {
                                    selectMember(updatedMemberId);
                                }
                            }
                        });
                        // 수정 끝
                    }
                } else {
                    // 선택된 회원이 있으나 현재 로그인한 사용자와 다른 경우 수정할 권한이 없음을 알림
                    JOptionPane.showMessageDialog(this, "수정할 권한이 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } 
         } else if (e.getSource() == deleteButton) {
            if (isAdmin) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "삭제할 회원을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedId = (String) table.getValueAt(selectedRow, 0);
                if (selectedId.equals("admin")) {
                    JOptionPane.showMessageDialog(this, "관리자 계정은 삭제할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int result = JOptionPane.showConfirmDialog(this, "선택한 회원을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                	
                    try { 
                    	RentalDAO rentalDAO = new RentalDAO(); // RentalDAO 객체 생성
                        // 대여중인 도서가 있는지 확인
                        if (rentalDAO.hasRentedBooks(selectedId)) {
                            JOptionPane.showMessageDialog(this, "현재 대여중인 도서가 있어 삭제할 수 없습니다.", "삭제 불가", JOptionPane.WARNING_MESSAGE);
                            return; // 대여중인 도서가 있을 경우 삭제 불가 메시지를 띄우고 메소드 종료
                        }
                    	
                    
                    	
                        memberDAO.deleteMember(selectedId);
                        JOptionPane.showMessageDialog(this, "회원이 삭제되었습니다.");
                        loadData();
                    
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "회원 삭제 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                    }
                } table.setRowSelectionInterval(selectedRow, selectedRow);
            } else {
                // 사용자 권한을 가진 경우, 현재 로그인한 사용자의 정보를 기준으로 삭제 창을 열음
                int result = JOptionPane.showConfirmDialog(this, "회원을 탈퇴하시겠습니까?", "회원 탈퇴 확인", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        MemberDTO selectedMember = memberDAO.getMember(member.getId());
                        RentalDAO rentalDAO = new RentalDAO(); // RentalDAO 객체 생성
                        // 대여중인 도서가 있는지 확인
                        if (rentalDAO.hasRentedBooks(selectedMember.getId())) {
                            JOptionPane.showMessageDialog(this, "현재 대여중인 도서가 있어 탈퇴할 수 없습니다.", "탈퇴 불가", JOptionPane.WARNING_MESSAGE);
                            return; // 대여중인 도서가 있을 경우 탈퇴 불가 메시지를 띄우고 메소드 종료
                        }

                        memberDAO.deleteMember(selectedMember.getId());
                        JOptionPane.showMessageDialog(this, "회원이 탈퇴되었습니다.");

                        // 로그인 창으로 이동
                        // 시작: Tabs에 회원 탈퇴 여부 설정
                        parentTabs.setMemberDeleted(true); // 회원 탈퇴 여부 설정
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        parentFrame.dispose();
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                new LoginFrame().setVisible(true);
                            }
                        });
                        // 끝: 로그인 창으로 이동
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "회원 탈퇴 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            
        } else if (e.getSource() == addButton) {
            RegistrationModal regFrame = new RegistrationModal((JFrame) SwingUtilities.getWindowAncestor(this), memberDAO);
            regFrame.getRegisterButton().setText("회원 등록"); // 버튼 텍스트 변경
            regFrame.setVisible(true);
            
            // 수정 시작: 회원 추가 후 해당 회원을 선택
            regFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    String addedMemberId = regFrame.getUpdatedMemberId();
                    if (addedMemberId != null) {
                    	loadData(); // 추가된 회원 데이터를 로드
                        selectMember(addedMemberId);
                    }
                }
            });
            // 수정 끝
        } 
    }

    private void searchMembers(String fieldName, String keyword) {
        try {
            List<MemberDTO> members = memberDAO.searchMembers(fieldName, keyword);
            displayMembers(members);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "검색 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        if (isAdmin) {
        	
            loadData();
            searchButton.doClick();
        } else {
            displayMember(memberDAO.getMember(member.getId()));
        }
    }
    
    // 수정된 메소드: 특정 회원을 선택하는 메소드 추가
    public void selectMember(String memberId) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).equals(memberId)) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
}