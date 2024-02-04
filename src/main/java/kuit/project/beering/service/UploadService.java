package kuit.project.beering.service;

import kuit.project.beering.domain.*;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.domain.image.ReviewImage;
import kuit.project.beering.dto.request.review.ReviewCreateRequestDto;
import kuit.project.beering.dto.request.selectedOption.SelectedOptionCreateRequestDto;
import kuit.project.beering.dto.response.review.ReviewResponseDto;
import kuit.project.beering.repository.ImageRepository;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.domain.DrinkException;
import kuit.project.beering.util.exception.domain.MemberException;
import kuit.project.beering.util.exception.domain.ReviewException;
import kuit.project.beering.util.s3.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UploadService {

    private final AwsS3Uploader awsS3Uploader;
    private final ImageRepository imageRepository;

    // 테스트 이미지를 S3에 저장하고 테스트 이미지 엔티티 생성
    public List<Image> uploadTestImages(List<MultipartFile> testImages) {

        List<String> uploadNames = testImages.stream()
                .map(testImage -> testImage.getOriginalFilename().toUpperCase())
                .collect(Collectors.toList());
        List<String> urls = testImages.stream()
                .map(testImage -> awsS3Uploader.upload(testImage, AttachmentType.TEST.getType()))
                .collect(Collectors.toList());
        List<Image> images = new ArrayList<>();
        for(int i=0; i< urls.size(); i++) {
            Image image = createImage(urls.get(i), uploadNames.get(i));
            images.add(image);
        }
        return images;
    }

    private Image createImage(String url, String uploadName) {
        log.info("url = {}, uploadName = {}", url, uploadName);

        return imageRepository.save(new Image(url, uploadName));
    }
}
