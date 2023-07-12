package kuit.project.beering.controller.advice;

import kuit.project.beering.controller.MemberController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = MemberController.class)
public class MemberControllerAdvice {

}
