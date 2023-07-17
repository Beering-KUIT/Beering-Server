package kuit.project.beering.controller.advice;

import kuit.project.beering.controller.DrinkController;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.DrinkException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = DrinkController.class)
public class DrinkControllerAdvice {
    @ExceptionHandler(DrinkException.class)
    public BaseResponse<Object> invalidOrder(DrinkException ex) {
        return new BaseResponse<>(BaseResponseStatus.INVALID_ORDER);
    }
}
