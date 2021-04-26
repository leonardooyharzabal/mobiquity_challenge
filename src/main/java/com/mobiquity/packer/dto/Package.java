package com.mobiquity.packer.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Package {

    //BigDecimal has more precision doing operations with decimal numbers
    private BigDecimal weight = new BigDecimal(0);
    private List<PackageItem> packageItems = new ArrayList<>();
}
