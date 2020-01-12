package ru.somecompany.loadmodule.parameters.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;


@Data
public class ParameterFieldForm {

    private Long id;

    @NotNull
    private String name;

    private String value;

    @NotNull
    private Long parameterId;


    public String toString() {
        return "ParameterFieldForm (Field name: " + this.name + ", value: "+ this.value +")";
    }
}
