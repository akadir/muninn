package io.github.akadir.muninn.model;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Date;

/**
 * @author akadir
 * Date: 3.05.2020
 * Time: 17:18
 */
@Data
@MappedSuperclass
public abstract class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;
    @Version
    @Column(name = "version")
    private int version;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
