package ru.somecompany.loadmodule.cards.models;


import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Indexed
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Field(store = Store.NO)
    @NotNull
    private String name;

    @Field(store = Store.NO)
    private String description;

    @NotNull
    private String pan;

    @NotNull
    @Column(name="expiry_date")
    private String expiryDate;

    private String cvv;
    private String pin;
    private String track2;

}
