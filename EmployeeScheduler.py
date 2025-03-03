"""
Python Class to implement employee scheduling mechanism.
@author Unique Karanjit 
"""

# EmployeeScheduler.py
from typing import Dict, List, Set, Optional
from collections import defaultdict

class EmployeeScheduler:
    """
    EmployeeScheduler manages employee shift scheduling with the following constraints:
    - No employee works more than one shift per day
    - Maximum 5 shifts per week per employee
    - Minimum 2 employees per shift
    - Requires minimum 9 employees total
    """
    
    # Define valid days and shifts in order
    DAYS_OF_WEEK = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
    SHIFT_TIMES = ["Morning", "Afternoon", "Evening"]
    
    # Convert to sets for efficient validation
    VALID_DAYS = set(DAYS_OF_WEEK)
    VALID_SHIFTS = set(SHIFT_TIMES)
    
    # Schedule constraints
    MAX_SHIFTS_PER_WEEK = 5
    MIN_EMPLOYEES_PER_SHIFT = 2
    MIN_TOTAL_EMPLOYEES = 9

    def __init__(self):
        """Initialize the scheduler with empty data structures"""
        # Day -> Shift -> List of Employees
        self.schedule: Dict[str, Dict[str, List[str]]] = {
            day: {shift: [] for shift in self.SHIFT_TIMES}
            for day in self.DAYS_OF_WEEK
        }
        
        # Employee -> Number of shifts this week
        self.weekly_shifts: Dict[str, int] = {}
        
        # Day -> Set of employees working that day
        self.daily_employees: Dict[str, Set[str]] = {
            day: set() for day in self.DAYS_OF_WEEK
        }
        
        # Employee -> Day -> List of preferred shifts
        self.preferences: Dict[str, Dict[str, List[str]]] = {}
        
        # Set of all employees
        self.all_employees: Set[str] = set()

    def add_employee_preference(self, employee: str, day: str, shift: str) -> None:
        """
        Adds an employee preference for a specific day and shift
        """
        if day not in self.VALID_DAYS or shift not in self.VALID_SHIFTS:
            return

        self.all_employees.add(employee)
        if employee not in self.preferences:
            self.preferences[employee] = {}
        if day not in self.preferences[employee]:
            self.preferences[employee][day] = []
        if shift not in self.preferences[employee][day]:
            self.preferences[employee][day].append(shift)

    def add_employee(self, employee: str) -> None:
        """Adds an employee to the system without any preferences"""
        self.all_employees.add(employee)

    def can_assign_shift(self, employee: str, day: str, shift: str) -> bool:
        """
        Checks if an employee can be assigned to a specific shift
        """
        return (
            employee not in self.daily_employees[day] and  # Not already working this day
            self.weekly_shifts.get(employee, 0) < self.MAX_SHIFTS_PER_WEEK and  # Not exceeded weekly limit
            employee not in self.schedule[day][shift]  # Not already in this shift
        )

    def assign_employee_to_shift(self, employee: str, day: str, shift: str) -> None:
        """
        Assigns an employee to a specific shift
        """
        self.schedule[day][shift].append(employee)
        self.daily_employees[day].add(employee)
        self.weekly_shifts[employee] = self.weekly_shifts.get(employee, 0) + 1

    def clear_schedule(self) -> None:
        """Clears all schedule data for a new schedule generation"""
        self.weekly_shifts.clear()
        for day in self.DAYS_OF_WEEK:
            self.daily_employees[day].clear()
            for shift in self.SHIFT_TIMES:
                self.schedule[day][shift].clear()

    def get_next_day(self, day: str) -> str:
        """Gets the next day in the week cycle"""
        current_index = self.DAYS_OF_WEEK.index(day)
        return self.DAYS_OF_WEEK[(current_index + 1) % len(self.DAYS_OF_WEEK)]

    def get_employee_workload_text(self) -> str:
        """Generates a text representation of employee workload"""
        lines = ["Employee Workload:"]
        for employee in self.all_employees:
            shifts = self.weekly_shifts.get(employee, 0)
            max_indicator = " (MAX)" if shifts >= self.MAX_SHIFTS_PER_WEEK else ""
            lines.append(f"{employee}: {shifts} shifts{max_indicator}")
        return "\n".join(lines)

    def get_schedule_text(self) -> str:
        """Gets the current schedule in a formatted text representation"""
        lines = ["Weekly Schedule:\n"]
        for day in self.DAYS_OF_WEEK:
            lines.append(f"{day}:")
            for shift in self.SHIFT_TIMES:
                employees = self.schedule[day][shift]
                if employees:
                    lines.append(f"  {shift}: {', '.join(employees)}")
            lines.append("")
        return "\n".join(lines)

    def generate_schedule(self) -> str:
        """
        Generates a weekly schedule based on employee preferences and scheduling constraints.
        Returns a formatted string containing either the schedule or an error message.
        """
        if len(self.all_employees) < self.MIN_TOTAL_EMPLOYEES:
            return (f"Error: At least {self.MIN_TOTAL_EMPLOYEES} employees are required.\n"
                   f"Current number of employees: {len(self.all_employees)}")

        self.clear_schedule()
        import random

        # First Pass: Handle Preferred Shifts
        for employee, day_prefs in self.preferences.items():
            for day, shifts in day_prefs.items():
                for shift in shifts:
                    if self.can_assign_shift(employee, day, shift):
                        shift_employees = self.schedule[day][shift]
                        if not shift_employees:  # Only assign if shift is empty
                            self.assign_employee_to_shift(employee, day, shift)
                            break

        # Second Pass: Systematic Assignment
        for day in self.DAYS_OF_WEEK:
            for shift in self.SHIFT_TIMES:
                current_employees = self.schedule[day][shift]
                
                while len(current_employees) < self.MIN_EMPLOYEES_PER_SHIFT:
                    available_employees = [
                        emp for emp in self.all_employees
                        if self.can_assign_shift(emp, day, shift)
                    ]
                    
                    if not available_employees:
                        return (f"Error: Unable to meet minimum staffing requirement for "
                               f"{day} {shift} shift.\n"
                               f"Current schedule state:\n{self.get_schedule_text()}\n"
                               f"Employee workload:\n{self.get_employee_workload_text()}")
                    
                    # Sort by current workload
                    available_employees.sort(
                        key=lambda x: self.weekly_shifts.get(x, 0)
                    )
                    
                    selected_employee = available_employees[0]
                    self.assign_employee_to_shift(selected_employee, day, shift)

        return (f"Schedule successfully generated!\n\n"
                f"{self.get_employee_workload_text()}\n\n"
                f"{self.get_schedule_text()}")