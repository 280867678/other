package com.carrydream.cardrecorder.tool;

import com.carrydream.cardrecorder.Model.GameType;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Game {
    public static List<GameType> getAllGame() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new GameType("斗地主", true));
        arrayList.add(new GameType("跑得快", true));
        arrayList.add(new GameType("麻将", false));
        arrayList.add(new GameType("跑胡子", false));
        return arrayList;
    }
}
