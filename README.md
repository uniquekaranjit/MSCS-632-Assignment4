# MSCS-632-Assignment4
# Employee Scheduler

A robust employee scheduling system that helps manage and generate weekly work schedules while considering employee preferences and workplace constraints.

## Features

- **Employee Management**
  - Add and manage employees
  - Set up to 3 shift preferences per employee
  - View current employee list and their preferences

- **Schedule Generation**
  - Automated weekly schedule creation
  - Respects employee preferences when possible
  - Enforces workplace scheduling constraints
  - Visual schedule display

## Scheduling Constraints

- No employee works more than one shift per day
- Maximum 5 shifts per week per employee
- Minimum 2 employees per shift
- Requires minimum 9 employees total

## Available Shifts

- **Days**: Monday through Sunday
- **Shift Times**: Morning, Afternoon, Evening

## Implementation

The project is available in both Python and Java:

### Python Version
- `EmployeeScheduler.py`: Core scheduling logic
- `EmployeeSchedulerGUI.py`: Tkinter-based GUI implementation

### Java Version
- `EmployeeScheduler.java`: Core scheduling logic
- `EmployeeSchedulerGUI.java`: Swing-based GUI implementation

## Getting Started

### Python Requirements
- Python 3.x
- tkinter (usually comes with Python)

To run the Python version:
```bash
python3 EmployeeSchedulerGUI.py
```

### Java Requirements
- Java 8 or higher
- Swing (included in JDK)

To compile and run the Java version:
```bash
javac EmployeeScheduler.java EmployeeSchedulerGUI.java
java EmployeeSchedulerGUI 
```

## Usage

1. **Adding Employees**
   - Navigate to the "Employee Management" tab
   - Enter employee name
   - Optionally set shift preferences
   - Click "Add Employee"

2. **Generating Schedule**
   - Navigate to the "Schedule Management" tab
   - Click "Generate Schedule"
   - Review the generated schedule and employee workload

