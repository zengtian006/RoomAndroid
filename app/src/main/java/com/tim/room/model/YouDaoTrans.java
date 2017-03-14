package com.tim.room.model;

import java.util.List;

/**
 * Created by zengtim on 14/3/2017.
 */

public class YouDaoTrans {

    private String query;
    private int errorCode;
    private List<String> translation;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }
}
