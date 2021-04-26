package com.mobiquity.packer;

import com.mobiquity.exception.APIException;
import com.mobiquity.packer.dto.Package;
import com.mobiquity.packer.dto.PackageItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PackageParser {

    private static final int PACKAGE_ITEM_TOTAL_FIELDS = 3;
    private static final int PACKAGE_ITEM_COST_POSITION = 2;
    private static final int PACKAGE_ITEM_WEIGHT_POSITION = 1;
    private static final int PACKAGE_ITEM_WEIGHT_INDEX = 0;

    private static final int LINE_PACKAGE_WEIGHT_INDEX = 0;
    private static final int LINE_PACKAGE_ITEMS_INDEX = 1;

    private static final BigDecimal MAX_PACKAGE_WEIGHT = new BigDecimal(100);
    private static final BigDecimal MAX_PACKAGE_ITEM_WEIGHT = new BigDecimal(100);
    private static final BigDecimal MAX_PACKAGE_ITEM_COST = new BigDecimal(100);
    private static final int MAX_PACKAGE_ITEMS_QUANTITY = 15;

    private static final String LINES_SEPARATOR = "\n";
    private static final String PACKAGE_LINE_SEPARATOR = ":";
    private static final String PACKAGE_ITEMS_SEPARATOR = " ";
    private static final String PACKAGE_ITEM_DATA_SEPARATOR = ",";
    private static final String ERROR_PARSING_FILE = "Error parsing file in line";
    private static final String LINE_INCORRECT_FORMAT = "Line has incorrect format";
    private static final String ERROR_PARSING_PACKAGE_DATA = "Package data has incorrect format";
    private static final String LINE_HAS_NO_ITEMS = "Line has no items";
    private static final String PACKAGE_ITEM_INCORRECT_FORMAT = "Package item has incorrect format";
    private static final String PACKAGE_ITEM_COST_INCORRECT_FORMAT = "Package item cost has incorrect format";
    private static final String PACKAGE_ITEM_INDEX_ERROR = "Package item index is incorrect";
    private static final String PACKAGE_ITEM_WEIGHT_OUT_OF_BOUNDS = String.format("Package item weight must be in the (0,%s] interval", MAX_PACKAGE_ITEM_WEIGHT.toString());
    private static final String PACKAGE_ITEM_COST_OUT_OF_BOUNDS = String.format("Package item cost must be in the (0,%s] interval", MAX_PACKAGE_ITEM_COST.toString());
    private static final String PACKAGE_ITEM_EXPECTED_CURRENCY = "€";
    private static final String PACKAGE_ITEMS_QUANTITY_EXCEEDED = String.format("Line has more than %d items", MAX_PACKAGE_ITEMS_QUANTITY);
    private static final String PACKAGE_WEIGHT_EXCEEDED = String.format("Package weight is greater than %s", MAX_PACKAGE_WEIGHT.toString());


    public static List<Package> parsePackages(String packagesFile) throws APIException {

        List<Package> packages = new ArrayList<>();
        String[] fileLines = packagesFile.split(LINES_SEPARATOR);

        //validate all files before starting processing, it's better than processing lines and then failing if there are malformed lines
        for (int fileIterator = 0; fileIterator < fileLines.length; fileIterator++) {
            int lineNumber = fileIterator+1;//file starts with line number 1
            Package linePackage = parseLine(fileLines[fileIterator], lineNumber);
            packages.add(linePackage);
        }

        return packages;
    }

    private static Package parseLine(String line, int lineNumber) throws APIException{

        String[] splittedLine = line.split(PACKAGE_LINE_SEPARATOR);

        if(splittedLine.length != 2){
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, LINE_INCORRECT_FORMAT));
        }

        Package parsedPackage = new Package();

        BigDecimal packageWeight = parseBigDecimal(splittedLine[LINE_PACKAGE_WEIGHT_INDEX],lineNumber);

        if(packageWeight.compareTo(MAX_PACKAGE_WEIGHT) > 0){
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, PACKAGE_WEIGHT_EXCEEDED));
        }

        parsedPackage.setWeight(packageWeight);
        parsedPackage.setPackageItems(parsePackageItems(splittedLine[LINE_PACKAGE_ITEMS_INDEX].trim(), lineNumber));

        return parsedPackage;
    }

    private static List<PackageItem> parsePackageItems(String packageItemsStr, int lineNumber) throws APIException {

        String[] splittedItems = packageItemsStr.split(PACKAGE_ITEMS_SEPARATOR);

        if(splittedItems.length == 0){
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, LINE_HAS_NO_ITEMS));
        }

        if(splittedItems.length > MAX_PACKAGE_ITEMS_QUANTITY){
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, PACKAGE_ITEMS_QUANTITY_EXCEEDED));
        }

        List<PackageItem> packageItems = new ArrayList<>(splittedItems.length);
        for (int itemIterator = 0; itemIterator < splittedItems.length; itemIterator++) {
            int indexChecker = itemIterator+1;
            packageItems.add(parsePackageItem(splittedItems[itemIterator].trim(), lineNumber, indexChecker));
        }

        return packageItems;
    }

    private static PackageItem parsePackageItem(String packageItemStr, int lineNumber, int indexChecker) throws APIException{

        if (!packageItemStr.startsWith("(")
                || !packageItemStr.endsWith(")")) {
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, PACKAGE_ITEM_INCORRECT_FORMAT));
        }

        String itemWithoutParenthesis = packageItemStr.substring(1, packageItemStr.length() - 1);
        String[] packageItemAttributes = itemWithoutParenthesis.split( PACKAGE_ITEM_DATA_SEPARATOR);

        //validated here to avoid parsing unnecessary possible large quantity of package items
        if (packageItemAttributes.length != PACKAGE_ITEM_TOTAL_FIELDS)
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, PACKAGE_ITEM_INCORRECT_FORMAT));

        if (!packageItemAttributes[PACKAGE_ITEM_COST_POSITION].startsWith(PACKAGE_ITEM_EXPECTED_CURRENCY))
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, PACKAGE_ITEM_COST_INCORRECT_FORMAT));

        int index = parseInteger(packageItemAttributes[PACKAGE_ITEM_WEIGHT_INDEX].trim(), lineNumber);
        BigDecimal weight = parseBigDecimal(packageItemAttributes[PACKAGE_ITEM_WEIGHT_POSITION].trim(), lineNumber);
        BigDecimal cost = parseBigDecimal(packageItemAttributes[PACKAGE_ITEM_COST_POSITION].substring(1).trim(), lineNumber);

        PackageItem parsedPackageItem = new PackageItem(index, weight, cost);
        validatePackageItem(parsedPackageItem, lineNumber, indexChecker);

        return new PackageItem(index, weight, cost);

    }

    private static int parseInteger(String indexStr, int lineNumber) throws APIException {

        try {
            return Integer.parseInt(indexStr.trim());
        } catch (NumberFormatException nfe) {
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, ERROR_PARSING_PACKAGE_DATA), nfe);
        }
    }

    private static BigDecimal parseBigDecimal(String packageWeightStr, int lineNumber) throws APIException {

        try {
            return new BigDecimal(packageWeightStr.trim());
        } catch (Exception e) {
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, ERROR_PARSING_PACKAGE_DATA), e);
        }
    }

    private static void validatePackageItem(PackageItem packageItem, int lineNumber, int indexChecker) throws APIException{

        if(indexChecker != packageItem.getIndex()){
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, PACKAGE_ITEM_INDEX_ERROR));
        }

        if(packageItem.getWeight().compareTo(BigDecimal.ZERO) < 1 || packageItem.getWeight().compareTo(MAX_PACKAGE_ITEM_WEIGHT) > 0){
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, PACKAGE_ITEM_WEIGHT_OUT_OF_BOUNDS));
        }

        if(packageItem.getCost().compareTo(BigDecimal.ZERO) < 1 || packageItem.getCost().compareTo(MAX_PACKAGE_ITEM_COST) > 0){
            throw new APIException(String.format("%s %d: %s", ERROR_PARSING_FILE, lineNumber, PACKAGE_ITEM_COST_OUT_OF_BOUNDS));
        }
    }
}
