package org.playground;

import model.Employee;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static utils.Utils.loadEmployeesFromCSV;

public class JavaStreamsTest {

    private List<Employee> employees = loadEmployeesFromCSV(Paths.get("src/main/resources/employees.csv"));

    @Test
    void test1() {
    }


}
