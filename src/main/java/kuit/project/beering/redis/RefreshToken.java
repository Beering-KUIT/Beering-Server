package kuit.project.beering.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String memberId;
    private String refreshToken;

}
