package com.amplifire.traves.feature.questdetail;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.adapter.ImageAdapter;
import com.amplifire.traves.feature.adapter.MarketAdapter;
import com.amplifire.traves.feature.adapter.TreasureAdapter;
import com.amplifire.traves.feature.base.BaseActivity;
import com.amplifire.traves.model.ImageDao;
import com.amplifire.traves.model.MarketDao;
import com.amplifire.traves.model.MarketItemDao;
import com.amplifire.traves.model.PictureDao;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.model.QuizDao;
import com.amplifire.traves.model.QuizItemDao;
import com.amplifire.traves.model.TreasureDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.Utils;
import com.amplifire.traves.widget.ImageViewMeasurement;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by user on 16/10/2017.
 */

public class QuestDetailActivity extends BaseActivity implements QuestDetailContract.View, ImageAdapter.event, TreasureAdapter.event, MarketAdapter.event {
    @BindView(R.id.img_quest_quiz)
    ImageViewMeasurement imgQuestQuiz;
    @BindView(R.id.txt_points_quest)
    TextView txtPointsQuest;
    @BindView(R.id.txt_progress_quest)
    TextView txtProgressQuest;

    @BindView(R.id.txt_desc_quest)
    TextView txtDescQuest;

    @BindView(R.id.layout_quiz)
    View layoutQuiz;
    @BindView(R.id.layout_quiz_add)
    LinearLayout layoutQuizAdd;

    @BindView(R.id.layout_market)
    View layoutMarket;
    @BindView(R.id.recycler_market)
    RecyclerView recyclerMarket;
    @BindView(R.id.edittext_validation_code)
    EditText validationCode;

    @BindView(R.id.layout_treasure)
    View layoutTreasure;
    @BindView(R.id.recycler_treasure)
    RecyclerView recyclerTreasure;
    @BindView(R.id.barcode_scanner)
    CompoundBarcodeView barcodeScanner;

    @BindView(R.id.layout_picture)
    View layoutPicture;
    @BindView(R.id.recycler_picture)
    RecyclerView recyclerPicture;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private ValueEventListener questListener;
    private DatabaseReference questReference;

    private List<TreasureDao> treasureDaos = new ArrayList<>();
    private TreasureAdapter treasureAdapter;
    private List<ImageDao> pictureDaos = new ArrayList<>();
    private ImageAdapter pictureAdapter;
    private List<MarketItemDao> marketItemDaos = new ArrayList<>();
    private MarketAdapter marketAdapter;

    private List<RadioGroup> radioGroupQuiz = new ArrayList<>();

    private DatabaseReference mDatabase;
    private String key;
    private QuestDao questDao;

    private PictureDao pictureDao;
    private QuizDao quizDao;
    //    public int status;
    private Map<String, TreasureDao> treasureDao;
    private MarketDao marketDao;

    private int treasureTargetPosition;
    private int point = 0;
    private int imgPosition;

    @Inject
    QuestDetailPresenter questDetailPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);
        ButterKnife.bind(this);
        key = getIntent().getStringExtra(Utils.DATA);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setQuestStatus();
        getQuest(key);

    }

    private void setQuestStatus() {
// todo       if not exist : add data.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getQuest(String questID) {
        questListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questDao = dataSnapshot.getValue(QuestDao.class);
                if (questDao != null) {
                    initView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        questReference = mDatabase.child(FirebaseUtils.QUEST).child(questID);
        questReference.addValueEventListener(questListener);
//        questReference.keepSynced(true);

    }

    private void initView() {
        Utils.setImage(this, questDao.getImageUrl(), imgQuestQuiz);

        layoutPicture.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.GONE);
        layoutMarket.setVisibility(View.GONE);
        layoutTreasure.setVisibility(View.GONE);

        setToolbarTitle(questDao.getTitle());
        point = 0;

        if (questDao.picture != null) {
            if (!questDao.picture.equals("null") && questDao.picture.isActivated()) {
                layoutPicture.setVisibility(View.VISIBLE);
                pictureDao = questDao.picture;
                point += pictureDao.point;
                initPicture();
            }
        }

        if (questDao.quiz != null) {
            if (!questDao.quiz.equals("null")) {
                layoutQuiz.setVisibility(View.VISIBLE);
                quizDao = questDao.getQuiz();
                point += quizDao.point;
                initQuiz();
            }
        }

        if (questDao.treasure != null) {
            if (!questDao.treasure.equals("null")) {
                treasureDao = questDao.treasure;
                initTreasure();
            }
        }


        if (questDao.market != null) {
            if (!questDao.market.equals("null")) {
                layoutMarket.setVisibility(View.VISIBLE);
                marketDao = questDao.market;
                initMarket();
            }
        }
        txtDescQuest.setText(questDao.getDesc() + "");
        txtPointsQuest.setText(this.getString(R.string.text_point) + " : " + point);
    }

    //start quiz
    private void initQuiz() {
        layoutQuizAdd.removeAllViews();
        Map<String, QuizItemDao> quizDaos = quizDao.getItems();
        for (Map.Entry<String, QuizItemDao> entry : quizDaos.entrySet()) {
            setLayoutQuizData(entry.getKey(), entry.getValue());
        }
    }

    private void setLayoutQuizData(String key, QuizItemDao quizItemDao) {
        View child = getLayoutInflater().inflate(R.layout.item_quiz, null);
        TextView tvQuestion = (TextView) child.findViewById(R.id.tv_question);
        RadioGroup radioGroup = (RadioGroup) child.findViewById(R.id.radio_group);
        tvQuestion.setText(quizItemDao.getQuestion());
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        Map<String, String> choicesDao = quizItemDao.getChoices();
        for (Map.Entry choice : choicesDao.entrySet()) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(Integer.parseInt(choice.getKey().toString().replace("cho", "")));
            rdbtn.setTag(choice.getKey());
            rdbtn.setText(choice.getValue().toString());
            radioGroup.addView(rdbtn);
        }
        radioGroup.setTag(key);
        radioGroupQuiz.add(radioGroup);
        layoutQuizAdd.addView(child);
    }
//end quiz


    //start treasure
    private void initTreasure() {
        barcodeScanner.decodeContinuous(callback);

        for (Map.Entry<String, TreasureDao> entry : treasureDao.entrySet()) {
            TreasureDao treasureDao = entry.getValue();
            treasureDao.setKey(entry.getKey());
            if (treasureDao.isActivated()) {
                treasureDaos.add(treasureDao);
                point += treasureDao.getPoint();
            }
        }

        if (treasureDaos.size() > 0) {
            layoutTreasure.setVisibility(View.VISIBLE);
            recyclerTreasure.setLayoutManager(new LinearLayoutManager(this));
            recyclerTreasure.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerTreasure.setItemAnimator(new DefaultItemAnimator());
            treasureAdapter = new TreasureAdapter(this, treasureDaos);
            recyclerTreasure.setAdapter(treasureAdapter);
        } else {
            layoutTreasure.setVisibility(View.GONE);
        }


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
            if (treasureTargetPosition > -1) {
                TreasureDao treasureDaoTarget = treasureDaos.get(treasureTargetPosition);
                if (treasureDaoTarget != null) {
                    if (treasureDaoTarget.getBarcode().equals(string)) {
                        treasureDaoTarget.setStatus(2);
                        treasureDaos.set(treasureTargetPosition, treasureDaoTarget);
                        treasureAdapter.notifyDataSetChanged();
//                        todo update firebase
                        pauseBarcode();
                    } else {
                        Toast.makeText(this, getString(R.string.text_treasure_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void resumeBarcode() {
        barcodeScanner.resume();
        barcodeScanner.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);
    }

    public void pauseBarcode() {
        treasureTargetPosition = -1;
        barcodeScanner.setVisibility(View.GONE);
        barcodeScanner.pause();
        btnSubmit.setVisibility(View.VISIBLE);
    }

    @Override
    public void searchBarcode(int position) {
        treasureTargetPosition = position;
        resumeBarcode();
    }
//end treasure

    //    start market
    private void initMarket() {
        for (Map.Entry<String, MarketItemDao> entry : marketDao.getItems().entrySet()) {
            MarketItemDao marketItemDao = entry.getValue();
            if (marketItemDao.isActivated()) {
                marketItemDaos.add(marketItemDao);
                point += marketItemDao.getPoint();
            }
        }

        if (marketItemDaos.size() > 0) {
            layoutMarket.setVisibility(View.VISIBLE);
            recyclerMarket.setLayoutManager(new LinearLayoutManager(this));
            recyclerMarket.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerMarket.setItemAnimator(new DefaultItemAnimator());
            marketAdapter = new MarketAdapter(this, marketItemDaos);
            recyclerMarket.setAdapter(marketAdapter);

        } else {
            layoutMarket.setVisibility(View.GONE);
        }

    }

    @Override
    public void updateCheckMarket(int position, boolean isChecked) {
        int check = 0;
        if (isChecked) {
            check = 1;
        }
        Log.wtf("Test_", "x" + position + " " + isChecked);
        marketItemDaos.get(position).setChecked(check);
        marketAdapter.notifyDataSetChanged();
    }
//end market

    //start picture
    private void initPicture() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerPicture.setLayoutManager(gridLayoutManager);
        recyclerPicture.setItemAnimator(new DefaultItemAnimator());
        pictureAdapter = new ImageAdapter(this, pictureDaos);
        recyclerPicture.setAdapter(pictureAdapter);

        if (pictureDaos.size() == 0) {
            pictureDaos.add(new ImageDao(true, null, null));
            pictureAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void imageViewer(int position) {
        final AlertDialog.Builder customDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialog = layoutInflater.inflate(R.layout.alert_imageview, null);

        final ImageView mImageView = (ImageView) dialog.findViewById(R.id.photoView);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mImageView.setImageBitmap(pictureDaos.get(position).getImage());
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.update();
        final Button close = (Button) dialog.findViewById(R.id.onClickDelete);
        final Button retake = (Button) dialog.findViewById(R.id.onClickRetake);
        customDialog.setView(dialog);
        final AlertDialog alertx = customDialog.show();

        close.setOnClickListener(new View.OnClickListener() {
                                     public void onClick(View v) {
                                         final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(QuestDetailActivity.this);
                                         builder.setMessage(QuestDetailActivity.this.getResources().getString(R.string.delete_picture));
                                         builder.setCancelable(false);
                                         builder.setPositiveButton(QuestDetailActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialog, int which) {
                                                 builder.create().dismiss();
                                                 pictureDaos.remove(position);
                                                 pictureAdapter.notifyDataSetChanged();
                                                 alertx.dismiss();
                                             }
                                         });
                                         builder.setNegativeButton(QuestDetailActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialog, int which) {
                                                 builder.create().dismiss();
                                             }
                                         });
                                         final android.support.v7.app.AlertDialog dialog = builder.create();
                                         dialog.setCanceledOnTouchOutside(false);
                                         dialog.show();
                                     }
                                 }
        );

        retake.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          alertx.dismiss();
                                          imgPosition = position;
                                          startPickImage();
                                      }
                                  }
        );
    }

    @Override
    public void imageAdd(int position) {
        imgPosition = -1;
        startPickImage();
    }

    public void startPickImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            CropImage.startPickImageActivity(this);
        } else {
            callPermission();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
                Uri imageUri = CropImage.getPickImageResultUri(this, data);
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                final CropImage.ActivityResult result = CropImage.getActivityResult(data);
                String uri = result.getUri().getPath() + "";
                Bitmap bitmap = result.getBitmap();
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeFile(uri);
                }
                try {
                    FileOutputStream out = new FileOutputStream(uri + "");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                    out.flush();
                    out.close();
                    if (imgPosition == -1) {
                        pictureDaos.add(0, new ImageDao(false, bitmap, uri));
                    } else {
                        pictureDaos.set(imgPosition, new ImageDao(false, bitmap, uri));
                    }
                    pictureAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
//end picture


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
    public void onStart() {
        super.onStart();
        questDetailPresenter.takeView(this, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (questReference != null && questListener != null) {
            questReference.removeEventListener(questListener);
        }
        questDetailPresenter.dropView();
    }

    @Override
    public void onBackPressed() {
        if (treasureTargetPosition > -1) {
            pauseBarcode();
        } else {
            super.onBackPressed();
        }
    }


    @OnClick({R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (questDao.picture != null) {
                    if (!questDao.picture.equals("null")) {
                        if (pictureDaos.size() < questDao.getPicture().getMin()) {
//                                false
                        }
                    }
                }


                if (questDao.treasure != null) {
                    if (!questDao.treasure.equals("null")) {
                        for (Map.Entry<String, TreasureDao> entry : treasureDao.entrySet()) {
                            TreasureDao treasureDao = entry.getValue();
                            if (treasureDao.getStatus() < 2) {
//                                false
                            }
                        }

                    }
                }

                if (questDao.quiz != null) {
                    if (!questDao.quiz.equals("null")) {
                        Map<String, QuizItemDao> quizDaos = quizDao.getItems();
                        for (Map.Entry<String, QuizItemDao> entry : quizDaos.entrySet()) {
                            for (int i = 0; i < radioGroupQuiz.size(); i++) {
                                RadioGroup radioGroup = radioGroupQuiz.get(i);
                                if (radioGroup.getTag().equals(entry.getKey())) {
                                    int checkId = radioGroupQuiz.get(i).getCheckedRadioButtonId();
                                    if (checkId > -1) {
                                        RadioButton rdbtn = (RadioButton) radioGroup.findViewById(checkId);
                                        int idx = radioGroup.indexOfChild(rdbtn);
                                        RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                                        String userAnswer = r.getTag().toString();
                                        if (!userAnswer.equals(entry.getValue().getAnswer())) {
                                            //false
                                        }
                                    } else {
                                        //false
                                    }

                                }
                            }
                        }
                    }
                }


//                todo
                break;
        }
    }


}
