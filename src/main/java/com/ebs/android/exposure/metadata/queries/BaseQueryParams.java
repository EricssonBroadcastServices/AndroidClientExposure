package com.ebs.android.exposure.metadata.queries;

/**
 * Created by Joao Coelho on 2017-11-07.
 */

public class BaseQueryParams {
    int pageSize;
    int pageNumber;
    boolean includeUserData;
    boolean onlyPublished;
    String sort;
    String fieldSet;

    BaseQueryParams() {
        pageSize = 500;
        pageNumber = 1;
        includeUserData = true;
        sort = "originalTitle";
        fieldSet = "ALL";
    }

    public int getPageSize() {
        return pageSize;
    }

    public BaseQueryParams setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public BaseQueryParams setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public boolean isIncludeUserData() {
        return includeUserData;
    }

    public BaseQueryParams setIncludeUserData(boolean includeUserData) {
        this.includeUserData = includeUserData;
        return this;
    }

    public boolean isOnlyPublished() {
        return onlyPublished;
    }

    public BaseQueryParams setOnlyPublished(boolean onlyPublished) {
        this.onlyPublished = onlyPublished;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public BaseQueryParams setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public String getFieldSet() {
        return fieldSet;
    }

    public BaseQueryParams setFieldSet(String fieldSet) {
        this.fieldSet = fieldSet;
        return this;
    }
}
