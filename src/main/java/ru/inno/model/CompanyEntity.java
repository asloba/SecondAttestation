package ru.inno.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "company", schema = "public", catalog = "x_clients_db_r06g")
public class CompanyEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @JsonProperty("isActive")
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    @Column(name = "create_timestamp", nullable = false)
    private Timestamp createDateTime;
    @Column(name = "change_timestamp", nullable = false)
    private Timestamp changedTimestamp;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "description", nullable = true, length = 300)
    private String description;
    @Column(name = "deleted_at", nullable = true)
    private Timestamp deletedAt;

    public CompanyEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyEntity that)) return false;
        return isActive() == that.isActive() && Objects.equals(getId(), that.getId()) && Objects.equals(getCreateDateTime(), that.getCreateDateTime()) && Objects.equals(getChangedTimestamp(), that.getChangedTimestamp()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getDeletedAt(), that.getDeletedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isActive(), getCreateDateTime(), getChangedTimestamp(), getName(), getDescription(), getDeletedAt());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Timestamp getChangedTimestamp() {
        return changedTimestamp;
    }

    public void setChangedTimestamp(Timestamp changedTimestamp) {
        this.changedTimestamp = changedTimestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}