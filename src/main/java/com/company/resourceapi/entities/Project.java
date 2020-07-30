package com.company.resourceapi.entities;


import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project", uniqueConstraints = { @UniqueConstraint(columnNames = { "sdlc_system_id", "external_id" }) })
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "external_id", nullable = false)
  @NotBlank
  @Size(max = 255, message = "ExternalId must be max 255 characters long")
  private String externalId;

  @Column(name = "name")
  @Size(max = 255, message = "Name must be max 255 characters long")
  private String name;

  @ManyToOne
  @JoinColumn(name = "sdlc_system_id")
  @NotNull
  private SdlcSystem sdlcSystem;

  @Column(name = "created_date", nullable = false)
  @CreatedDate
  private Instant createdDate;

  @Column(name = "last_modified_date", nullable = false)
  @LastModifiedDate
  private Instant lastModifiedDate;

  @PrePersist
  private void onInsert() {
    this.createdDate = Instant.now();
    onUpdate();
  }

  @PreUpdate
  private void onUpdate() {
    this.lastModifiedDate = Instant.now();
  }
}
