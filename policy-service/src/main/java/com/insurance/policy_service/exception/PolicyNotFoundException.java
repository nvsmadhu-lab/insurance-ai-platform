package com.insurance.policy_service.exception;

public class PolicyNotFoundException extends RuntimeException{
    public PolicyNotFoundException(String message) {
        super(message);
    }
}
