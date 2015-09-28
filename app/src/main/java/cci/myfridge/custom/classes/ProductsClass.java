package cci.myfridge.custom.classes;

import java.io.Serializable;

/**
 * Created by Fabrice KISTNER on 6/17/2015.
 */
public class ProductsClass implements Serializable {
    public String product_name;
    public String product_price;
    public String product_image;

    public String getEncoded_image() {
        return encoded_image;
    }

    public void setEncoded_image(String encoded_image) {
        this.encoded_image = encoded_image;
    }

    public String encoded_image;



    public String product_expiry_days;


    public String product_expiry_date;
    public String product_added_date;
    public String product_id;
    public String product_userid;
    public String product_about_expires;
    public String product_description;
    public ProductsClass()
    {}

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_expiry_date() {
        return product_expiry_date;
    }

    public void setProduct_expiry_date(String product_expiry_date) {
        this.product_expiry_date = product_expiry_date;
    }

    public String getProduct_added_date() {
        return product_added_date;
    }

    public void setProduct_added_date(String product_added_date) {
        this.product_added_date = product_added_date;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_userid() {
        return product_userid;
    }

    public void setProduct_userid(String product_userid) {
        this.product_userid = product_userid;
    }

    public String getProduct_about_expires() {
        return product_about_expires;
    }

    public void setProduct_about_expires(String product_about_expires) {
        this.product_about_expires = product_about_expires;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }
    public String getProduct_expiry_days() {
        return product_expiry_days;
    }

    public void setProduct_expiry_days(String product_expiry_days) {
        this.product_expiry_days = product_expiry_days;
    }


}
