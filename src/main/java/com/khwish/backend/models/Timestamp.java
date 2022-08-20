package com.khwish.backend.models;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class Timestamp {

    @Column(name = "created_at")
    public Date createdAt;

    @Column(name = "updated_at")
    public Date updatedAt;

    @Version
    @ColumnDefault(value = "0")
    public int version;

    @PrePersist
    void createdAt() {
        this.createdAt = this.updatedAt = new Date();
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}