package org.kiss.service;

import org.bson.types.ObjectId;
import org.kiss.api.model.CarTypeCreationResponse;
import org.kiss.api.model.CarTypePage;
import org.kiss.api.model.CarTypeRequest;
import org.kiss.exception.CarTypeAlreadyExistsException;
import org.kiss.exception.CarTypeNotFoundException;
import org.kiss.mapper.CarTypeMapper;
import org.kiss.model.entity.CarType;
import org.kiss.repository.CarTypeRepository;
import org.kiss.storage.StorageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarTypeService {

    private final CarTypeRepository carTypeRepository;
    private final CarTypeMapper carTypeMapper;
    private final StorageService storageService;

    public CarTypePage listCarTypes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "make", "model"));
        return carTypeMapper.toPage(carTypeRepository.findAll(pageable));
    }

    public CarTypeCreationResponse createCarType(CarTypeRequest request) {
        if (carTypeRepository.existsByMakeAndModel(request.getMake(), request.getModel())) {
            throw new CarTypeAlreadyExistsException(request.getMake(), request.getModel());
        }

        String id = new ObjectId().toHexString();
        String objectKey = "car-types/" + id + "/preview";

        CarType carType = carTypeMapper.toEntity(request);
        carType.setId(id);
        carType.setPreviewUrl(storageService.publicUrlFor(objectKey));
        carTypeRepository.save(carType);

        String previewUploadUrl = storageService.generateUploadUrl(objectKey);

        return carTypeMapper.toCreationResponse(carType, previewUploadUrl);
    }

    public void deleteCarType(String id) {
        if (!carTypeRepository.existsById(id)) {
            throw new CarTypeNotFoundException(id);
        }
        carTypeRepository.deleteById(id);
    }
}
