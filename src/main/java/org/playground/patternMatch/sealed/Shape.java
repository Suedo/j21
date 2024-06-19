package org.playground.patternMatch.sealed;

public sealed interface Shape permits Circle, Rectangle, Square {
    double area();
}

record Circle(double radius) implements Shape {
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

record Rectangle(double width, double height) implements Shape {
    @Override
    public double area() {
        return width * height;
    }
}

record Square(double side) implements Shape {
    @Override
    public double area() {
        return side * side;
    }
}
