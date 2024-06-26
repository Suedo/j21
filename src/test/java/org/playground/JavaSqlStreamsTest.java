package org.playground;

import model.Employee;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Utils.loadEmployeesFromCSV;

public class JavaSqlStreamsTest {

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

    /*
    11. Find the department with the highest number of employees.

        -- m1. when join not needed
        SELECT e.department_id , COUNT(e.employee_id) count  from employees e
        group by e.department_id
        order by count DESC limit 1;

        -- m2. when join is needed
        SELECT d.department_id , d.department_name , j.count from departments d
        join (
            SELECT e.department_id d_id , COUNT(e.employee_id) count  from employees e
            group by e.department_id order by count desc limit 1
        ) j on d.department_id  = j.d_id;

     */
    @Test
    void test11() {

        employees.stream()
                .collect(groupingBy(Employee::getDepartmentName, counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresentOrElse(
                        entry -> System.out.printf("Department with most employees: %s, employee count: %s", entry.getKey(), entry.getValue()),
                        () -> System.out.println("nothing found")
                );
    }

    /*
    -- 12. List the employees who have been hired for more than 3 years.

            select e.employee_id , e.hire_date from employees e
            WHERE hire_date <= DATE_SUB(CURDATE(), INTERVAL 3 year);
    */
    @Test
    void test12() {
        employees.stream()
                .filter(employee -> employee.getHireDate().isBefore(LocalDate.now().minusYears(3)))
                .forEach(System.out::println);
    }

/*
    -- 13. Retrieve the names of employees who do not work in 'Marketing' or 'Finance' departments.

    SELECT * from employees e join departments d on e.department_id  = d.department_id
    WHERE d.department_name not in ('Marketing', 'Finance');
 */

    @Test
    void test13() {
        List<String> whiteList = List.of("Marketing", "Finance");
        employees.stream()
                .filter(employee -> !whiteList.contains(employee.getDepartmentName()))
                .map(Employee::getEmployeeId)
                .forEach(System.out::println);
    }

/*
    -- 14. Find the number of employees hired each year.

        SELECT YEAR(hire_date) hire_year, COUNT(e.employee_id) count
        from employees e
        group by YEAR(hire_date)
        ORDER by hire_year;
*/

    @Test
    void test14() {
        employees.stream()
                .collect(groupingBy(
                        employee -> employee.getHireDate().getYear(),
                        counting()
                )).forEach((year, count) -> System.out.printf("Year: %s, hires: %s\n", year, count));
    }

/*
    -- 15. List the employees whose salary is within 10% of the highest salary in the company.

    SELECT * from employees
    WHERE salary >= (SELECT MAX(salary) * 0.9 from employees)
 */

    @Test
    void test15() {
        var maxSalary = employees.stream()
                .map(Employee::getSalary)
                .max(Double::compareTo).orElse(0.0);

        System.out.println("90% of Max Salary: " + maxSalary * 0.9);

        employees.stream()
                .filter(employee -> employee.getSalary() >= maxSalary * 0.9)
                .forEach(employee -> System.out.printf("employee: %s %s, salary: %s\n", employee.getFirstName(), employee.getLastName(), employee.getSalary()));

    }

    /*
        -- 17. Find the top 3 highest-paid employees in each department.
            WITH ranked AS (
                SELECT employee_id, first_name, last_name, department_id, salary,
                    DENSE_RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS salary_rank
                FROM employees
            )
            SELECT employee_id, first_name, last_name, department_id, salary, salary_rank
            FROM ranked
            WHERE salary_rank <= 3;
     */
    @Test
    void test17() {

        record RankedEmployee(Employee employee, Integer rank) {
        }

        // partition by dept, order by salary desc
        Map<String, List<Employee>> employeesByDeptSortedBySalaryDesc = employees.stream()
                .collect(groupingBy(
                        Employee::getDepartmentName,
                        collectingAndThen(
                                toList(),   // how to collect
                                list -> {   // what to do after collecting
                                    // sort in descending order of salary
                                    list.sort(Comparator.comparingDouble(Employee::getSalary).reversed());
                                    return list;
                                }
                        )
                ));


        /*
            we go from a Map<String, List<Employee>> to Map<String, List<RankedEmployee>>
            very similar to our SQL, where the final projection is different than tha native projection from the table
         */
        Map<String, List<RankedEmployee>> rankedEmployeesByDept = employeesByDeptSortedBySalaryDesc.entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        entry -> {
                            // add rank
                            AtomicInteger rank = new AtomicInteger(1);

                            // create a new projection, with added field : rank
                            return entry.getValue().stream()
                                    .map(employee -> new RankedEmployee(employee, rank.getAndIncrement()))
                                    .filter(rankedEmployee -> rankedEmployee.rank() <= 3) // top 3
                                    .collect(toList());
                            // new projection == new return type
                        }
                ));
        System.out.println("Ranked Projection");
        rankedEmployeesByDept
                .forEach((key, value) -> System.out.printf("%s, %s\n",
                        key,
                        value.stream().map(re -> re.employee().getEmployeeId()).toList()));

        /*
            SIMPLER:
            if just the answer is needed: top 3 salaried employee for each dept, all the above extra work
            for creating `rankedEmployeesByDept` is not needed.
         */
        System.out.println("Simple Result");
        employeesByDeptSortedBySalaryDesc
                .forEach((key, value) -> System.out.printf("%s, %s\n",
                        key,
                        value.stream().limit(3).map(Employee::getEmployeeId).toList()));


    }


}
