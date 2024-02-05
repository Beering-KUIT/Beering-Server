package kuit.project.beering.domain;

public enum Role {

    GUEST("GUEST"),
    MEMBER("MEMBER"),
    ADMIN("ADMIN");

    Role(String value) {
        this.value = value;
        this.role = PREFIX + value;
    }

    private final String PREFIX = "ROLE_";
    private final String value;
    private final String role;

    public String getValue() {
        return value;
    }

    public String getRole() {
        return role;
    }
}
