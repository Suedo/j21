package org.playground;

import model.Employee;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static utils.Utils.loadEmployeesFromCSV;

@FunctionalInterface
interface RandomIntList<S, E, C, R> {
    R apply(S start, E end, C count);
}


/**
 * !! THE EXAMPLES HERE ARE NOT ALGO-DS PRACTICE, STREAMS MAY NOT BE THE BEST WAY OF DOING SOME OF THESE THINGS !!
 */
public class JavaStreamsTest {


    private List<Employee> employees = loadEmployeesFromCSV(Paths.get("src/main/resources/employees.csv"));

    /*
    --- Much easier to do this in kotlin though ---
    -----------------------------------------------
        import kotlin.random.Random

        val randomIntList: (Int, Int, Int) -> List<Int> = { start, end, count ->
            generateSequence { Random.nextInt(start, end) }
                .distinct().take(count).toList()
        }

        val random20Numbers = randomIntList(1, 100, 20)
        println(random20Numbers)

     */
    RandomIntList<Integer, Integer, Integer, List<Integer>> randomIntList = (start, end, count) -> new Random().ints(start, end).distinct().limit(count).boxed().toList();


    /*
        1. How would you find the second highest number in a list using streams?
    */
    @Test
    void test1() {
        var random20Numbers = randomIntList.apply(1, 100, 20);

        Integer secondHighest = random20Numbers.stream()
                .sorted(Comparator.reverseOrder())
                .skip(1) // skip the highest, we want second highest
                .findFirst()
                .orElse(-1);

        System.out.println(random20Numbers);
        System.out.println(secondHighest);
    }


    /*
        flatten a list of list
     */
    @Test
    void test2() {
        List<String> inner1 = List.of("a", "b", "c");
        List<String> inner2 = List.of("x", "y", "z");

        List<List<String>> outer = List.of(inner1, inner2);

        outer.stream()
                .flatMap(List::stream)
                .forEach(System.out::println);

    }

    /*
        Group a list of transactions by currency and then sum the value of transactions in each currency:
     */
    @Test
    void test4() {
        record Txn(String currency, Integer amount) {
        }

        Txn t1 = new Txn("USD", 10);
        Txn t2 = new Txn("USD", 10);
        Txn t3 = new Txn("USD", 10);
        Txn t4 = new Txn("EUR", 20);
        Txn t5 = new Txn("EUR", 10);
        Txn t6 = new Txn("EUR", 30);
        Txn t7 = new Txn("GBP", 50);
        Txn t8 = new Txn("INR", 1000);
        Txn t9 = new Txn("YEN", 100);


        List<Txn> txns = List.of(t1, t2, t3, t4, t5, t6, t7, t8, t9);

        txns.stream()
                .collect(groupingBy(Txn::currency, summingInt(Txn::amount)))
                .forEach((currency, sum) -> System.out.printf("Sum for group: %s == %s\n", currency, sum));

    }

    /*
        How do you partition a list of integers into even and odd numbers using streams?
     */
    @Test
    void test5() {
        randomIntList.apply(1, 1000, 30)
                .stream()
                //.collect(groupingBy(integer -> integer%2 == 0))
                .collect(partitioningBy(integer -> integer % 2 == 0))
                // partitionBy always gives two entries: one when predicate is false, another for true
                .forEach((isEven, integers) -> System.out.printf("%s Numbers : %s\n", isEven ? "Even" : "Odd", integers));
    }

    /*
        Find the MODE of a list. The mode of the list is the item that occurs most often
     */
    @Test
    void test6() {

        // merge multiple lists into a single list
        List<Integer> ints = Stream.of(
                        randomIntList.apply(1, 50, 25),
                        randomIntList.apply(1, 50, 25),
                        randomIntList.apply(1, 50, 25),
                        randomIntList.apply(1, 50, 25)
                ).flatMap(List::stream)
                //.distinct() // remove duplicates, making all entries modes, and mode = 1
                .toList();

        System.out.println("Ints:");
        System.out.println(ints);

        // how many times each entry has occurred in the list
        Map<Integer, Long> occurrences = ints.stream()
                .collect(groupingBy(Function.identity(), counting()));

        // find the max number of occurrences
        Long max = occurrences
                .entrySet().stream()
                .peek(System.out::println)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue).orElse(Long.MAX_VALUE);

        // in case of duplicates, find all ties
        List<Integer> allModes = occurrences.entrySet()
                .stream()
                .filter(map -> map.getValue() >= max)
                .map(Map.Entry::getKey)
                .toList();

        System.out.println("Mode: " + max);
        System.out.println("Keys: " + allModes);

    }

    @Test
    void test6_v2() {
        List<Integer> ints = Stream.of(
                randomIntList.apply(1, 50, 25),
                randomIntList.apply(1, 50, 25),
                randomIntList.apply(1, 50, 25),
                randomIntList.apply(1, 50, 25)
        ).flatMap(List::stream).toList();

        List<Map.Entry<Integer, Long>> sorted = ints.stream()
                .collect(groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        sorted.forEach(System.out::println);

        Long max = sorted.getFirst().getValue();

        List<Integer> modes = sorted.stream()
                .filter(integerLongEntry -> integerLongEntry.getValue() == max)
                .map(Map.Entry::getKey)
                .toList();

        System.out.println("Mode: " + max);
        System.out.println("Keys: " + modes);

    }

    /*
        find the first non-repeated character in a string
     */
    @Test
    void test7() {
        String result = Arrays.stream("eaaafzabbbccddefffhh".split(""))
                .sequential()
                .collect(groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .filter(map -> map.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst().orElse("not found");

        System.out.println(result);
    }

    /*
        sum of squares of all even numbers in a list
     */
    @Test
    void test8() {

        var ints = randomIntList.apply(1, 50, 25);
        System.out.println(ints);

        var sum = ints.stream().
                filter(i -> i % 2 == 0)
                .mapToInt(i -> i * i)
                .peek(value -> System.out.print(value + ","))
                .sum();

        System.out.println("\nSUM:");
        System.out.println(sum);
    }

    /*
        Intersection of 2 list
     */
    @Test
    void test10() {
        List<String> list1 = Arrays.asList("abcde".split(""));
        List<String> list2 = Arrays.asList("pkcez".split(""));

        HashSet<String> intersection = new HashSet<>(list1);
        System.out.println("Intersection");
        intersection.retainAll(list2);
        System.out.println(intersection);
    }

    /*
    Intersection of multiple lists
 */
    @Test
    void test10_v2() {
        List<String> list1 = Arrays.asList("abcde".split(""));
        List<String> list2 = Arrays.asList("pkcez".split(""));
        List<String> list3 = Arrays.asList("deczz".split(""));
        List<String> list4 = Arrays.asList("electra".split(""));

        List<List<String>> allLists = List.of(list1, list2, list3, list4);

        HashSet<String> intersection = new HashSet<>(allLists.getFirst());
        allLists.stream().skip(1).forEach(intersection::retainAll);

        System.out.println("Intersection");
        System.out.println(intersection);
    }



}
