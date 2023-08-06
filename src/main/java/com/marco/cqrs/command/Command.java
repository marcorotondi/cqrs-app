package com.marco.cqrs.command;

import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;

public interface Command {

    String id();

    String computationId();

    ComputationType computationType();

    Operation operation();

    Integer index();
}