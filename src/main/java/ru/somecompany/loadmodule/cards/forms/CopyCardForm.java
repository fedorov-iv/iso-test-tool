package ru.somecompany.loadmodule.cards.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CopyCardForm {
    private Long cardId;

    @NotNull
    private String name;

    public String toString() {
        return "CopyCardForm (Card id: " + this.cardId + ", name: "+ this.name +")";
    }
}
