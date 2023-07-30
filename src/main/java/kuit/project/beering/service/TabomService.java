package kuit.project.beering.service;


import kuit.project.beering.repository.TabomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TabomService {
    private static TabomRepository tabomRepository;
}
