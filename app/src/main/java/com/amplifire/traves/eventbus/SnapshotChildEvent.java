package com.amplifire.traves.eventbus;


import com.firebase.client.DataSnapshot;

/**
 * Created by pratama on 5/20/2016.
 */
public class SnapshotChildEvent {


    public DataSnapshot dataSnapshot;
    public int type; //firebaseutils add, changed, moved, cancel

    public SnapshotChildEvent(DataSnapshot dataSnapshot, int type) {
        this.dataSnapshot = dataSnapshot;
        this.type = type;
    }

    public DataSnapshot getDataSnapshot() {
        return dataSnapshot;
    }

    public int getType() {
        return type;
    }
}
