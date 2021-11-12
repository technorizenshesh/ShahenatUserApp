package com.shahenat.User.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SameDayBookingModel {

    @SerializedName("result")
    @Expose
    public Result result;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("driver_id")
        @Expose
        public String driverId;
        @SerializedName("pickup_add")
        @Expose
        public String pickupAdd;
        @SerializedName("p_lat")
        @Expose
        public String pLat;
        @SerializedName("p_lon")
        @Expose
        public String pLon;
        @SerializedName("drop_add")
        @Expose
        public String dropAdd;
        @SerializedName("d_lat")
        @Expose
        public String dLat;
        @SerializedName("d_lon")
        @Expose
        public String dLon;
        @SerializedName("vehicle_id")
        @Expose
        public String vehicleId;
        @SerializedName("total_price")
        @Expose
        public String totalPrice;
        @SerializedName("total_km")
        @Expose
        public String totalKm;
        @SerializedName("payment_type")
        @Expose
        public String paymentType;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("date_time")
        @Expose
        public String dateTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public String getPickupAdd() {
            return pickupAdd;
        }

        public void setPickupAdd(String pickupAdd) {
            this.pickupAdd = pickupAdd;
        }

        public String getpLat() {
            return pLat;
        }

        public void setpLat(String pLat) {
            this.pLat = pLat;
        }

        public String getpLon() {
            return pLon;
        }

        public void setpLon(String pLon) {
            this.pLon = pLon;
        }

        public String getDropAdd() {
            return dropAdd;
        }

        public void setDropAdd(String dropAdd) {
            this.dropAdd = dropAdd;
        }

        public String getdLat() {
            return dLat;
        }

        public void setdLat(String dLat) {
            this.dLat = dLat;
        }

        public String getdLon() {
            return dLon;
        }

        public void setdLon(String dLon) {
            this.dLon = dLon;
        }

        public String getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(String vehicleId) {
            this.vehicleId = vehicleId;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getTotalKm() {
            return totalKm;
        }

        public void setTotalKm(String totalKm) {
            this.totalKm = totalKm;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

    }

}