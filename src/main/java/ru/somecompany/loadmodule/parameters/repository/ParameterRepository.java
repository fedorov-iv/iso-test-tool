package ru.somecompany.loadmodule.parameters.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.parameters.models.Parameter;

import java.util.List;

@Repository
public interface ParameterRepository extends CrudRepository<Parameter, Long>, JpaSpecificationExecutor<Parameter> {
    @Query(getAllParametersByPage)
    List<Parameter> getAllParametersByPage(Pageable pageable);
    String getAllParametersByPage = "from Parameter order by id";
}
