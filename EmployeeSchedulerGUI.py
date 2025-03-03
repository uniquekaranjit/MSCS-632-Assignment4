"""
Python Class to implement employee scheduling GUI.
@author Unique Karanjit 
"""
# EmployeeSchedulerGUI.py
import tkinter as tk
from tkinter import ttk, messagebox
from EmployeeScheduler import EmployeeScheduler

class EmployeeSchedulerGUI:
    """GUI for Employee Scheduler application"""
    
    def __init__(self):
        self.scheduler = EmployeeScheduler()
        self.employee_list = set()
        
        # Create main window
        self.root = tk.Tk()
        self.root.title("Employee Scheduler")
        self.root.geometry("1200x800")
        
        # Create notebook (tabbed interface)
        self.notebook = ttk.Notebook(self.root)
        self.notebook.pack(fill='both', expand=True, padx=10, pady=5)
        
        # Create tabs
        self.create_employee_tab()
        self.create_schedule_tab()
        
    def create_employee_tab(self):
        """Creates the Employee Management tab"""
        employee_frame = ttk.Frame(self.notebook)
        self.notebook.add(employee_frame, text="Employee Management")
        
        # Employee input section
        input_frame = ttk.LabelFrame(employee_frame, text="Add New Employee", padding=10)
        input_frame.pack(fill='x', padx=10, pady=5)
        
        # Name input
        ttk.Label(input_frame, text="Employee Name:").grid(row=0, column=0, sticky='w')
        self.name_entry = ttk.Entry(input_frame, width=30)
        self.name_entry.grid(row=0, column=1, padx=5, pady=5)
        
        # Preference inputs
        days = ["None"] + EmployeeScheduler.DAYS_OF_WEEK
        shifts = ["None"] + EmployeeScheduler.SHIFT_TIMES
        
        # First preference
        ttk.Label(input_frame, text="1st Choice:").grid(row=1, column=0, sticky='w')
        self.first_day = ttk.Combobox(input_frame, values=days, state="readonly")
        self.first_day.set("None")
        self.first_day.grid(row=1, column=1, padx=5, pady=5)
        self.first_shift = ttk.Combobox(input_frame, values=shifts, state="readonly")
        self.first_shift.set("None")
        self.first_shift.grid(row=1, column=2, padx=5, pady=5)
        
        # Second preference
        ttk.Label(input_frame, text="2nd Choice:").grid(row=2, column=0, sticky='w')
        self.second_day = ttk.Combobox(input_frame, values=days, state="readonly")
        self.second_day.set("None")
        self.second_day.grid(row=2, column=1, padx=5, pady=5)
        self.second_shift = ttk.Combobox(input_frame, values=shifts, state="readonly")
        self.second_shift.set("None")
        self.second_shift.grid(row=2, column=2, padx=5, pady=5)
        
        # Third preference
        ttk.Label(input_frame, text="3rd Choice:").grid(row=3, column=0, sticky='w')
        self.third_day = ttk.Combobox(input_frame, values=days, state="readonly")
        self.third_day.set("None")
        self.third_day.grid(row=3, column=1, padx=5, pady=5)
        self.third_shift = ttk.Combobox(input_frame, values=shifts, state="readonly")
        self.third_shift.set("None")
        self.third_shift.grid(row=3, column=2, padx=5, pady=5)
        
        # Submit button
        submit_btn = ttk.Button(input_frame, text="Add Employee", command=self.add_employee)
        submit_btn.grid(row=4, column=0, columnspan=3, pady=10)
        
        # Employee list display
        list_frame = ttk.LabelFrame(employee_frame, text="Current Employees", padding=10)
        list_frame.pack(fill='both', expand=True, padx=10, pady=5)
        
        self.employee_text = tk.Text(list_frame, height=20, width=70)
        self.employee_text.pack(fill='both', expand=True)
        
    def create_schedule_tab(self):
        """Creates the Schedule Management tab"""
        schedule_frame = ttk.Frame(self.notebook)
        self.notebook.add(schedule_frame, text="Schedule Management")
        
        # Generate button
        generate_btn = ttk.Button(schedule_frame, text="Generate Schedule", 
                                command=self.generate_schedule)
        generate_btn.pack(pady=10)
        
        # Schedule display
        self.schedule_text = tk.Text(schedule_frame, height=30, width=100)
        self.schedule_text.pack(fill='both', expand=True, padx=10, pady=5)
        
    def add_employee(self):
        """Handles adding a new employee"""
        try:
            name = self.name_entry.get().strip()
            if not name:
                messagebox.showwarning("Invalid Input", "Please enter an employee name.")
                return
            
            # Add to employee list
            self.employee_list.add(name)
            
            # Add to scheduler
            self.scheduler.add_employee(name)
            
            # Add preferences if specified
            preferences_added = []
            
            if self.first_day.get() != "None" and self.first_shift.get() != "None":
                self.scheduler.add_employee_preference(name, self.first_day.get(), 
                                                    self.first_shift.get())
                preferences_added.append(f"1st Choice: {self.first_day.get()} "
                                      f"{self.first_shift.get()}")
            
            if self.second_day.get() != "None" and self.second_shift.get() != "None":
                self.scheduler.add_employee_preference(name, self.second_day.get(), 
                                                    self.second_shift.get())
                preferences_added.append(f"2nd Choice: {self.second_day.get()} "
                                      f"{self.second_shift.get()}")
            
            if self.third_day.get() != "None" and self.third_shift.get() != "None":
                self.scheduler.add_employee_preference(name, self.third_day.get(), 
                                                    self.third_shift.get())
                preferences_added.append(f"3rd Choice: {self.third_day.get()} "
                                      f"{self.third_shift.get()}")
            
            # Show success message
            message = f"Employee {name} added successfully"
            if preferences_added:
                message += "\n" + "\n".join(preferences_added)
            else:
                message += "\n(No preferences specified)"
            
            messagebox.showinfo("Success", message)
            
            # Update employee list display
            self.update_employee_list()
            
            # Clear inputs
            self.clear_inputs()
            
        except Exception as e:
            messagebox.showerror("Error", f"Error adding employee: {str(e)}")
            
    def generate_schedule(self):
        """Handles schedule generation"""
        try:
            result = self.scheduler.generate_schedule()
            self.schedule_text.delete(1.0, tk.END)
            self.schedule_text.insert(tk.END, result)
        except Exception as e:
            messagebox.showerror("Error", f"Error generating schedule: {str(e)}")
            
    def update_employee_list(self):
        """Updates the employee list display"""
        self.employee_text.delete(1.0, tk.END)
        if not self.employee_list:
            self.employee_text.insert(tk.END, "No employees found.")
            return
        
        text = "Current Employees:\n\n"
        for employee in sorted(self.employee_list):
            text += f"• {employee}\n"
            prefs = self.scheduler.preferences.get(employee, {})
            if prefs:
                text += "  Preferences:\n"
                for day, shifts in prefs.items():
                    text += f"    {day}: {', '.join(shifts)}\n"
            else:
                text += "  No preferences specified\n"
            text += "\n"
        
        self.employee_text.insert(tk.END, text)
        
    def clear_inputs(self):
        """Clears all input fields"""
        self.name_entry.delete(0, tk.END)
        self.first_day.set("None")
        self.first_shift.set("None")
        self.second_day.set("None")
        self.second_shift.set("None")
        self.third_day.set("None")
        self.third_shift.set("None")
        
    def run(self):
        """Starts the GUI application"""
        self.root.mainloop()

if __name__ == "__main__":
    app = EmployeeSchedulerGUI()
    app.run()