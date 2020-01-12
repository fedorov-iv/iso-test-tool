package ru.somecompany.loadmodule.projects.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.projects.models.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @Query(getAllProjectsByPage)
    List<Project> getAllProjectsByPage(Pageable pageable);
    final String getAllProjectsByPage = "from Project order by id";


}
