package ru.inno.db;

import ru.inno.model.Employee;
import ru.inno.model.EmployeeEntity;

import java.util.List;

public interface EmployeeRepository {

    List<EmployeeEntity> getAll();

    EmployeeEntity getById(int id);

    int create(Employee employee);

    int update(EmployeeEntity e);

    void deleteById(int id);
}