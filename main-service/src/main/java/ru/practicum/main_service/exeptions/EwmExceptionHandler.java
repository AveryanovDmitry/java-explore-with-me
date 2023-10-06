package ru.practicum.main_service.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.main_service.dto.ApiError;

import java.time.LocalDateTime;

import static ru.practicum.main_service.MainServiceApplication.DATE_TIME_FORMATTER;

@ControllerAdvice
@Slf4j
public class EwmExceptionHandler {

    @ExceptionHandler({AlreadyCreatedException.class, ConflictParametersException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError notUniqEmailExceptionHandler(Exception exception) {
        return new ApiError(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError alreadyCreatedExceptionHandler(BadParametersException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Bad request",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError alreadyCreatedExceptionHandler(NotFoundException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Not found required target",
                exception.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }
}
