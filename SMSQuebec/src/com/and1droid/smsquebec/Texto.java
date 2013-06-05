package com.and1droid.smsquebec;

import android.text.TextUtils;

public class Texto {

    private String from;
    private String to;
    private String message;

    public Texto(String to, String message) {
        this.from = "";
        this.to = formatNumber(to);
        this.message = message;
    }

    public String getCode() {
        return to.substring(0, 3);
    }

    public String getNum() {
        return to.substring(3);
    }

    public String getMessage() {
        if (TextUtils.isEmpty(from)) {
            return message;
        }
        return message + "\r\n" + from;
    }

    public String getCount() {
        return "" + message.length();
    }

    public void setFrom(String from) {
        this.from = from;
    }

    protected String formatNumber(String destinationNumber) {
        if (destinationNumber.startsWith("1")) {
            destinationNumber = destinationNumber.substring(1);
        }
        return destinationNumber.replace("-", "").replace("+1", "").replace("(", "").replace(")", "").replace(" ", "").trim();
    }
}
