package com.marco.cqrs.service;

import com.marco.cqrs.events.CloneEvent;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CloneService {

    private static final Logger log = LoggerFactory.getLogger(CloneService.class);

    @EventHandler
    public void onCloneEvent(CloneEvent cloneEvent) {
        log.info("Event: {}", cloneEvent);


    }
}