package com.in28minutes.microservices.limitsservice.bean;

public class LimitConfiguration {

    private int maximun;
    private int minimum;

    public LimitConfiguration() {
    }

    public LimitConfiguration(int maximun, int minimum) {
        this.maximun = maximun;
        this.minimum = minimum;
    }

    public int getMaximun() {
        return maximun;
    }

    public int getMinimum() {
        return minimum;
    }
}
