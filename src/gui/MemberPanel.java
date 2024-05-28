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
    private Tabs parentTabs; // Tabs �ν��Ͻ��� �����ϴ� ���� �߰�

    public MemberPanel(MemberDTO member, boolean isAdmin, Tabs parentTabs) {
        this.isAdmin = isAdmin;
        this.member = member;
        this.memberDAO = new MemberDAO();
        this.parentTabs = parentTabs; // Tabs �ν��Ͻ� ����
        this.setLayout(new BorderLayout());
        
        

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        String[] searchOptions = {"��ü", "���̵�", "�̸�"};
        comboBox = new JComboBox<>(searchOptions);
        controlPanel.add(comboBox);
        
        textField = new JTextField(20);
        controlPanel.add(textField);
        
        searchButton = new JButton("�˻�");
        controlPanel.add(searchButton);
        searchButton.addActionListener(this);
        
        updateButton = new JButton("���� ����");
        controlPanel.add(updateButton);
        updateButton.addActionListener(this);
        
        deleteButton = new JButton("ȸ�� Ż��");
        controlPanel.add(deleteButton);
        deleteButton.addActionListener(this);

        addButton = new JButton("ȸ�� ���");
        controlPanel.add(addButton);
        addButton.addActionListener(this);
        
        this.add(controlPanel, BorderLayout.SOUTH);
        
        String[] columnNames = {"ID", "�̸�", "��ȭ��ȣ", "�ּ�", "�̸���", "����", "��������"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // ��� ���� ��Ȱ��ȭ�Ͽ� ����ڰ� ���� ������ �� ���� ��
            }
        };
        
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        this.add(tableScrollPane, BorderLayout.CENTER);

        // �����ڿ� ȸ���� ��� ����ȭ
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
            JOptionPane.showMessageDialog(this, "�����͸� �ε��ϴ� ���� ������ �߻��߽��ϴ�: " + e.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
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
            String keyword = textField.getText().trim(); // �Է°��� ������ �� ���� ����

            // �Է°��� ��� ���� �� ��ü ����� ǥ��
            if (keyword.isEmpty()) {
                loadData();
                return;
            }

            // �˻� �ʵ� �̸� ����
            switch (fieldName) {
                case "��ü":
                    fieldName = ""; // "��ü" �ɼ��� ���� ��� �ʵ带 �˻��ϵ��� ó��
                    break;
                case "���̵�":
                    fieldName = "ID";
                    break;
                case "�̸�":
                    fieldName = "NAME";
                    break;
                default:
                    fieldName = ""; // ���������� ������ �ʵ尡 �ƴ� ��� �� ���ڿ��� ����
                    break;
            }

            try {
                List<MemberDTO> members = memberDAO.searchMembers(fieldName, keyword);
                if (members.isEmpty()) {
                    
                    loadData(); // �˻� ����� ������ ��ü ȸ�� ����� ������
                    textField.setText(""); // �˻� �ؽ�Ʈ �ʵ带 �ʱ�ȭ
                } else {
                    displayMembers(members);
                }
            } catch (Exception es) {
                es.printStackTrace();
                JOptionPane.showMessageDialog(this, "�˻� �� ���� �߻�: " + es.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
            }

            try {
                List<MemberDTO> members = memberDAO.searchMembers(fieldName, keyword);
                if (members.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "�ش� �˻� ����� �����ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // �˻� ����� ������ ��ü ȸ�� ����� ������
                    textField.setText(""); // �˻� �ؽ�Ʈ �ʵ带 �ʱ�ȭ
                } else {
                    displayMembers(members);
                }
            } catch (Exception es) {
                es.printStackTrace();
                JOptionPane.showMessageDialog(this, "�˻� �� ���� �߻�: " + es.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
            }
        
    
        } else if (e.getSource() == updateButton) {
            int selectedRow = table.getSelectedRow();
            String selectedId = null;

            // ���õ� ���� �ִ� ��쿡�� �ش� ȸ���� ID�� ������
            if (selectedRow != -1) {
                selectedId = (String) table.getValueAt(selectedRow, 0);
            }
            
            if (isAdmin) {
                // �����ڷ� �α����� ���
                if (selectedRow == -1) {
                    // ���õ� ȸ���� ���� ��� �޽��� ǥ��
                    JOptionPane.showMessageDialog(this, "������ ȸ���� �����ϼ���.", "����", JOptionPane.ERROR_MESSAGE);
                } else {
                    MemberDTO selectedMember = memberDAO.getMember(selectedId);
                    if (selectedMember != null) {
                        // ���õ� ȸ���� ���� ���� â ����
                        RegistrationModal regFrame = new RegistrationModal((JFrame) SwingUtilities.getWindowAncestor(this), memberDAO, selectedMember, this, isAdmin);
                        regFrame.setVisible(true);
                        
                        // ���� ����: ���� ���� �� �ش� ȸ���� ����
                        regFrame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                String updatedMemberId = regFrame.getUpdatedMemberId();
                                if (updatedMemberId != null) {
                                    selectMember(updatedMemberId);
                                }
                            }
                        });
                        // ���� ��
                    }
                }
            } else {
                // ����ڷ� �α����� ���
                if (selectedRow == -1 || member.getId().equals(selectedId)) {
                    // ���õ� ȸ���� ���ų� ���õ� ȸ���� ���� �α����� ����ڿ� ���� ���
                    MemberDTO selectedMember = memberDAO.getMember(member.getId()); // ���� �α����� ������� ������ ������
                    if (selectedMember != null) {
                        // ���� �α����� ������� ���� ���� â ����
                        RegistrationModal regFrame = new RegistrationModal((JFrame) SwingUtilities.getWindowAncestor(this), memberDAO, selectedMember, this);
                        regFrame.setVisible(true);
                        
                        // ���� ����: ���� ���� �� �ش� ȸ���� ����
                        regFrame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                String updatedMemberId = regFrame.getUpdatedMemberId();
                                if (updatedMemberId != null) {
                                    selectMember(updatedMemberId);
                                }
                            }
                        });
                        // ���� ��
                    }
                } else {
                    // ���õ� ȸ���� ������ ���� �α����� ����ڿ� �ٸ� ��� ������ ������ ������ �˸�
                    JOptionPane.showMessageDialog(this, "������ ������ �����ϴ�.", "����", JOptionPane.ERROR_MESSAGE);
                }
            } 
         } else if (e.getSource() == deleteButton) {
            if (isAdmin) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "������ ȸ���� �����ϼ���.", "����", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedId = (String) table.getValueAt(selectedRow, 0);
                if (selectedId.equals("admin")) {
                    JOptionPane.showMessageDialog(this, "������ ������ ������ �� �����ϴ�.", "����", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int result = JOptionPane.showConfirmDialog(this, "������ ȸ���� �����Ͻðڽ��ϱ�?", "���� Ȯ��", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                	
                    try { 
                    	RentalDAO rentalDAO = new RentalDAO(); // RentalDAO ��ü ����
                        // �뿩���� ������ �ִ��� Ȯ��
                        if (rentalDAO.hasRentedBooks(selectedId)) {
                            JOptionPane.showMessageDialog(this, "���� �뿩���� ������ �־� ������ �� �����ϴ�.", "���� �Ұ�", JOptionPane.WARNING_MESSAGE);
                            return; // �뿩���� ������ ���� ��� ���� �Ұ� �޽����� ���� �޼ҵ� ����
                        }
                    	
                    
                    	
                        memberDAO.deleteMember(selectedId);
                        JOptionPane.showMessageDialog(this, "ȸ���� �����Ǿ����ϴ�.");
                        loadData();
                    
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "ȸ�� ���� �� ���� �߻�: " + ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
                    }
                } table.setRowSelectionInterval(selectedRow, selectedRow);
            } else {
                // ����� ������ ���� ���, ���� �α����� ������� ������ �������� ���� â�� ����
                int result = JOptionPane.showConfirmDialog(this, "ȸ���� Ż���Ͻðڽ��ϱ�?", "ȸ�� Ż�� Ȯ��", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        MemberDTO selectedMember = memberDAO.getMember(member.getId());
                        RentalDAO rentalDAO = new RentalDAO(); // RentalDAO ��ü ����
                        // �뿩���� ������ �ִ��� Ȯ��
                        if (rentalDAO.hasRentedBooks(selectedMember.getId())) {
                            JOptionPane.showMessageDialog(this, "���� �뿩���� ������ �־� Ż���� �� �����ϴ�.", "Ż�� �Ұ�", JOptionPane.WARNING_MESSAGE);
                            return; // �뿩���� ������ ���� ��� Ż�� �Ұ� �޽����� ���� �޼ҵ� ����
                        }

                        memberDAO.deleteMember(selectedMember.getId());
                        JOptionPane.showMessageDialog(this, "ȸ���� Ż��Ǿ����ϴ�.");

                        // �α��� â���� �̵�
                        // ����: Tabs�� ȸ�� Ż�� ���� ����
                        parentTabs.setMemberDeleted(true); // ȸ�� Ż�� ���� ����
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        parentFrame.dispose();
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                new LoginFrame().setVisible(true);
                            }
                        });
                        // ��: �α��� â���� �̵�
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "ȸ�� Ż�� �� ���� �߻�: " + ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            
        } else if (e.getSource() == addButton) {
            RegistrationModal regFrame = new RegistrationModal((JFrame) SwingUtilities.getWindowAncestor(this), memberDAO);
            regFrame.getRegisterButton().setText("ȸ�� ���"); // ��ư �ؽ�Ʈ ����
            regFrame.setVisible(true);
            
            // ���� ����: ȸ�� �߰� �� �ش� ȸ���� ����
            regFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    String addedMemberId = regFrame.getUpdatedMemberId();
                    if (addedMemberId != null) {
                    	loadData(); // �߰��� ȸ�� �����͸� �ε�
                        selectMember(addedMemberId);
                    }
                }
            });
            // ���� ��
        } 
    }

    private void searchMembers(String fieldName, String keyword) {
        try {
            List<MemberDTO> members = memberDAO.searchMembers(fieldName, keyword);
            displayMembers(members);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "�˻� �� ���� �߻�: " + e.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
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
    
    // ������ �޼ҵ�: Ư�� ȸ���� �����ϴ� �޼ҵ� �߰�
    public void selectMember(String memberId) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).equals(memberId)) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
}