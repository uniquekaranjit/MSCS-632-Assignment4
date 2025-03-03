/*
 * Java Class to implement employee scheduling mechanism.
 * @author Unique Karanjit 
 * 
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;  // Explicitly import java.util.List

public class EmployeeSchedulerGUI {
    private JTextArea scheduleArea;
    private JButton printButton;
    private Set<String> employeeList;
    private CardLayout cardLayout;
    private JPanel cards;
    private EmployeeScheduler scheduler;
    private JComboBox<String> employeeComboBox;

    public EmployeeSchedulerGUI() {
        employeeList = new HashSet<>();
        scheduler = new EmployeeScheduler();
        employeeComboBox = new JComboBox<>();  // Initialize here
        createGUI();
    }

    private void createGUI() {
        JFrame frame = new JFrame("Employee Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);  // Increased window size

        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Employee Management Panel
        tabbedPane.addTab("Employee Management", createEmployeePanel());
        
        // Schedule Management Panel
        tabbedPane.addTab("Schedule Management", createSchedulePanel());

        frame.add(tabbedPane);
        frame.setLocationRelativeTo(null);  // Center on screen
        frame.setVisible(true);
    }

    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));  // Added spacing
        
        // North panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Employee");
        JButton viewButton = new JButton("View Employees");
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        
        // Center panel for content
        JPanel contentPanel = new JPanel(new BorderLayout());
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        
        // Add Employee Panel
        JPanel addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Employee name field
        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(new JLabel("Employee Name:"), gbc);
        gbc.gridx = 1;
        JTextField newEmployeeField = new JTextField(20);
        addPanel.add(newEmployeeField, gbc);

        // First preference
        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("1st Preference Day:"), gbc);
        String[] days = {"None", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        JComboBox<String> firstDayCombo = new JComboBox<>(days);
        gbc.gridx = 1;
        addPanel.add(firstDayCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        addPanel.add(new JLabel("1st Preference Shift:"), gbc);
        String[] shifts = {"None", "Morning", "Afternoon", "Evening"};
        JComboBox<String> firstShiftCombo = new JComboBox<>(shifts);
        gbc.gridx = 1;
        addPanel.add(firstShiftCombo, gbc);

        // Second preference
        gbc.gridx = 0; gbc.gridy = 3;
        addPanel.add(new JLabel("2nd Preference Day:"), gbc);
        JComboBox<String> secondDayCombo = new JComboBox<>(days);
        gbc.gridx = 1;
        addPanel.add(secondDayCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        addPanel.add(new JLabel("2nd Preference Shift:"), gbc);
        JComboBox<String> secondShiftCombo = new JComboBox<>(shifts);
        gbc.gridx = 1;
        addPanel.add(secondShiftCombo, gbc);

        // Third preference
        gbc.gridx = 0; gbc.gridy = 5;
        addPanel.add(new JLabel("3rd Preference Day:"), gbc);
        JComboBox<String> thirdDayCombo = new JComboBox<>(days);
        gbc.gridx = 1;
        addPanel.add(thirdDayCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        addPanel.add(new JLabel("3rd Preference Shift:"), gbc);
        JComboBox<String> thirdShiftCombo = new JComboBox<>(shifts);
        gbc.gridx = 1;
        addPanel.add(thirdShiftCombo, gbc);

        // Submit button
        gbc.gridx = 1; gbc.gridy = 7;
        JButton submitButton = new JButton("Submit");
        addPanel.add(submitButton, gbc);
        
        // View Employees Panel
        JPanel viewPanel = new JPanel(new BorderLayout(10, 10));  // Added spacing
        JTextArea employeeListArea = new JTextArea(25, 50);  // Increased size
        employeeListArea.setEditable(false);
        employeeListArea.setFont(new Font("Monospaced", Font.PLAIN, 14));  // Larger font
        JScrollPane viewScrollPane = new JScrollPane(employeeListArea);
        viewPanel.add(viewScrollPane, BorderLayout.CENTER);
        
        cards.add(addPanel, "ADD");
        cards.add(viewPanel, "VIEW");
        contentPanel.add(cards, BorderLayout.CENTER);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        // View Button Action
        viewButton.addActionListener(e -> {
            try {
                if (employeeList.isEmpty()) {
                    employeeListArea.setText("No employees found.");
                } else {
                    StringBuilder sb = new StringBuilder("Current Employees:\n\n");
                    for (String employee : employeeList) {
                        sb.append("• ").append(employee);
                        
                        Map<String, List<String>> employeePrefs = scheduler.getEmployeePreferences(employee);
                        if (employeePrefs != null && !employeePrefs.isEmpty()) {
                            sb.append("\n  Preferences:");
                            for (Map.Entry<String, List<String>> entry : employeePrefs.entrySet()) {
                                String day = entry.getKey();
                                ArrayList<String> shiftList = new ArrayList<>(entry.getValue());
                                sb.append("\n    ").append(day).append(": ")
                                  .append(String.join(", ", shiftList));
                            }
                        } else {
                            sb.append("\n  No preferences specified");
                        }
                        sb.append("\n\n");
                    }
                    employeeListArea.setText(sb.toString());
                }
                cardLayout.show(cards, "VIEW");
            } catch (Exception ex) {
                System.err.println("Error in view button: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, 
                    "Error displaying employees: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add Button Action
        addButton.addActionListener(e -> {
            cardLayout.show(cards, "ADD");
            newEmployeeField.setText("");
            firstDayCombo.setSelectedItem("None");
            firstShiftCombo.setSelectedItem("None");
            secondDayCombo.setSelectedItem("None");
            secondShiftCombo.setSelectedItem("None");
            thirdDayCombo.setSelectedItem("None");
            thirdShiftCombo.setSelectedItem("None");
        });

        // Submit Button Action
        submitButton.addActionListener(e -> {
            try {
                String name = newEmployeeField.getText().trim();
                String firstDay = (String) firstDayCombo.getSelectedItem();
                String firstShift = (String) firstShiftCombo.getSelectedItem();
                String secondDay = (String) secondDayCombo.getSelectedItem();
                String secondShift = (String) secondShiftCombo.getSelectedItem();
                String thirdDay = (String) thirdDayCombo.getSelectedItem();
                String thirdShift = (String) thirdShiftCombo.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, 
                        "Please enter an employee name.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Add employee to the list
                employeeList.add(name);
                
                // Always add employee to scheduler, even without preferences
                scheduler.addEmployee(name);

                // Add preferences only if they're not "None"
                if (!firstDay.equals("None") && !firstShift.equals("None")) {
                    scheduler.addEmployeePreference(name, firstDay, firstShift);
                }
                if (!secondDay.equals("None") && !secondShift.equals("None")) {
                    scheduler.addEmployeePreference(name, secondDay, secondShift);
                }
                if (!thirdDay.equals("None") && !thirdShift.equals("None")) {
                    scheduler.addEmployeePreference(name, thirdDay, thirdShift);
                }

                // Update UI components
                updateEmployeeComboBox();
                
                // Show success message with only the actual preferences
                StringBuilder message = new StringBuilder("Employee " + name + " added successfully");
                if (!firstDay.equals("None") && !firstShift.equals("None")) {
                    message.append("\n1st Choice: ").append(firstDay).append(" ").append(firstShift);
                }
                if (!secondDay.equals("None") && !secondShift.equals("None")) {
                    message.append("\n2nd Choice: ").append(secondDay).append(" ").append(secondShift);
                }
                if (!thirdDay.equals("None") && !thirdShift.equals("None")) {
                    message.append("\n3rd Choice: ").append(thirdDay).append(" ").append(thirdShift);
                }
                if (firstDay.equals("None") || firstShift.equals("None")) {
                    message.append("\n(No preferences specified)");
                }
                
                JOptionPane.showMessageDialog(panel, 
                    message.toString(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                // Clear input fields
                newEmployeeField.setText("");
                firstDayCombo.setSelectedItem("None");
                firstShiftCombo.setSelectedItem("None");
                secondDayCombo.setSelectedItem("None");
                secondShiftCombo.setSelectedItem("None");
                thirdDayCombo.setSelectedItem("None");
                thirdShiftCombo.setSelectedItem("None");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error adding employee: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        return panel;
    }


    private void updateEmployeeComboBox() {
        employeeComboBox.removeAllItems();
        employeeComboBox.addItem("None");
        for (String employee : employeeList) {
            employeeComboBox.addItem(employee);
        }
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());  // Changed to BorderLayout
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        printButton = new JButton("Generate Schedule");
        buttonPanel.add(printButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(buttonPanel, BorderLayout.NORTH);

        // Center panel to hold both text areas
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));  // 10 pixel vertical gap
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));  // Add padding

        // Schedule display area
        scheduleArea = new JTextArea(30, 100);
        scheduleArea.setEditable(false);
        scheduleArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(scheduleArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Current preferences display area
        JTextArea preferencesArea = new JTextArea(10, 100);
        preferencesArea.setEditable(false);
        preferencesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane prefScrollPane = new JScrollPane(preferencesArea);
        centerPanel.add(prefScrollPane, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Generate Schedule Button Action
        printButton.addActionListener(e -> {
            if (employeeList.size() < 3) {
                JOptionPane.showMessageDialog(panel,
                    "At least 3 employees are required to generate a schedule.\n" +
                    "Current number of employees: " + employeeList.size(),
                    "Insufficient Employees",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String result = scheduler.generateSchedule();
            if (result.startsWith("Error:") || result.startsWith("Warning:")) {
                JOptionPane.showMessageDialog(panel,
                    result,
                    "Schedule Generation Issue",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                scheduleArea.setText(result);
                preferencesArea.setText(scheduler.getPreferencesText());
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeSchedulerGUI::new);
    }
}
