package edu.rit.croatia.company.business;

import companydata.Employee;
import jakarta.ws.rs.NotFoundException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class EmployeeBusiness extends BusinessEntity {

    // Retrieve all employees by company
    public List<Employee> getAll(String companyName) {
        if (companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty.");
        }

        List<Employee> employees = this.dl.getAllEmployee(companyName);
        if (employees == null || employees.isEmpty()) {
            throw new NotFoundException("No employees found for company: " + companyName);
        }
        return employees;
    }

    // Retrieve an employee by emp_id
    public Employee getEmployee(int empId) {
        Employee employee = this.dl.getEmployee(empId);
        if (employee == null) {
            throw new NotFoundException("Employee not found for emp_id: " + empId);
        }
        return employee;
    }

    // Add a new employee with validations
    public Employee addEmployee(String companyName, Employee employee) {
        if (this.dl.getDepartment(companyName, employee.getDeptId()) == null) {
            throw new IllegalArgumentException("Invalid department ID for company.");
        }

        if (employee.getMngId() != 0 && this.dl.getEmployee(employee.getMngId()) == null) {
            throw new IllegalArgumentException("Invalid manager ID.");
        }

        List<Employee> employees = this.dl.getAllEmployee(companyName);
        for (Employee emp : employees) {
            if (emp.getEmpNo().equals(employee.getEmpNo())) {
                throw new IllegalArgumentException("Employee number must be unique within the company.");
            }
        }

        validateHireDate(employee.getHireDate());
        return this.dl.insertEmployee(employee);
    }

    // Update an existing employee
    public Employee updateEmployee(String companyName, Employee employee) {
        Employee existingEmployee = this.dl.getEmployee(employee.getId());
        if (existingEmployee == null) {
            throw new NotFoundException("Employee not found for update.");
        }

        if (this.dl.getDepartment(companyName, employee.getDeptId()) == null) {
            throw new IllegalArgumentException("Invalid department ID for company.");
        }

        if (employee.getMngId() != 0 && this.dl.getEmployee(employee.getMngId()) == null) {
            throw new IllegalArgumentException("Invalid manager ID.");
        }

        List<Employee> employees = this.dl.getAllEmployee(companyName);
        for (Employee emp : employees) {
            if (emp.getEmpNo().equals(employee.getEmpNo()) && emp.getId() != employee.getId()) {
                throw new IllegalArgumentException("Employee number must be unique within the company.");
            }
        }

        validateHireDate(employee.getHireDate());
        this.dl.updateEmployee(employee);
        return this.dl.getEmployee(employee.getId());
    }

    // Delete an employee by emp_id
    public void deleteEmployee(int empId) {
        Employee employee = this.dl.getEmployee(empId);
        if (employee == null) {
            throw new NotFoundException("Employee not found for deletion.");
        }
        this.dl.deleteEmployee(empId);
    }

    // Validate that hire_date is in the past or today and on a weekday
    private void validateHireDate(Date hireDate) {
        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date is required.");
        }

        Date currentDate = new Date(System.currentTimeMillis());
        if (hireDate.after(currentDate)) {
            throw new IllegalArgumentException("Hire date cannot be in the future.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(hireDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            throw new IllegalArgumentException("Hire date cannot be on a weekend.");
        }
    }
}
