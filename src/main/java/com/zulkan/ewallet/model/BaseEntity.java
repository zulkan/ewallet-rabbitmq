package com.zulkan.ewallet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@ToString
@Getter
@Setter
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    @JsonIgnore
    private String createdBy;

    @Column(name = "updated_by")
    @JsonIgnore
    private String updatedBy;

}
