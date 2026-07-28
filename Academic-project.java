import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IDCardVerificationSystem extends JFrame {

    // Database connection details
    private static final String DB_URL = "jdbc:sqlite:verification.db";
    
    // UI Components
    private JLabel imageLabel;
    private JTextArea extractionArea;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton uploadButton;

    public IDCardVerificationSystem() {
        setTitle("Automated ID Card Verification System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeDatabase();
        initComponents();
        loadHistoryData();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS verification_logs (" +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "name TEXT NOT NULL, " +
                                    "roll_no TEXT NOT NULL, " +
                                    "score INTEGER, " +
                                    "status TEXT, " +
                                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(createTableSQL);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Initialization Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top Header
        JLabel headerLabel = new JLabel("ID Card Verification System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Center Panel (Upload and Details)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Image Panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("ID Card Image"));
        
        imageLabel = new JLabel("No Image Selected", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 200));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        uploadButton = new JButton("Select ID Card Image");
        uploadButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        uploadButton.addActionListener(new UploadAction());
        imagePanel.add(uploadButton, BorderLayout.SOUTH);

        centerPanel.add(imagePanel);

        // Extraction Panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Extracted Details & Status"));
        
        extractionArea = new JTextArea();
        extractionArea.setEditable(false);
        extractionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        extractionArea.setText("Please upload an image to begin verification...\n");
        detailsPanel.add(new JScrollPane(extractionArea), BorderLayout.CENTER);

        centerPanel.add(detailsPanel);
        
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel (Database History Table)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Verification Database History"));
        bottomPanel.setPreferredSize(new Dimension(900, 200));

        String[] columns = {"ID", "Date & Time", "Name", "Roll No", "Score (%)", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        historyTable.setFillsViewportHeight(true);
        bottomPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadHistoryData() {
        tableModel.setRowCount(0); // Clear existing data
        
        String query = "SELECT * FROM verification_logs ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("timestamp"),
                    rs.getString("name"),
                    rs.getString("roll_no"),
                    rs.getInt("score"),
                    rs.getString("status")
                };
                tableModel.addRow(row);
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to load history: " + e.getMessage());
        }
    }

    private void saveRecordToDatabase(String name, String rollNo, int score, String status) {
        String insertSQL = "INSERT INTO verification_logs (name, roll_no, score, status, timestamp) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            pstmt.setString(1, name);
            pstmt.setString(2, rollNo);
            pstmt.setInt(3, score);
            pstmt.setString(4, status);
            pstmt.setString(5, currentTimestamp);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Insert Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inner Action Class for Uploading and Verifying
    private class UploadAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(IDCardVerificationSystem.this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                // Show Image Name
                imageLabel.setText("Loaded: " + selectedFile.getName());
                
                // Simulate processing delay and OCR Data Extraction
                extractionArea.setText("Analyzing image...\n");
                extractionArea.append("Extracting text via OCR...\n");
                extractionArea.append("Scanning for Face...\n\n");
                
                // Mock Data for Demonstration
                String[] sampleNames = {"ROSHAN KUMAR", "JOHN DOE", "ALICE SMITH"};
                String[] sampleRolls = {"COL-2024-8901", "COL-2024-1122", "COL-2024-3344"};
                
                Random rand = new Random();
                int idx = rand.nextInt(sampleNames.length);
                
                String extractedName = sampleNames[idx];
                String extractedRoll = sampleRolls[idx];
                int confidenceScore = 80 + rand.nextInt(21); // Random score between 80 and 100
                String status = confidenceScore >= 85 ? "VERIFIED" : "FLAGGED";
                
                // Update UI
                extractionArea.append("--- Extracted Data ---\n");
                extractionArea.append("Name: " + extractedName + "\n");
                extractionArea.append("ID/Roll No: " + extractedRoll + "\n");
                extractionArea.append("Confidence Score: " + confidenceScore + "%\n");
                extractionArea.append("Final Status: " + status + "\n");
                
                // Save to SQL Database
                saveRecordToDatabase(extractedName, extractedRoll, confidenceScore, status);
                
                // Refresh Table
                loadHistoryData();
                
                JOptionPane.showMessageDialog(IDCardVerificationSystem.this, 
                        "Verification Complete. Record saved to SQL Database.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Set Look and Feel to System default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch application
        SwingUtilities.invokeLater(() -> {
            IDCardVerificationSystem app = new IDCardVerificationSystem();
            app.setVisible(true);
        });
    }
}
