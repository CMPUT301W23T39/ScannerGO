package com.example.myapplication;

import java.util.ArrayList;
import java.util.Date;

public class QRCode {
    protected static class ScannerInfo {
        private String username;
        private String imageLink;
        private Date scannedDate;

        public ScannerInfo(String username, String imageLink, Date scannedDate) {
            this.username = username;
            this.imageLink = imageLink;
            this.scannedDate = scannedDate;
        }

        public String getUsername() {
            return username;
        }

        public String getImageLink() {
            return imageLink;
        }

        public Date getScannedDate() {
            return scannedDate;
        }

        // optional feature
        public void deleteImage() {
            this.imageLink = null;
        }
    }

    protected static class Comment{
        private String username;
        private Date date;
        private String content;

        public Comment(String username, Date date, String content) {
            this.username = username;
            this.date = date;
            this.content = content;
        }

        public String getUsername() {
            return username;
        }

        public Date getDate() {
            return date;
        }

        public String getContent() {
            return content;
        }
    }
    private final String hashValue;
    private final String codeName;
    private final String visualization;
    private final int score;
    private ArrayList<ScannerInfo> scannersInfo;
    private ArrayList<Comment> comments;
    private int timesScanned;

    public QRCode(String hashValue, String codeName, String visualization, int score) {
        this.hashValue = hashValue;
        this.codeName = codeName;
        this.visualization = visualization;
        this.score = score;
        DB.saveQRCodeInDB(this);
    }

    public void comment(String username, Date date, String content){
        Comment comment = new Comment(username, date, content);
        comments.add(comment);
        DB.saveCommentInDB(this, comment);
    }

    public void addScanner(String username, String imageLink, Date scannedDate){
        ScannerInfo newScannerInfo = new ScannerInfo(username, imageLink, scannedDate);
        scannersInfo.add(newScannerInfo);
        DB.saveScannerInfoInDB(this, newScannerInfo);
    }

    public String getHashValue() {
        return hashValue;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getVisualization() {
        return visualization;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<ScannerInfo> getScannersInfo() {
        return scannersInfo;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public int getTimesScanned() {
        return timesScanned;
    }
}
