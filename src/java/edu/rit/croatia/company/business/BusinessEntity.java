package edu.rit.croatia.company.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import companydata.DataLayer;

/**
 *
 * @author Kristina Marasovic [kristina.marasovic@croatia.rit.edu]
 */
public class BusinessEntity {

    protected DataLayer dl = null;
    protected Gson gson = null;

    public BusinessEntity() {
        try {
            this.dl = new DataLayer(BusinessConfig.COMPANY_NAME);
            this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        } catch (Exception e) {
        }

    }
    public Gson getGson() {
        return this.gson;
    }

}
