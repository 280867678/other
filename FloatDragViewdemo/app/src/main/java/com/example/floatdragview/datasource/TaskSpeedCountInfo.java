package com.example.floatdragview.datasource;

import java.io.Serializable;

public class TaskSpeedCountInfo implements Serializable {
    private static final long serialVersionUID = 1;
    public long mHighestSpeed = 0;
    public long mTaskId = 0;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.mTaskId == ((TaskSpeedCountInfo) obj).mTaskId;
    }

    public int hashCode() {
        return (int) (this.mTaskId ^ (this.mTaskId >>> 32));
    }
}
