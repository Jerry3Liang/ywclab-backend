package com.jerryliang.ywclab.model;


import lombok.Data;

import java.util.List;

@Data
public class OCTFourLayerEntity {

    private Object value;
    private List<Object> valueList;
    private int columnIndex;
    private double sum;
    private int count;
    public double average;

    public void addToSum(double value) {
        sum += value;
    }

    public void incrementCount() {
        count++;
    }

    public void calculateAverage() {
        average = (count > 0) ? (sum / count) : 0.0;
    }

    @Override
    public String toString() {
        return String.format("Column %d: Sum=%.2f, Count=%d, Average=%.2f",
                columnIndex, sum, count, average);
    }
}
