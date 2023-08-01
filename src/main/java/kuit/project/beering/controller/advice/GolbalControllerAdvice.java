package kuit.project.beering.controller.advice;

import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.DrinkException;
import kuit.project.beering.util.exception.FavoriteException;
import kuit.project.beering.util.exception.TabomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GolbalControllerAdvice {
    @ExceptionHandler(DrinkException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> handleDrinkException(DrinkException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

    @ExceptionHandler(TabomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> handleTabomException(TabomException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

    @ExceptionHandler(FavoriteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> handleFavoriteException(FavoriteException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

}
