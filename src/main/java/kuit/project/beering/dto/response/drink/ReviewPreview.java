package kuit.project.beering.dto.response.drink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ReviewPreview {
    String profile_image_url;
    String nickname;
    String content;
    LocalDateTime createdAt;
}
