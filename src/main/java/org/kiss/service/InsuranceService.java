package org.kiss.service;

import org.kiss.api.model.InsurancePage;
import org.kiss.api.model.InsuranceRequest;
import org.kiss.api.model.InsuranceResponse;
import org.kiss.exception.InsuranceAlreadyExistsException;
import org.kiss.exception.InsuranceNotFoundException;
import org.kiss.mapper.InsuranceMapper;
import org.kiss.model.entity.Insurance;
import org.kiss.repository.InsuranceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final InsuranceMapper insuranceMapper;

    public InsuranceResponse createInsurance(InsuranceRequest request) {
        if (insuranceRepository.existsByName(request.getName())) {
            throw new InsuranceAlreadyExistsException(request.getName());
        }

        Insurance insurance = insuranceMapper.toEntity(request);
        insuranceRepository.save(insurance);

        return insuranceMapper.toResponse(insurance);
    }

    public InsurancePage listInsurances(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return insuranceMapper.toPage(insuranceRepository.findAll(pageable));
    }

    public InsuranceResponse getInsuranceById(String id) {
        Insurance insurance = insuranceRepository.findById(id)
                .orElseThrow(() -> new InsuranceNotFoundException(id));
        return insuranceMapper.toResponse(insurance);
    }

    public InsuranceResponse updateInsurance(String id, InsuranceRequest request) {
        Insurance existing = insuranceRepository.findById(id)
                .orElseThrow(() -> new InsuranceNotFoundException(id));

        if (!request.getName().equals(existing.getName()) && insuranceRepository.existsByName(request.getName())) {
            throw new InsuranceAlreadyExistsException(request.getName());
        }

        Insurance updated = insuranceMapper.applyUpdate(existing, request);
        insuranceRepository.save(updated);

        return insuranceMapper.toResponse(updated);
    }

    public void deleteInsurance(String id) {
        if (!insuranceRepository.existsById(id)) {
            throw new InsuranceNotFoundException(id);
        }
        insuranceRepository.deleteById(id);
    }
}
