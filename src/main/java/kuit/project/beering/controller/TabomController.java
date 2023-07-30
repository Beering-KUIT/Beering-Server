package kuit.project.beering.controller;


import kuit.project.beering.service.TabomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TabomController {
    private static TabomService tabomService;
}
