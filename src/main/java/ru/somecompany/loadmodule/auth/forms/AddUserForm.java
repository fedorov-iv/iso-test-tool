package ru.somecompany.loadmodule.auth.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddUserForm {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String passwordRepeat;

    private boolean enabled;

    private long [] roles;

}
