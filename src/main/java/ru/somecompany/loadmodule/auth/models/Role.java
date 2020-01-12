package ru.somecompany.loadmodule.auth.models;


import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    private String rolename;

    @Override
    public String toString() {
//        return String.format(
////                "Role[rolename='%s']", rolename);
        return rolename;
    }
}
