package edu.rit.croatia.company.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rit.croatia.company.business.TimecardBusiness;
import companydata.Timecard;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.List;

@Path("")
public class TimecardResource {

    private final TimecardBusiness timecardBusiness;
    private final Gson gson;

    public TimecardResource() {
        this.timecardBusiness = new TimecardBusiness();
        this.gson = new Gson();
    }

    // GET /timecard?timecard_id=#
    @GET
    @Path("/timecard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimecardById(@QueryParam("timecard_id") int timecardId) {
        try {
            Timecard timecard = timecardBusiness.getTimecard(timecardId);
            if (timecard != null) {
                return Response.ok("{\"timecard\":" + gson.toJson(timecard) + "}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Timecard not found.\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error retrieving timecard: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /timecards?emp_id=#
    @GET
    @Path("/timecards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimecardsByEmployee(@QueryParam("emp_id") int empId) {
        try {
            List<Timecard> timecards = timecardBusiness.getTimecardsByEmployee(empId);
            if (timecards != null && !timecards.isEmpty()) {
                return Response.ok("{\"timecards\":" + gson.toJson(timecards) + "}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"No timecards found for employee.\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error retrieving timecards: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/timecard")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTimecard(@FormParam("emp_id") int empId,
                                   @FormParam("start_time") String startTime,
                                   @FormParam("end_time") String endTime) {
        try {
            // Convert startTime and endTime strings to Timestamp objects
            Timestamp startTimestamp = Timestamp.valueOf(startTime);
            Timestamp endTimestamp = Timestamp.valueOf(endTime);

            // Call the business layer to create the timecard
            Timecard newTimecard = timecardBusiness.createTimecard(empId, startTimestamp, endTimestamp);

            // Return success response with the created timecard information, including its ID
            JsonObject successResponse = new JsonObject();
            JsonObject successData = new JsonObject();
            successData.addProperty("timecard_id", newTimecard.getId());
            successData.addProperty("start_time", newTimecard.getStartTime().toString());
            successData.addProperty("end_time", newTimecard.getEndTime().toString());
            successData.addProperty("emp_id", newTimecard.getEmpId());
            successResponse.add("success", successData);

            return Response.status(Response.Status.CREATED)
                    .entity(successResponse.toString())
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error creating timecard: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    // PUT /timecard
    @PUT
    @Path("/timecard")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTimecard(String timecardJson) {
        try {
            JsonObject jsonObject = JsonParser.parseString(timecardJson).getAsJsonObject();

            if (!jsonObject.has("timecard_id")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Timecard ID (timecard_id) is required.\"}")
                        .build();
            }

            int timecardId = jsonObject.get("timecard_id").getAsInt();
            Timestamp startTime = jsonObject.has("start_time") ? Timestamp.valueOf(jsonObject.get("start_time").getAsString()) : null;
            Timestamp endTime = jsonObject.has("end_time") ? Timestamp.valueOf(jsonObject.get("end_time").getAsString()) : null;

            Timecard timecard = new Timecard(timecardId, startTime, endTime, 0);
            Timecard updatedTimecard = timecardBusiness.updateTimecard(timecard);

            // Return updated timecard in the response
            JsonObject successResponse = new JsonObject();
            JsonObject successData = new JsonObject();
            successData.addProperty("timecard_id", updatedTimecard.getId());
            successData.addProperty("start_time", updatedTimecard.getStartTime().toString());
            successData.addProperty("end_time", updatedTimecard.getEndTime().toString());
            successData.addProperty("emp_id", updatedTimecard.getEmpId());
            successResponse.add("success", successData);

            return Response.ok(successResponse.toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error updating timecard: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    // DELETE /timecard?timecard_id=#
    @DELETE
    @Path("/timecard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTimecard(@QueryParam("timecard_id") int timecardId) {
        try {
            timecardBusiness.deleteTimecard(timecardId);
            return Response.ok("{\"success\":\"Timecard " + timecardId + " deleted.\"}").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error deleting timecard: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
