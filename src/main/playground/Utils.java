import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Employee;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Utils {


    public static List<Employee> loadEmployeesFromCSV(File csvFile) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());


        /*
        https://cowtowncoder.medium.com/reading-csv-with-jackson-c4e74a15ddc1
            If the CSV does not come with a header row, the below can be used.
            1. make sure the pojo, Employee class, has `@JsonPropertyOrder` with correct order of fields
            2. `csvMapper.schemaFor(Employee.class)` to get the schema, which you can use in the csvMapper
         */
        CsvSchema employeeSchema = csvMapper.schemaFor(Employee.class);

        /*
            However, If the CSV does come with a header row, use a blank schema header like below
            header row for employee:
                employeeId,firstName,lastName,email,phoneNumber,hireDate,jobId,salary,managerId,departmentName
         */
        CsvSchema headerSchema = CsvSchema.emptySchema().withHeader();


        try (MappingIterator<Employee> iterator = csvMapper
                .readerFor(Employee.class)
                .with(headerSchema)     // csv has a header row
                .readValues(csvFile)) {

            return iterator.readAll();
        }
    }


    public static void main(String[] args) {
        try {
            File csvFile = Paths.get("src/main/resources/employees.csv").toFile();
            List<Employee> employees = loadEmployeesFromCSV(csvFile);
            employees.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

