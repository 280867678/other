package com.carrydream.cardrecorder.Model;

import java.util.List;

/* loaded from: classes.dex */
public class Info {
    private LocationDTO location;
    private String token;
    private UserDTO user;

    public String getToken() {
        return this.token;
    }

    public void setToken(String str) {
        this.token = str;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public void setUser(UserDTO userDTO) {
        this.user = userDTO;
    }

    public LocationDTO getLocation() {
        return this.location;
    }

    public void setLocation(LocationDTO locationDTO) {
        this.location = locationDTO;
    }

    /* loaded from: classes.dex */
    public static class UserDTO {
        private String created_at;
        private int id;
        private String mobile;
        private String package_tag;
        private String reg_city;
        private Object reg_version_code;
        private String updated_at;

        public String getMobile() {
            return this.mobile;
        }

        public void setMobile(String str) {
            this.mobile = str;
        }

        public String getPackage_tag() {
            return this.package_tag;
        }

        public void setPackage_tag(String str) {
            this.package_tag = str;
        }

        public String getReg_city() {
            return this.reg_city;
        }

        public void setReg_city(String str) {
            this.reg_city = str;
        }

        public Object getReg_version_code() {
            return this.reg_version_code;
        }

        public void setReg_version_code(Object obj) {
            this.reg_version_code = obj;
        }

        public String getUpdated_at() {
            return this.updated_at;
        }

        public void setUpdated_at(String str) {
            this.updated_at = str;
        }

        public String getCreated_at() {
            return this.created_at;
        }

        public void setCreated_at(String str) {
            this.created_at = str;
        }

        public int getId() {
            return this.id;
        }

        public void setId(int i) {
            this.id = i;
        }

        public String toString() {
            return "UserDTO{mobile='" + this.mobile + "', package_tag='" + this.package_tag + "', reg_city='" + this.reg_city + "', reg_version_code=" + this.reg_version_code + ", updated_at='" + this.updated_at + "', created_at='" + this.created_at + "', id=" + this.id + '}';
        }
    }

    /* loaded from: classes.dex */
    public static class LocationDTO {
        private List<String> city;
        private String ip;
        private int province_code;
        private String province_name;

        public String getIp() {
            return this.ip;
        }

        public void setIp(String str) {
            this.ip = str;
        }

        public int getProvince_code() {
            return this.province_code;
        }

        public void setProvince_code(int i) {
            this.province_code = i;
        }

        public String getProvince_name() {
            return this.province_name;
        }

        public void setProvince_name(String str) {
            this.province_name = str;
        }

        public List<String> getCity() {
            return this.city;
        }

        public void setCity(List<String> list) {
            this.city = list;
        }

        public String toString() {
            return "LocationDTO{ip='" + this.ip + "', province_code=" + this.province_code + ", province_name='" + this.province_name + "', city=" + this.city + '}';
        }
    }

    public String toString() {
        return "Info{token='" + this.token + "', user=" + this.user + ", location=" + this.location + '}';
    }
}
