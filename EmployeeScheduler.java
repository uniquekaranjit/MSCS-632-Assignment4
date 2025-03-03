/*
 * Java Class to implement employee scheduling GUI.
 * @author Unique Karanjit 
 * 
 */
import java.util.*;

/**
 * EmployeeScheduler manages employee shift scheduling with the following constraints:
 * - No employee works more than one shift per day
 * - Maximum 5 shifts per week per employee
 * - Minimum 2 employees per shift
 * - Requires minimum 9 employees total
 */
public class EmployeeScheduler {
    // Valid days and shifts for the schedule
    private static final Set<String> VALID_DAYS = 
        new HashSet<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
    private static final Set<String> VALID_SHIFTS = 
        new HashSet<>(Arrays.asList("Morning", "Afternoon", "Evening"));
    
    // Schedule constraints
    private static final int MAX_SHIFTS_PER_WEEK = 5;    // Maximum shifts an employee can work per week
    private static final int MIN_EMPLOYEES_PER_SHIFT = 2; // Minimum employees needed per shift
    private static final int MIN_TOTAL_EMPLOYEES = 9;     // Minimum total employees needed

    // Data structures to track schedule and employee information
    private Map<String, Map<String, List<String>>> schedule;      // Day -> Shift -> List of Employees
    private Map<String, Integer> weeklyShifts;                    // Employee -> Number of shifts this week
    private Map<String, Set<String>> dailyEmployees;             // Day -> Set of employees working that day
    private Map<String, Map<String, List<String>>> preferences;   // Employee -> Day -> List of preferred shifts
    private Set<String> allEmployees;                            // Set of all employees

    /**
     * Initializes the scheduler with empty data structures and default values
     */
    public EmployeeScheduler() {
        this.schedule = new HashMap<>();
        this.weeklyShifts = new HashMap<>();
        this.dailyEmployees = new HashMap<>();
        this.preferences = new HashMap<>();
        this.allEmployees = new HashSet<>();
        
        // Initialize schedule and dailyEmployees for each day
        for (String day : VALID_DAYS) {
            Map<String, List<String>> dayShifts = new HashMap<>();
            for (String shift : VALID_SHIFTS) {
                dayShifts.put(shift, new ArrayList<>());
            }
            schedule.put(day, dayShifts);
            dailyEmployees.put(day, new HashSet<>());
        }
    }

    /**
     * Generates a weekly schedule based on employee preferences and scheduling constraints.
     * The schedule is generated in two passes:
     * 1. First pass: Assigns employees to their preferred shifts where possible
     * 2. Second pass: Fills remaining slots to ensure minimum staffing requirements
     *
     * Constraints enforced:
     * - No employee works more than one shift per day
     * - Maximum 5 shifts per week per employee
     * - Minimum 2 employees per shift
     * - Requires at least 9 employees total
     *
     * @return A formatted string containing either:
     *         - The complete schedule with employee assignments
     *         - An error message if constraints cannot be met
     */
    public String generateSchedule() {
        // Verify minimum employee requirement
        if (allEmployees.size() < MIN_TOTAL_EMPLOYEES) {
            return "Error: At least " + MIN_TOTAL_EMPLOYEES + 
                   " employees are required to generate a schedule.\n" +
                   "Current number of employees: " + allEmployees.size();
        }

        // Reset all schedule data
        clearSchedule();
        //Random random = new Random();

        // First Pass: Handle Preferred Shifts
        // Try to assign employees to their preferred shifts while keeping shifts partially open
        for (Map.Entry<String, Map<String, List<String>>> empPrefs : preferences.entrySet()) {
            String employee = empPrefs.getKey();
            Map<String, List<String>> dayPrefs = empPrefs.getValue();
            
            // Process each day's preferences
            for (Map.Entry<String, List<String>> dayPref : dayPrefs.entrySet()) {
                String day = dayPref.getKey();
                List<String> shifts = dayPref.getValue();
                
                // Try to assign to preferred shifts if available
                for (String shift : shifts) {
                    if (canAssignShift(employee, day, shift)) {
                        List<String> shiftEmployees = schedule.get(day).get(shift);
                        // Only assign if shift is empty to ensure even distribution
                        if (shiftEmployees.isEmpty()) {
                            assignEmployeeToShift(employee, day, shift);
                            break;
                        }
                    }
                }
            }
        }

        // Second Pass: Systematic Assignment
        // Ensure all shifts have minimum required staff
        List<String> allDays = new ArrayList<>(VALID_DAYS);
        List<String> allShifts = new ArrayList<>(VALID_SHIFTS);
        
        // Process each day and shift systematically
        for (String day : allDays) {
            for (String shift : allShifts) {
                List<String> currentEmployees = schedule.get(day).get(shift);
                
                // Keep adding employees until minimum requirement is met
                while (currentEmployees.size() < MIN_EMPLOYEES_PER_SHIFT) {
                    List<String> availableEmployees = new ArrayList<>();
                    
                    // First attempt: Find employees who can work this shift
                    for (String employee : allEmployees) {
                        if (canAssignShift(employee, day, shift)) {
                            availableEmployees.add(employee);
                        }
                    }
                    
                    // Second attempt: Try employees under max shifts who haven't worked today
                    if (availableEmployees.isEmpty()) {
                        for (String employee : allEmployees) {
                            if (weeklyShifts.getOrDefault(employee, 0) < MAX_SHIFTS_PER_WEEK &&
                                !dailyEmployees.get(day).contains(employee)) {
                                availableEmployees.add(employee);
                            }
                        }
                    }
                    
                    // If no employees available, schedule cannot be completed
                    if (availableEmployees.isEmpty()) {
                        return "Error: Unable to meet minimum staffing requirement for " + 
                               day + " " + shift + " shift.\n" +
                               "Current schedule state:\n" + getScheduleText() + "\n" +
                               "Employee workload:\n" + getEmployeeWorkloadText();
                    }
                    
                    // Sort employees by current workload to ensure fair distribution
                    availableEmployees.sort((e1, e2) -> 
                        weeklyShifts.getOrDefault(e1, 0) - weeklyShifts.getOrDefault(e2, 0));
                    
                    // Assign employee with lowest current workload
                    String selectedEmployee = availableEmployees.get(0);
                    assignEmployeeToShift(selectedEmployee, day, shift);
                }
            }
        }

        // Generate final report
        StringBuilder report = new StringBuilder();
        report.append("Schedule successfully generated!\n\n");
        
        // Add detailed workload information
        report.append(getEmployeeWorkloadText()).append("\n");

        // Add complete schedule
        report.append(getScheduleText());

        return report.toString();
    }

    /**
     * Clears all schedule data for a new schedule generation
     */
    private void clearSchedule() {
        weeklyShifts.clear();
        for (String day : VALID_DAYS) {
            dailyEmployees.get(day).clear();
            for (String shift : VALID_SHIFTS) {
                schedule.get(day).get(shift).clear();
            }
        }
    }

    /**
     * Checks if an employee can be assigned to a specific shift
     * @param employee The employee to check
     * @param day The day to check
     * @param shift The shift to check
     * @return true if the employee can be assigned, false otherwise
     */
    private boolean canAssignShift(String employee, String day, String shift) {
        return !dailyEmployees.get(day).contains(employee) && // Not already working this day
               weeklyShifts.getOrDefault(employee, 0) < MAX_SHIFTS_PER_WEEK && // Not exceeded weekly limit
               !schedule.get(day).get(shift).contains(employee); // Not already in this shift
    }

    /**
     * Assigns an employee to a specific shift
     * @param employee The employee to assign
     * @param day The day of the shift
     * @param shift The shift to assign
     */
    private void assignEmployeeToShift(String employee, String day, String shift) {
        schedule.get(day).get(shift).add(employee);
        dailyEmployees.get(day).add(employee);
        weeklyShifts.put(employee, weeklyShifts.getOrDefault(employee, 0) + 1);
    }

    /**
     * Generates a text representation of employee workload
     * @return Formatted string showing shifts per employee
     */
    private String getEmployeeWorkloadText() {
        StringBuilder sb = new StringBuilder("Employee Workload:\n");
        for (String employee : allEmployees) {
            int shifts = weeklyShifts.getOrDefault(employee, 0);
            sb.append(employee).append(": ")
              .append(shifts).append(" shifts")
              .append(shifts >= MAX_SHIFTS_PER_WEEK ? " (MAX)" : "")
              .append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets the current schedule in a formatted text representation
     * @return Formatted string showing the complete schedule
     */
    public String getScheduleText() {
        StringBuilder sb = new StringBuilder("Weekly Schedule:\n\n");
        for (String day : VALID_DAYS) {
            sb.append(day).append(":\n");
            for (String shift : VALID_SHIFTS) {
                List<String> employees = schedule.get(day).get(shift);
                if (!employees.isEmpty()) {
                    sb.append("  ").append(shift).append(": ")
                      .append(String.join(", ", employees))
                      .append("\n");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets an employee's shift preferences
     * @param employee The employee name
     * @return Map of day to list of preferred shifts
     */
    public Map<String, List<String>> getEmployeePreferences(String employee) {
        return preferences.getOrDefault(employee, new HashMap<>());
    }

    /**
     * Gets all current preferences in a formatted text representation
     * @return Formatted string showing all employee preferences
     */
    public String getPreferencesText() {
        StringBuilder sb = new StringBuilder("Current Preferences:\n");
        for (String employee : preferences.keySet()) {
            sb.append(employee).append(":\n");
            Map<String, List<String>> employeePrefs = preferences.get(employee);
            for (String day : employeePrefs.keySet()) {
                sb.append("  ").append(day).append(": ")
                  .append(String.join(", ", employeePrefs.get(day)))
                  .append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * Removes all preferences for the specified employee
     * @param employee The employee whose preferences should be removed
     */
    public void removeEmployeePreferences(String employee) {
        preferences.remove(employee);
        allEmployees.remove(employee);  // Remove from all employees set
        // Also remove from schedule if they're in it
        for (Map<String, List<String>> daySchedule : schedule.values()) {
            for (List<String> shiftEmployees : daySchedule.values()) {
                shiftEmployees.remove(employee);
            }
        }
        // Remove from daily employees
        for (Set<String> dailyEmps : dailyEmployees.values()) {
            dailyEmps.remove(employee);
        }
        // Remove from weekly shifts
        weeklyShifts.remove(employee);
    }

    public void addEmployee(String employee) {
        // Method to add employee without preferences
        allEmployees.add(employee);
    }

    /**
     * Adds an employee preference for a specific day and shift
     * @param employee The employee's name
     * @param day The preferred day
     * @param shift The preferred shift
     * @return true if preference was added successfully, false if invalid day or shift
     */
    public void addEmployeePreference(String employee, String day, String shift) {
        if (!VALID_DAYS.contains(day) || !VALID_SHIFTS.contains(shift)) {
            return;
        }
        allEmployees.add(employee);
        preferences.putIfAbsent(employee, new HashMap<>());
        preferences.get(employee).putIfAbsent(day, new ArrayList<>());
        if (!preferences.get(employee).get(day).contains(shift)) {
            preferences.get(employee).get(day).add(shift);
        }
    }

    public static void main(String[] args) {
        new EmployeeSchedulerGUI();
    }
}

