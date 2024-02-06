package kuit.project.beering.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    // 1100 : 회원가입 마저 진행
    SUCCESS_CONTINUE_SIGNUP(true, 1100, "회원가입을 마저 진행해주세요."),

    // 1200 : favorite 요청 성공 (주류 찜)
    SUCCESS_DELETE_FAVORITE(true, 1200, "찜을 취소하였습니다."),
    SUCCESS_ADD_FAVORITE(true, 1201, "찜하였습니다."),

    // 1300 : tabom 요청 성공 (리뷰 따봉)
    SUCCESS_DELETE_TABOM(true, 1300, "좋아요 / 싫어요를 취소하였습니다."),
    SUCCESS_CHANGE_TABOM(true, 1301, "좋아요 / 싫어요로 변경하였습니다."),
    SUCCESS_ADD_TABOM(true, 1302, "좋아요 / 싫어요를 추가하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // 입력값 예외
    INVALID_REQUEST(false, 2000, "잘못된 요청이 존재합니다."),

    // JWT 예외 - Filter에서 처리
    ACCESS_DENIED(false, 2001, "권한이 없는 유저의 접근입니다."),
    EMPTY_AUTHORIZATION_HEADER(false, 2002, "Authorization 헤더가 존재하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(false, 2003, "이미 만료된 Access 토큰입니다."),
    UNSUPPORTED_TOKEN_TYPE(false, 2004, "지원되지 않는 토큰 형식입니다."),
    MALFORMED_TOKEN_TYPE(false, 2005, "인증 토큰이 올바르게 구성되지 않았습니다."),
    INVALID_SIGNATURE_JWT(false, 2006, "인증 시그니처가 올바르지 않습니다"),
    INVALID_TOKEN_TYPE(false, 2007, "잘못된 토큰입니다."),

    // Refresh Token 예외 - Exception Handler에서 처리
    EXPIRED_REFRESH_TOKEN(false, 2008, "Refresh 토큰이 만료되어 재로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(false, 2009, "잘못된 Refresh 토큰입니다."),

    // IdToken 예외
    EXPIRED_ID_TOKEN(false, 2020, "이미 만료된 ID 토큰입니다."),

    // UserException
    INVALID_FIELD(false, 2010, "요청 값이 잘못되었습니다."),
    DUPLICATED_EMAIL(false, 2011, "중복된 이메일입니다."),
    DUPLICATED_NICKNAME(false, 2012, "중복된 닉네임입니다."),
    INVALID_CHECKED_PASSWORD(false, 2013, "비밀번호 확인 값이 다릅니다."),
    INVALID_EMAIL_OR_PASSWORD(false, 2014, "이메일 혹은 비밀번호가 잘못되었습니다."),
    NONE_MEMBER(false, 2015, "존재하지 않는 회원입니다."),
    NONE_OAUTH_PROVIDER(false, 2016, "존재하지 않는 소셜로그인 타입입니다."),
    OAUTH_LOGIN_FAILED(false, 2017, "로그인이 취소되었습니다."),

    // 2400 : ReviewException
    POST_REVIEW_EMPTY_USER(false, 2400, "해당 사용자가 존재하지 않습니다."),
    POST_REVIEW_EMPTY_SCORE(false, 2402, "score를 입력해주세요."),
    POST_REVIEW_ALREADY_CREATED(false, 2403, "이미 리뷰를 작성하였습니다."),
    INVALID_REVIEW(false, 2404, "유효하지 않은 리뷰입니다."),
    NONE_REVIEW(false, 2405, "존재하지 않는 리뷰입니다."),
    UNMATCHED_OPTION_SIZE(false, 2406, "리뷰 옵션의 개수가 올바르지 않습니다."),
    INVALID_MEMBER_FOR_DELETE_REVIEW(false, 2407, "리뷰를 삭제할 권한이 없는 멤버입니다."),

    // RequestParam exception
    EMPTY_REQUEST_PARAMETER(false, 2098, "Request Parameter가 존재하지 않습니다."),
    METHOD_ARGUMENT_TYPE_MISMATCH(false, 2099, "Request Parameter나 Path Variable의 유형이 불일치합니다."),

    // 2100 : DrinkException
    NONE_DRINK(false, 2100, "해당 주류가 존재하지 않습니다."),
    INVALID_ORDER(false, 2101, "유효하지 않은 정렬방식 입니다."),
    INVALID_SUB_OPTION(false, 2102, "유효하지 않은 하위 옵션(들)입니다."),
    UNSUPPORTED_SUB_OPTION(false, 2103, "하위 옵션을 제공하지 않습니다."),


    // 2200 : FavoriteException
    TOKEN_PATH_MISMATCH(false, 2200, "토큰 정보와 pathVariable로 받은 Member 정보가 다릅니다."),
    FAIL_CREATE_FAVORITE(false, 2201, "찜에 실패했습니다."),
    POST_FAVORITE_ALREADY_CREATED(false, 2202, "이미 찜하였습니다."),


    // 2300 : TabomException
    POST_TABOM_ALREADY_CREATED(false, 2300, "이미 좋아요나 싫어요가 존재합니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    /**
     * 5000 : AWS 오류
     */
    // AWS S3
    POST_IMAGE_CONVERT_ERROR(false, 5000, "사진이 없거나 변환되지 않았습니다."),
    POST_IMAGE_INVALID_EXTENSION(false, 5001, "올바른 확장자가 아닙니다."),
    IMAGE_CONVERT_ERROR(false, 5002, "사진이 없거나 변환되지 않았습니다."),
    IMAGE_INVALID_EXTENSION(false, 5003, "올바른 확장자가 아닙니다."),

    /**
     * 6000 : 보안 이슈
     */
    // 보안(리프레시 토큰 탈취) 이슈
    USING_STOLEN_TOKEN(false, 6000, "토큰이 탈취되었습니다.");

    private final boolean isSuccess;
    private final int responseCode;
    private final String responseMessage;
}
