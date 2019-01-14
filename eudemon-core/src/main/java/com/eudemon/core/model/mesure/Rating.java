package com.eudemon.core.model.mesure;

import java.math.BigDecimal;
import java.util.Date;

public class Rating<T> {
    Unit<T> from ;
    Unit<T> to;
    BigDecimal rate;
    Date lastupdate;

    RatingCurve<T> rateCurve;
}
