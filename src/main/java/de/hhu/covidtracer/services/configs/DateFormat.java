package de.hhu.covidtracer.services.configs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum DateFormat {
    YYYY_MM_DD("yyyy-MM-dd"),
    M_D_YYYY("M/d/yyyy"),
    M_D_YY("M/d/yy"),
    MM_DD_YYY("MM/dd/yyyy"),
    DD_MM_YYYY("dd-MM-yyyy"),
    DD__MM__YYYY("dd.MM.yyyy"),
    DDMMYY("dd.MM.yy"),
    DDMMYYYY("ddMMYYYY");

    private final String format;
    private static final List<String> DATE_FORMATS;

    DateFormat(String dateFormat) {
        this.format = dateFormat;
    }


    public static List<String> getValues() {
        return Collections.unmodifiableList(DATE_FORMATS);
    }


    static {
        DATE_FORMATS = new ArrayList<>();

        for (DateFormat enumEntity : DateFormat.values()) {
            DATE_FORMATS.add(enumEntity.format);
        }
    }
}
