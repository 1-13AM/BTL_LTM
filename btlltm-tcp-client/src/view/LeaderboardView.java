package view;

import view.helper.ViewStyleHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class LeaderboardView extends JFrame {
    private JTable tblLeaderboard;
    private JButton btnClose;

    public LeaderboardView() {
        initComponents();
        
        // Apply base styling
        getContentPane().setBackground(ViewStyleHelper.BACKGROUND_COLOR);
        
        // Style table
        ViewStyleHelper.styleTable(tblLeaderboard);
        
        // Style close button
        ViewStyleHelper.styleButton(btnClose, ViewStyleHelper.SECONDARY_COLOR);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Leaderboard");

        tblLeaderboard = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblLeaderboard);

        btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(btnClose, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void customizeComponents() {
        // Add custom styling here, similar to HomeView
    }

    public void setLeaderboardData(Vector<Vector<Object>> data, Vector<String> columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        tblLeaderboard.setModel(model);
    }
}
