package com.loanplatform.underwriting;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class Percent {

    private Percent() {
    }

    // 0.3512 -> "35.1%"
    static String format(BigDecimal ratio) {
        return ratio.movePointRight(2).setScale(1, RoundingMode.HALF_UP) + "%";
    }
}
