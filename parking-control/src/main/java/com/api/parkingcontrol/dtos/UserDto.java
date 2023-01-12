package com.api.parkingcontrol.dtos;

import com.api.parkingcontrol.models.RoleModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private List<RoleModel> roles;

}
