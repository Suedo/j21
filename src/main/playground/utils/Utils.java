package utils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Utils {


    public static List<Employee> loadEmployeesFromCSV(Path csvFile) {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());


        /*
        https://cowtowncoder.medium.com/reading-csv-with-jackson-c4e74a15ddc1
            If the CSV does NOT come with a header row, the below can be used.
            1. make sure the pojo, Employee class, has `@JsonPropertyOrder` with correct order of fields
            2. `csvMapper.schemaFor(Employee.class)` to get the schema, which you can use in the csvMapper
         */
        CsvSchema employeeSchema = csvMapper.schemaFor(Employee.class);

        /*
            However, If the CSV includes a header row, use a blank schema header like below
            header row for employee:
                employeeId,firstName,lastName,email,phoneNumber,hireDate,jobId,salary,managerId,departmentName
         */
        CsvSchema headerSchema = CsvSchema.emptySchema().withHeader();


        try (BufferedReader reader = Files.newBufferedReader(csvFile)) {
            MappingIterator<Employee> iterator = csvMapper
                    .readerFor(Employee.class)
                    .with(headerSchema) // csv has a header row
                    .readValues(reader);

            return iterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        List<Employee> employees = loadEmployeesFromCSV(Paths.get("src/main/resources/employees.csv"));
        employees.forEach(System.out::println);
    }
}

