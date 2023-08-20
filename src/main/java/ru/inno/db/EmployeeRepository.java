package ru.inno.db;

import ru.inno.model.EmployeeEntity;

import java.util.List;

public interface EmployeeRepository {

    List<EmployeeEntity> getAll();

    EmployeeEntity getById(int id);

    int create(EmployeeEntity emp);
    int update(EmployeeEntity e);

    void deleteById(int id);
}
