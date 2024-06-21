### Pattern Types

- **Constant**: Match on a constant (already in use in a switch statement)
- **Type**: Match on a type
- **Deconstruction**: Match and extract
- **var**: Uses type inference to map to a type pattern
- **Any (_)**: Matches anything but binds to nothing (an unused pattern variable). See JEP 302

### Switch Expression (jdk 12)

```java
numberOfLetters =switch(day){
        case MONDAY,FRIDAY,SUNDAY ->6;
        case TUESDAY ->7;
        case THURSDAY,SATURDAY ->8;
        case WEDNESDAY ->9;
default ->throw new

IllegalStateException("Huh?: "+day);
};
```

### Records (JDK 14)

```java
record Point(double x, double y) {
}

record Anything<T>(T t) {
}  // Generic Record

public record Circle(double radius) {
    private static final double PI = 3.142;  // Static instance fields are allowed

    public double area() {
        return PI * radius * radius;
    }
}
```

### Pattern Matching `instanceOf` (JDK 14)

```java
if(obj instanceOf
String s)
        System.out.

println(s.length())
        else
// here means s is not a string, use of `s` not allowed here
```

### Guarded Patterns (JDK 19)

In the below example, `Triangle t when t.area() > 25` the `when` is the **guard**.
Upto jdk 18, it was like `Triangle t && t.area() > 25`, i.e, `&&` instead of `when`

```java
void shapeTester(Shape shape) {  // Using previous sealed class example
    switch (shape) {
        case Triangle t when t.area() > 25 -> System.out.println("It's a big triangle");
        case Triangle t -> System.out.println("It's a small triangle");
        case Square s -> System.out.println("It's a square");
        case Pentagon p -> System.out.println("It's a pentagon");
        case Shape s -> System.out.println("It's a shape");
    }
}
```

### Pattern Dominance

Similar to order of `Exception`s in a catch block, put the 'smaller bucket' above the 'larger bucket', so that the '
larger bucket' catches what the smaller one does not
By 'bucket', analogy is towards a 'type'. Put more specific type first, then a broader type after it, else the broader
type will catch all,
and the smaller type won't be reached.

```java
void shapeTester(Shape shape) {  // Using previous sealed class example
    switch (shape) {
        case Shape s -> System.out.println("All shapes caught here");
        case Triangle t -> System.out.println("this will never be reached");
        .
        .
    }
}
```

### Record Patterns (JDK 19)

- Record patterns are a **deconstruction** (same as javascript destructuring) pattern
- Deconstruction patterns only work with records

Here, we are deconstructing the values from the record into variables x and y, and using it directly in our code

```java
public void pythagoras(Object o) {
    if (o instanceof Point(double x, double y)) {
        System.out.println("Hypotonuse = " + Math.sqrt((x * x) + (y * y)));
    }
}
```

## Combining things together:

```java
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
```