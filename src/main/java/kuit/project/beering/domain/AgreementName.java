package kuit.project.beering.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgreementName {
    SERVICE("SERVICE"), PERSONAL("PERSONAL"), MARKETING("MARKETING");

    private final String name;
}
