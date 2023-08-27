package ru.inno.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ru.inno.model.CompanyEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class CompanyRepositoryJPA implements CompanyRepository {
    private EntityManager entityManager;

    public CompanyRepositoryJPA(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<CompanyEntity> getAll() {
        TypedQuery<CompanyEntity> selectAll = entityManager.createQuery("SELECT c FROM CompanyEntity c WHERE c.deletedAt is not null", CompanyEntity.class);
        return selectAll.getResultList();
    }

    @Override
    public List<CompanyEntity> getAll(boolean isActive) {
        return null;
    }

    @Override
    public CompanyEntity getLast() {
        TypedQuery<CompanyEntity> query = entityManager.createQuery(
                "SELECT c FROM CompanyEntity c ORDER BY c.id DESC LIMIT 1", CompanyEntity.class);
        return query.getSingleResult();
    }

    @Override
    public CompanyEntity getById(int id) {
        return entityManager.find(CompanyEntity.class, id);
    }

    @Override
    public int create(String name) {
        return 0;
    }

    @Override
    public int create(String name, String description) {
        CompanyEntity company = new CompanyEntity();
        int id = getLast().getId() + 2;
        company.setId(id);
        company.setName(name);
        company.setDescription(description);
        company.setCreateDateTime(Timestamp.valueOf(LocalDateTime.now()));
        company.setChangedTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.persist(company);
        entityManager.getTransaction().commit();
        return company.getId();
    }

    @Override
    public void deleteById(int id) {
        CompanyEntity companyEntity = entityManager.find(CompanyEntity.class, id);
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        if (companyEntity == null) return;
        entityManager.remove(companyEntity);
        entityManager.getTransaction().commit();
    }
}