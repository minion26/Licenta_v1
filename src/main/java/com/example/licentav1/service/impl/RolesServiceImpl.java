package com.example.licentav1.service.impl;

import com.example.licentav1.domain.Roles;
import com.example.licentav1.repository.RolesRepository;
import com.example.licentav1.service.RolesService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolesServiceImpl implements RolesService {

    private final RolesRepository rolesRepository;

    public RolesServiceImpl(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }
    @Override
    public Optional<Roles> getRoleById(Integer id) {
        return rolesRepository.findById(id);
    }
}
