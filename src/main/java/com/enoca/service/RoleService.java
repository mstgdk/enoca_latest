package com.enoca.service;

import com.enoca.domain.Role;
import com.enoca.domain.enums.RoleTypes;
import com.enoca.exception.ResourceNotFoundException;
import com.enoca.exception.message.ErrorMessage;
import com.enoca.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findByType(RoleTypes roleType){

        Role role= roleRepository.findByType(roleType).
                orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMessage.ROLE_NOT_FOUND_EXCEPTION, roleType.name())));

        return role;
    }
}
