package com.amplifire.traves.feature.maps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.adapter.ScoreAdapter;
import com.amplifire.traves.model.ScoreDao;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScoreboardActivity extends AppCompatActivity {
    String [][] dataRewards = new String[][]{
            {"Burger King 10 % Discount", "10","https://s3-eu-west-1.amazonaws.com/cdn.cobone.com/deals/uae/burgerking/bigburgerkingbooklet.jpg?v=76"},
            {"Adidas Voucher 50 %", "50","https://media.dontpayfull.com/media/deals/adidas-promo-code-0.jpg"},
            {"DC Shoes Voucher 50 %", "50","https://media.dontpayfull.com/media/deals/dc-shoes-coupon.jpg"},
            {"Nike Voucher 50 %", "50","https://media.dontpayfull.com/media/deals/nike-coupons.jpg"},
            {"Pepper Lunch Voucher 30 %", "30", "http://katalogkuliner.com/wp-content/uploads/2015/10/katalogkuliner-Pepper-Lunch-Anniversary.jpg"}
    };

    @BindView(R.id.txt_points)
    TextView txtPoints;
    @BindView(R.id.txt_rank)
    TextView txtRanks;
    @BindView(R.id.lvReward)
    ListView lvRewards;

    private ArrayList arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        ButterKnife.bind(this);
        arrayList =  new ArrayList<>();

        ScoreDao scoreDao = null;
        for (int i = 0; i<dataRewards.length; i++){
            scoreDao = new ScoreDao();
            scoreDao.setTitle(dataRewards[i][0]);
            scoreDao.setPoints(dataRewards[i][1]);
            scoreDao.setImgUrl(dataRewards[i][2]);

            arrayList.add(scoreDao);
        }

        ScoreAdapter adapter = new ScoreAdapter(arrayList, ScoreboardActivity.this);
        lvRewards.setAdapter(adapter);
    }

}
