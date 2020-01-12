package ru.somecompany.loadmodule.auth.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.auth.models.User;

import java.util.List;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Query(getAllUsersByPage)
    List<User> getAllUsersByPage(Pageable pageable);
    final String getAllUsersByPage = "from User order by id";
}
