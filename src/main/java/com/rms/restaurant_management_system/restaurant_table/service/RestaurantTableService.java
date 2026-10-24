package com.rms.restaurant_management_system.restaurant_table.service;

import com.rms.restaurant_management_system.common.exception.BusinessConflictException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.restaurant_table.dto.TableCreateRequest;
import com.rms.restaurant_management_system.restaurant_table.dto.TableUpdateRequest;
import com.rms.restaurant_management_system.restaurant_table.model.RestaurantTable;
import com.rms.restaurant_management_system.restaurant_table.model.TableStatus;
import com.rms.restaurant_management_system.restaurant_table.repo.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantTableService {

    private final TableRepository tableRepository;

    @Transactional(readOnly = true)
    public List<RestaurantTable> getAll() {
        return tableRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<RestaurantTable> getAvailable() {
        return tableRepository.findByStatus(TableStatus.FREE);
    }

    @Transactional(readOnly = true)
    public RestaurantTable getById(Long id) {
        return findTable(id);
    }

    @Transactional
    public RestaurantTable create(TableCreateRequest request) {
        if (tableRepository.existsByNumber(request.number())) {
            throw new BusinessConflictException("Table number already exists");
        }

        RestaurantTable table = RestaurantTable.builder()
                .number(request.number())
                .seatsCount(request.seatsCount())
                .status(TableStatus.FREE)
                .build();
        return tableRepository.save(table);
    }

    @Transactional
    public RestaurantTable update(Long id, TableUpdateRequest request) {
        RestaurantTable table = findTable(id);
        tableRepository.findByNumber(request.number())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessConflictException("Table number already exists");
                });

        table.setNumber(request.number());
        table.setSeatsCount(request.seatsCount());
        table.setStatus(request.status());
        return tableRepository.save(table);
    }

    @Transactional
    public void delete(Long id) {
        tableRepository.delete(findTable(id));
    }

    private RestaurantTable findTable(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
    }
}