package com.company.resourceapi.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.company.resourceapi.entities.Project;
import com.company.resourceapi.entities.SdlcSystem;
import com.company.resourceapi.exceptions.GeneralException;
import com.company.resourceapi.exceptions.NotFoundException;
import com.company.resourceapi.repositories.ProjectRepository;
import com.company.resourceapi.repositories.SdlcSystemRepository;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplTest {

  private static final long ID_1 = 1L;
  private static final long ID_10 = 10L;
  private static final String validExId = "validExId";
  private static final Project createdProject = new Project();

  @InjectMocks
  private ProjectServiceImpl projectServiceImpl;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private SdlcSystemRepository sdlcSystemRepository;

  @Mock
  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);

  @Before
  public void setUp() {
    createdProject.setId(ID_1);
    when(projectRepository.save(any(Project.class))).thenReturn(createdProject);
    when(sdlcSystemRepository.findById(eq(ID_10))).thenReturn(Optional.ofNullable(new SdlcSystem()));
  }

  @Test
  public void shouldCreateProject_whenDataHasNoIssue() {
    // Arrange
    Project project = getProject();

    // Act
    Project createdProject = projectServiceImpl.createProject(project);

    // Assert
    verify(projectRepository).save(projectCaptor.capture());
    SoftAssertions assertions = new SoftAssertions();
    assertions.assertThat(createdProject.getId()).isEqualTo(ID_1);
    assertions.assertThat(projectCaptor.getValue()).isEqualTo(project);
    assertions.assertAll();
  }

  @Test
  public void shouldThrowNotFoundException_whenSdlcSystemNotExist() {
    // Arrange
    Project project = getProject();
    project.getSdlcSystem().setId(123);

    // Act && Assert
    assertThrows(NotFoundException.class, () -> projectServiceImpl.createProject(project));
  }

  @Test
  public void shouldThrowNotBadException_whenNewProjectHasId() {
    // Arrange
    Project project = getProject();
    project.setId(ID_1);

    // Act && Assert
    assertThrows(GeneralException.class, () -> projectServiceImpl.createProject(project));
  }

  @Test
  public void shouldThrowNotBadException_whenUniqueKeyGetAffectted() {
    // Arrange
    Project project = getProject();
    when(projectRepository.findBySdlcSystemIdAndExternalId(eq(ID_10), eq(validExId)))
        .thenReturn(Optional.ofNullable(new Project()));

    // Act && Assert
    assertThrows(GeneralException.class, () -> projectServiceImpl.createProject(project));
  }

  private Project getProject() {
    SdlcSystem sdlcSystem = SdlcSystem.builder().id(ID_10).build();
    return Project.builder().externalId(validExId).sdlcSystem(sdlcSystem).build();
  }
}
