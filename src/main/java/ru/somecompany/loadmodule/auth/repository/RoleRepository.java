package ru.somecompany.loadmodule.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.auth.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRolename(String rolename);
}
