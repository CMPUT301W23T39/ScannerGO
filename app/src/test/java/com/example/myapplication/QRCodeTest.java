package com.example.myapplication;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class QRCodeTest {
    @Test
    public void testAddScanner() {
        QRCode qrCode = new QRCode("hashValue", "codeName", "visualization", 0);
        assertEquals(0, qrCode.getScannersInfo().size());

        String username = "test_user";
        String imageLink = "http://example.com/image.png";
        Date scannedDate = new Date();
        qrCode.addScanner(username, imageLink, scannedDate);

        ArrayList<QRCode.ScannerInfo> scannersInfo = qrCode.getScannersInfo();
        assertEquals(1, scannersInfo.size());

        QRCode.ScannerInfo scannerInfo = scannersInfo.get(0);
        assertEquals(username, scannerInfo.getUsername());
        assertEquals(imageLink, scannerInfo.getImageLink());
        assertEquals(scannedDate, scannerInfo.getScannedDate());
    }

    @Test
    public void testComment() {
        QRCode qrCode = new QRCode("hashValue", "codeName", "visualization", 0);
        assertEquals(0, qrCode.getComments().size());

        String username = "test_user";
        Date date = new Date();
        String content = "This is a test comment";
        qrCode.comment(username, date, content);

        ArrayList<QRCode.Comment> comments = qrCode.getComments();
        assertEquals(1, comments.size());

        QRCode.Comment comment = comments.get(0);
        assertEquals(username, comment.getUsername());
        assertEquals(date, comment.getDate());
        assertEquals(content, comment.getContent());
    }
}
