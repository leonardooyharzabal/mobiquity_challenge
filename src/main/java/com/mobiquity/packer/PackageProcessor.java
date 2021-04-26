package com.mobiquity.packer;

import com.mobiquity.packer.dto.Package;
import com.mobiquity.packer.dto.PackageItem;
import com.mobiquity.packer.dto.PackageOption;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PackageProcessor {

    private static final String EMPTY_SOLUTION = "-";
    private static final String SOLUTION_INDEXES_SEPARATOR = ",";

    public static String processPackage(Package packageData){

            List<PackageItem> itemsToConsider = removeExceededItems(packageData.getPackageItems(), packageData.getWeight());

            PackageOption packageSolution = new PackageOption();
            if(itemsToConsider.size() == 1){
                packageSolution.addPackageItem(itemsToConsider.get(0));
                return convertPackageOptionToString(packageSolution);
            }

            packageSolution = findBestPackageOption(itemsToConsider, packageData.getWeight());

        return convertPackageOptionToString(packageSolution);
    }


    private static List<PackageItem> removeExceededItems(List<PackageItem> packageItems, BigDecimal packageWeight){

        return packageItems.stream().
                filter(packageItem -> packageItem.getWeight().compareTo(packageWeight) <= 0 ).
                collect(Collectors.toList());
    }

    private static String convertPackageOptionToString(PackageOption packageOption){

        if(packageOption.getIndexes().size() == 0){
            return EMPTY_SOLUTION;
        }

        List<Integer> packageItemIndexes = new ArrayList<>(packageOption.getIndexes());

        packageItemIndexes.sort(Integer::compareTo);
        return  StringUtils.join(packageOption.getIndexes(), SOLUTION_INDEXES_SEPARATOR);
    }

    private static PackageOption findBestPackageOption(List<PackageItem> packageItems, BigDecimal maxWeight){

        if(packageItems.size() == 0){
            return new PackageOption();
        }

        //Compare between to decisions: Add next item to package or not
        List<PackageOption> optionsToCompare = new ArrayList<>();

        //auxiliary list to not lose items
        List<PackageItem> leftPackageItemsWhenDontUseCurrentItem = new ArrayList<>(packageItems);
        PackageItem currentItem = leftPackageItemsWhenDontUseCurrentItem.remove(0);

        //Option 1: don't use current item, try to find best package option with the same weight and subtracting the discarded item
        PackageOption dontUseCurrentItem = findBestPackageOption(leftPackageItemsWhenDontUseCurrentItem, maxWeight);
        optionsToCompare.add(dontUseCurrentItem);

        BigDecimal updatedMaxWeight = maxWeight.subtract(currentItem.getWeight());

        if(!addingCurrentItemExceedPackage(updatedMaxWeight)){

            //different auxiliary list needed to not lose items after recursion
            List<PackageItem> leftPackageItemsWhenUseCurrentItem = new ArrayList<>(packageItems);
            leftPackageItemsWhenUseCurrentItem.remove(0);

            //Option 2: use current item, try to find best package option with the updated weight and discarding
            //all items that not fit in the new package weight
            PackageOption useNextItem = findBestPackageOption(removeExceededItems(leftPackageItemsWhenUseCurrentItem, updatedMaxWeight), updatedMaxWeight);
            useNextItem.addPackageItem(currentItem);

            optionsToCompare.add(useNextItem);
        }

        return bestPackageOption(optionsToCompare);
    }

    private static PackageOption bestPackageOption(List<PackageOption> packageOptions){

        List<PackageOption> optionsSortedByCost = packageOptions.stream()
                .sorted(Comparator.comparing(PackageOption::getCost).reversed())
                .collect(Collectors.toList());

        BigDecimal bestCostOption = optionsSortedByCost.get(0).getCost();

        return optionsSortedByCost.stream()
                .filter(packageOption -> bestCostOption.equals(packageOption.getCost()))
                .sorted(Comparator.comparing(PackageOption::getWeight))
                .collect(Collectors.toList()).get(0);

    }

    private static boolean addingCurrentItemExceedPackage(BigDecimal newMaxWeight){

        return newMaxWeight.compareTo(BigDecimal.ZERO) < 0;
    }
}
