package org.playground.streams.basic;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Filtering and Mapping:
 * Task: Given a list of integers, filter even numbers and square them.
 * Finding Minimum and Maximum:
 * Task: From a list of floating-point numbers, find the minimum and maximum values.
 * Sorting and Collecting to List:
 * Task: Given a list of strings, sort them alphabetically and collect them back to a list.
 * Converting to Uppercase:
 * Task: Take a list of lowercase strings and convert them to uppercase.
 * Counting Elements:
 * Task: Count how many strings in a list start with a specific character.
 */

public class Basic01 {

    @Test
    void evenAndSquare() {
        var res = IntStream.range(0, 20)
                .filter(i -> i % 2 == 0)
                .map(i -> i * i)
                .mapToObj(String::valueOf)
                .toList();

        System.out.println(res);
    }

    @Test
    void minAndMax() {
        final RandomGenerator randomGenerator = RandomGenerator.getDefault();
        var randoms = randomGenerator.ints(15).toArray();
        final var randomCopy = Arrays.copyOf(randoms, randoms.length);
        Arrays.sort(randomCopy);
        System.out.println(Arrays.toString(randomCopy));
        var stats = Arrays.stream(randoms).summaryStatistics();

        System.out.println(String.format("min: %s, max: %s", stats.getMin(), stats.getMax()));
    }

    //* Task: Given a list of strings, sort them alphabetically and collect them back to a list.
    @Test
    void sortStream() {
        var s = "Somjit:20,Chandrima:21,Pom:22";

        record Person(String name, Integer age) {
        }
        final Pattern comma = Pattern.compile(",");
        final Pattern colon = Pattern.compile(":");
        //Comparator<Person> comparator = (p1, p2) -> p1.name.length() - p2.name.length(); // ascending order of name length
        Comparator<Person> comparator = Comparator.comparingInt(p -> p.name.length()); // ascending order of name length

        var res = comma.splitAsStream(s)
                .map(colon::split)
                .map(arr -> new Person(arr[0], Integer.valueOf(arr[1])))
                .sorted(comparator)
                .map(person -> String.format("%s:%s", person.name, person.age))
                .collect(Collectors.joining(","));

        System.out.println(res);
    }

    @Test
    void toUpperCase() {
        final Pattern spaces = Pattern.compile(" ");
        var res = spaces.splitAsStream("Given a list of strings, sort them and collect them back to a list")
                .map(String::toUpperCase)
                //.sorted() // alphabetical order
                .sorted(Comparator.comparingInt(String::length)) // order of increasing length
                .collect(Collectors.joining(" "));

        System.out.println(res);

    }

    @Test
    void countStart() {
        var ip = "Count how many strings in a list start with a specific character";
        Character start = 's';
        final Pattern spaces = Pattern.compile(" ");

        var count = spaces.splitAsStream(ip)
                .filter(s -> start.equals(s.charAt(0)))
                .count();

        System.out.println(String.format("Number of words begining with %s: %d", start, count));
    }

    @Test
    void countStartingwith() {
        var ip = "Sorting algorithms use the compare() method to determine the order of two objects";
        var spaces = Pattern.compile(" ");

        Map<Character, List<String>> zerochars = spaces.splitAsStream(ip)
                .collect(Collectors.groupingBy(
                        s -> s.charAt(0)
                ));

        zerochars.forEach((character, strings) -> System.out.println(String.format("%s: %s", character, strings)));
    }
}
