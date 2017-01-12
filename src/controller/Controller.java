package controller;

import java.sql.Time;

import java.util.ArrayList;

import java.util.Arrays;

import model.Point;
import model.RangeTree;

public class Controller {
    public Controller() {
        int nbOfNodes = 3000000;
        int lowerBound = 5000;
        int upperBound = 13000;
        
        Point[] values = new Point[nbOfNodes];
        for(int i = 0; i < nbOfNodes; i++) {
            int val = i;
            double weight = i / 1000.0f;
            values[i] = new Point(val, weight);
        }
        
        RangeTree rt = new RangeTree(values);
        RangeTree.mergeSort(values);
        
        long start, stop;
        start = System.currentTimeMillis();
        Object[] rtResult = rt.rangeQuery(lowerBound, upperBound);
        stop = System.currentTimeMillis();
        System.out.println(rtResult.length + " values collected in " + (stop - start) + " msec");
    
        start = System.currentTimeMillis();
        Object[] bfResult = bruteForceRange(values, lowerBound, upperBound);
        stop = System.currentTimeMillis();
        System.out.println(bfResult.length + " values collected in " + (stop - start) + " msec");

        start = System.currentTimeMillis();
        int rtNumber = rt.numberInRange(lowerBound, upperBound);
        stop = System.currentTimeMillis();
        System.out.println(rtNumber + " values collected in " + (stop - start) + " msec");

        start = System.currentTimeMillis();
        int bfNumber = bruteForceCount(values, lowerBound, upperBound);
        stop = System.currentTimeMillis();
        System.out.println(bfNumber + " values collected in " + (stop - start) + " msec");
        
        start = System.currentTimeMillis();
        double rtWeight = rt.maxWeightInRange(lowerBound, upperBound);
        stop = System.currentTimeMillis();
        System.out.println("max weight is " + rtWeight+  " in " + (stop - start) + " msec");
        
        start = System.currentTimeMillis();
        double bfWeight = bruteForceWeight(values, lowerBound, upperBound);
        stop = System.currentTimeMillis();
        System.out.println("max weight is " + bfWeight+  " in " + (stop - start) + " msec");
    }
    
    private Object[] bruteForceRange(Point[] values, int lowerBound, int upperBound) {
        ArrayList<Point> list = new ArrayList<Point>();
        for (int i = 0; i< values.length; i++) {
            if (values[i].value >= lowerBound && values[i].value <= upperBound)
                list.add(values[i]);
        }
        return list.toArray();
    }
    
    private int bruteForceCount(Point[] values, int lowerBound, int upperBound) {
        int res = 0;
        for (int i = 0; i< values.length; i++) {
            if (values[i].value >= lowerBound && values[i].value <= upperBound)
                res++;
        }
        return res;
    }
    
    private double bruteForceWeight(Point[] values, int lowerBound, int upperBound) {
        double res = 0;
        for (int i = 0; i< values.length; i++) {
            if (values[i].value >= lowerBound && values[i].value <= upperBound) {
                if (values[i].weight > res)
                    res = values[i].weight;
            }
        }
        return res;
    }
    
    public static void main(String[] args) {
        Controller c = new Controller();
    }
}
