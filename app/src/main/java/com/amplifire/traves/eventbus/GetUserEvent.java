package com.amplifire.traves.eventbus;


import com.firebase.client.DataSnapshot;

/**
 * Created by pratama on 5/20/2016.
 */
public class GetUserEvent {
    public DataSnapshot dataSnapshot;

    public GetUserEvent(DataSnapshot dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }


}
