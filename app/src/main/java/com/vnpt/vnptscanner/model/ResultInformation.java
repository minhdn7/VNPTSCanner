package com.vnpt.vnptscanner.model;

import java.math.BigDecimal;

/**
 * Created by MinhDN on 23/11/2016.
 */

public class ResultInformation {
    private String label;
    private String value;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ResultInformation(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public ResultInformation() {
    }
}
