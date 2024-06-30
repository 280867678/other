package com.carrydream.cardrecorder.tool;

import com.carrydream.cardrecorder.Model.Config;
import com.carrydream.cardrecorder.Model.Info;
import com.carrydream.cardrecorder.Model.Item;
import com.carrydream.cardrecorder.Model.Platform;
import com.carrydream.cardrecorder.Model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/* loaded from: classes.dex */
public class DataUtils extends Observable {
    private static DataUtils instance;
    private Gson gson = new Gson();

    public static DataUtils getInstance() {
        if (instance == null) {
            synchronized (DataUtils.class) {
                if (instance == null) {
                    instance = new DataUtils();
                }
            }
        }
        return instance;
    }

    public void setConfig(Config config) {
        SpUtils.getInstance().putString("config", this.gson.toJson(config));
    }

    public Config getConfig() {
        return (Config) this.gson.fromJson(SpUtils.getInstance().getString("config"),  Config.class);
    }

    public void setInfo(Info info) {
        SpUtils.getInstance().putString("info", this.gson.toJson(info));
    }

    public Info getInfo() {
        return (Info) this.gson.fromJson(SpUtils.getInstance().getString("info"), Info.class);
    }

    public void setPoker(HashMap<String, Integer> hashMap) {
        SpUtils.getInstance().putString("poker", this.gson.toJson(hashMap));
    }

    public HashMap<String, Integer> getPoker() {
        HashMap hashMap = new HashMap();
        HashMap<String, Integer> hashMap2 = new HashMap<>();
        HashMap hashMap3 = (HashMap) this.gson.fromJson(SpUtils.getInstance().getString("poker"),  hashMap.getClass());
        if (hashMap3 == null) {
            return null;
        }
        for (Object str : hashMap3.keySet()) {
            hashMap2.put(str.toString(), Integer.valueOf(Double.valueOf(((Double) hashMap3.get(str)).doubleValue()).intValue()));
        }
        return hashMap2;
    }

    public void setUser(User user) {
        SpUtils.getInstance().putString("user", this.gson.toJson(user));
    }

    public User getUser() {
        return (User) this.gson.fromJson(SpUtils.getInstance().getString("user"),  User.class);
    }

    public void setProtocol(boolean z) {
        SpUtils.getInstance().putBoolean("protocol", z);
    }

    public boolean getProtocol() {
        return SpUtils.getInstance().getBoolean("protocol").booleanValue();
    }

    public void setNumber(int i) {
        SpUtils.getInstance().putInt("number", i);
    }

    public int getNumber() {
        return SpUtils.getInstance().getInt("number", 1);
    }

    public void setItem(int i) {
        SpUtils.getInstance().putInt("item", i);
    }

    public int getItem() {
        return SpUtils.getInstance().getInt("item", 0);
    }

    public void setHand(int i) {
        SpUtils.getInstance().putInt("hand", i);
    }

    public int getHand() {
        return SpUtils.getInstance().getInt("hand", 0);
    }

    public List<Item> getPlatform() {
        return (List) this.gson.fromJson(SpUtils.getInstance().getString("platform"), new TypeToken<List<Item>>() { // from class: com.carrydream.cardrecorder.tool.DataUtils.1
        }.getType());
    }

    public void setPlatform(List<Item> list) {
        SpUtils.getInstance().putString("platform", this.gson.toJson(list));
    }

    public void setGamePlatform(Platform platform) {
        SpUtils.getInstance().putString("game", this.gson.toJson(platform));
    }

    public Platform getGamePlatform() {
        return (Platform) this.gson.fromJson(SpUtils.getInstance().getString("game"),  Platform.class);
    }
}
