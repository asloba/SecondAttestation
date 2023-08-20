package ru.inno.db;

import jakarta.persistence.EntityManager;
import ru.inno.model.EmployeeEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class EmployeeRepositoryJPA implements EmployeeRepository {

    private final EntityManager entityManager;

    public EmployeeRepositoryJPA(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<EmployeeEntity> getAll() {
        return entityManager
                .createQuery("SELECT e FROM EmployeeEntity e", EmployeeEntity.class)
                .getResultList();
    }

    @Override
    public EmployeeEntity getById(int id) {
        return entityManager.find(EmployeeEntity.class, id);
    }

    @Override
    public int create(EmployeeEntity employee) {
        employee.setCreateTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        employee.setChangeTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        entityManager.getTransaction().begin();
        entityManager.persist(employee);
        entityManager.getTransaction().commit();
        return employee.getId();
    }

    @Override
    public int update(EmployeeEntity e) {
        return 0;
    }

    @Override
    public void deleteById(int id) {
        EmployeeEntity entity = entityManager.find(EmployeeEntity.class, id);
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }
}
