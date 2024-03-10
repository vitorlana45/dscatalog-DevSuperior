package com.devsuperior.dscatalog.dto;

import java.io.Serial;

public class UserInsertDTO extends UserDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private String password;

    UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
