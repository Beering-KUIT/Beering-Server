package kuit.project.beering.service;

import kuit.project.beering.domain.*;
import kuit.project.beering.domain.image.ReviewImage;
import kuit.project.beering.dto.request.review.ReviewCreateRequestDto;
import kuit.project.beering.dto.request.selectedOption.SelectedOptionCreateRequestDto;
import kuit.project.beering.dto.response.drink.DrinkPreview;
import kuit.project.beering.dto.response.drink.DrinkPreviewBuilder;
import kuit.project.beering.dto.response.review.ReviewDeleteResponseDto;
import kuit.project.beering.dto.response.SliceResponse;
import kuit.project.beering.dto.response.review.ReviewDetailReadResponseDto;
import kuit.project.beering.dto.response.review.ReviewReadResponseDto;
import kuit.project.beering.dto.response.review.ReviewResponseDto;
import kuit.project.beering.repository.*;
import kuit.project.beering.repository.drink.DrinkRepository;
import kuit.project.beering.util.exception.domain.DrinkException;
import kuit.project.beering.util.exception.domain.MemberException;
import kuit.project.beering.util.exception.domain.ReviewException;
import kuit.project.beering.util.s3.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SelectedOptionRepository selectedOptionRepository;
    private final ReviewOptionRepository reviewOptionRepository;
    private final DrinkRepository drinkRepository;
    private final MemberRepository memberRepository;
    private final AwsS3Uploader awsS3Uploader;
    private final ReviewImageRepository reviewImageRepository;
    private final TabomRepository tabomRepository;
    private final MemberService memberService;

    //리뷰 생성
    public ReviewResponseDto save(Long memberId, Long drinkId, ReviewCreateRequestDto requestDto, List<MultipartFile> reviewImages) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));
        List<ReviewOption> reviewOptions = reviewOptionRepository.findAllByCategoryId(drink.getCategory().getId());

        Review review = requestDto.toEntity(member, drink);
        reviewRepository.save(review);
        log.info("reviewImages = {}", reviewImages.isEmpty());

        List<MultipartFile> images = reviewImages.stream()
                .filter(m -> !m.isEmpty())
                .collect(Collectors.toList());
        log.info("images = {}", images.isEmpty());
        log.info("images = {}", images.size());
        if(!images.isEmpty())
            uploadReviewImages(reviewImages, review);

        List<SelectedOptionCreateRequestDto> selectedOptionCreateRequestDtos = requestDto.getSelectedOptions().stream()
                .collect(Collectors.toList());

        List<SelectedOption> selectedOptions = new ArrayList<>();

        for (SelectedOptionCreateRequestDto dto : selectedOptionCreateRequestDtos) {
            for(ReviewOption reviewOption : reviewOptions) {
                if (dto.getReviewOptionId() == reviewOption.getId()) {
                    selectedOptions.add(dto.toEntity(review, reviewOption));
                }
            }
        }

        log.info("ReviewOptionList size = {}", reviewOptions.size());
        log.info("SelectedOptionCreateRequestDtoList size= {}", selectedOptionCreateRequestDtos.size());
        log.info("SelectedOptionList size = {}", selectedOptions.size());

        if(selectedOptionCreateRequestDtos.size() != selectedOptions.size())
            throw new ReviewException(UNMATCHED_OPTION_SIZE);

        selectedOptionRepository.saveAll(selectedOptions);

        log.info("Review created !! memberId={}, drinkId={}", memberId, drinkId);
        return ReviewResponseDto.of(review);
    }

    // 리뷰이미지를 S3에 저장하고 리뷰이미지 엔티티 생성
    private List<ReviewImage> uploadReviewImages(List<MultipartFile> reviewImages, Review review) {

        List<String> uploadNames = reviewImages.stream()
                .map(reviewImage -> reviewImage.getOriginalFilename().toUpperCase())
                .collect(Collectors.toList());
        List<String> urls = reviewImages.stream()
                .map(reviewImage -> awsS3Uploader.upload(reviewImage, "review"))
                .collect(Collectors.toList());
        List<ReviewImage> images = new ArrayList<>();
        for(int i=0; i< urls.size(); i++) {
            ReviewImage image = createPostImage(review, urls.get(i), uploadNames.get(i));
            images.add(image);
        }
        return  images;
    }

    // 리뷰이미지를 S3에 업로드하는 로직
    private ReviewImage createPostImage(Review review, String url, String uploadName) {
        log.info("url = {}, uploadName = {}", url, uploadName);
        review.clearImages();

        return reviewImageRepository.save(ReviewImage.builder()
                .imageUrl(url)
                .uploadName(uploadName)
                .review(review)
                .build());
    }

    // 리뷰 상세 보기
    public ReviewDetailReadResponseDto readReviewDetail(long memberId, long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NONE_REVIEW));

        // 멤버 프로필 image 조회
        String profileImageUrl = memberService.getProfileImageUrl(review.getMember());

        // 멤버의 리뷰에 대한 따봉 유무 조회
        Boolean isTabomed;
        String tabomed;
        if(memberId == 0) {
            isTabomed = null;
            tabomed = "null";
        }
        else {
            isTabomed = tabomRepository.existsByMemberIdAndReviewIdAndIsUp(memberId, reviewId, true);
            tabomed = isTabomed.toString();
        }

        log.info("좋아요 관련 쿼리 시작");
        long upCount = tabomRepository.findCountByIsUPAndReviewId(review.getId()).orElseThrow();
        long downCount = tabomRepository.findCountByIsDownAndReviewId(review.getId()).orElseThrow();
        return new ReviewDetailReadResponseDto(review, profileImageUrl, upCount, downCount, tabomed);
    }

    @Transactional(readOnly = true)
    public SliceResponse<ReviewReadResponseDto> findAllReviewByMemberIdByPage(long memberId, Pageable pageable) {

        Slice<Review> allReviewsSliceBy = reviewRepository.findAllReviewsSliceByMemberIdByCreatedAtDesc(memberId, pageable);
        List<Review> reviews = allReviewsSliceBy.getContent();
        List<String> tabomed = new ArrayList<>();
        List<Boolean> isExist = reviews.stream()
                .map(r -> tabomRepository.existsByMemberIdAndReviewId(memberId, r.getId()))
                .collect(Collectors.toList());
        List<Boolean> isTabomed = reviews.stream()
                .map(r -> tabomRepository.existsByMemberIdAndReviewIdAndIsUp(memberId, r.getId(), true))
                .collect(Collectors.toList());
        for(int i=0; i<reviews.size(); i++) {
            log.info("isExist = {}", isExist.get(i));
            log.info("isTabomed = {}", isTabomed.get(i));
            if(isExist.get(i) && isTabomed.get(i))
                tabomed.add("true");
            else if(!(isExist.get(i)))
                tabomed.add("null");
            else
                tabomed.add("false");
        }


        log.info("좋아요 관련 쿼리 시작");
        List<Long> upCounts = reviews.stream()
                .map(r -> tabomRepository.findCountByIsUPAndReviewId(r.getId()).orElseThrow())
                .collect(Collectors.toList());
        log.info("싫어요 관련 쿼리 시작");
        List<Long> downCounts = reviews.stream()
                .map(r -> tabomRepository.findCountByIsDownAndReviewId(r.getId()).orElseThrow())
                .collect(Collectors.toList());

        // drink preview 관련
        List<DrinkPreview> drinkPreviews = reviews.stream()
                .map(Review::getDrink)
                .map(d -> new DrinkPreviewBuilder()
                        .setDrink(d)
                        .buildResponse())
                .collect(Collectors.toList());

        List<ReviewReadResponseDto> responseDtos = new ArrayList<>();
        for(int i=0; i<reviews.size(); i++) {
            String profileImageUrl = memberService.getProfileImageUrl(reviews.get(i).getMember());
            responseDtos.add(new ReviewReadResponseDto(reviews.get(i), profileImageUrl, upCounts.get(i), downCounts.get(i), tabomed.get(i), drinkPreviews.get(i)));
        }

        return new SliceResponse<>(responseDtos, allReviewsSliceBy.getPageable().getPageNumber(), allReviewsSliceBy.isLast());

    }

    @Transactional(readOnly = true)
    public SliceResponse<ReviewReadResponseDto> findReviewByPage(long memberId, Pageable pageable) {

        Slice<Review> allReviewsSliceBy = reviewRepository.findAllReviewsSliceByCreatedAtDesc(pageable);
        List<Review> reviews = allReviewsSliceBy.getContent();
        List<String> tabomed = new ArrayList<>();
        List<Boolean> isExist = reviews.stream()
                .map(r -> tabomRepository.existsByMemberIdAndReviewId(memberId, r.getId()))
                .collect(Collectors.toList());
        List<Boolean> isTabomed = reviews.stream()
                .map(r -> tabomRepository.existsByMemberIdAndReviewIdAndIsUp(memberId, r.getId(), true))
                .collect(Collectors.toList());
        for(int i=0; i<reviews.size(); i++) {
            log.info("isExist = {}", isExist.get(i));
            log.info("isTabomed = {}", isTabomed.get(i));
            if(isExist.get(i) && isTabomed.get(i))
                tabomed.add("true");
            else if(!(isExist.get(i)))
                tabomed.add("null");
            else
                tabomed.add("false");
        }

        log.info("좋아요 관련 쿼리 시작");
        List<Long> upCounts = reviews.stream()
                .map(r -> tabomRepository.findCountByIsUPAndReviewId(r.getId()).orElseThrow())
                .collect(Collectors.toList());
        log.info("싫어요 관련 쿼리 시작");
        List<Long> downCounts = reviews.stream()
                .map(r -> tabomRepository.findCountByIsDownAndReviewId(r.getId()).orElseThrow())
                .collect(Collectors.toList());

        // drink preview 관련
        List<DrinkPreview> drinkPreviews = reviews.stream()
                .map(Review::getDrink)
                .map(d -> new DrinkPreviewBuilder()
                        .setDrink(d)
                        .buildResponse())
                .collect(Collectors.toList());

        List<ReviewReadResponseDto> responseDtos = new ArrayList<>();
        for(int i=0; i<reviews.size(); i++) {
            String profileImageUrl = memberService.getProfileImageUrl(reviews.get(i).getMember());
            responseDtos.add(new ReviewReadResponseDto(reviews.get(i), profileImageUrl, upCounts.get(i), downCounts.get(i), tabomed.get(i), drinkPreviews.get(i)));
        }

        return new SliceResponse<>(responseDtos, allReviewsSliceBy.getPageable().getPageNumber(), allReviewsSliceBy.isLast());
    }

    @Transactional(readOnly = true)
    public SliceResponse<ReviewReadResponseDto> findAllReviewByDrinkIdByPage(long drinkId, long memberId, Pageable pageable) {

        Slice<Review> allReviewsSliceBy = reviewRepository.findAllReviewsSliceByDrinkIdByCreatedAtDesc(drinkId, pageable);
        List<Review> reviews = allReviewsSliceBy.getContent();
        List<String> tabomed = new ArrayList<>();
        List<Boolean> isExist = reviews.stream()
                .map(r -> tabomRepository.existsByMemberIdAndReviewId(memberId, r.getId()))
                .collect(Collectors.toList());
        List<Boolean> isTabomed = reviews.stream()
                .map(r -> tabomRepository.existsByMemberIdAndReviewIdAndIsUp(memberId, r.getId(), true))
                .collect(Collectors.toList());
        for(int i=0; i<reviews.size(); i++) {
            log.info("isExist = {}", isExist.get(i));
            log.info("isTabomed = {}", isTabomed.get(i));
            if(isExist.get(i) && isTabomed.get(i))
                tabomed.add("true");
            else if(!(isExist.get(i)))
                tabomed.add("null");
            else
                tabomed.add("false");
        }

        log.info("좋아요 관련 쿼리 시작");
        List<Long> upCounts = reviews.stream()
                .map(r -> tabomRepository.findCountByIsUPAndReviewId(r.getId()).orElseThrow())
                .collect(Collectors.toList());
        log.info("싫어요 관련 쿼리 시작");
        List<Long> downCounts = reviews.stream()
                .map(r -> tabomRepository.findCountByIsDownAndReviewId(r.getId()).orElseThrow())
                .collect(Collectors.toList());

        // drink preview 관련
        List<DrinkPreview> drinkPreviews = reviews.stream()
                .map(Review::getDrink)
                .map(d -> new DrinkPreviewBuilder()
                        .setDrink(d)
                        .buildResponse())
                .collect(Collectors.toList());

        List<ReviewReadResponseDto> responseDtos = new ArrayList<>();
        for(int i=0; i<reviews.size(); i++) {
            String profileImageUrl = memberService.getProfileImageUrl(reviews.get(i).getMember());
            responseDtos.add(new ReviewReadResponseDto(reviews.get(i), profileImageUrl, upCounts.get(i), downCounts.get(i), tabomed.get(i), drinkPreviews.get(i)));
        }

        return new SliceResponse<>(responseDtos, allReviewsSliceBy.getPageable().getPageNumber(), allReviewsSliceBy.isLast());
    }

    public ReviewDeleteResponseDto deleteReview(Long reviewId, Long memberId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NONE_REVIEW));
        if(review.getMember().getId() != memberId)
            throw new ReviewException(INVALID_MEMBER_FOR_DELETE_REVIEW);
        review.changeStatus();

        return ReviewDeleteResponseDto.builder()
                .id(reviewId)
                .build();
    }
}
