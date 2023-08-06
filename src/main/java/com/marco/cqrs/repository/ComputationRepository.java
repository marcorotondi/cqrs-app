package com.marco.cqrs.repository;


import com.marco.cqrs.entity.ComputationEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ComputationRepository {

    private final Map<String, ComputationEntity> data;

    public ComputationRepository() {
        data = new ConcurrentHashMap<>();
    }

    public Optional<ComputationEntity> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }

    public void addComputation(ComputationEntity computationEntity) {
        data.putIfAbsent(computationEntity.getId(), computationEntity);
    }

    public void updateComputation(ComputationEntity computationEntity) {
        data.put(computationEntity.getId(), computationEntity);
    }
}