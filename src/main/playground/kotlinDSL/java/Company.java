package kotlinDSL.java;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Company {
    private final String myName;
    private final String myCity;
}