package com.ebs.android.exposure.models;

/**
 * Created by Joao Coelho on 15/07/2017.
 */

public class EmpCustomer {
    //private String customerId;
    //private String businessUnitId;
    //private String name;
    //private String slogan;
    //private String logoUrl;
    private String carouselGroupId;

    public EmpCustomer() {

    }

    public String getCarouselGroupId() {
        return this.carouselGroupId;
    }

    public EmpCustomer withCarouselGroupId(String carouselGroupId) {
        this.carouselGroupId = carouselGroupId;
        return this;
    }



}
