package org.kiss.model.entity;

import java.math.BigDecimal;
import java.util.List;

import org.kiss.model.entity.util.CoveragePoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "insurances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private List<CoveragePoint> coveragePoints;

    private BigDecimal dailyPriceAmount;
    private String dailyPriceCurrency;

    private boolean hasFranchise;
    private BigDecimal franchiseAmount;
}
