package com.mobiquity.packer.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

@Getter
public class PackageOption {

    Set<Integer> indexes = new HashSet<>();
    BigDecimal weight = new BigDecimal(0);
    BigDecimal cost = new BigDecimal(0);

    public void addPackageItem(PackageItem packageItem){

        if(!indexes.contains(packageItem.getIndex())){
            indexes.add(packageItem.index);
            weight = weight.add(packageItem.getWeight());
            cost = cost.add(packageItem.getCost());
        }
    }

    //only indexes needed to compare equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageOption that = (PackageOption) o;

        return CollectionUtils.isEqualCollection(indexes, that.indexes);
    }

    @Override
    public int hashCode() {
        return indexes.hashCode();
    }
}
