package com.mobiquity.packer;

import com.mobiquity.exception.APIException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class PackerTest {
    @Test
    public void pack_sampleInput_CompareWithExpectedOutput() throws APIException, IOException {
        String resourceName = "example_input";
        String resultResourceName = "example_output";

        String filePath = Paths.get("src", "test", "resources", resourceName).toAbsolutePath().toString();
        String result = Packer.pack(filePath);
        String expectedResult = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resultResourceName)), StandardCharsets.UTF_8);

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void pack_emptyInput_emptyResult() throws APIException {
        String resourceName = "empty_input";

        String filePath = Paths.get("src", "test", "resources", resourceName).toAbsolutePath().toString();
        Assertions.assertThrows(APIException.class, () -> Packer.pack(filePath));
    }
}
