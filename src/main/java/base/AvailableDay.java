package base;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvailableDay extends Object{

    private String date;
    @JsonProperty("business_day")
    private String businessDay;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBusinessDay() {
        return businessDay;
    }

    public void setBusinessDay(String businessDay) {
        this.businessDay = businessDay;
    }

    @Override
    public String toString() {
        return "AvailableDay{" +
                "date='" + date + '\'' +
                ", businessDay='" + businessDay + '\'' +
                '}';
    }
}
