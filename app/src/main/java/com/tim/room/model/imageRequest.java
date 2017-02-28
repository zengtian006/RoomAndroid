package com.tim.room.model;

/**
 * Created by Zeng on 2017/2/28.
 */

public class imageRequest {

    /**
     * image_request : {"locale":"en","remote_image_url":"http://tim198789.oss-cn-shanghai.aliyuncs.com/0a00036a-5a26-1b79-815a-269d607e0000/15.jpg"}
     */

    private ImageRequestBean image_request;

    public ImageRequestBean getImage_request() {
        return image_request;
    }

    public void setImage_request(ImageRequestBean image_request) {
        this.image_request = image_request;
    }

    public static class ImageRequestBean {
        public ImageRequestBean(String locale, String remote_image_url) {
            this.locale = locale;
            this.remote_image_url = remote_image_url;
        }

        /**
         * locale : en
         * remote_image_url : http://tim198789.oss-cn-shanghai.aliyuncs.com/0a00036a-5a26-1b79-815a-269d607e0000/15.jpg
         */


        private String locale;
        private String remote_image_url;

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getRemote_image_url() {
            return remote_image_url;
        }

        public void setRemote_image_url(String remote_image_url) {
            this.remote_image_url = remote_image_url;
        }
    }
}
