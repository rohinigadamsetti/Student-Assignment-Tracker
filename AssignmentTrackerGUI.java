import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;

public class AssignmentTrackerGUI extends JFrame {

    private JTextField studentField;
    private JTextField subjectField;
    private JTextField assignmentField;
    private JTextField dueDateField;
    private JTextField searchField;

    private JComboBox<String> statusBox;

    private JTable table;
    private DefaultTableModel model;

    private JLabel totalLabel;
    private JLabel completedLabel;
    private JLabel pendingLabel;
    private JLabel percentageLabel;
    private JLabel overdueLabel;

    private final ArrayList<String[]> assignments = new ArrayList<>();

    private final String FILE_NAME = "gui_assignments.txt";

    public AssignmentTrackerGUI() {

        setTitle("Student Assignment Tracker");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadAssignments();

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel heading = new JLabel(
                "STUDENT ASSIGNMENT TRACKER",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(new Color(40, 70, 140));

        mainPanel.add(heading, BorderLayout.NORTH);

        // Input fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        studentField = new JTextField();
        subjectField = new JTextField();
        assignmentField = new JTextField();
        dueDateField = new JTextField();

        dueDateField.setToolTipText("Enter date as yyyy-MM-dd");

        statusBox = new JComboBox<>(
                new String[]{"Pending", "Completed"}
        );

        inputPanel.add(new JLabel("Student Name:"));
        inputPanel.add(studentField);

        inputPanel.add(new JLabel("Subject Name:"));
        inputPanel.add(subjectField);

        inputPanel.add(new JLabel("Assignment Name:"));
        inputPanel.add(assignmentField);

        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusBox);

        inputPanel.add(new JLabel("Due Date (yyyy-MM-dd):"));
        inputPanel.add(dueDateField);

        // Assignment table
        String[] columns = {
                "Student Name",
                "Subject",
                "Assignment",
                "Status",
                "Due Date"
        };

        model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(27);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        JButton addButton = new JButton("Add Assignment");
        JButton updateButton = new JButton("Update Status");
        JButton deleteButton = new JButton("Delete Assignment");
        JButton clearButton = new JButton("Clear Fields");
        JButton showButton = new JButton("Show All");
        JButton searchButton = new JButton("Search");
        JButton overdueButton = new JButton("Overdue Alerts");

        JPanel buttonPanel = new JPanel(
                new GridLayout(2, 4, 10, 10)
        );

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(showButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(overdueButton);

        // Search panel
        searchField = new JTextField();

        JPanel searchPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        searchPanel.add(
                new JLabel("Search by subject or assignment:"),
                BorderLayout.WEST
        );

        searchPanel.add(searchField, BorderLayout.CENTER);

        // Dashboard
        JPanel dashboardPanel = new JPanel(
                new GridLayout(1, 5, 10, 10)
        );

        totalLabel = new JLabel("Total: 0", SwingConstants.CENTER);
        completedLabel = new JLabel("Completed: 0", SwingConstants.CENTER);
        pendingLabel = new JLabel("Pending: 0", SwingConstants.CENTER);
        percentageLabel = new JLabel("Progress: 0.0%", SwingConstants.CENTER);
        overdueLabel = new JLabel("Overdue: 0", SwingConstants.CENTER);

        dashboardPanel.add(totalLabel);
        dashboardPanel.add(completedLabel);
        dashboardPanel.add(pendingLabel);
        dashboardPanel.add(percentageLabel);
        dashboardPanel.add(overdueLabel);

        JPanel bottomPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(dashboardPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Button actions
        addButton.addActionListener(e -> addAssignment());
        updateButton.addActionListener(e -> updateStatus());
        deleteButton.addActionListener(e -> deleteAssignment());
        clearButton.addActionListener(e -> clearFields());
        showButton.addActionListener(e -> showAllAssignments());
        searchButton.addActionListener(e -> searchAssignment());
        overdueButton.addActionListener(e -> showOverdueAssignments());

        refreshTable();
        updateDashboard();
        SwingUtilities.invokeLater(this::showOverdueAlertIfAny);
    }

    // Add assignment
    private void addAssignment() {

        String student = studentField.getText().trim();
        String subject = subjectField.getText().trim();
        String assignment = assignmentField.getText().trim();
        String status = (String) statusBox.getSelectedItem();
        String dueDate = dueDateField.getText().trim();

        if (student.isEmpty()
                || subject.isEmpty()
                || assignment.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter student name, subject and assignment name."
            );

            return;
        }

        if (!dueDate.isEmpty()) {
            try {
                LocalDate.parse(dueDate);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid date! Use yyyy-MM-dd, for example 2026-10-25."
                );
                return;
            }
        }

        String[] data = {
                student,
                subject,
                assignment,
                status,
                dueDate
        };

        assignments.add(data);

        saveAssignments();
        refreshTable();
        updateDashboard();
        clearFields();

        JOptionPane.showMessageDialog(
                this,
                "Assignment added successfully!"
        );
    }

    // Update assignment status
    private void updateStatus() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an assignment from the table."
            );
            return;
        }

        String[] data = getSelectedAssignment(row);

        if (data == null) {
            return;
        }

        if (data[3].equals("Pending")) {
            data[3] = "Completed";
        } else {
            data[3] = "Pending";
        }

        saveAssignments();
        refreshTable();
        updateDashboard();

        JOptionPane.showMessageDialog(
                this,
                "Assignment status updated successfully!"
        );
    }

    // Get selected assignment safely
    private String[] getSelectedAssignment(int row) {

        if (row < 0 || row >= model.getRowCount()) {
            return null;
        }

        String assignmentName =
                model.getValueAt(row, 2).toString();

        String subject =
                model.getValueAt(row, 1).toString();

        String student =
                model.getValueAt(row, 0).toString();

        for (String[] data : assignments) {

            if (data[0].equals(student)
                    && data[1].equals(subject)
                    && data[2].equals(assignmentName)) {

                return data;
            }
        }

        return null;
    }

    // Delete assignment
    private void deleteAssignment() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an assignment to delete."
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this assignment?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            String[] selected = getSelectedAssignment(row);

            if (selected != null) {
                assignments.remove(selected);
                saveAssignments();
                refreshTable();
                updateDashboard();

                JOptionPane.showMessageDialog(
                        this,
                        "Assignment deleted successfully!"
                );
            }
        }
    }

    // Search by subject or assignment name
    private void searchAssignment() {

        String keyword = searchField.getText()
                .trim()
                .toLowerCase();

        model.setRowCount(0);

        for (String[] data : assignments) {

            if (data[1].toLowerCase().contains(keyword)
                    || data[2].toLowerCase().contains(keyword)) {

                model.addRow(toDisplayRow(data));
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No matching assignments found."
            );
        }
    }

    // Show all assignments
    private void showAllAssignments() {
        refreshTable();
    }

    // Clear fields
    private void clearFields() {

        studentField.setText("");
        subjectField.setText("");
        assignmentField.setText("");
        dueDateField.setText("");

        statusBox.setSelectedIndex(0);
        searchField.setText("");
    }

    // Refresh table
    private void refreshTable() {

        model.setRowCount(0);

        for (String[] data : assignments) {
            model.addRow(toDisplayRow(data));
        }
    }

    // Update dashboard
    private void updateDashboard() {

        int total = assignments.size();
        int completed = 0;
        int pending = 0;
        int overdue = 0;

        for (String[] data : assignments) {

            if (data[3].equals("Completed")) {
                completed++;
            } else {
                pending++;
                if (isOverdue(data)) {
                    overdue++;
                }
            }
        }

        double percentage = 0;

        if (total > 0) {
            percentage = completed * 100.0 / total;
        }

        totalLabel.setText("Total: " + total);
        completedLabel.setText("Completed: " + completed);
        pendingLabel.setText("Pending: " + pending);

        percentageLabel.setText(
                String.format("Progress: %.1f%%", percentage)
        );
        overdueLabel.setText("Overdue: " + overdue);
    }

    // Encode text
    private String encode(String value) {

        return Base64.getEncoder().encodeToString(
                value.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    // Decode text
    private String decode(String value) {

        return new String(
                Base64.getDecoder().decode(value),
                java.nio.charset.StandardCharsets.UTF_8
        );
    }

    // Save assignments with due date
    private void saveAssignments() {

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(FILE_NAME))) {

            for (String[] data : assignments) {

                String[] safeData = new String[5];

                for (int i = 0; i < 5; i++) {
                    safeData[i] = encode(data[i]);
                }

                writer.println(String.join("\t", safeData));
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving assignments."
            );
        }
    }

    // Load old and new assignments
    private void loadAssignments() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\t", -1);

                try {

                    // New format: five encoded fields
                    if (parts.length == 5) {

                        String[] data = new String[5];

                        for (int i = 0; i < 5; i++) {
                            data[i] = decode(parts[i]);
                        }

                        assignments.add(data);
                    }

                    // Old format: four encoded fields
                    else if (parts.length == 4) {

                        String[] data = new String[5];

                        for (int i = 0; i < 4; i++) {
                            data[i] = decode(parts[i]);
                        }

                        data[4] = "";

                        assignments.add(data);
                    }

                    // Old console-style format
                    else if (line.startsWith("Subject: ")) {

                        String[] data = {
                                "",
                                "",
                                "",
                                "Pending",
                                ""
                        };

                        String[] fields = line.split(" \\| ");

                        for (String field : fields) {

                            if (field.startsWith("Subject: ")) {
                                data[1] = field.substring(9);
                            } else if (field.startsWith("Assignment: ")) {
                                data[2] = field.substring(12);
                            } else if (field.startsWith("Status: ")) {
                                data[3] = field.substring(8);
                            }
                        }

                        assignments.add(data);
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Skipping an unreadable saved assignment."
                    );
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading assignments.");
        }
    }
    // Return true only for pending assignments whose due date has passed.
    private boolean isOverdue(String[] data) {
        if (data == null || data.length < 5
                || data[3] == null
                || data[3].equalsIgnoreCase("Completed")
                || data[4] == null
                || data[4].trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate dueDate = LocalDate.parse(data[4].trim());
            return dueDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            // Empty or invalid dates are not treated as overdue.
            return false;
        }
    }

    // Build a table row without changing the data saved in gui_assignments.txt.
    private Object[] toDisplayRow(String[] data) {
        String displayStatus = isOverdue(data) ? "OVERDUE" : data[3];
        return new Object[] { data[0], data[1], data[2], displayStatus, data[4] };
    }

    // Show a list of overdue assignments when the user presses the alert button.
    private void showOverdueAssignments() {
        StringBuilder message = new StringBuilder();
        int count = 0;

        for (String[] data : assignments) {
            if (isOverdue(data)) {
                count++;
                message.append(count).append(". ")
                        .append(data[2]).append(" (")
                        .append(data[1]).append(") — due ")
                        .append(data[4]).append("\\n");
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Great! There are no overdue pending assignments.",
                    "Overdue Assignment Alerts",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "You have " + count + " overdue assignment(s):\\n\\n" + message,
                    "Overdue Assignment Alert",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // Automatically alert the user on startup if overdue assignments exist.
    private void showOverdueAlertIfAny() {
        for (String[] data : assignments) {
            if (isOverdue(data)) {
                showOverdueAssignments();
                return;
            }
        }
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AssignmentTrackerGUI app =
                    new AssignmentTrackerGUI();

            app.setVisible(true);
        });
    }
}