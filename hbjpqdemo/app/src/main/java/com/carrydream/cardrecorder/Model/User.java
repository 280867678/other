package com.carrydream.cardrecorder.Model;

/* loaded from: classes.dex */
public class User {
    private UserDTO user;

    public UserDTO getUser() {
        return this.user;
    }

    public void setUser(UserDTO userDTO) {
        this.user = userDTO;
    }

    /* loaded from: classes.dex */
    public static class UserDTO {
        private String created_at;
        private Object deleted_at;
        private int id;
        private int is_vip;
        private String mobile;
        private String package_tag;
        private String reg_city;
        private int reg_version_code;
        private String updated_at;
        private Object vip_end;
        private Object vip_start;

        public int getId() {
            return this.id;
        }

        public void setId(int i) {
            this.id = i;
        }

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

        public int getIs_vip() {
            return this.is_vip;
        }

        public void setIs_vip(int i) {
            this.is_vip = i;
        }

        public Object getVip_start() {
            return this.vip_start;
        }

        public void setVip_start(Object obj) {
            this.vip_start = obj;
        }

        public Object getVip_end() {
            return this.vip_end;
        }

        public void setVip_end(Object obj) {
            this.vip_end = obj;
        }

        public String getReg_city() {
            return this.reg_city;
        }

        public void setReg_city(String str) {
            this.reg_city = str;
        }

        public int getReg_version_code() {
            return this.reg_version_code;
        }

        public void setReg_version_code(int i) {
            this.reg_version_code = i;
        }

        public String getCreated_at() {
            return this.created_at;
        }

        public void setCreated_at(String str) {
            this.created_at = str;
        }

        public String getUpdated_at() {
            return this.updated_at;
        }

        public void setUpdated_at(String str) {
            this.updated_at = str;
        }

        public Object getDeleted_at() {
            return this.deleted_at;
        }

        public void setDeleted_at(Object obj) {
            this.deleted_at = obj;
        }

        public String toString() {
            return "UserDTO{id=" + this.id + ", mobile='" + this.mobile + "', package_tag='" + this.package_tag + "', is_vip=" + this.is_vip + ", vip_start=" + this.vip_start + ", vip_end=" + this.vip_end + ", reg_city='" + this.reg_city + "', reg_version_code=" + this.reg_version_code + ", created_at='" + this.created_at + "', updated_at='" + this.updated_at + "', deleted_at=" + this.deleted_at + '}';
        }
    }

    public String toString() {
        return "User{user=" + this.user + '}';
    }
}
