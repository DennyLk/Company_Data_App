package edu.rit.croatia.company.business;

import jakarta.ws.rs.NotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompanyBusiness extends BusinessEntity {

    public void deleteCompany(String companyName) {
        try {
            if (companyName == null || companyName.isBlank()) {
                throw new IllegalArgumentException("Company name cannot be empty.");
            }

            int result = this.dl.deleteCompany(companyName);
            if (result == 0  ) {
                throw new NotFoundException("Company not found for deletion.");
            }
        } catch (Exception e) {
            Logger.getLogger(BusinessEntity.class.getName()).log(Level.SEVERE, "Error deleting company", e);
            throw new RuntimeException("Company not found");
        }
    }
}
