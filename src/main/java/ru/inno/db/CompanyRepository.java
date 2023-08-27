package ru.inno.db;

import ru.inno.model.CompanyEntity;

import java.util.List;

public interface CompanyRepository {

    List<CompanyEntity> getAll();

    List<CompanyEntity> getAll(boolean isActive);

    CompanyEntity getLast();

    CompanyEntity getById(int id);

    int create(String name);

    int create(String name, String description);

    void deleteById(int id);
}

