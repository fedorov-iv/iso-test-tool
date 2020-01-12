package ru.somecompany.loadmodule.steps.models;


import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Indexed
@Table(name = "steps")
public class Step {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @Field(store = Store.NO)
    @NotEmpty(message = "*Please provide step name")
    private String name;

    @Field(store = Store.NO)
    private String description;


    private String type;

   /* @OneToMany(mappedBy = "step")
    private Set<ScenarioStep> scenarioSteps = new HashSet<>();*/



}
