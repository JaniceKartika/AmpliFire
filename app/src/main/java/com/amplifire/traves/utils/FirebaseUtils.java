package com.amplifire.traves.utils;

import android.os.Bundle;
import android.text.TextUtils;

import com.amplifire.traves.App;
import com.amplifire.traves.eventbus.GetUserEvent;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;

import org.greenrobot.eventbus.EventBus;

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
    public static String LOCATION = "location/";
    public static String QUEST = "quest/";
    public static String STATUS = "status/";
    public static String USER = "user/";

    public static String EMAIL = "email";
    public static String POINT = "point";

    public static String CREATE = "CREATE";
    public static String UPDATE = "UPDATE";

    //child listener
    public static int ADD = 0;
    public static int CHANGE = 1;
    public static int REMOVE = 2;
    public static int MOVED = 3;
    public static int CANCEL = 4;

    //config
    public static String RADIUS = "radius";
    public static String SPLASHSCREEN = "splashscreen";

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

    public void createUser(Task<AuthResult> task) {
        createOrUpdateUser(task, CREATE, null);
    }

    public void updateUser(Task<AuthResult> task, HashMap<String, Object> map) {
        createOrUpdateUser(task, UPDATE, map);
    }

    public void createOrUpdateUser(Task<AuthResult> task, String type, HashMap<String, Object> map) {
        Firebase ref = (Firebase) getData(USER, null, null);
        String email = task.getResult().getUser().getEmail();
        String name = task.getResult().getUser().getDisplayName();

        if (TextUtils.isEmpty(name)) {
            name = email;
        }

        String finalName = name;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Observable.from(dataSnapshot.getChildren())
                        .subscribeOn(Schedulers.newThread())
                        .filter(data -> {
                            if (data.child(EMAIL).exists()) {
                                if (data.child(EMAIL).getValue().equals(email)) {
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
                                           if (type.equals(CREATE)) {
                                               if (dataSnapshots.size() == 0) {
                                                   HashMap<String, Object> map = new HashMap<String, Object>();
                                                   map.put("email", email);
                                                   map.put("name", finalName);
                                                   updateFirebase(USER, null, null, map);
                                               }
                                           } else {
                                               updateFirebase(USER, null, dataSnapshots.get(0).getKey(), map);
                                           }
                                       }
                                   }
                        );


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void getUser(String email) {
        Firebase ref = (Firebase) getData(USER, null, null);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Observable.from(dataSnapshot.getChildren())
                        .subscribeOn(Schedulers.newThread())
                        .filter(data -> {
                            if (data.child(EMAIL).exists()) {
                                if (data.child(EMAIL).getValue().equals(email)) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        })
                        .observeOn(mainThread())
                        .subscribe(new Observer<DataSnapshot>() {
                                       @Override
                                       public void onCompleted() {

                                       }

                                       @Override
                                       public void onError(Throwable e) {

                                       }

                                       @Override
                                       public void onNext(DataSnapshot dataSnapshots) {
                                           EventBus.getDefault().post(new GetUserEvent(dataSnapshots));
                                       }
                                   }
                        );


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public Query getData(String path, String orderByChild, String equalTo) {

//example : new Firebase(BASE_API + "location").orderByChild(orderByChild).equalTo(equalTo);

        Query query = new Firebase(BASE_API + path);
        if (!TextUtils.isEmpty(orderByChild)) {
            query.orderByChild(orderByChild);
        }
        if (!TextUtils.isEmpty(equalTo)) {
            query.equalTo(equalTo);
        }
        return query;
    }


//    public void searchSingleData(String path, String orderByChild, String equalTo, String search) {
//        Firebase ref = (Firebase) getData(path, orderByChild, equalTo);
//        ref.addListenerForSingleValueEvent(valueEventListener(search));
//    }


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
        push.put(ref.push().getKey(), map);
        ref.updateChildren(push);
    }


    public ValueEventListener valueEventListener(ValueSnapshootListener valueSnapshootListener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    valueSnapshootListener.onDataChange(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                valueSnapshootListener.onCancelled(firebaseError);
            }
        };
    }


    public static ChildEventListener childListener(ChildSnapshootListener childSnapshootListener) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                childSnapshootListener.onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                childSnapshootListener.onChildChanged(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                childSnapshootListener.onChildRemoved(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                childSnapshootListener.onChildMoved(dataSnapshot, s);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                childSnapshootListener.onCancelled(firebaseError);
            }
        };

    }

    public void removeListener(Firebase ref, ChildEventListener childEventListener) {
        try {
            if(childEventListener!=null) {
                ref.removeEventListener(childEventListener);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void removeListener(Firebase ref, ValueEventListener valueEventListener) {
        try {
            if(valueEventListener!=null) {
                ref.removeEventListener(valueEventListener);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public interface ChildSnapshootListener {
        void onChildAdded(DataSnapshot dataSnapshot, String s);

        void onChildChanged(DataSnapshot dataSnapshot, String s);

        void onChildRemoved(DataSnapshot dataSnapshot);

        void onChildMoved(DataSnapshot dataSnapshot, String s);

        void onCancelled(FirebaseError firebaseError);
    }

    public interface ValueSnapshootListener {
        void onDataChange(DataSnapshot dataSnapshot);

        void onCancelled(FirebaseError firebaseError);
    }

}
