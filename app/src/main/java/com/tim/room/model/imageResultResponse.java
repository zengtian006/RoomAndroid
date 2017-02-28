package com.tim.room.model;

/**
 * Created by Zeng on 2017/2/28.
 */

public class imageResultResponse {


    /**
     * token : ZFBIrrBcEIwH6Z0q_uwaxQ
     * url : http://d1spq65clhrg1f.cloudfront.net/uploads/image_request/image/186/186142/186142386/15.jpg
     * ttl : 51
     * status : completed
     * name : men's red blazer and white short outfit
     */

    private String token;
    private String url;
    private int ttl;
    private String status;
    private String name;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
