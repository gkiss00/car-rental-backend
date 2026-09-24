package org.kiss.mapper;

import org.kiss.api.model.CarTypeCreationResponse;
import org.kiss.api.model.CarTypePage;
import org.kiss.api.model.CarTypeRequest;
import org.kiss.api.model.CarTypeResponse;
import org.kiss.model.entity.CarType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CarTypeMapper {

    public CarType toEntity(CarTypeRequest request) {
        return CarType.builder()
                .make(request.getMake())
                .model(request.getModel())
                .build();
    }

    public CarTypeResponse toResponse(CarType carType) {
        return new CarTypeResponse()
                .id(carType.getId())
                .make(carType.getMake())
                .model(carType.getModel())
                .previewUrl(carType.getPreviewUrl());
    }

    public CarTypeCreationResponse toCreationResponse(CarType carType, String previewUploadUrl) {
        return new CarTypeCreationResponse()
                .carType(toResponse(carType))
                .previewUploadUrl(previewUploadUrl);
    }

    public CarTypePage toPage(Page<CarType> page) {
        return new CarTypePage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
