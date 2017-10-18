package com.amplifire.traves.feature.maps;

import android.content.Context;
import android.content.Intent;
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

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.model.MarketDao;
import com.amplifire.traves.model.PictureDao;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.model.QuizDao;
import com.amplifire.traves.model.TreasureDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.ResultPoint;
import com.joanzapata.iconify.widget.IconTextView;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 16/10/2017.
 */

public class QuestDetailActivity extends AppCompatActivity {
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

    @BindView(R.id.img_quest_quiz)
    ImageView imgQuestQuiz;
    @BindView(R.id.txt_points_quest)
    TextView txtPointsQuest;
    @BindView(R.id.txt_progress_quest)
    TextView txtProgressQuest;
    @BindView(R.id.txt_desc_quiz)
    TextView txtDescQuiz;
    @BindView(R.id.barcode_scanner)
    CompoundBarcodeView barcodeScanner;
    @BindView(R.id.up_layout)
    LinearLayout upLayout;
    @BindView(R.id.label_desc)
    TextView labelDesc;
    @BindView(R.id.up_layout1)
    RelativeLayout upLayout1;
    @BindView(R.id.label_task)
    TextView labelTask;
    @BindView(R.id.picture)
    ImageView picture;
    @BindView(R.id.treasure_title)
    TextView treasureTitle;
    @BindView(R.id.treasure_complete)
    IconTextView treasureComplete;
    @BindView(R.id.relative)
    LinearLayout relative;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private DatabaseReference mDatabase;
    private String key;

    private PictureDao pictureDao;
    private MarketDao marketDao;
    private QuizDao quizDao;
    //    public int status;
    private Map<String, TreasureDao> treasureDao;

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
        setContentView(R.layout.activity_quest_detail);
        key = getIntent().getStringExtra(Utils.DATA);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupToolbar();

        getQuest(key);

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }


    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            invalidateOptionsMenu();
        }
    }


    private void getQuest(String questID) {
        ValueEventListener questListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QuestDao questDao = dataSnapshot.getValue(QuestDao.class);
                if (questDao != null) {
                    initView(questDao);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        DatabaseReference questReference = mDatabase.child(FirebaseUtils.QUEST).child(questID);
        questReference.addValueEventListener(questListener);

    }

    private void initView(QuestDao questDao) {
        Utils.setImage(this, questDao.getImageUrl(), imgQuestQuiz);

//        private PictureDao picture;
//        private MarketDao market;
//        private QuizDao quiz;
//        //    public int status;
//        private Map<String, TreasureDao> treasure;

        setToolbarTitle(questDao.getTitle());
        int point = 0;
        if (!questDao.picture.equals("null")) {
            pictureDao = questDao.picture;
            point += pictureDao.point;
        }
        if (!questDao.market.equals("null")) {
            marketDao = questDao.market;
            point += marketDao.point;
        }
        if (!questDao.quiz.equals("null")) {
            quizDao = questDao.quiz;
            point += quizDao.point;
        }
        if (!questDao.treasure.equals("null")) {
            treasureDao = questDao.treasure;

            for (Map.Entry<String, TreasureDao> entry : treasureDao.entrySet()) {
                if (entry.getKey().equals("point")) {
                    try {
                        if (!TextUtils.isEmpty(entry.getValue().toString())) {
                            try {
                                int p = Integer.parseInt(entry.getValue().toString());
                                point += p;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        txtDescQuiz.setText(questDao.getDesc() + "");
        txtPointsQuest.setText(this.getString(R.string.text_point) + " : " + point);

        //todo txtProgressQuest
    }


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                setBarcodeText(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void setBarcodeText(String string) {

    }


    public static void startThisActivity(Context context, String key) {
        Intent intent = new Intent(context, QuestDetailActivity.class);
        intent.putExtra(Utils.DATA, key);
        context.startActivity(intent);
    }


}
