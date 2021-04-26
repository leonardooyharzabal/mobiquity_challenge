package com.mobiquity.packer;

import com.mobiquity.exception.APIException;
import com.mobiquity.packer.dto.Package;
import com.mobiquity.util.FileParser;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Packer {

    private static final String SOLUTION_LINE_SEPARATOR = "\n";

    private Packer() {
    }

    public static String pack(String filePath) throws APIException {

        try{
            String fileToString = FileParser.parseFileIntoString(filePath);
            List<Package> packagesToPack = PackageParser.parsePackages(fileToString);

            List<String> result = new ArrayList<>();
            packagesToPack.forEach( p -> result.add(PackageProcessor.processPackage(p)));

            return StringUtils.join(result, SOLUTION_LINE_SEPARATOR);


        } catch (IOException ioe){
            throw new APIException("An error has ocurred parsing packages file", ioe);
        }
    }
}
