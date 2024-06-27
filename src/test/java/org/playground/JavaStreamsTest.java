package org.playground;

import model.Employee;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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


}
