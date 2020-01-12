package ru.somecompany.loadmodule.scenarios.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ScenarioForm {
    private Long id;

    @NotNull
    private String name;

    private String description;

    private Long channelId;

    private Long cardId;

    private Long terminalId;


    public String toString() {
        return "ScenarioForm (Scenario name: " + this.name + ", Description: "+ this.description +")";
    }


}
