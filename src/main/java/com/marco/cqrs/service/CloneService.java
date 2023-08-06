package com.marco.cqrs.service;

import com.marco.cqrs.events.CloneEvent;
import com.marco.cqrs.repository.ComputationRepository;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CloneService {

    private static final Logger log = LoggerFactory.getLogger(CloneService.class);

    private final ComputationRepository repository;

    public CloneService(ComputationRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void onCloneEvent(CloneEvent event) {
        log.info("Event: {}", event);

        repository.findById(event.id()).ifPresent(computationEntity -> {

        });

    }
}