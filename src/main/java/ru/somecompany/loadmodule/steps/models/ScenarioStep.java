package ru.somecompany.loadmodule.steps.models;


import lombok.Data;
import ru.somecompany.loadmodule.scenarios.models.Scenario;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "scenarios_steps")
public class ScenarioStep {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long scenarioStepId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scenario_id")
    @NotNull
    private Scenario scenario;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "step_id")
    @NotNull
    private Step step;


    private int sort;

}
