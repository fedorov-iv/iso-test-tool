package ru.somecompany.loadmodule.cards.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CardForm {
    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private String pan;

    @NotNull
    private String expiryDate;

    private String cvv;

    private String pin;

    private String track2;

}
