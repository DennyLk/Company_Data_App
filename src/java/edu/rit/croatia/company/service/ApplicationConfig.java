/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.rit.croatia.company.service;

import java.util.Set;

/**
 *
 * @author Kristina Marasovic [kristina.marasovic@croatia.rit.edu]
 */
@jakarta.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends jakarta.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(edu.rit.croatia.company.service.DepartmentResource.class);
        resources.add(edu.rit.croatia.company.service.EmployeeResource.class); // Add this line
        resources.add(edu.rit.croatia.company.service.CompanyResource.class); // Add this line
        resources.add(edu.rit.croatia.company.service.TimecardResource.class); // Add this line
    }
    
}
