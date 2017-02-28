package com.tim.room.model;

/**
 * Created by Zeng on 2017/2/28.
 */

public class imageSendResponse {

    /**
     * token : ZFBIrrBcEIwH6Z0q_uwaxQ
     * url : http://d1spq65clhrg1f.cloudfront.net/uploads/image_request/image/186/186142/186142386/15.jpg
     * ttl : 50.888842168
     * status : not completed
     */

    private String token;
    private String url;
    private double ttl;
    private String status;

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

    public double getTtl() {
        return ttl;
    }

    public void setTtl(double ttl) {
        this.ttl = ttl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
