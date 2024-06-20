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
