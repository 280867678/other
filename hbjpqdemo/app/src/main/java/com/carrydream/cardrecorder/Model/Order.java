package com.carrydream.cardrecorder.Model;

import com.google.gson.annotations.SerializedName;

/* loaded from: classes.dex */
public class Order {
    private int code;
    private String msg;
    private ParamsDTO params;

    public int getCode() {
        return this.code;
    }

    public void setCode(int i) {
        this.code = i;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String str) {
        this.msg = str;
    }

    public ParamsDTO getParams() {
        return this.params;
    }

    public void setParams(ParamsDTO paramsDTO) {
        this.params = paramsDTO;
    }

    /* loaded from: classes.dex */
    public static class ParamsDTO {
        private String appid;
        private String noncestr;
        @SerializedName("package")
        private String packageX;
        private String partnerid;
        private String prepayid;
        private String sign;
        private String timestamp;

        public String getAppid() {
            return this.appid;
        }

        public void setAppid(String str) {
            this.appid = str;
        }

        public String getPartnerid() {
            return this.partnerid;
        }

        public void setPartnerid(String str) {
            this.partnerid = str;
        }

        public String getPrepayid() {
            return this.prepayid;
        }

        public void setPrepayid(String str) {
            this.prepayid = str;
        }

        public String getPackageX() {
            return this.packageX;
        }

        public void setPackageX(String str) {
            this.packageX = str;
        }

        public String getNoncestr() {
            return this.noncestr;
        }

        public void setNoncestr(String str) {
            this.noncestr = str;
        }

        public String getTimestamp() {
            return this.timestamp;
        }

        public void setTimestamp(String str) {
            this.timestamp = str;
        }

        public String getSign() {
            return this.sign;
        }

        public void setSign(String str) {
            this.sign = str;
        }

        public String toString() {
            return "ParamsDTO{appid='" + this.appid + "', partnerid='" + this.partnerid + "', prepayid='" + this.prepayid + "', packageX='" + this.packageX + "', noncestr='" + this.noncestr + "', timestamp='" + this.timestamp + "', sign='" + this.sign + "'}";
        }
    }

    public String toString() {
        return "Order{code=" + this.code + ", msg='" + this.msg + "', params=" + this.params + '}';
    }
}
