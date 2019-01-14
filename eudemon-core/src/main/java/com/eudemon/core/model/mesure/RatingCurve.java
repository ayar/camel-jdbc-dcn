package com.eudemon.core.model.mesure;

import java.util.Date;
import java.util.List;

public class RatingCurve<T> {
    String name;
    Date ratingDate;
    List<Rating<T>> ratings;
    Unit<T> baseUnit;
}
