package ru.somecompany.loadmodule.terminals.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.terminals.models.Terminal;

import java.util.List;

@Repository
public interface TerminalsRepository extends CrudRepository<Terminal, Long> {

    @Query(getAllTerminalsByPage)
    List<Terminal> getAllTerminalsByPage(Pageable pageable);
    String getAllTerminalsByPage = "from Terminal order by id";
}
