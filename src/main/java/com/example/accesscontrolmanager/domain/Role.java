package com.example.accesscontrolmanager.domain;

import static com.example.accesscontrolmanager.domain.enums.InheritanceType.ASSIGNMENT;
import static com.example.accesscontrolmanager.domain.enums.InheritanceType.INHERIT;

import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents an entry in the Roles database table.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
@Table(name = "Roles")
public class Role extends JpaAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roe_id")
    private Long id;

    @Column(nullable = false)
    private String roleName;

    /**
     * Defines what the role type is and how it is used.
     *
     * <p>Permission - the role level that will be assigned to a user. All privileges for this
     * role are inherited from the capability groups assigned to the permission. </p>
     *
     * <p>Capability Group - A grouping of related privileges that can be assigned to a permission.
     * This grouping simplifies the allocation of individual capabilities to a permission.</p>
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleTypeCode roleTypeCode;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "roleForPrivilege", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<RolePrivilege> rolePrivileges = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "parentRole", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<RoleInheritance> roleInheritances = new HashSet<>();

    /**
     * Logic to retrieve inherited roles.
     */
    public Set<Role> getInheritedRoles() {
        return roleInheritances.stream()
                .filter(inheritance -> INHERIT == inheritance.getInheritanceType())
                .map(RoleInheritance::getChildRole)
                .collect(Collectors.toSet());
    }

    /**
     * Logic to retrieve role Assignments.
     */
    public Set<RoleInheritance> getAssignments() {
        return roleInheritances.stream()
                .filter(inheritance -> ASSIGNMENT == inheritance.getInheritanceType())
                .collect(Collectors.toSet());
    }
}
