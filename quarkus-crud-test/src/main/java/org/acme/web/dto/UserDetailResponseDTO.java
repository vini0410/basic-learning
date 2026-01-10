package org.acme.web.dto;

import java.util.List;

public class UserDetailResponseDTO extends UserResponseDTO {
    private List<AddressDTO> addresses;

    public List<AddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDTO> addresses) {
        this.addresses = addresses;
    }
}
