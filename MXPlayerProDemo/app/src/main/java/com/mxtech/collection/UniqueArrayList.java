package com.mxtech.collection;

import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes2.dex */
public class UniqueArrayList<T> extends ArrayList<T> {
    private static final long serialVersionUID = 4825712605105946769L;

    public UniqueArrayList(int capacity) {
        super(capacity);
    }

    public UniqueArrayList() {
    }

    public UniqueArrayList(Collection<? extends T> collection) {
        super(collection);
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public void add(int index, T object) {
        if (contains(object)) {
            throw new IllegalArgumentException();
        }
        super.add(index, object);
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(T object) {
        if (contains(object)) {
            return false;
        }
        return super.add(object);
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends T> collection) {
        for (T object : collection) {
            if (!contains(object)) {
                super.add(object);
            }
        }
        return true;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public boolean addAll(int location, Collection<? extends T> collection) {
        for (T object : collection) {
            if (!contains(object)) {
                super.add(location, object);
                location++;
            }
        }
        return true;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public T set(int index, T object) {
        if (get(index).equals(object) || !contains(object)) {
            return (T) super.set(index, object);
        }
        throw new IllegalArgumentException();
    }
}
