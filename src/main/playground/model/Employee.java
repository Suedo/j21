package model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@JsonPropertyOrder({"employeeId", "firstName", "lastName", "email", "phoneNumber", "hireDate", "jobId", "salary", "managerId", "departmentName"})
@Data
@NoArgsConstructor
public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    private String jobId;
    private double salary;
    private Integer managerId; // Nullable field
    private String departmentName;
}
