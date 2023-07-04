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

    // UserException
    DUPLICATED_EMAIL(false, 2010, "중복된 이메일입니다."),
    INVALID_CHECKED_PASSWORD(false, 2011, "비밀번호 확인 값이 다릅니다."),
    INVALID_EMAIL_OR_PASSWORD(false, 2012, "이메일 혹은 비밀번호가 잘못되었습니다."),
    NONE_USER(false, 2013, "존재하지 않는 회원입니다."),
    NONE_OAUTH_PROVIDER(false, 2014, "존재하지 않는 소셜로그인 타입입니다."),

    // ReviewException
    POST_REVIEW_EMPTY_USER(false, 2020, "해당 사용자가 존재하지 않습니다."),
    POST_REVIEW_EMPTY_RESERVATION(false, 2021, "해당 예약이 존재하지 않습니다."),
    POST_REVIEW_EMPTY_SCORE(false, 2022, "score를 입력해주세요."),
    POST_REVIEW_ALREADY_CREATED(false, 2023, "이미 리뷰를 작성하였습니다."),
    INVALID_REVIEW(false, 2024, "유효하지 않은 리뷰입니다."),

    // ReservationException
    GET_RESERVATION_EMPTY_USER(false, 2230, "해당 사용자가 존재하지 않습니다."),
    GET_RESERVATION_EMPTY_ROOM(false, 2231, "해당 숙소가 존재하지 않습니다."),
    POST_RESERVATION_INVALID_ROOM_AVAILABLE_STATUS(false, 2232, "해당 숙소가 예약 불가능한 상태입니다."),
    POST_RESERVATION_OVERCAPACITY_TOTAL_GUEST(false, 2233, "숙소이용 정원이 초과되었습니다."),
    POST_RESERVATION_OVERCAPACITY_TOTAL_PET(false, 2234, "숙소 동반 가능한 반려동물 수가 초과되었습니다."),
    POST_RESERVATION_INVALID_STARTDATE(false, 2235, "예약 불가능한 날짜입니다."),
    PATCH_RESERVATION_INVALID_RESERVATION_STATUS(false, 2236, "유효하지 않은 예약입니다."),
    PATCH_RESERVATION_INVALID_USER(false, 2237, "유효하지 않은 유저의 접근입니다."),
    NONE_RESERVATION(false, 2238, "존재하지 않는 예약입니다."),
    POST_RESERVATION_INVALID_ROOM_AVAILABLE_DATE(false, 2239, "유효하지 않은 예약 날짜입니다."),


    // RoomException
    NONE_ROOM(false, 2030, "존재하지 않는 숙소입니다."),

    // RequestParam exception
    EMPTY_REQUEST_PARAMETER(false, 2098, "Request Parameter가 존재하지 않습니다."),
    METHOD_ARGUMENT_TYPE_MISMATCH(false, 2099, "Request Parameter의 유형이 불일치합니다."),

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
    POST_IMAGE_INVALID_EXTENSION(false, 5001, "올바른 확장자가 아닙니다.");


    private final boolean isSuccess;
    private final int responseCode;
    private final String responseMessage;
}
