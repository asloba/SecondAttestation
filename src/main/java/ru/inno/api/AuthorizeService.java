package ru.inno.api;

import java.io.IOException;

public interface AuthorizeService {

    String auth(String username, String password) throws IOException;
}
