package org.playground;

import model.Employee;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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


}
