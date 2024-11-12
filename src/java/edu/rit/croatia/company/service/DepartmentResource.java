package edu.rit.croatia.company.service;

import edu.rit.croatia.company.business.DepartmentBusiness;
import companydata.Department;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.List;

@Path("")
public class DepartmentResource {

    private final DepartmentBusiness departmentBusiness;
    private final Gson gson = new Gson();

    public DepartmentResource() {
        this.departmentBusiness = new DepartmentBusiness();
    }

    // GET method to retrieve all departments by company name
    @GET
    @Path("/departments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("company") String companyName) {
        try {
            List<Department> departments = this.departmentBusiness.getAll(companyName);
            return Response.status(Response.Status.OK).entity(gson.toJson(departments)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(new ErrorResponse(e.getMessage()))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(gson.toJson(new ErrorResponse(e.getMessage()))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(new ErrorResponse("An error occurred: " + e.getMessage()))).build();
        }
    }

    // GET method to retrieve a specific department by company and dept_id
    @GET
    @Path("/department")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("company") String companyName, @QueryParam("dept_id") int deptId) {
        try {
            Department department = this.departmentBusiness.getDepartment(companyName, deptId);
            return Response.status(Response.Status.OK).entity(gson.toJson(department)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(new ErrorResponse(e.getMessage()))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(gson.toJson(new ErrorResponse(e.getMessage()))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(new ErrorResponse("An error occurred: " + e.getMessage()))).build();
        }
    }

    // POST method to insert a new department
    // POST method to insert a new department
    @POST
    @Path("/department")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(@FormParam("company") String companyName, @FormParam("dept_name") String deptName,
            @FormParam("dept_no") String deptNo, @FormParam("location") String location) {
        try {
            Department department = new Department(companyName, deptName, deptNo, location);
            Department insertedDepartment = this.departmentBusiness.addDepartment(department);

            return Response.status(Response.Status.CREATED)
                    .entity("{\"success\": " + gson.toJson(insertedDepartment) + "}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse("An error occurred while adding the department")).build();
        }
    }

    // PUT method to update a department
    @PUT
    @Path("/department")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String departmentJson) {
        try {
            Department departmentToUpdate = gson.fromJson(departmentJson, Department.class);

            Department updatedDepartment = this.departmentBusiness.updateDepartment(departmentToUpdate);
            return Response.status(Response.Status.OK)
                    .entity("{\"success\":" + gson.toJson(updatedDepartment) + "}").build();
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(new ErrorResponse("Invalid JSON format"))).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(new ErrorResponse(e.getMessage()))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(gson.toJson(new ErrorResponse(e.getMessage()))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(new ErrorResponse("An error occurred while updating the department"))).build();
        }
    }

    // DELETE method to delete a department
    @DELETE
    @Path("/department")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@QueryParam("company") String companyName, @QueryParam("dept_id") int deptId) {
        try {
            this.departmentBusiness.deleteDepartment(companyName, deptId);
            return Response.status(Response.Status.OK)
                    .entity("{\"success\":\"Department " + deptId + " from " + companyName + " deleted.\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(new ErrorResponse(e.getMessage()))).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(gson.toJson(new ErrorResponse(e.getMessage()))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(new ErrorResponse("An error occurred while deleting the department"))).build();
        }
    }
}
