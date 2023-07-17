package kuit.project.beering.controller.advice;

import kuit.project.beering.controller.DrinkController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = DrinkController.class)
public class DrinkControllerAdvice {

}
