package com.cookplan.models;

import com.cookplan.R;
import com.cookplan.RApplication;

import java.util.Locale;

/**
 * Created by DariaEfimova on 20.03.17.
 */

public enum MeasureUnit {
    UNITS(0, R.string.unit_title, true),
    GRAMM(1, R.string.gramm_title, true),
    KILOGRAMM(2, R.string.kilogramm_title, false),
    LITRE(3, R.string.litre_title, false),
    MILILITRE(4, R.string.mililitre_title, true),
    CUP(5, R.string.cup_title, true),
    TEASPOON(6, R.string.teaspoon_title, true),
    TABLESPOON(7, R.string.tablespoon_title, true),
    BOTTLE(8, R.string.bottle_title, true),
    PACKAGE(9, R.string.package_title, true);

    private int id;
    private int nameRecourseId;
    private boolean isItIntValue;

    MeasureUnit(int id, int nameRecourseId, boolean isItIntValue) {
        this.id = id;
        this.nameRecourseId = nameRecourseId;
        this.isItIntValue = isItIntValue;
    }

    @Override
    public String toString() {
        return RApplication.getAppContext().getString(nameRecourseId);
    }

    public String toValueString(double value) {
        String valueString;
        if (this == KILOGRAMM && value < 1.) {
            valueString = getIntOrDoubleValueString(value * 1000);
            return valueString + " " + GRAMM.toString();
        } else if (this == GRAMM && value > 1000.) {
            valueString = getIntOrDoubleValueString(value / 1000);
            return valueString + " " + KILOGRAMM.toString();
        } else if (this == LITRE && value < 1.) {
            valueString = getIntOrDoubleValueString(value * 1000);
            return valueString + " " + MILILITRE.toString();
        } else if (this == MILILITRE && value > 1000.) {
            valueString = getIntOrDoubleValueString(value / 1000);
            return valueString + " " + LITRE.toString();
        } else {
            valueString = getIntOrDoubleValueString(value);
            return valueString + " " + toString();
        }
    }

    private String getIntOrDoubleValueString(double value) {
        String valueString;
        if (isItIntValue()) {
            valueString = String.valueOf((int) value);
        } else {
            if (value > 10.) {
                valueString = String.format(Locale.getDefault(), "%.0f", value);
            } else {
                valueString = String.format(Locale.getDefault(), "%.2f", value);
            }
        }
        return valueString;
    }

    public int getId() {
        return id;
    }

    public static double getMultiplier(MeasureUnit from, MeasureUnit to) {
        if (from == to) {
            return 1;
        }

        switch (from) {
            case UNITS: {
                switch (to) {
                    case PACKAGE:
                        return 1;
                    case BOTTLE:
                        return 1;
                    default:
                        return -1;
                }
            }
            case PACKAGE: {
                switch (to) {
                    case UNITS:
                        return 1;
                    case BOTTLE:
                        return 1;
                    default:
                        return -1;
                }

            }
            case BOTTLE: {
                switch (to) {
                    case UNITS:
                        return 1;
                    case PACKAGE:
                        return 1;
                    default:
                        return -1;
                }

            }
            case GRAMM: {
                switch (to) {
                    case GRAMM:
                        return 1;
                    case KILOGRAMM:
                        return 0.001;
                    case LITRE:
                        return 0.001;
                    case MILILITRE:
                        return 1;
                    case CUP:
                        return 0.004;
                    case TEASPOON:
                        return 0.2;
                    case TABLESPOON:
                        return 0.06;
                    default:
                        return -1;
                }
            }
            case KILOGRAMM: {
                switch (to) {
                    case KILOGRAMM:
                        return 1;
                    case GRAMM:
                        return 1000;
                    case LITRE:
                        return 1;
                    case MILILITRE:
                        return 1000;
                    case CUP:
                        return 4;
                    case TEASPOON:
                        return 200;
                    case TABLESPOON:
                        return 66.6;
                    default:
                        return -1;
                }
            }
            case LITRE: {
                switch (to) {
                    case LITRE:
                        return 1;
                    case GRAMM:
                        return 1000;
                    case KILOGRAMM:
                        return 1;
                    case MILILITRE:
                        return 1000;
                    case CUP:
                        return 4;
                    case TEASPOON:
                        return 200;
                    case TABLESPOON:
                        return 66.6;
                    default:
                        return -1;
                }
            }
            case MILILITRE: {
                switch (to) {
                    case MILILITRE:
                        return 1;
                    case KILOGRAMM:
                        return 0.001;
                    case LITRE:
                        return 0.001;
                    case GRAMM:
                        return 1;
                    case CUP:
                        return 0.004;
                    case TEASPOON:
                        return 0.2;
                    case TABLESPOON:
                        return 0.06;
                    default:
                        return -1;
                }
            }
            case CUP: {
                switch (to) {
                    case CUP:
                        return 1;
                    case KILOGRAMM:
                        return 0.25;
                    case LITRE:
                        return 0.25;
                    case GRAMM:
                        return 250;
                    case MILILITRE:
                        return 250;
                    case TEASPOON:
                        return 50;
                    case TABLESPOON:
                        return 15;
                    default:
                        return -1;
                }
            }
            case TEASPOON: {
                switch (to) {
                    case TEASPOON:
                        return 1;
                    case KILOGRAMM:
                        return 0.005;
                    case LITRE:
                        return 0.005;
                    case MILILITRE:
                        return 5;
                    case CUP:
                        return 0.02;
                    case GRAMM:
                        return 5;
                    case TABLESPOON:
                        return 16;
                    default:
                        return -1;
                }
            }
            case TABLESPOON: {
                switch (to) {
                    case TABLESPOON:
                        return 1;
                    case KILOGRAMM:
                        return 0.015;
                    case LITRE:
                        return 0.015;
                    case MILILITRE:
                        return 15;
                    case CUP:
                        return 0.06;
                    case GRAMM:
                        return 15;
                    case TEASPOON:
                        return 3;
                    default:
                        return -1;
                }
            }
            default:
                return -1;
        }
    }

    private boolean isItIntValue() {
        return isItIntValue;
    }
}
