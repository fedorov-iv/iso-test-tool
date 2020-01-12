package ru.somecompany.loadmodule.steps.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StepForm {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private String type;


    public String toString() {
        return "StepForm (Step name: " + this.name + ", Description: "+ this.description +")";
    }
}
