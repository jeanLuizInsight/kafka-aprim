package br.com.alura.ecommerce.dto;

public class UserDTO {

    private final String uuid;


    public UserDTO(final String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
