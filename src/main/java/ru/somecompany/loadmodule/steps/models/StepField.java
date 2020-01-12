package ru.somecompany.loadmodule.steps.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Table(name="step_fields")
public class StepField {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty(message = "*Please provide field identifier")
    private String name;


    @Column(length = 1024)
    private String value;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "step_id")
    private Step step;

}
