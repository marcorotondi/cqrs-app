package com.marco.cqrs.events;

import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;

public interface Event {

    String id();

    ComputationType computationType();

    Operation operation();

    Integer index();
}