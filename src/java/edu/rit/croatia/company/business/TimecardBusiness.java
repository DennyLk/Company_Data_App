package edu.rit.croatia.company.business;

import companydata.Timecard;
import companydata.Employee;
import jakarta.ws.rs.NotFoundException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class TimecardBusiness extends BusinessEntity {

    // Method to create a new timecard with necessary validations
    public Timecard createTimecard(int empId, Timestamp startTimestamp, Timestamp endTimestamp) {
        // Validate if employee exists
        if (!validateEmployeeExists(empId)) {
            throw new IllegalArgumentException("Employee with ID " + empId + " does not exist.");
        }

        // Perform necessary validations on start and end time
        validateTimeConstraints(startTimestamp, endTimestamp, empId);

        // Create and insert the timecard
        Timecard timecard = new Timecard(startTimestamp, endTimestamp, empId);
        return dl.insertTimecard(timecard);  // Assuming insertTimecard is defined in DataLayer
    }

    // Method to retrieve a specific timecard by its ID
    public Timecard getTimecard(int timecardId) {
        Timecard timecard = dl.getTimecard(timecardId);  // Assuming getTimecard is defined in DataLayer
        if (timecard == null) {
            throw new NotFoundException("Timecard not found.");
        }
        return timecard;
    }

    // Method to retrieve all timecards for a specific employee
    public List<Timecard> getTimecardsByEmployee(int empId) {
        List<Timecard> timecards = dl.getAllTimecard(empId);  // Assuming getAllTimecard is defined in DataLayer
        if (timecards == null || timecards.isEmpty()) {
            throw new NotFoundException("No timecards found for employee with ID " + empId + ".");
        }
        return timecards;
    }

    // Method to update an existing timecard
    public Timecard updateTimecard(Timecard timecard) {
        // Fetch the existing timecard from the data layer
        Timecard existingTimecard = dl.getTimecard(timecard.getId());
        if (existingTimecard == null) {
            throw new NotFoundException("Timecard not found for update.");
        }

        // Validate the input fields for start and end time
        validateTimeConstraints(timecard.getStartTime(), timecard.getEndTime(), timecard.getEmpId());

        // Update fields and save the updated timecard
        existingTimecard.setStartTime(timecard.getStartTime());
        existingTimecard.setEndTime(timecard.getEndTime());
        dl.updateTimecard(existingTimecard);  // Assuming updateTimecard is defined in DataLayer

        return existingTimecard;
    }

    // Method to delete a timecard by ID
    public void deleteTimecard(int timecardId) {
        // Check if the timecard exists before attempting to delete
        Timecard timecard = dl.getTimecard(timecardId);  // Assuming getTimecard is defined in DataLayer
        if (timecard == null) {
            throw new NotFoundException("Timecard not found for deletion.");
        }
        dl.deleteTimecard(timecardId);  // Assuming deleteTimecard is defined in DataLayer
    }

    // Method to validate time constraints for creating/updating timecards
    private void validateTimeConstraints(Timestamp startTime, Timestamp endTime, int empId) {
        // Ensure both times are not null
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required.");
        }

        // Ensure start_time is within the last week
        long currentTime = System.currentTimeMillis();
        long oneWeekAgo = currentTime - (7 * 24 * 60 * 60 * 1000);
        if (startTime.getTime() < oneWeekAgo || startTime.getTime() > currentTime) {
            throw new IllegalArgumentException("Start time must be within the last 7 days.");
        }

        // Ensure end time is after start time and on the same day
        if (!endTime.after(startTime) || !isSameDay(startTime, endTime)) {
            throw new IllegalArgumentException("End time must be on the same day and at least 1 hour after start time.");
        }

        // Ensure times are on a weekday and between 06:00 - 18:00
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startTime);
        int startHour = startCal.get(Calendar.HOUR_OF_DAY);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endTime);
        int endHour = endCal.get(Calendar.HOUR_OF_DAY);

        if (isWeekend(startTime) || isWeekend(endTime)) {
            throw new IllegalArgumentException("Start and end times must be on a weekday (Monday to Friday).");
        }
        if (startHour < 6 || startHour > 18 || endHour < 6 || endHour > 18) {
            throw new IllegalArgumentException("Start and end times must be between 06:00 and 18:00.");
        }

        // Check for duplicate start_time on the same day for the employee
        List<Timecard> timecards = dl.getAllTimecard(empId);
        for (Timecard tc : timecards) {
            if (isSameDay(tc.getStartTime(), startTime)) {
                throw new IllegalArgumentException("Duplicate start time for the same day.");
            }
        }
    }

    // Helper method to check if two timestamps are on the same day
    private boolean isSameDay(Timestamp start, Timestamp end) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTime(start);
        endCal.setTime(end);
        return startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)
                && startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR);
    }

    // Helper method to check if a timestamp falls on a weekend
    private boolean isWeekend(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    // Helper method to validate if an employee exists
    private boolean validateEmployeeExists(int empId) {
        Employee employee = dl.getEmployee(empId);  // Assuming getEmployee is defined in DataLayer
        return employee != null;
    }
}
