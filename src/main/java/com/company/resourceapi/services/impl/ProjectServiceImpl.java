package com.company.resourceapi.services.impl;

import com.company.resourceapi.entities.Project;
import com.company.resourceapi.entities.SdlcSystem;
import com.company.resourceapi.exceptions.GeneralException;
import com.company.resourceapi.exceptions.NotFoundException;
import com.company.resourceapi.repositories.ProjectRepository;
import com.company.resourceapi.repositories.SdlcSystemRepository;
import com.company.resourceapi.services.ProjectService;

import lombok.RequiredArgsConstructor;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final SdlcSystemRepository sdlcSystemRepository;
  private final Validator validator;

  public Project getProject(long id) {
    return projectRepository.findById(id).orElseThrow(() -> new NotFoundException(Project.class, id));
  }

  @Override
  public Project createProject(Project project) {
    validateOnCreate(project);
    injectSystem(project);
    return projectRepository.save(project);
  }

  private void validateOnCreate(Project project) {
    if (project.getId() != 0) {
      throw new GeneralException("New project must have no ID");
    }
    validateAnnotations(project);
    validateUniqueConstrain(project);
  }

  private void validateAnnotations(Project project) {
    Set<ConstraintViolation<Project>> violations = validator.validate(project);
    if (!violations.isEmpty()) {
      ConstraintViolation<Project> violation = violations.iterator().next();
      // Needs enhancement
      throw new GeneralException(violation.getPropertyPath() + " " + violation.getMessage());
    }
  }

  private void validateUniqueConstrain(Project project) {
    if (projectRepository.findBySdlcSystemIdAndExternalId(project.getSdlcSystem().getId(), project.getExternalId())
        .isPresent()) {
      throw new GeneralException("The external id already exist and linked to same SdlcSystem");
    }
  }

  private void injectSystem(Project project) {
    SdlcSystem sdlcSystem = sdlcSystemRepository.findById(project.getSdlcSystem().getId())
        .orElseThrow(() -> new NotFoundException(SdlcSystem.class, project.getSdlcSystem().getId()));
    project.setSdlcSystem(sdlcSystem);

  }
}
