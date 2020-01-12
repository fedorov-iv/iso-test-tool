package ru.somecompany.loadmodule.scenarios.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.scenarios.models.Scenario;

import java.util.List;

@Repository
public interface ScenarioRepository extends CrudRepository<Scenario, Long>, JpaSpecificationExecutor<Scenario> {

    @Query(getAllScenariosByPage)
    List<Scenario> getAllScenariosByPage(Pageable pageable);
    final String getAllScenariosByPage = "from Scenario order by id";


    
}
