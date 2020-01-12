package ru.somecompany.loadmodule.projects.models;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Indexed
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @Field(store = Store.NO)
    @NotEmpty(message = "*Please provide project name")
    private String name;

    @Field(store = Store.NO)
    private String description;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "terminal_id")
    private Long terminalId;

    @Override
    public String toString() {
        return String.format("Project [id=%s, name='%s']", id, name);
    }
}
