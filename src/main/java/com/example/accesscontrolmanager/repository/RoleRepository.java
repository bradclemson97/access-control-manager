package com.example.accesscontrolmanager.repository;

import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Role Repository
 *
 * <p>
 *     Methods to retrieve existing roles
 * </p>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Set<Role> findByRoleNameContains(String name);

    Set<Role> findByRoleTypeCode(RoleTypeCode roleTypeCode);

    Optional<Role> findByRoleName(String name);
}
