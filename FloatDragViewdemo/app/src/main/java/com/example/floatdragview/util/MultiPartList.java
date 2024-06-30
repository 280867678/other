package com.example.floatdragview.util;

import java.util.ArrayList;
import java.util.Collection;

public final class MultiPartList<E> {

    /* renamed from: a */
    public final ArrayList<E> f19538a = new ArrayList<>();

    /* renamed from: b */
    public final ArrayList<E> f19539b = new ArrayList<>();

    /* renamed from: c */
    public final ArrayList<E> f19540c = new ArrayList<>();

    /* renamed from: d */
    public final ArrayList<E> f19541d = new ArrayList<>();

    /* renamed from: e */
    public boolean f19542e = true;

    /* renamed from: f */
    public boolean f19543f = false;

    /* renamed from: a */
    public final E m9461a(int i) {
        int m9462a = m9462a();
        if (i < 0 || i >= m9462a) {
            return null;
        }
        if (this.f19538a.size() > 0 && i < this.f19538a.size()) {
            return this.f19538a.get(i);
        }
        int size = i - this.f19538a.size();
        ArrayList<E> arrayList = (this.f19542e || this.f19543f) ? this.f19540c : this.f19539b;
        if (arrayList.size() > 0 && size < arrayList.size()) {
            return arrayList.get(size);
        }
        return this.f19541d.get(size - arrayList.size());
    }

    /* renamed from: b */
    public final void m9459b() {
        this.f19539b.clear();
        this.f19540c.clear();
    }

    /* renamed from: a */
    public final boolean m9460a(Collection<E> collection) {
        if (collection == null) {
            return false;
        }
        return this.f19538a.addAll(collection);
    }

    /* renamed from: b */
    public final boolean m9458b(Collection<E> collection) {
        if (collection == null) {
            return false;
        }
        return this.f19539b.addAll(collection);
    }

    /* renamed from: c */
    public final boolean m9457c(Collection<E> collection) {
        if (collection == null) {
            return false;
        }
        return this.f19540c.addAll(collection);
    }

    /* renamed from: d */
    public final boolean m9456d(Collection<E> collection) {
        if (collection == null) {
            return false;
        }
        return this.f19541d.addAll(collection);
    }

    /* renamed from: a */
    public final int m9462a() {
        return this.f19538a.size() + ((this.f19542e || this.f19543f) ? this.f19540c : this.f19539b).size() + this.f19541d.size();
    }
}

