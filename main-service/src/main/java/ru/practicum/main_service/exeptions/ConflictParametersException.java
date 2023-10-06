package ru.practicum.main_service.exeptions;

public class ConflictParametersException extends RuntimeException {
    public ConflictParametersException(String message) {
        super(message);
    }
}
