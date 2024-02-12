package kuit.project.beering.controller;

import kuit.project.beering.domain.AttachmentType;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.service.UploadService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.s3.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(value="/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<List<Image>> saveImage(
            @RequestParam(value="image") List<MultipartFile> images,
            @RequestParam(value="type") String type) throws IOException {

        List<Image> imageList = uploadService.uploadImages(images, AttachmentType.fromString(type));

        return new BaseResponse<>(imageList);

    }
}
