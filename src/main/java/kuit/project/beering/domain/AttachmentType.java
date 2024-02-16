package kuit.project.beering.domain;


public enum AttachmentType {

    REVIEW("review"),
    MEMBER("MEMBER"),
    DRINK("drink"),
    TEST("test");
    // 추후에 추가될 다른 업로드 타입들

    private final String type;

    AttachmentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static AttachmentType fromString(String text) {
        for (AttachmentType uploadType : AttachmentType.values()) {
            if (uploadType.type.equalsIgnoreCase(text)) {
                return uploadType;
            }
        }
        throw new IllegalArgumentException("Invalid uploadType: " + text);
    }
}
