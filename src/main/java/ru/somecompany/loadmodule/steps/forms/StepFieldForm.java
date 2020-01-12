package ru.somecompany.loadmodule.steps.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StepFieldForm {

    private Long id;

    @NotNull
    private String name;

    private String value;

    @NotNull
    private Long stepId;
}
