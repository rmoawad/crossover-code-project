package com.company.resourceapi.services;


import com.company.resourceapi.entities.Project;

public interface ProjectService {

    Project getProject(long id);

    Project createProject(Project project);
}
