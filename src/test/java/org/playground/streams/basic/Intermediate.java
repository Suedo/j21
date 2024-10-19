package org.playground.streams.basic;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
Grouping by Age:
Task: Given a list of Person objects, group them by age.
Partitioning by Predicate:
Task: Partition a list of integers into even and odd numbers.
Summing and Averaging:
Task: Calculate the average salary of employees in a list.
FlatMap Example:
Task: Given a list of sentences, split each sentence into words and collect them into a list.
Finding Duplicates:
Task: From a list of integers, find and return all duplicates.
Transform Map Values:
Task: Given a Map<String, List<Integer>>, transform each list of integers into its sum, resulting in a Map<String, Integer>.
Multi-Level Sorting:
Task: Sort a list of Person objects first by age, then by name.
Find Top-N Elements:
Task: Find the top 3 highest values from a list of integers.
Filtering and Reducing:
Task: Filter a list of products by price > 100 and then calculate the total price using reduce().
Counting Elements by Criteria:
Task: Count the number of strings in a list that have a length greater than 5.
 */
public class Intermediate {

    public static final int[] RANDOM_INTS = {19, 25, 26, 29, 35, 36, 38, 47, 61, 68, 68, 71, 71, 72, 73, 75, 79, 92, 94, 96};

    record Person(String name, Integer age) {
    }

    static List<Person> people = List.of(
            new Person("a", 10),
            new Person("b", 10),
            new Person("c", 20),
            new Person("d", 30)
    );

    @Test
    void groupByAge() {
        people.stream()
                .collect(Collectors.groupingBy(person -> person.age))
                .forEach((age, peoples) -> System.out.println(String.format("%d: %s", age, peoples)));
    }

    @Test
    void partitionBy() {
        Predicate<Integer> isEven = integer -> integer % 2 == 0;
        final Map<Boolean, List<Integer>> collect = IntStream.range(0, 25)
                .boxed()
                .collect(Collectors.partitioningBy(isEven));

        System.out.println("Even: " + collect.get(true));
        System.out.println("Odd: " + collect.get(false));
    }

    @Test
    void averageAge() {
        System.out.println(people.stream()
                .mapToInt(Person::age)
                .average()
                .orElse(-1.0)
        );
    }

    @Test
    void getWords() {
        var sentences = List.of("Given a list of sentences", "split each sentence into words", "and collect them into a list");

        final Pattern spaces = Pattern.compile(" ");

        final List<String> words = sentences.stream()
                .flatMap(spaces::splitAsStream)
                .toList();

        System.out.println(words);
    }

    @Test
    void findDuplicates() {
        /*
        final RandomGenerator randomGenerator = RandomGenerator.getDefault();
        var randomInts = randomGenerator.ints(20, 0, 100).toArray();

        var copy = Arrays.copyOf(randomInts, randomInts.length);
        Arrays.sort(copy);
        System.out.println(Arrays.toString(copy));
        */

        var randomInts = RANDOM_INTS;

        // find thw duplicates
        Set<Integer> uniques = new HashSet<>();
        Predicate<Integer> isPresent = integer -> !uniques.add(integer); // adding to set returns false when element already present in set

        var res = Arrays.stream(randomInts)
                .boxed()
                .filter(isPresent)
                .toList();

        System.out.println(res);
    }

    @Test
    void listToSum() {
        // Given a Map<String, List<Integer>>, transform each list of integers into its sum, resulting in a Map<String, Integer>
        var mymap = Map.of("a", List.of(2, 4, 5), "b", List.of(5, 6, 7), "c", List.of(7, 8, 9));

        Map<String, Integer> mySumMap = mymap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().reduce(0, Integer::sum)
                ));

        System.out.println(mySumMap);
    }
    // todo: notetoself: practice more reduce, and toMap()


    @Test
    void top3() {
        var ints = RANDOM_INTS;

        // topmost 3 ints means sort in desc order
        var res = Arrays.stream(ints).boxed()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .toList();

        System.out.println(res);
    }


    // ---------------------------------------
    // use of Collector<> downstream in many stream terminal operations
    // The downstream collector in the groupingBy method allows further processing of the grouped elements after they are grouped.
    // It’s useful for collecting results in more complex ways, such as counting, summing, or converting elements within each group.
    // ---------------------------------------
    @Test
    void groupByWithDownStream() {
        List<String> words = List.of("apple", "banana", "apricot", "cherry", "avocado", "date");

        // Group words by their length, then count occurrences of words in each group
        Map<Integer, Long> lengthCountMap = words.stream()
                .collect(Collectors.groupingBy(
                        String::length, // Classifier - groups by word length
                        HashMap::new, // Map factory - specify map implementation (optional)
                        Collectors.counting() // Downstream - counts elements in each group
                ));

        // Output: {5=2, 6=2, 7=1, 4=1}
        System.out.println(lengthCountMap);
    }

    @Test
    void partitionByWithDownstream() {
        List<Integer> numbers = List.of(12, 3, 45, 22, 67, 8, 10, 99);

        // Partition numbers into even and odd, then find the max in each partition
        Map<Boolean, Optional<Integer>> maxInPartitions = numbers.stream()
                .collect(Collectors.partitioningBy(
                        num -> num % 2 == 0, // Predicate - partitions into even/odd
                        Collectors.maxBy(Comparator.naturalOrder()) // Downstream - find max in each partition
                ));

        // Output: {false=Optional[99], true=Optional[22]}
        System.out.println(maxInPartitions);
    }


    // ---------------------------------------
    // Advanced toMap uses
    // ---------------------------------------
    @Test
    void toMapExample1() {
        List<String> words = List.of("apple", "apricot", "banana", "blueberry", "avocado", "blackberry", "cherry");

        // Create a map of the first letter and the longest word starting with that letter
        Map<Character, String> longestWordMap = words.stream()
                .collect(Collectors.toMap(
                        word -> word.charAt(0), // Key mapper - uses first letter of word as key
                        word -> word, // Value mapper - uses the word itself as the value
                        (existing, replacement) -> // Merge function - keeps the longer word
                                existing.length() >= replacement.length() ? existing : replacement
                ));

        // Output: {a=avocado, b=blackberry, c=cherry}
        System.out.println(longestWordMap);
    }

    @Test
    void toMapWithMapFactory() {
        List<String> words = List.of("apple", "apricot", "banana", "blueberry", "avocado", "blackberry", "cherry");

        // Create a LinkedHashMap of first letter and longest word, preserving insertion order
        Map<Character, String> longestWordMap = words.stream()
                .collect(Collectors.toMap(
                        word -> word.charAt(0), // Key mapper - first letter as key
                        word -> word, // Value mapper - word as value
                        (existing, replacement) -> // Merge function - keeps the longer word
                                existing.length() >= replacement.length() ? existing : replacement,
                        LinkedHashMap::new // Map factory - preserves insertion order
                ));
        // Map factory argument: Allows customization of the map type,
        // such as LinkedHashMap or any other map implementation, depending on the requirements.

        // Output: {a=avocado, b=blackberry, c=cherry}
        System.out.println(longestWordMap);
    }
}
