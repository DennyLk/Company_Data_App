package edu.rit.croatia.company.service;

import edu.rit.croatia.company.business.CompanyBusiness;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/company")
public class CompanyResource {

    private final CompanyBusiness companyBusiness;

    public CompanyResource() {
        this.companyBusiness = new CompanyBusiness();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCompany(@QueryParam("company") String companyName) {
        try {
            if (companyName == null || companyName.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Company name is required"))
                        .build();
            }
            companyBusiness.deleteCompany(companyName);
            return Response.status(Response.Status.OK)
                    .entity("{\"success\":\"Company " + companyName + " deleted.\"}")
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error deleting company: " + e.getMessage()))
                    .build();
        }
    }
}
