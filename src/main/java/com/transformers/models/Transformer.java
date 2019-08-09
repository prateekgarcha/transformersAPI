package com.transformers.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
    private String name;
    private int strength;
    private int intelligence;
    private int speed;
    private int endurance;
    private int rank;
    private int courage;
    private int firepower;
    private int skill;
    private TYPE type;

    public int getOverallRating() {
	return this.strength + this.intelligence + this.speed + this.endurance
		+ this.firepower;
    }
}
