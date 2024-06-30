package com.mxtech.collection;

import java.util.Map;

/* loaded from: classes2.dex */
public final class SimpleMapEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public SimpleMapEntry(K theKey, V theValue) {
        this.key = theKey;
        this.value = theValue;
    }

    public SimpleMapEntry(Map.Entry<? extends K, ? extends V> copyFrom) {
        this.key = copyFrom.getKey();
        this.value = copyFrom.getValue();
    }

    @Override // java.util.Map.Entry
    public K getKey() {
        return this.key;
    }

    @Override // java.util.Map.Entry
    public V getValue() {
        return this.value;
    }

    @Override // java.util.Map.Entry
    public V setValue(V object) {
        V result = this.value;
        this.value = object;
        return result;
    }

    @Override // java.util.Map.Entry
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry) object;
            if (this.key != null ? this.key.equals(entry.getKey()) : entry.getKey() == null) {
                if (this.value == null) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                } else if (this.value.equals(entry.getValue())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // java.util.Map.Entry
    public int hashCode() {
        return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value != null ? this.value.hashCode() : 0);
    }

    public String toString() {
        return this.key + "=" + this.value;
    }
}
