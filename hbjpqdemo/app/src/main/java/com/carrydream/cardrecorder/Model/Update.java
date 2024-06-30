package com.carrydream.cardrecorder.Model;

import java.util.List;

/* loaded from: classes.dex */
public class Update {
    private String backup_url;
    private String download_url;
    private int is_force;
    private int is_upgrade;
    private String package_name;
    private String package_tag;
    private List<String> upgrade_log;
    private int version_code;
    private String version_num;

    public String getPackage_name() {
        return this.package_name;
    }

    public void setPackage_name(String str) {
        this.package_name = str;
    }

    public String getPackage_tag() {
        return this.package_tag;
    }

    public void setPackage_tag(String str) {
        this.package_tag = str;
    }

    public int getVersion_code() {
        return this.version_code;
    }

    public void setVersion_code(int i) {
        this.version_code = i;
    }

    public String getVersion_num() {
        return this.version_num;
    }

    public void setVersion_num(String str) {
        this.version_num = str;
    }

    public String getDownload_url() {
        return this.download_url;
    }

    public void setDownload_url(String str) {
        this.download_url = str;
    }

    public String getBackup_url() {
        return this.backup_url;
    }

    public void setBackup_url(String str) {
        this.backup_url = str;
    }

    public List<String> getUpgrade_log() {
        return this.upgrade_log;
    }

    public void setUpgrade_log(List<String> list) {
        this.upgrade_log = list;
    }

    public int getIs_force() {
        return this.is_force;
    }

    public void setIs_force(int i) {
        this.is_force = i;
    }

    public int getIs_upgrade() {
        return this.is_upgrade;
    }

    public void setIs_upgrade(int i) {
        this.is_upgrade = i;
    }
}
