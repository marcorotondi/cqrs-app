package com.marco.cqrs.events;

import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.io.Serializable;

public record ErrorEvent(
        @TargetAggregateIdentifier
        String id,
        String computationId,
        ComputationType computationType,
        Operation operation,
        Integer index
) implements Event, Serializable {
}