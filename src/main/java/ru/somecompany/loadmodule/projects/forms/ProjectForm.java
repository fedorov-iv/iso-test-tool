package ru.somecompany.loadmodule.projects.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ProjectForm {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private Long channelId;

    private Long cardId;

    private Long terminalId;


    public String toString() {
        return "ProjectForm (Project name: " + this.name + ", Description: "+ this.description +")";
    }


}
