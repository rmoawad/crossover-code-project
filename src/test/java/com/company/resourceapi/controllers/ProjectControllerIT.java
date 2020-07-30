package com.company.resourceapi.controllers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.company.resourceapi.entities.Project;
import com.company.resourceapi.exceptions.GeneralException;
import com.company.resourceapi.exceptions.GlobalExceptionHandler;
import com.company.resourceapi.exceptions.NotFoundException;
import com.company.resourceapi.services.ProjectService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ProjectControllerIT {

  private static final long ID_1 = 1;
  
  private MockMvc mockMvc;

  @Mock
  private ProjectService projectService;

  @InjectMocks
  private ProjectRestController projectRestController;

  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(projectRestController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
    
  }

  @Test
  public void returnCreatedProject_whenCreateSucceess() throws Exception {
    // Arrange
    Project project = new Project();
    project.setId(ID_1);
    when(projectService.createProject(any(Project.class))).thenReturn(project);

    // Act & Assert
    mockMvc.perform(post("/api/v2/projects").contentType(MediaType.APPLICATION_JSON).content("{}"))
    .andExpect(status().isCreated())
    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    .andExpect(jsonPath("$..id").value(1));
  }


  @Test
  public void returnException_whenServiceThrowsNotFoundException() throws Exception {
    // Arrange
    Project project = new Project();
    project.setId(ID_1);
    when(projectService.createProject(any(Project.class))).thenThrow(new NotFoundException("resource not found"));

    // Act & Assert
    mockMvc.perform(post("/api/v2/projects").contentType(MediaType.APPLICATION_JSON).content("{}"))
    .andExpect(status().isNotFound())
    .andExpect(result -> assertThat(result.getResolvedException() instanceof NotFoundException).isTrue())
    .andExpect(result -> assertThat("resource not found").isEqualTo(result.getResolvedException().getMessage()));
  }


  @Test
  public void returnException_whenServiceThrowsBadException() throws Exception {
    // Arrange
    Project project = new Project();
    project.setId(ID_1);
    when(projectService.createProject(any(Project.class))).thenThrow(new GeneralException("Bad Request"));

    // Act & Assert
    mockMvc.perform(post("/api/v2/projects").contentType(MediaType.APPLICATION_JSON).content("{}"))
    .andExpect(status().isBadRequest())
    .andExpect(result -> assertThat(result.getResolvedException() instanceof GeneralException).isTrue())
    .andExpect(result -> assertThat("Bad Request").isEqualTo(result.getResolvedException().getMessage()));
  }

}
