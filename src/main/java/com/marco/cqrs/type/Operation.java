package com.marco.cqrs.type;

public enum Operation {

    CLONE_START,
    CLONE_END,
    INIT,
    POWER_FLOW_START,
    POWER_FLOW,
    POWER_FLOW_END,
    POWER_FLOW_COMPLETED,
    REQUEST_START,
    REQUEST,
    REQUEST_END,
    REQUEST_COMPLETED,
    COMPLETED,
    ERROR;
}