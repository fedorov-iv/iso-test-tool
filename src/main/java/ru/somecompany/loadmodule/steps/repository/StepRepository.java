package ru.somecompany.loadmodule.steps.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.steps.models.Step;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface StepRepository extends CrudRepository<Step, Long> {
    @Query(getAllStepsByPage)
    List<Step> getAllStepsByPage(Pageable pageable);
    String getAllStepsByPage = "from Step order by id";
}
