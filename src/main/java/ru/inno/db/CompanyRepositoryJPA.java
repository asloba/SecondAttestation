package ru.inno.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ru.inno.model.CompanyEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class CompanyRepositoryJPA implements CompanyRepository {
    private EntityManager entityManager;
    private Connection connection;
    public CompanyRepositoryJPA(Connection connection) {
        this.connection = connection;
    }

    public CompanyRepositoryJPA(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<CompanyEntity> getAll() throws SQLException {
        TypedQuery<CompanyEntity> selectAll = entityManager.createQuery("SELECT c FROM CompanyEntity c WHERE c.deletedAt is not null", CompanyEntity.class);
        return selectAll.getResultList();
    }

    @Override
    public List<CompanyEntity> getAll(boolean isActive) {
        return null;
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
    public int create(String name) throws SQLException {
        return 0;
    }

    @Override
    public int create(String name, String description) throws SQLException {
        CompanyEntity company = new CompanyEntity();
        int id = getLast().getId() + 2;
        company.setId(id);
        company.setName(name);
        company.setDescription(description);
        company.setCreateDateTime(Timestamp.valueOf(LocalDateTime.now()));
        company.setChangedTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        entityManager.getTransaction().begin();
        entityManager.persist(company);
        entityManager.getTransaction().commit();
        return company.getId();
    }

    @Override
    public void deleteById(int id) {
        CompanyEntity entity = entityManager.find(CompanyEntity.class, id);
        if (!entityManager.getTransaction().isActive()){
            entityManager.getTransaction().begin();
        }
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }
}
