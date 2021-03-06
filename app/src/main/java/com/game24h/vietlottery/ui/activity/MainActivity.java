package com.game24h.vietlottery.ui.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.game24h.vietlottery.adapter.LotteryPagerAdapter;
import com.game24h.vietlottery.broadcast.NotifycationPublisher;
import com.game24h.vietlottery.ui.fragment.Max4DFragment;
import com.game24h.vietlottery.ui.fragment.Mega645Fragment;
import com.edu.gvn.vietlottery.R;

import java.util.Calendar;

import angtrim.com.fivestarslibrary.FiveStarsDialog;
import angtrim.com.fivestarslibrary.NegativeReviewListener;
import angtrim.com.fivestarslibrary.ReviewListener;

public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LotteryPagerAdapter mLottAdapter;
    private Mega645Fragment megaFragment;
    private Max4DFragment maxFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTabLayout = (TabLayout) findViewById(R.id.tab_lottery);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_lottery);

        maxFragment = new Max4DFragment();
        megaFragment = new Mega645Fragment();

        mLottAdapter = new LotteryPagerAdapter(this, getSupportFragmentManager());
        mLottAdapter.addFragment(megaFragment, getResources().getString(R.string.tab_mega));
        mLottAdapter.addFragment(maxFragment, getResources().getString(R.string.tab_max));

        mViewPager.setAdapter(mLottAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        //set Push notify
        scheduleNotify(18, 0, 0);
        openDialogRating();
    }

    /**
     *  đánh giá 4,5 *  vào google play store để đánh giá thực tế
     *  nếu nhỏ hơn 3 sao thì gửi bug hoặc ... cho dev bằng email : example@gmail.com
     *  setForceMode (true) nhảy thẳng vào gg play k cần nhấn ok
     */
    private void openDialogRating() {
        FiveStarsDialog fiveStarsDialog = new FiveStarsDialog(this,"example@gmail.com");
        fiveStarsDialog.setRateText("Rate us")
                .setTitle("Hãy chọn 5 sao")
                .setForceMode(true)
                .setUpperBound(2) // Market opened if a rating >= 2 is selected
                .setNegativeReviewListener(new NegativeReviewListener() {
                    @Override
                    public void onNegativeReview(int i) {

                    }
                }) // OVERRIDE mail intent for negative review
                .setReviewListener(new ReviewListener() {
                    @Override
                    public void onReview(int i) {

                    }
                }) // Used to listen for reviews (if you want to track them )
                .showAfter(1);
    }

    /**
     * set push notify
     *
     * @param hour
     * @param minute
     * @param second
     */
    private void scheduleNotify(int hour, int minute, int second) {
        Intent notiIntent = new Intent(this, NotifycationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarAlarm = Calendar.getInstance();

        calendarAlarm.set(Calendar.HOUR_OF_DAY, hour);
        calendarAlarm.set(Calendar.MINUTE, minute);
        calendarAlarm.set(Calendar.SECOND, second);
        calendarAlarm.set(Calendar.MILLISECOND, 0);
        long tringgerTime = calendarAlarm.getTimeInMillis();

        if (calendarAlarm.before(calendarNow)) {
            tringgerTime += 86400000L;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, tringgerTime, pendingIntent);
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (isNetworkConnection()) {
            maxFragment.requestData(MainActivity.this);
            megaFragment.requestData(MainActivity.this);
        } else {
            checkInternet();
        }

    }
}
