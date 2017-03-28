package com.challenge.svakt.qantasairportdetails.model;

/**
 * Created by sunny on 27-03-2017.
 */

public class QantasAirportData {
    private String airportName;
    private String country;
    private String currency;
    private String timezone;
    private String latitude;
    private String longitude;

    public String getAirportName() {
        return airportName;
    }

    public String getCountry() {
        return country;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public QantasAirportData(String airportName, String country, String currency, String timezone, String latitude, String longitude) {
        this.airportName = airportName;
        this.country = country;
        this.currency = currency;
        this.timezone = timezone;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
