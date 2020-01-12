package ru.somecompany.loadmodule.auth.models;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Entity
@Indexed
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @Field(store = Store.NO)
    @NotEmpty(message = "*Please provide your username")
    private String username;

//    @NotEmpty(message = "*Please provide your password")
    private String password;

    private boolean enabled;


    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(
                "User[id=%s, username='%s', password='*********', enabled='%s']", id, username, enabled));
        if (roles != null) {
            for(Role role : roles) {
                builder.append(String.format(
                        "Role[id=%d, rolename='%s']",
                        role.getId(), role.getRolename()));
            }
        }

        return builder.toString();
    }

}
