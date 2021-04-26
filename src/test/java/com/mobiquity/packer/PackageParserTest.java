package com.mobiquity.packer;

import com.mobiquity.exception.APIException;
import com.mobiquity.packer.dto.Package;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackageParserTest {

    @Test
    public void parsePackages_correctLine_expectedPackage() throws APIException{

        List<Package> parsedPackage = PackageParser.parsePackages("8 : (1,15.3,€34)");

        Assertions.assertEquals(1, parsedPackage.size());
        Assertions.assertEquals(0, new BigDecimal("8").compareTo(parsedPackage.get(0).getWeight()));
        Assertions.assertEquals(1, parsedPackage.get(0).getPackageItems().size());
        Assertions.assertEquals(1, parsedPackage.get(0).getPackageItems().get(0).getIndex());
        Assertions.assertEquals(0, new BigDecimal("15.3").compareTo(parsedPackage.get(0).getPackageItems().get(0).getWeight()));
        Assertions.assertEquals(0, new BigDecimal("34").compareTo(parsedPackage.get(0).getPackageItems().get(0).getCost()));
    }

    @Test
    public void parsePackages_incorrectLines_expectedError(){

        Map<String, String> incorrectLines = new HashMap<>();
        incorrectLines.put("", "Error parsing file in line 1: Line has incorrect format");
        incorrectLines.put("10", "Error parsing file in line 1: Line has incorrect format");
        incorrectLines.put("10:", "Error parsing file in line 1: Line has incorrect format");
        incorrectLines.put("10: (1,2,€1", "Error parsing file in line 1: Package item has incorrect format");
        incorrectLines.put("10: 1,2,€1", "Error parsing file in line 1: Package item has incorrect format");
        incorrectLines.put("1: (a,2,€1", "Error parsing file in line 1: Package item has incorrect format");
        incorrectLines.put("1: (a,2,€1)", "Error parsing file in line 1: Package data has incorrect format");
        incorrectLines.put("1: (1,a,€1)", "Error parsing file in line 1: Package data has incorrect format");
        incorrectLines.put("1: (1,1,€a)", "Error parsing file in line 1: Package data has incorrect format");
        incorrectLines.put("101: (1,2,€1)", "Error parsing file in line 1: Package weight is greater than 100");
        incorrectLines.put("10: (1,2,1)", "Error parsing file in line 1: Package item cost has incorrect format");
        incorrectLines.put("101: (1,2)", "Error parsing file in line 1: Package weight is greater than 100");
        incorrectLines.put("10: (1,101,€1)", "Error parsing file in line 1: Package item weight must be in the (0,100] interval");
        incorrectLines.put("10: (1,1,€101)", "Error parsing file in line 1: Package item cost must be in the (0,100] interval");
        incorrectLines.put("10: (1,1,€101) (1,1,€101)", "Error parsing file in line 1: Package item cost must be in the (0,100] interval");
        incorrectLines.put("10: (1,0,€101)", "Error parsing file in line 1: Package item weight must be in the (0,100] interval");
        incorrectLines.put("10: (1,1,€1) (2,1,€1) (3,1,€1) (4,1,€1) (5,1,€1) (6,1,€1) (7,1,€1) (8,1,€1) (9,1,€1) (10,1,€1) (11,1,€1) (12,1,€1) (13,1,€1) (14,1,€1) (15,1,€1) (16,1,€1)", "Error parsing file in line 1: Line has more than 15 items");

        for (Map.Entry<String, String> entry : incorrectLines.entrySet()) {

            APIException exception = assertThrows(
                    APIException.class,
                    () -> PackageParser.parsePackages(entry.getKey())
            );

            assertEquals(entry.getValue(), exception.getMessage());
        }
    }
}
