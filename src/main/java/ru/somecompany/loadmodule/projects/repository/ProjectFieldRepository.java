package ru.somecompany.loadmodule.projects.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.projects.models.Project;
import ru.somecompany.loadmodule.projects.models.ProjectField;

import java.util.List;

@Repository
public interface ProjectFieldRepository extends CrudRepository<ProjectField, Long> {

    List<ProjectField> findByProject(Project project);
}
