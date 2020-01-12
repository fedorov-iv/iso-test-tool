package ru.somecompany.loadmodule.auth.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EditUserForm {

    private Long id;

    private boolean enabled;

    private long [] roles;
}
