package com.carrydream.cardrecorder.PokerGame;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.carrydream.cardrecorder.tool.DataUtils;
import com.carrydream.cardrecorder.tool.OnOcr;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public abstract class Poker {
    public HashMap<String, Integer> initData;
    OnOcr onOcr;
    public HashMap<String, Integer> data = new HashMap<>();
    List<String> oneself = new ArrayList();
    List<List<String>> next = new ArrayList();
    List<List<String>> last = new ArrayList();

    public abstract Poker init();

    public void play(int i, List<String> list) {
        Log.e("识别到新的牌局play  ", String.valueOf(i));
        HashMap<String, Integer> hashMap;
        List<List<String>> list2;
        List<List<String>> list3;
        if (list.size() == 0) {
            return;
        }
        if (i == 0) {
            int size = list.size();
            int hand = DataUtils.getInstance().getHand();
            if (size >= hand && !list.equals(this.oneself)) {
                if ((size < this.oneself.size() && size > hand) || Tools.isSubList(this.oneself, list)) {
                    return;
                }
                Log.e("识别到新的牌局", list.toString());
                reset(list);
                for (String str : list) {
                    if (this.data.get(str) != null) {
                        int intValue = this.data.get(str).intValue() - 1;
                        if (intValue < 0) {
                            Log.e("+++++++", "出现错误的牌" + list.toString());
                            return;
                        }
                        this.data.put(str, Integer.valueOf(intValue));
                    }
                }
            } else if (this.oneself != null) {
                return;
            }
        } else if (i == 1) {
            if (this.next.size() > 0) {
                List<List<String>> list4 = this.next;
                if (list4.get(list4.size() - 1).equals(list)) {
                    return;
                }
            }
            if (this.next.size() > 0) {
                List<List<String>> list5 = this.next;
                if (list.containsAll(list5.get(list5.size() - 1))) {
                    List<List<String>> list6 = this.next;
                    List<List<String>> list7 = this.next;
                    list7.remove(list7.size() - 1);
                    for (String str2 : list6.get(list6.size() - 1)) {
                        if (this.data.get(str2) != null) {
                            this.data.put(str2, Integer.valueOf(this.data.get(str2).intValue() + 1));
                        }
                    }
                }
            }
            for (String str3 : list) {
                if (this.data.get(str3) != null) {
                    int intValue2 = this.data.get(str3).intValue() - 1;
                    if (intValue2 < 0) {
                        Log.e("+++++++", "下家的牌出现错误" + list.toString());
                        return;
                    }
                    this.data.put(str3, Integer.valueOf(intValue2));
                }
            }
            this.next.add(list);
            if (this.onOcr != null && (list2 = this.next) != null && list2.size() > 0) {
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.carrydream.cardrecorder.PokerGame.Poker$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Poker.this.m48lambda$play$0$comcarrydreamcardrecorderPokerGamePoker();
                    }
                });
            }
        } else if (i == 2) {
            if (this.last.size() > 0) {
                List<List<String>> list8 = this.last;
                if (list8.get(list8.size() - 1).equals(list)) {
                    return;
                }
            }
            if (this.last.size() > 0) {
                List<List<String>> list9 = this.last;
                if (list.containsAll(list9.get(list9.size() - 1))) {
                    List<List<String>> list10 = this.last;
                    List<List<String>> list11 = this.last;
                    list11.remove(list11.size() - 1);
                    for (String str4 : list10.get(list10.size() - 1)) {
                        if (this.data.get(str4) != null) {
                            this.data.put(str4, Integer.valueOf(this.data.get(str4).intValue() + 1));
                        }
                    }
                }
            }
            for (String str5 : list) {
                if (this.data.get(str5) != null) {
                    int intValue3 = this.data.get(str5).intValue() - 1;
                    if (intValue3 < 0) {
                        Log.e("+++++++", "上家的牌出现错误" + list.toString());
                        return;
                    }
                    this.data.put(str5, Integer.valueOf(intValue3));
                }
            }
            this.last.add(list);
            if (this.onOcr != null && (list3 = this.last) != null && list3.size() > 0) {
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.carrydream.cardrecorder.PokerGame.Poker$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Poker.this.m49lambda$play$1$comcarrydreamcardrecorderPokerGamePoker();
                    }
                });
            }
        }
        if (this.onOcr == null || (hashMap = this.data) == null || hashMap.size() <= 0) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.carrydream.cardrecorder.PokerGame.Poker$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Poker.this.m50lambda$play$2$comcarrydreamcardrecorderPokerGamePoker();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$play$0$com-carrydream-cardrecorder-PokerGame-Poker  reason: not valid java name */
    public /* synthetic */ void m48lambda$play$0$comcarrydreamcardrecorderPokerGamePoker() {
        this.onOcr.next(this.next);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$play$1$com-carrydream-cardrecorder-PokerGame-Poker  reason: not valid java name */
    public /* synthetic */ void m49lambda$play$1$comcarrydreamcardrecorderPokerGamePoker() {
        this.onOcr.last(this.last);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$play$2$com-carrydream-cardrecorder-PokerGame-Poker  reason: not valid java name */
    public /* synthetic */ void m50lambda$play$2$comcarrydreamcardrecorderPokerGamePoker() {
        this.onOcr.output(this.data);
    }

    public void reset(List<String> list) {
        HashMap<String, Integer> hashMap;
        this.data.clear();
        this.data = (HashMap) this.initData.clone();
        this.oneself.clear();
        this.oneself = list;
        this.next.clear();
        this.last.clear();
        if (this.onOcr == null || (hashMap = this.data) == null || hashMap.size() <= 0) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.carrydream.cardrecorder.PokerGame.Poker$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                Poker.this.m51lambda$reset$3$comcarrydreamcardrecorderPokerGamePoker();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$reset$3$com-carrydream-cardrecorder-PokerGame-Poker  reason: not valid java name */
    public /* synthetic */ void m51lambda$reset$3$comcarrydreamcardrecorderPokerGamePoker() {
        this.onOcr.NewRound(this.data);
    }

    public HashMap<String, Integer> getData() {
        return this.data;
    }

    public Poker setData(HashMap<String, Integer> hashMap) {
        this.initData = hashMap;
        return this;
    }

    public void setOnOcr(OnOcr onOcr) {
        this.onOcr = onOcr;
    }
}
