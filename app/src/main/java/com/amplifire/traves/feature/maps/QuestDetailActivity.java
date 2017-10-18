package com.amplifire.traves.feature.maps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 16/10/2017.
 */

public class QuestDetailActivity extends AppCompatActivity {

    @BindView(R.id.img_quest_quiz)
    ImageView imgQuestQuiz;
    @BindView(R.id.txt_points_quest)
    TextView txtPointsQuest;
    @BindView(R.id.txt_progress_quest)
    TextView txtProgressQuest;
    @BindView(R.id.txt_desc_quiz)
    TextView txtDescQuiz;
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

//    layout_quiz
//            layout_market
//    layout_picture
//            layout_treasure

    @BindView(R.id.barcode_scanner)
    CompoundBarcodeView barcodeScanner;

    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.layout_quiz)
    RelativeLayout layoutQuiz;
    @BindView(R.id.layout_market)
    RelativeLayout layoutMarket;
    @BindView(R.id.layout_picture)
    RelativeLayout layoutPicture;
    @BindView(R.id.layout_treasure)
    LinearLayout layoutTreasure;
    private List<TreasureDao> treasureDaos = new ArrayList<>();

    private DatabaseReference mDatabase;
    private String key;

    private PictureDao pictureDao;
    private MarketDao marketDao;
    private QuizDao quizDao;
    //    public int status;
    private Map<String, TreasureDao> treasureDao;

    private TreasureDao treDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);
        ButterKnife.bind(this);
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
//                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        DatabaseReference questReference = mDatabase.child(FirebaseUtils.QUEST).child(questID);
        questReference.addValueEventListener(questListener);

    }

    private void initView(QuestDao questDao) {
        Utils.setImage(this, questDao.getImageUrl(), imgQuestQuiz);

        setToolbarTitle(questDao.getTitle());
        int point = 0;
        if (!questDao.picture.equals("null")) {
            layoutPicture.setVisibility(View.VISIBLE);
            pictureDao = questDao.picture;
            point += pictureDao.point;
        }
        if (!questDao.market.equals("null")) {
            layoutMarket.setVisibility(View.VISIBLE);
            marketDao = questDao.market;
            point += marketDao.point;
        }
        if (!questDao.quiz.equals("null")) {
            layoutQuiz.setVisibility(View.VISIBLE);
            quizDao = questDao.quiz;
            point += quizDao.point;
        }
        if (!questDao.treasure.equals("null")) {
            layoutTreasure.setVisibility(View.VISIBLE);
            treasureDao = questDao.treasure;
            barcodeScanner.decodeContinuous(callback);
            layoutTreasure.removeAllViews();

            for (Map.Entry<String, TreasureDao> entry : treasureDao.entrySet()) {

                TreasureDao treasureDao = entry.getValue();
                treasureDao.setKey(entry.getKey());
                treasureDaos.add(treasureDao);
                setLayoutTreasure(treasureDao);

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

    private void setLayoutTreasure(TreasureDao entry) {
        View child = getLayoutInflater().inflate(R.layout.item_treasure, null);
        LinearLayout treasure = (LinearLayout) child.findViewById(R.id.treasure);
        TextView treasureTitle = (TextView) child.findViewById(R.id.treasure_title);
        IconTextView treasureComplete = (IconTextView) child.findViewById(R.id.treasure_complete);
        treasureTitle.setText(entry.getDesc() + "");
        treasure.setTag(entry.getKey());
        if (entry.getStatus() == 1) {
            treasureComplete.setText("{fa-check-circle}");
        }
        treasure.setOnClickListener(v -> {
            for (int i = 0; i < treasureDaos.size(); i++) {
                if (treasureDaos.get(i).getKey().equals(treasure.getTag())) {
                    treDao = treasureDaos.get(i);
                    resumeBarcode();
                    break;
                }
            }


        });
        layoutTreasure.addView(child);
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
        try {
            if (treDao.getBarcode().equals(string)) {
                treDao.setStatus(1);
                pauseBarcode();
            } else {
                Toast.makeText(this, getString(R.string.text_treasure_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void resumeBarcode() {
        barcodeScanner.resume();
        barcodeScanner.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);
    }


    public void pauseBarcode() {
        treDao = null;
        layoutTreasure.removeAllViews();
        for (int i = 0; i < treasureDaos.size(); i++) {
            setLayoutTreasure(treasureDaos.get(i));
        }
        barcodeScanner.setVisibility(View.GONE);
        barcodeScanner.pause();
        btnSubmit.setVisibility(View.VISIBLE);

    }


    public static void startThisActivity(Context context, String key) {
        Intent intent = new Intent(context, QuestDetailActivity.class);
        intent.putExtra(Utils.DATA, key);
        context.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScanner.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScanner.pause();
    }

    @Override
    public void onBackPressed() {
        if (treDao != null) {
            pauseBarcode();
        } else {
            super.onBackPressed();
        }
    }


}
