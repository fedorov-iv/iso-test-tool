package ru.somecompany.loadmodule.auth.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LogonForm {

    @NotNull
    private String username;

    @NotNull
    private String password;


    public String toString() {
        return "LogonForm (Username: " + this.username + ", Password:  **********)";
    }
}

