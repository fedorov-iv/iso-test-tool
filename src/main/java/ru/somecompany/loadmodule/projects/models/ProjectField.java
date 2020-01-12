package ru.somecompany.loadmodule.projects.models;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Table(name = "project_fields")
public class ProjectField {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty(message = "*Please provide field identifier")
    private String name;

    private String description;

    private String value;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "project_id")
    private Project project;

    @Override
    public String toString() {
        return String.format("ProjectField [id=%s, name='%s', value='%s']", id, name, value);
    }


}
