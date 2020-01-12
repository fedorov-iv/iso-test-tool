package ru.somecompany.loadmodule.terminals.models;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;

@Data
@Entity
@Indexed
@Table(name = "terminals")
public class Terminal {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @Field(store = Store.NO)
    private String name;

    @Field(store = Store.NO)
    private String description;

    private String terminalType;
    private Long terminalId;
    private Long merchantId;
    private String mcc;
    private String tpk;

}
