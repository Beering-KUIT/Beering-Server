package kuit.project.beering.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("beer")
public class Beer extends Drink{
}
