package com.example.linquanlianmeng.utils;

public class UrlUtils {
    public static String createHomePagerUrl(int materialId, int page) {
        return "discovery/" + materialId + "/" + page;
    }

    public static String getCoverPath(String pic_url, int size) {
        return "https:" + pic_url + "_" + size + "x" + size + ".jpg";
    }

    public static String getCoverPath(String pic_url) {
        if (pic_url.startsWith("http") || pic_url.startsWith("https")) {
            return pic_url;
        } else {
            return "https:" + pic_url;
        }
    }

    public static String getTicketUrl(String url) {
        if (url == null) {
            return "";
        }
        if (url.startsWith("http") || url.startsWith("https")) {
            return url;
        } else {
            return "https:" + url;
        }
    }

    public static String getSelectedPageContentUrl(int categoryId) {
        return "recommend/" + categoryId;
    }

    public static String getOnSellPageUrl(int currentPage) {
        return "onSell/" + currentPage;
    }
}
