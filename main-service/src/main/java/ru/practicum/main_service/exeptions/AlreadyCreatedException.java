package ru.practicum.main_service.exeptions;

public class AlreadyCreatedException extends RuntimeException {
    public AlreadyCreatedException(String message) {
        super(message);
    }

}
