package com.shahenat.User.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class NearestDriverModel {

    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
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
        @SerializedName("customer_id")
        @Expose
        public String customerId;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("last_name")
        @Expose
        public String lastName;
        @SerializedName("phone_code")
        @Expose
        public String phoneCode;
        @SerializedName("mobile")
        @Expose
        public String mobile;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("social_id")
        @Expose
        public String socialId;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("address2")
        @Expose
        public String address2;
        @SerializedName("lat")
        @Expose
        public String lat;
        @SerializedName("lon")
        @Expose
        public String lon;
        @SerializedName("register_id")
        @Expose
        public String registerId;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("country")
        @Expose
        public String country;
        @SerializedName("state")
        @Expose
        public String state;
        @SerializedName("city")
        @Expose
        public String city;
        @SerializedName("online_status")
        @Expose
        public String onlineStatus;
        @SerializedName("wallet")
        @Expose
        public String wallet;
        @SerializedName("distance")
        @Expose
        public String distance;
        @SerializedName("estimate_time")
        @Expose
        public Integer estimateTime;
        @SerializedName("result")
        @Expose
        public String result;
        @SerializedName("driver_vehicle")
        @Expose
        public DriverVehicle driverVehicle;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneCode() {
            return phoneCode;
        }

        public void setPhoneCode(String phoneCode) {
            this.phoneCode = phoneCode;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getSocialId() {
            return socialId;
        }

        public void setSocialId(String socialId) {
            this.socialId = socialId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getOnlineStatus() {
            return onlineStatus;
        }

        public void setOnlineStatus(String onlineStatus) {
            this.onlineStatus = onlineStatus;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public Integer getEstimateTime() {
            return estimateTime;
        }

        public void setEstimateTime(Integer estimateTime) {
            this.estimateTime = estimateTime;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public DriverVehicle getDriverVehicle() {
            return driverVehicle;
        }

        public void setDriverVehicle(DriverVehicle driverVehicle) {
            this.driverVehicle = driverVehicle;
        }
        public class DriverVehicle {

            @SerializedName("id")
            @Expose
            public String id;
            @SerializedName("driver_id")
            @Expose
            public String driverId;
            @SerializedName("equipment_id")
            @Expose
            public String equipmentId;
            @SerializedName("vehicle_image")
            @Expose
            public String vehicleImage;
            @SerializedName("equipment_name")
            @Expose
            public String equipmentName;
            @SerializedName("manufacturer")
            @Expose
            public String manufacturer;
            @SerializedName("model")
            @Expose
            public String model;
            @SerializedName("brand")
            @Expose
            public String brand;
            @SerializedName("size")
            @Expose
            public String size;
            @SerializedName("number_plate")
            @Expose
            public String numberPlate;
            @SerializedName("color")
            @Expose
            public String color;
            @SerializedName("price_km")
            @Expose
            public String priceKm;
            @SerializedName("description")
            @Expose
            public String description;
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

            public String getDriverId() {
                return driverId;
            }

            public void setDriverId(String driverId) {
                this.driverId = driverId;
            }

            public String getEquipmentId() {
                return equipmentId;
            }

            public void setEquipmentId(String equipmentId) {
                this.equipmentId = equipmentId;
            }

            public String getVehicleImage() {
                return vehicleImage;
            }

            public void setVehicleImage(String vehicleImage) {
                this.vehicleImage = vehicleImage;
            }

            public String getEquipmentName() {
                return equipmentName;
            }

            public void setEquipmentName(String equipmentName) {
                this.equipmentName = equipmentName;
            }

            public String getManufacturer() {
                return manufacturer;
            }

            public void setManufacturer(String manufacturer) {
                this.manufacturer = manufacturer;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getNumberPlate() {
                return numberPlate;
            }

            public void setNumberPlate(String numberPlate) {
                this.numberPlate = numberPlate;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public String getPriceKm() {
                return priceKm;
            }

            public void setPriceKm(String priceKm) {
                this.priceKm = priceKm;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
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
}


