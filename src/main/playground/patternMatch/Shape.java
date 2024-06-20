package patternMatch;

sealed interface Shape {
    double area();

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
}

class Test {


    void TypeTester(Shape shape) {
        switch (shape) {
            // smaller/ more restrictive type matched first
            case Shape.Circle circle when circle.radius() > 25 -> System.out.println("big circle");
            // larger/ more general type should come later
            case Shape.Circle circle -> System.out.printf("normal cirle, radius: %s", circle.radius());
            // destructuring/deconstructing record to extract constituent values
            case Shape.Rectangle(double w, double h) -> System.out.printf("Rectangle with width %s, height %s%n", w, h);
            // can use `var` if not bothered about type info
            case Shape.Square(var side) -> System.out.printf("Square with each side: %s", side);
        }
    }
}



