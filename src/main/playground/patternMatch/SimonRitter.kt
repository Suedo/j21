package patternMatch

/**
 * Based on YT Devoxx video on Java 21 pattern matching by Simon Ritter
 * The Art of Java Language Pattern Matching by Simon Ritter
 * https://www.youtube.com/watch?v=OlW724WaJJQ
 */

data class Point(val x: Double, val y: Double) {}

data class Anything<T>(val t: T) {}  // generic record

data class Circle(val radius: Double) {

    // java: private static final double PI = 3.142
    companion object {
        private const val PI: Double = 3.142
    }

    fun area(): Double {
        return PI * radius * radius
    }
}
