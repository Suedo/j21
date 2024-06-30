package kotlinDSL.java;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
public class Client {
    private final String myFirstName;
    private final String myLastName;
    private final Company myCompany;
    private final Twitter myTwitter;

    private LocalDate dob;
}