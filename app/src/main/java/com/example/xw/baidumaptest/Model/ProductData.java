package com.example.xw.baidumaptest.Model;

/**
 * Created by xw on 2017/2/9.
 */
public class ProductData {
    private String msg;
    private Product product;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public static  class Product{
       private String sn;
       private String worker;
       private String agency;
       private String factory;
       private String date;

        public String getAgency() {
            return agency;
        }

        public void setAgency(String agency) {
            this.agency = agency;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getFactory() {
            return factory;
        }

        public void setFactory(String factory) {
            this.factory = factory;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getWorker() {
            return worker;
        }

        public void setWorker(String worker) {
            this.worker = worker;
        }
    }
}
