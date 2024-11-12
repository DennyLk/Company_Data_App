package edu.rit.croatia.company.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import edu.rit.croatia.company.business.EmployeeBusiness;
import companydata.Employee;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.google.gson.Gson;
import java.sql.Date;
import java.util.List;

@Path("")
public class EmployeeResource {

    private final EmployeeBusiness employeeBusiness;
    private final Gson gson = new Gson();

    public EmployeeResource() {
        this.employeeBusiness = new EmployeeBusiness();
    }

    @GET
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmployees(@QueryParam("company") String companyName) {
        try {
            List<Employee> employees = this.employeeBusiness.getAll(companyName);
            return Response.status(Response.Status.OK)
                    .entity("{\"success\": " + gson.toJson(employees) + "}")
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @GET
    @Path("/employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployeeById(@QueryParam("emp_id") int empId) {
        try {
            Employee employee = this.employeeBusiness.getEmployee(empId);
            return Response.status(Response.Status.OK)
                    .entity("{\"success\": " + gson.toJson(employee) + "}")
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("An error occurred while retrieving the employee"))
                    .build();
        }
    }

    @POST
    @Path("/employee")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertEmployee(@FormParam("company") String companyName, 
                                   @FormParam("emp_name") String empName,
                                   @FormParam("emp_no") String empNo, 
                                   @FormParam("hire_date") String hireDateString,
                                   @FormParam("job") String job, 
                                   @FormParam("salary") double salary,
                                   @FormParam("dept_id") int deptId, 
                                   @FormParam("mng_id") int mngId) {
        try {
            Date hireDate = Date.valueOf(hireDateString);
            Employee employee = new Employee(empName, empNo, hireDate, job, salary, deptId, mngId);
            Employee createdEmployee = this.employeeBusiness.addEmployee(companyName, employee);

            return Response.status(Response.Status.CREATED)
                    .entity("{\"success\": " + gson.toJson(createdEmployee) + "}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("An error occurred while adding the employee"))
                    .build();
        }
    }

    @PUT
    @Path("/employee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmployee(String employeeJson) {
        try {
            JsonObject jsonObject = JsonParser.parseString(employeeJson).getAsJsonObject();
            String companyName = jsonObject.get("company").getAsString();
            Date hireDate = jsonObject.has("hire_date") ? Date.valueOf(jsonObject.get("hire_date").getAsString()) : null;
            jsonObject.remove("hire_date");

            Employee employeeToUpdate = gson.fromJson(jsonObject.toString(), Employee.class);
            if (hireDate != null) {
                employeeToUpdate.setHireDate(hireDate);
            }

            Employee updatedEmployee = this.employeeBusiness.updateEmployee(companyName, employeeToUpdate);

            return Response.status(Response.Status.OK)
                    .entity("{\"success\":" + gson.toJson(updatedEmployee) + "}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("An error occurred while updating the employee"))
                    .build();
        }
    }

    @DELETE
    @Path("/employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEmployee(@QueryParam("emp_id") int empId) {
        try {
            this.employeeBusiness.deleteEmployee(empId);
            return Response.status(Response.Status.OK)
                    .entity("{\"success\":\"Employee " + empId + " deleted.\"}")
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse(e.getMessage())).build();
        }
    }
}
