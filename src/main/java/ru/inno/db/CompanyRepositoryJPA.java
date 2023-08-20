package ru.inno.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ru.inno.model.CompanyEntity;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class CompanyRepositoryJPA implements CompanyRepository {
    private final EntityManager entityManager;

    public CompanyRepositoryJPA(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<CompanyEntity> getAll() throws SQLException {
        TypedQuery<CompanyEntity> selectAll = entityManager.createQuery("SELECT c FROM CompanyEntity c WHERE c.deletedAt is not null", CompanyEntity.class);
        return selectAll.getResultList();
    }

    @Override
    public CompanyEntity getLast() throws SQLException {
        List<CompanyEntity> companiesList = getAll();
        int size = companiesList.size();
        return companiesList.get(size - 1);
    }

    @Override
    public CompanyEntity getById(int id) {
        return entityManager.find(CompanyEntity.class, id);
    }

    @Override
    public int create(String name, String description) throws SQLException {
        CompanyEntity company = new CompanyEntity();
        company.setName(name);
        company.setDescription(description);
        company.setCreateDateTime(Timestamp.valueOf(LocalDateTime.now()));
        company.setLastChangedDateTime(Timestamp.valueOf(LocalDateTime.now()));
        entityManager.getTransaction().begin();
        entityManager.persist(company);
        entityManager.getTransaction().commit();
        return company.getId();
    }

    @Override
    public void deleteById(int id) {
        CompanyEntity entity = entityManager.find(CompanyEntity.class, id);
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }
}
