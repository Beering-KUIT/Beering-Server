package kuit.project.beering.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("wine")
public class Wine extends Drink{
    private int sweetness;
    private String country;
}