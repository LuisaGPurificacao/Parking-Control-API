package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.UserDto;
import com.api.parkingcontrol.models.UserModel;
import com.api.parkingcontrol.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository repository;

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto dto) {
        if (repository.existsByUsername(dto.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Username is already in use!");
        dto.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        UserModel model = new UserModel();
        BeanUtils.copyProperties(dto, model);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(model));
    }

}
