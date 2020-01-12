package ru.somecompany.loadmodule.projects.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProjectFieldForm {

    private Long id;

    @NotNull
    private String name;

    private String value;

    @NotNull
    private Long projectId;


    public String toString() {
        return "ProjectFieldForm (Project name: " + this.name + ", value: "+ this.value +")";
    }
}
