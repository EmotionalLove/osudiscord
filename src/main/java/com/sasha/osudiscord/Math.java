package com.sasha.osudiscord;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Sasha at 6:59 PM on 12/4/2018
 */
public class Math {

    public static float fround(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value + "");
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static double dround(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value + "");
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
