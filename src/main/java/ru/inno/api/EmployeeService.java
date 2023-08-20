package ru.inno.api;

import ru.inno.model.Employee;

import java.util.List;

public interface EmployeeService {

    void setURI(String uri);

    Employee createRandomEmployee(int companyId);

    List<Employee> getAll(int companyId);

    Employee getById(int id);

    int create(Employee employee, String token);

    int update(Employee employee, String token);
}