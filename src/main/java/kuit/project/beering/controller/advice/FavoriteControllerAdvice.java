package kuit.project.beering.controller.advice;

import kuit.project.beering.controller.FavoriteController;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.FavoriteException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = FavoriteController.class)
public class FavoriteControllerAdvice {
    @ExceptionHandler(FavoriteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> handleDrinkException(FavoriteException ex) {
        return new BaseResponse<>(ex.getStatus());
    }
}
