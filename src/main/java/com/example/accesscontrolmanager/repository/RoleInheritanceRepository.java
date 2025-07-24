package com.example.accesscontrolmanager.repository;

import com.example.accesscontrolmanager.domain.RoleInheritance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleInheritanceRepository extends JpaRepository<RoleInheritance, Long> {

    Set<RoleInheritance> findByParentRoleIdAndChildRoleId(Long parentId, Long childId);

    Set<RoleInheritance> findByParentRoleId(Long parentId);
}
