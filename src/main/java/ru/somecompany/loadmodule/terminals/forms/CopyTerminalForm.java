package ru.somecompany.loadmodule.terminals.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CopyTerminalForm {
    private Long terminalId;

    @NotNull
    private String name;

    public String toString() {
        return "CopyTerminalForm (Terminal id: " + this.terminalId + ", name: "+ this.name +")";
    }
}
