package com.agh.bsct.datacollector.library.adapter.queryresult;

import com.google.gson.annotations.SerializedName;

public class Tags {

    @SerializedName("area")
    private String area;

    @SerializedName("type")
    private String type;

    @SerializedName("amenity")
    private String amenity;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("contact:email")
    private String contactEmail;

    @SerializedName("website")
    private String website;

    @SerializedName("addr:city")
    private String addressCity;

    @SerializedName("addr:postcode")
    private String addressPostCode;

    @SerializedName("addr:street")
    private String addressStreet;

    @SerializedName("addr:housenumber")
    private String addressHouseNumber;

    @SerializedName("wheelchair")
    private String wheelchair;

    @SerializedName("wheelchair:description")
    private String wheelchairDescription;

    @SerializedName("opening_hours")
    private String openingHours;

    @SerializedName("internet_access")
    private String internetAccess;

    @SerializedName("fee")
    private String fee;

    @SerializedName("operator")
    private String operator;

    public String getArea() {
        return area;
    }

    public String getType() {
        return type;
    }

    public String getAmenity() {
        return amenity;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getWebsite() {
        return website;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public String getAddressPostCode() {
        return addressPostCode;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public String getAddressHouseNumber() {
        return addressHouseNumber;
    }

    public String getWheelchair() {
        return wheelchair;
    }

    public String getWheelchairDescription() {
        return wheelchairDescription;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getInternetAccess() {
        return internetAccess;
    }

    public String getFee() {
        return fee;
    }

    public String getOperator() {
        return operator;
    }
}