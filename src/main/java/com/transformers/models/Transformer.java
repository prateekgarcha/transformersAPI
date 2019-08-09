package com.transformers.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transformer {

    public enum TYPE {
	AUTOBOT, DECEPTICON
    }

    private @Id @GeneratedValue @Setter(AccessLevel.NONE) int id;
    private @Size(min = 1) @NotBlank String name;
    private @Min(1) @Max(10) int strength;
    private @Min(1) @Max(10) int intelligence;
    private @Min(1) @Max(10) int speed;
    private @Min(1) @Max(10) int endurance;
    private @Min(1) @Max(10) int rank;
    private @Min(1) @Max(10) int courage;
    private @Min(1) @Max(10) int firepower;
    private @Min(1) @Max(10) int skill;
    private TYPE type;

    public int getOverallRating() {
	return this.strength + this.intelligence + this.speed + this.endurance
		+ this.firepower;
    }
}
