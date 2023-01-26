package com.jesys.notify.enums;

public enum Months {
    янв("01"),
    фев("02"),
    мар("03"),
    апр("04"),
    май("05"), мая("05"),
    июн("06"),
    июл("07"),
    авг("08"),
    сен("09"),
    окт("10"),
    ноя("11"),
    дек("12");

    private final String numeric;

    Months(String numeric) {
        this.numeric = numeric;
    }

    public String getNumeric() {
        return numeric;
    }


}
