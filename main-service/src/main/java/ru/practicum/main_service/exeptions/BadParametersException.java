package ru.practicum.main_service.exeptions;

public class BadParametersException extends RuntimeException {
    public BadParametersException(String message){
        super(message);
    }
}
