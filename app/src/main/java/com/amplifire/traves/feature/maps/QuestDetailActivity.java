package com.amplifire.traves.feature.maps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 16/10/2017.
 */

public class QuestDetailActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    @BindView(R.id.img_quest_quiz)
    ImageView imgQuest;
    @BindView(R.id.txt_points_quest)
    TextView txtPoints;
    @BindView(R.id.txt_progress_quest)
    TextView txtProgress;
    @BindView(R.id.txt_title_quest)
    TextView txtTitle;
    @BindView(R.id.txt_desc_quiz)
    TextView txtDesc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_quiz);
        ButterKnife.bind(this);

        getQuest("quest1");
    }

    private void getQuest(String questID) {
        mDatabase.child(FirebaseUtils.QUEST).child(questID).keepSynced(true);
        mDatabase.child(FirebaseUtils.QUEST).child(questID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                QuestDao questDao = dataSnapshot.getValue(QuestDao.class);
                if (questDao != null) {
                    questDao.setKey(key);
                    txtTitle.setText(questDao.getTitle());
                    txtDesc.setText(questDao.getDesc());
                    txtPoints.setText(""+questDao.getQuiz().getPoint());
                    Glide.with(QuestDetailActivity.this).load(questDao.getImageUrl())
                            .placeholder(android.R.color.darker_gray)
                            .error(android.R.color.black)
                            .into(imgQuest);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
