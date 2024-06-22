package org.playground;

import model.Employee;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Utils.loadEmployeesFromCSV;

public class JavaStreamsTest {

    //private static List<Employee> employees;
    private List<Employee> employees = loadEmployeesFromCSV(Paths.get("src/main/resources/employees.csv"));

    //@BeforeAll
    //public static void oneTime() {
    //    employees = loadEmployeesFromCSV(Paths.get("src/main/resources/employees.csv"));
    //}

    @Test
    public void testTotalSalary() {
        double totalSalary = employees.stream()
                .mapToDouble(Employee::getSalary)
                .sum();

        assertEquals(1330000.00, totalSalary);
    }

}
