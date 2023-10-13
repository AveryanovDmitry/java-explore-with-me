package ru.practicum.main_service.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.ApiError;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class EwmExceptionHandler {

    @ExceptionHandler({AlreadyCreatedException.class, ConflictParametersException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError notUniqEmailExceptionHandler(Exception exception) {
        log.info("Получен статус 409 CONFLICT {}", exception.getMessage());
        return new ApiError(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError argumentExceptionHandler(MethodArgumentNotValidException exception) {
        log.info("Получен статус 400 BAD_REQUEST {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, "Not valid method argument exception",
                exception.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError alreadyCreatedExceptionHandler(BadParametersException exception) {
        log.info("Получен статус 400 BAD_REQUEST {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, "Bad request",
                exception.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError alreadyCreatedExceptionHandler(NotFoundException exception) {
        log.info("Получен статус 404 NOT_FOUND {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, "Not found required target",
                exception.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError throwableExceptionHandler(Throwable exception) {
        log.info("Получен статус 500 INTERNAL_SERVER_ERROR {}", exception.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, "INTERNAL_SERVER_ERROR throwable handle",
                exception.getMessage(),
                LocalDateTime.now());
    }
}
