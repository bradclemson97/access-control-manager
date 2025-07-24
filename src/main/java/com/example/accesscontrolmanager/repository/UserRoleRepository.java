package com.example.accesscontrolmanager.repository;

import com.example.accesscontrolmanager.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
