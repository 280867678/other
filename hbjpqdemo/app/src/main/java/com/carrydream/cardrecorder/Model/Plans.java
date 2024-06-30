package com.carrydream.cardrecorder.Model;

import java.util.List;

/* loaded from: classes.dex */
public class Plans {
    private List<PlansDTO> plans;

    public List<PlansDTO> getPlans() {
        return this.plans;
    }

    public void setPlans(List<PlansDTO> list) {
        this.plans = list;
    }

    /* loaded from: classes.dex */
    public static class PlansDTO {
        private String created_at;
        private int day_count;
        private int id;
        private String intro;
        private int level;
        private double original_price;
        private double price;
        private String updated_at;

        public int getId() {
            return this.id;
        }

        public void setId(int i) {
            this.id = i;
        }

        public int getLevel() {
            return this.level;
        }

        public void setLevel(int i) {
            this.level = i;
        }

        public double getPrice() {
            return this.price;
        }

        public void setPrice(int i) {
            this.price = i;
        }

        public double getOriginal_price() {
            return this.original_price;
        }

        public void setOriginal_price(int i) {
            this.original_price = i;
        }

        public int getDay_count() {
            return this.day_count;
        }

        public void setDay_count(int i) {
            this.day_count = i;
        }

        public String getIntro() {
            return this.intro;
        }

        public void setIntro(String str) {
            this.intro = str;
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
    }
}
