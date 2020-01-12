package ru.somecompany.loadmodule.terminals.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TerminalForm {

    private Long id;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private String terminalType;
    @NotNull
    private Long terminalId;
    @NotNull
    private Long merchantId;
    @NotNull
    private String mcc;
    @NotNull
    private String tpk;
}
