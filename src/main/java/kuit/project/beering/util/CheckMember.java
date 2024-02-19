package kuit.project.beering.util;

import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.util.exception.domain.DomainException;

import java.util.Objects;
import java.util.function.Function;

import static kuit.project.beering.util.BaseResponseStatus.TOKEN_PATH_MISMATCH;

public class CheckMember {
    public static void validateMember(AuthMember member, Long memberId, Function<BaseResponseStatus, DomainException> exceptionSupplier) {
        // TODO : member.isGuest(), member.isMember()
        if (!Objects.equals(member.getId(), memberId))
            throw exceptionSupplier.apply(TOKEN_PATH_MISMATCH);
    }

    public static Long getMemberId(AuthMember member) {
        // TODO : member.isGuest()
        // if(member.isGuest()) return 0L;
        if(member == null) return 0L;
        else return member.getId();
    }
}
