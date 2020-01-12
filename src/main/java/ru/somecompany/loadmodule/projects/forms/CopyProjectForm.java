package ru.somecompany.loadmodule.projects.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CopyProjectForm {

    private Long projectId;

    @NotNull
    private String name;

    public String toString() {
        return "CopyProjectForm (Project id: " + this.projectId + ", name: "+ this.name +")";
    }
}
