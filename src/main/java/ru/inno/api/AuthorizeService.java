package ru.inno.api;

import java.io.IOException;

public interface AuthorizeService {

    String getToken() throws IOException;
}
