package org.playground;

import model.Employee;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
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

    @Test
    public void employeesNameGreaterThan5() {
        //    Retrieve the names of employees whose last names contain more than 5 characters.
        employees.stream()
                .map(Employee::getLastName)
                .filter(name -> name.length() > 5)
                .forEach(System.out::println);
    }

    /*
    FInd the average salary of each department
     */
    @Test
    void avgSalaryDept() {
        employees.stream()
                .collect(groupingBy(Employee::getDepartmentName, averagingDouble(Employee::getSalary)))
                .forEach((dept, avgSalary) -> System.out.printf("Dept: %s, avg salary: %s\n", dept, avgSalary));
    }

    /*
    10. List the employees who have a salary greater than the average salary in their department.

        SELECT * from employees e
        join (
            SELECT e.department_id deptid, d.department_name deptname, AVG(e.salary) avgsal
            from employees e join departments d
            WHERE e.department_id = d.department_id
            group by e.department_id
        ) davg on davg.deptid = e.department_id
        WHERE e.salary > davg.avgsal;
     */
    @Test
    public void test2() {

        Map<String, List<Employee>> employeesByDept = employees.stream().collect(groupingBy(Employee::getDepartmentName));

        Map<String, Double> avgSalaryByDept = employees.stream().collect(groupingBy(Employee::getDepartmentName, averagingDouble(Employee::getSalary)));

        employeesByDept.entrySet()
                .stream()
                .flatMap(set -> {
                    Double avgSalary = avgSalaryByDept.get(set.getKey());
                    // returns a stream, and since we are already in a stream, the outer flatmap is needed
                    // to get Stream<Employee> instead of Stream<Stream<Employee>>
                    return set.getValue().stream().filter(employee -> employee.getSalary() > avgSalary);
                })
                .sorted(Comparator.comparingInt(Employee::getEmployeeId))
                .forEach(System.out::println);
    }


}
