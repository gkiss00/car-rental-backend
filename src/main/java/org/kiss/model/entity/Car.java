package org.kiss.model.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.kiss.model.entity.util.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {

    @Id
    private String id;

    private String companyId;

    @Indexed(unique = true, sparse = true)
    private String vin;

    private String licensePlate;

    private String make;
    private String model;
    private int year;
    private CarCategory category;
    private TransmissionType transmission;
    private FuelType fuelType;
    private int seats;
    private Integer doors;
    private String color;
    private int mileageKm;

    private BigDecimal dailyPriceAmount;
    private String dailyPriceCurrency;

    private CarStatus status;
    private CarLocation location;

    private List<String> features;
    private List<String> imageUrls;
    private String description;

    private Instant createdAt;
    private Instant updatedAt;
}
