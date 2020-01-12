package ru.somecompany.loadmodule.parameters.models;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Data
@Entity
@Indexed
@Table(name = "parameters")
public class Parameter {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @Field(store = Store.NO)
    @NotEmpty(message = "*Please provide parameter name")
    private String name;

    @Field(store = Store.NO)
    @NotEmpty(message = "*Please provide parameter code")
    private String code;

    @Field(store = Store.NO)
    private String description;

    @Override
    public String toString() {
        return String.format("Parameter [id=%s, name='%s', code='%s']", id, name, code);
    }
}
