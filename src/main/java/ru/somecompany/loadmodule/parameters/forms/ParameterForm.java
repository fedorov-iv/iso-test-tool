package ru.somecompany.loadmodule.parameters.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ParameterForm {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String code;

    private String description;

    public String toString() {
        return "ParameterForm (Parameter name: " + this.name + ", Description: "+ this.description +")";
    }
}
