package com.game24h.vietlottery.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.edu.gvn.vietlottery.R;
import com.game24h.vietlottery.Config;
import com.game24h.vietlottery.adapter.Mega645CurrentAdapter;
import com.game24h.vietlottery.entity.Mega645Current;
import com.game24h.vietlottery.entity.sub.Mega645Prize;
import com.game24h.vietlottery.network.Mega645CurrentAsync;
import com.game24h.vietlottery.ui.activity.PreviousMega645Activity;
import com.game24h.vietlottery.utils.DateTimeUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import static com.edu.gvn.vietlottery.R.id.hour;
import static com.edu.gvn.vietlottery.R.id.minute;
import static com.edu.gvn.vietlottery.R.id.second;


public class Mega645Fragment extends Fragment implements Mega645CurrentAsync.Mega645AsyncCallback, View.OnClickListener {

    private final String TAG = Mega645Fragment.class.getSimpleName();

    private TextView kyQuayThuong, ngayQuayThuong;
    private TextView so1, so2, so3, so4, so5, so6;
    private TextView giaTriUocTinh;
    private TextView ngay, gio, phut, giay;
    private Button lanQuayTruoc;

    private RecyclerView viewResult;
    private RecyclerView.LayoutManager linearManager;
    private Mega645CurrentAdapter mAdapter;
    private ArrayList<Mega645Prize> datas;

    CountDownTimer mCountDownTimer;

    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getContext(), getResources().getString(R.string.banner_id));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mega645, container, false);

        AdView mAdView = (AdView) view.findViewById(R.id.qc);
        mInterstitialAd = createNewIntAd();
        loadIntAdd();
        showBannerAd(mAdView);
        initView(view);
        return view;
    }

    public void requestData(Context context) {
        Mega645CurrentAsync mega645Async = new Mega645CurrentAsync(context, this);
        mega645Async.execute(Config.VIETLOTT_HOME);
    }

    private void initView(View view) {
        kyQuayThuong = (TextView) view.findViewById(R.id.txt_time_number);
        ngayQuayThuong = (TextView) view.findViewById(R.id.txt_time_date);
        so1 = (TextView) view.findViewById(R.id.txt_ball_1);
        so2 = (TextView) view.findViewById(R.id.txt_ball_2);
        so3 = (TextView) view.findViewById(R.id.txt_ball_3);
        so4 = (TextView) view.findViewById(R.id.txt_ball_4);
        so5 = (TextView) view.findViewById(R.id.txt_ball_5);
        so6 = (TextView) view.findViewById(R.id.txt_ball_6);
        giaTriUocTinh = (TextView) view.findViewById(R.id.values_prize);
        ngay = (TextView) view.findViewById(R.id.date);
        gio = (TextView) view.findViewById(hour);
        phut = (TextView) view.findViewById(minute);
        giay = (TextView) view.findViewById(second);

        viewResult = (RecyclerView) view.findViewById(R.id.view_result);
        lanQuayTruoc = (Button) view.findViewById(R.id.btn_previous);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearManager = new LinearLayoutManager(getActivity());
        datas = new ArrayList<>();
        mAdapter = new Mega645CurrentAdapter(getActivity(), datas);
        viewResult.setLayoutManager(linearManager);
        viewResult.setAdapter(mAdapter);

        lanQuayTruoc.setOnClickListener(this);

        // add default value recyclerview
        if (datas != null && datas.size() == 0) {
            datas.add(new Mega645Prize(getString(R.string.giai_thuong), getString(R.string.trung_khop), getString(R.string.so_luong_giai), getString(R.string.gia_tri_giai)));
            datas.add(new Mega645Prize("Jackpot", "0", "0", "0"));
            datas.add(new Mega645Prize("Giải nhất", "0", "0", "0"));
            datas.add(new Mega645Prize("Giải nhì", "0", "0", "0"));
            datas.add(new Mega645Prize("Giải ba", "0", "0", "0"));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void callBack(Mega645Current current) {
        try {
            if (current != null) {
                if (current.mega645Previous.soMayMan != null) {
                    String[] arrLuckyNumber = current.mega645Previous.soMayMan.split(" ");
                    setTextAndChangeColorBall(so1, arrLuckyNumber[0]);
                    setTextAndChangeColorBall(so2, arrLuckyNumber[1]);
                    setTextAndChangeColorBall(so3, arrLuckyNumber[2]);
                    setTextAndChangeColorBall(so4, arrLuckyNumber[3]);
                    setTextAndChangeColorBall(so5, arrLuckyNumber[4]);
                    setTextAndChangeColorBall(so6, arrLuckyNumber[5]);
                }

                kyQuayThuong.setText(current.mega645Previous.kyQuayThuong);
                ngayQuayThuong.setText(current.mega645Previous.ngayQuayThuong);
                giaTriUocTinh.setText(current.giaTriUocTinh);

                datas.clear();
                datas.addAll(current.mega645Previous.mega645Prizes);
                mAdapter.notifyDataSetChanged();

                DateTimeUtils dateTimeUtils = new DateTimeUtils();
                String remain = dateTimeUtils.remainingTime(current.curentTime, current.endTime);
                countTime(remain, ngay, gio, phut, giay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextAndChangeColorBall(TextView textView, String values) {
        textView.setText(values);

        int intValues = Integer.parseInt(values);
        if (intValues <= 10) {
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ball_red));
            return;
        }
        if (intValues <= 20) {
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ball_yellow));
            return;
        }
        if (intValues <= 30) {
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ball_green));
            return;
        }
        if (intValues <= 40) {
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ball_blue));
            return;
        }
        if (intValues <= 45) {
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ball_pupple));
        }
    }


    private void countTime(String remain, final TextView ngay, final TextView gio, final TextView phut, final TextView giay) {
        try {
            String[] myDateTime = remain.split("-");

            long mInitialTime =
                    DateUtils.DAY_IN_MILLIS * Integer.parseInt(myDateTime[0]) +
                            DateUtils.HOUR_IN_MILLIS * Integer.parseInt(myDateTime[1]) +
                            DateUtils.MINUTE_IN_MILLIS * Integer.parseInt(myDateTime[2]) +
                            DateUtils.SECOND_IN_MILLIS * Integer.parseInt(myDateTime[3]);

            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mCountDownTimer = null;
            }
            mCountDownTimer = new CountDownTimer(mInitialTime, 1000) {

                @Override
                public void onFinish() {
                    //mTextView.setText("Times Up!");
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished > 0) {
                        long day = millisUntilFinished / 86400000;
                        long hour = (millisUntilFinished - day * 86400000) / 3600000;
                        long minute = (millisUntilFinished - day * 86400000L - hour * 3600000L) / 60000;
                        long second = (millisUntilFinished - day * 86400000L - hour * 3600000L - minute * 60000) / 1000;

                        ngay.setText(day + "");
                        gio.setText(hour + "");
                        phut.setText(minute + "");
                        giay.setText(second + "");

                    } else {
                        ngay.setText("00");
                        gio.setText("00");
                        phut.setText("00");
                        giay.setText("00");
                    }
                }
            }.start();
        } catch (Exception e) {
            ngay.setText("00");
            gio.setText("00");
            phut.setText("00");
            giay.setText("00");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_previous) {
            //  getActivity().startActivity(new Intent(getActivity(), PreviousMega645Activity.class));
            showIntAdd();
        }
    }


    // add quảng cáo vào view
    private void showBannerAd(AdView mAdView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private InterstitialAd createNewIntAd() {
        InterstitialAd intAd = new InterstitialAd(getActivity());
        intAd.setAdUnitId(getString(R.string.banner_full_id));
        intAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                getActivity().startActivity(new Intent(getActivity(), PreviousMega645Activity.class));
            }
        });
        return intAd;
    }

    // Show quảng cáo nếu đã load, chưa load thì startActivity
    private void showIntAdd() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            getActivity().startActivity(new Intent(getActivity(), PreviousMega645Activity.class));
        }
    }

    // Load quảng cáo
    private void loadIntAdd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

}
