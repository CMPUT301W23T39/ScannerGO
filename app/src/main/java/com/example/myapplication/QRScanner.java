package com.example.myapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class QRScanner {
    private HashMap<String, Scan> scanData;

    public QRScanner() {
        scanData = new HashMap<>();
    }

    public void scanQRCode(String qrCodeData) {
        Scan scan = scanData.get(qrCodeData);
        if (scan == null) {
            scan = new Scan(qrCodeData);
            scanData.put(qrCodeData, scan);
        }
        scan.incrementScanCount();
        scan.setLastScanTimestamp(new Date());
    }

    public void displayRanking() {
        List<Scan> sortedScans = new ArrayList<>(scanData.values());
        Collections.sort(sortedScans, new ScanComparator());

        System.out.println("QR Code Scan Ranking:");
        for (int i = 0; i < sortedScans.size(); i++) {
            Scan scan = sortedScans.get(i);
            System.out.println((i+1) + ". QR Code: " + scan.getQrCodeData() + ", Scans: " + scan.getScanCount() + ", Last Scan: " + scan.getLastScanTimestamp());
        }
    }

    private class Scan {
        private String qrCodeData;
        private int scanCount;
        private Date lastScanTimestamp;

        public Scan(String qrCodeData) {
            this.qrCodeData = qrCodeData;
            this.scanCount = 0;
            this.lastScanTimestamp = null;
        }

        public String getQrCodeData() {
            return qrCodeData;
        }

        public int getScanCount() {
            return scanCount;
        }

        public void incrementScanCount() {
            scanCount++;
        }

        public Date getLastScanTimestamp() {
            return lastScanTimestamp;
        }

        public void setLastScanTimestamp(Date lastScanTimestamp) {
            this.lastScanTimestamp = lastScanTimestamp;
        }
    }

    private class ScanComparator implements Comparator<Scan> {
        @Override
        public int compare(Scan s1, Scan s2) {
            // Sorting by scan count in descending order
            return Integer.compare(s2.getScanCount(), s1.getScanCount());
        }

        //public int compare(T t, T t1) {
            //return 0;
        }
    }