package com.example.poller_bear.model.Audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
@JsonIgnoreProperties(
        value = { "createdBy", "updatedBy" },
        allowGetters = true
)
@Setter
@Getter
public abstract class UserDateAuditModel extends DateAuditModel {

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;

}
