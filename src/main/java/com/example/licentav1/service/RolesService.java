package com.example.licentav1.service;

import com.example.licentav1.domain.Roles;

import java.util.Optional;


public interface RolesService {

    Optional<Roles> getRoleById(Integer id);
}
