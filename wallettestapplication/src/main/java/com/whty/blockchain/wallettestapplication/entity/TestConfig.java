package com.whty.blockchain.wallettestapplication.entity;

public class TestConfig {

    //测试目标方法
    TestDetail testDetail;

    //测试时长(分)
    float testDuration;

    public TestDetail getTestDetail() {
        return testDetail;
    }

    public void setTestDetail(TestDetail testDetail) {
        this.testDetail = testDetail;
    }

    public float getTestDuration() {
        return testDuration;
    }

    public void setTestDuration(float testDuration) {
        this.testDuration = testDuration;
    }
}
