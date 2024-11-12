package edu.rit.croatia.company.business;

import companydata.Department;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartmentBusiness extends BusinessEntity {

    // Retrieve departments with company name
    public List<Department> getAll(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }

        List<Department> departments = this.dl.getAllDepartment(companyName);
        if (departments == null || departments.isEmpty()) {
            throw new NotFoundException("No departments found for company: " + companyName);
        }
        return departments;
    }

    // Retrieve a department by company and dept_id
    public Department getDepartment(String companyName, int deptId) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }

        if (deptId <= 0) {
            throw new IllegalArgumentException("Invalid department ID");
        }

        Department department = this.dl.getDepartment(companyName, deptId);
        if (department == null) {
            throw new NotFoundException("Department not found for company: " + companyName + " and dept_id: " + deptId);
        }
        return department;
    }

    // Add a department
// Add 
    public Department addDepartment(Department department) {
    if (department.getCompany() == null || department.getCompany().isBlank()) {
        throw new IllegalArgumentException("Company name cannot be empty.");
    }
    if (this.dl.getDepartmentNo(department.getCompany(), department.getDeptNo()) != null) {
        throw new IllegalArgumentException("Department number must be unique within the company.");
    }

    // Insert department and retrieve the inserted department
    return this.dl.insertDepartment(department);
}


    // Update a department
    public Department updateDepartment(Department department) {
        if (department.getCompany() == null || department.getCompany().isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }

        Department existingDepartment = this.dl.getDepartment(department.getCompany(), department.getId());
        if (existingDepartment == null) {
            throw new NotFoundException("Department not found for update");
        }

        Department deptWithSameNo = this.dl.getDepartmentNo(department.getCompany(), department.getDeptNo());
        if (deptWithSameNo != null && deptWithSameNo.getId() != department.getId()) {
            throw new IllegalArgumentException("Department number must be unique within the company");
        }

        existingDepartment.setDeptName(department.getDeptName());
        existingDepartment.setDeptNo(department.getDeptNo());
        existingDepartment.setLocation(department.getLocation());

        this.dl.updateDepartment(existingDepartment);
        return existingDepartment;
    }

    // Delete a department
    public void deleteDepartment(String companyName, int deptId) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }

        if (deptId <= 0) {
            throw new IllegalArgumentException("Invalid department ID");
        }

        Department department = this.dl.getDepartment(companyName, deptId);
        if (department == null) {
            throw new NotFoundException("Department not found for deletion");
        }
        this.dl.deleteDepartment(companyName, deptId);
    }
}
