package com.amplifire.traves.feature;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.amplifire.traves.App;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.model.UserDao;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by pratama on 9/25/2017.
 */

public class FirebaseUtils {

    public static String ENTER = "ENTER";
    public static String CLOSE = "CLOSE";
    public static String BASE_API = "https://traves-55a1c.firebaseio.com/";
    public static String LOCATION = "location";
    public static String QUEST = "quest";
    public static String STATUS = "status";
    public static String USER = "user";


    @Inject
    public FirebaseUtils() {
    }


    public void Log(String name, String event) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name + "");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name + "");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, event);
        App.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    public void createOrUpdateUser(Task<AuthResult> task) {
        Firebase ref = (Firebase) getData(USER, null, null);
        ref.addValueEventListener(valueEventListener(task));
    }


    private ValueEventListener valueEventListener(Task<AuthResult> task) {
        String email = task.getResult().getUser().getEmail();
        String name = task.getResult().getUser().getDisplayName();

        if (TextUtils.isEmpty(name)) {
            name = email;
        }

        String finalName = name;

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Observable.from(dataSnapshot.getChildren())
                        .subscribeOn(Schedulers.newThread())
                        .filter(data -> {
                            if (data.child("email").exists()) {
                                if (data.child("email").getValue().equals(email)) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        })
                        .toList()
                        .observeOn(mainThread())
                        .subscribe(new Observer<List<DataSnapshot>>() {
                                       @Override
                                       public void onCompleted() {

                                       }

                                       @Override
                                       public void onError(Throwable e) {

                                       }

                                       @Override
                                       public void onNext(List<DataSnapshot> dataSnapshots) {
                                           if (dataSnapshots.size() == 0) {
                                               createUser(email, finalName);
                                           }
                                       }
                                   }
                        );


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };
    }
    
    public void createUser(String email, String name) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("email", email);
        map.put("name", name);
        updateFirebase(USER, null, null, map);
    }
    
    public Query getData(String path, String orderByChild, String equalTo) {

//example : new Firebase(BASE_API + "location").orderByChild(orderByChild).equalTo(equalTo);
        Query query = new Firebase(BASE_API+ path);
        if (!TextUtils.isEmpty(orderByChild)) {
            query.orderByChild(orderByChild);
        }
        if (!TextUtils.isEmpty(equalTo)) {
            query.equalTo(equalTo);
        }
        return query;
    }


    public void searchSingleData(String path, String orderByChild, String equalTo, String search) {
        Firebase ref = (Firebase) getData(path, orderByChild, equalTo);
        ref.addListenerForSingleValueEvent(valueEventListener(search));

    }


    public void updateFirebase(String path, String orderByChild, String equalTo, HashMap<String, Object> map) {

        /*
        for child
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("current_location", "location1");
        map.put("current_quest", "quest1");
        //if {userid} == null, createnew
        updateFirebase(USER, null, {userid}, map);
        * */

        /*
        * for map inside map
        HashMap<String, Object> statuslocation = new HashMap<String, Object>();
        statuslocation.put("status", "a");
        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("location", statuslocation);
        updateFirebase(USER, null, {userid}, map);
        * */


        Firebase ref = (Firebase) getData(path, orderByChild, equalTo);
        HashMap<String, Object> push = new HashMap<String, Object>();
        push.put(ref.push().getKey(),map);
        ref.updateChildren(push);
    }


    public ValueEventListener valueEventListener(String search) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    //todo when data not found
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };
    }

    public ChildEventListener childListener(String type) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (type.equals(LOCATION)) {
                    LocationDao locationDao = dataSnapshot.getValue(LocationDao.class);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //todo
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //todo
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //todo
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //todo
            }
        };

    }


    public void removeListener(Firebase ref, ChildEventListener childEventListener) {
        ref.removeEventListener(childEventListener);
    }


}
