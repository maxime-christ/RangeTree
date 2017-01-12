package model;

public class Point {
    public int value;
    public double weight;
    
    public Point(int aValue, double aWeight) {
        value = aValue;
        weight = aWeight;
    }
    
    public Point(Point other) {
        value = other.value;
        weight = other.weight;
    }
    
    public int getValue() {
        return value;
    }
    
    public double getWeigh() {
        return weight;
    }
}
