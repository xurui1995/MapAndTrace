package com.example.xw.baidumaptest.Model;

import java.util.List;

/**
 * Created by xw on 2016/12/25.
 */
public class AgencyData  {
    private int size;

    public List<Agency> getAgencyList() {
        return AgencyList;
    }

    public void setAgencyList(List<Agency> agencyList) {
        AgencyList = agencyList;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private List<Agency> AgencyList;
    public static class Agency{
        int total;
        String name;
        double longitude;
        double latitude;
        int A_num;
        int B_num;

        public int getA_num() {
            return A_num;
        }

        public void setA_num(int a_num) {
            A_num = a_num;
        }

        public int getB_num() {
            return B_num;
        }

        public void setB_num(int b_num) {
            B_num = b_num;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
