package kuit.project.beering.controller.advice;

import kuit.project.beering.controller.DrinkController;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.DrinkException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = DrinkController.class)
public class DrinkControllerAdvice {
    @ExceptionHandler(DrinkException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> handleDrinkException(DrinkException ex) {
        return new BaseResponse<>(ex.getStatus());
    }
}
