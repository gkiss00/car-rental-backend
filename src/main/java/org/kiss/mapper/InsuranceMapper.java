package org.kiss.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.kiss.api.model.InsurancePage;
import org.kiss.api.model.InsuranceRequest;
import org.kiss.api.model.InsuranceResponse;
import org.kiss.model.entity.Insurance;
import org.kiss.model.entity.util.CoveragePoint;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class InsuranceMapper {

    private static final String DEFAULT_CURRENCY = "EUR";

    public Insurance toEntity(InsuranceRequest request) {
        return Insurance.builder()
                .name(request.getName())
                .coveragePoints(toEntityCoveragePoints(request.getCoveragePoints()))
                .dailyPriceAmount(BigDecimal.valueOf(request.getDailyPriceAmount()))
                .dailyPriceCurrency(StringUtils.hasText(request.getDailyPriceCurrency())
                        ? request.getDailyPriceCurrency()
                        : DEFAULT_CURRENCY)
                .hasFranchise(Boolean.TRUE.equals(request.getHasFranchise()))
                .franchiseAmount(request.getFranchiseAmount() != null
                        ? BigDecimal.valueOf(request.getFranchiseAmount())
                        : null)
                .build();
    }

    public Insurance applyUpdate(Insurance existing, InsuranceRequest request) {
        existing.setName(request.getName());
        existing.setCoveragePoints(toEntityCoveragePoints(request.getCoveragePoints()));
        existing.setDailyPriceAmount(BigDecimal.valueOf(request.getDailyPriceAmount()));
        existing.setDailyPriceCurrency(StringUtils.hasText(request.getDailyPriceCurrency())
                ? request.getDailyPriceCurrency()
                : DEFAULT_CURRENCY);
        existing.setHasFranchise(Boolean.TRUE.equals(request.getHasFranchise()));
        existing.setFranchiseAmount(request.getFranchiseAmount() != null
                ? BigDecimal.valueOf(request.getFranchiseAmount())
                : null);
        return existing;
    }

    public InsurancePage toPage(Page<Insurance> page) {
        return new InsurancePage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }

    public InsuranceResponse toResponse(Insurance insurance) {
        return new InsuranceResponse()
                .id(insurance.getId())
                .name(insurance.getName())
                .coveragePoints(toApiCoveragePoints(insurance.getCoveragePoints()))
                .dailyPriceAmount(insurance.getDailyPriceAmount().doubleValue())
                .dailyPriceCurrency(insurance.getDailyPriceCurrency())
                .hasFranchise(insurance.isHasFranchise())
                .franchiseAmount(insurance.getFranchiseAmount() != null
                        ? insurance.getFranchiseAmount().doubleValue()
                        : null);
    }

    private List<CoveragePoint> toEntityCoveragePoints(List<org.kiss.api.model.CoveragePoint> points) {
        if (points == null) {
            return List.of();
        }
        return points.stream()
                .map(point -> CoveragePoint.builder()
                        .label(point.getLabel())
                        .covered(Boolean.TRUE.equals(point.getCovered()))
                        .build())
                .toList();
    }

    private List<org.kiss.api.model.CoveragePoint> toApiCoveragePoints(List<CoveragePoint> points) {
        if (points == null) {
            return List.of();
        }
        return points.stream()
                .map(point -> new org.kiss.api.model.CoveragePoint()
                        .label(point.getLabel())
                        .covered(point.isCovered()))
                .toList();
    }
}
