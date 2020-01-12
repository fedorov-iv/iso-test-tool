package ru.somecompany.loadmodule.scenarios.models;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import ru.somecompany.loadmodule.parameters.models.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Indexed
@Table(name = "scenarios")
public class Scenario {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @Field(store = Store.NO)
    @NotEmpty(message = "*Please provide scenario name")
    private String name;

    @Field(store = Store.NO)
    private String description;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "terminal_id")
    private Long terminalId;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(name = "scenarios_parameters", joinColumns = @JoinColumn(name = "scenario_id"), inverseJoinColumns = @JoinColumn(name = "parameter_id"))
    private Set<Parameter> parameters = new HashSet<>();

    public void addParameter(Parameter parameter){
        this.parameters.add(parameter);
    }

    public void deleteParameter(Parameter parameter){
        this.parameters.remove(parameter);
    }


   /* @OneToMany(mappedBy = "scenario")
    private Set<ScenarioStep> scenarioSteps = new HashSet<>();*/


    @Override
    public String toString() {
        return String.format("Scenario [id=%s, name='%s']", id, name);
    }
}
