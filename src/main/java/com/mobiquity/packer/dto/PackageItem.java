package com.mobiquity.packer.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class PackageItem {
    Integer index;
    BigDecimal weight;
    BigDecimal cost;
}