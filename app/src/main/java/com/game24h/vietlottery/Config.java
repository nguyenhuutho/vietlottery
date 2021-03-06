package com.game24h.vietlottery;

/**
 * Created by hnc on 29/11/2016.
 */

public class Config {
    public static final Boolean DEBUG = true;
    public static final int REQUEST_TIME_OUT = 4000;

    private static final String URL = "http://vietlott.vn/";
    public static final String VIETLOTT_HOME = URL + "vi/home/";
    public static final String VIETLOTT_PREVIOUS_MEGA = URL + "vi/trung-thuong/ket-qua-trung-thuong/mega-6-45/winning-numbers/?p=";
    public static final String VIETLOTT_PREVIOUS_MAX = URL + "vi/trung-thuong/ket-qua-trung-thuong/max-4d/winning-numbers/";
    public static final String VIETLOTT_DETAIL_MEGA = URL + "vi/trung-thuong/ket-qua-trung-thuong/mega-6-45/?dayPrize=";

}
