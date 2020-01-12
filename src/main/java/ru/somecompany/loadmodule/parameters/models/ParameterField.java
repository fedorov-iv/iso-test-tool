package ru.somecompany.loadmodule.parameters.models;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Table(name = "parameter_fields")
public class ParameterField {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty(message = "*Please provide parameter field identifier")
    private String name;

    private String value;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "parameter_id")
    private Parameter parameter;

    @Override
    public String toString() {
        return String.format("ParameterField [id=%s, name='%s', value='%s']", id, name, value);
    }
}
