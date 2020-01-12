package ru.somecompany.loadmodule.scenarios.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CopyScenarioForm {

    private Long scenarioId;

    @NotNull
    private String name;

    public String toString() {
        return "CopyScenarioForm (Scenario id: " + this.scenarioId + ", name: "+ this.name +")";
    }
}
