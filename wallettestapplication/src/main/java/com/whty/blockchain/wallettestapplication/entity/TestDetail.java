package com.whty.blockchain.wallettestapplication.entity;

public class TestDetail {

    private TestType testType;

    private String description;

    public TestDetail(TestType testType, String description) {
        this.testType = testType;
        this.description = description;
    }

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
