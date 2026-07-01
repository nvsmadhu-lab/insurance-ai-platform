package com.insurance.party_service.exception;

public class PartyAlreadyExistsException extends RuntimeException{
    public PartyAlreadyExistsException(String message){
        super(message);
    }
}
