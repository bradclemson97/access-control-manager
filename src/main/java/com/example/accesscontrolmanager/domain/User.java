package com.example.accesscontrolmanager.domain;


import com.example.accesscontrolmanager.domain.enums.YesNo;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.accesscontrolmanager.domain.enums.RoleTypeCode.PERMISSION;
import static com.example.accesscontrolmanager.domain.enums.YesNo.NA;
import static com.example.accesscontrolmanager.domain.enums.YesNo.NO;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

/**
 * Represents the Users database table
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@Entity
@Table(name = "users")
public class User extends JpaAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id")
    private Long id;

    @Column(nullable = false)
    private UUID systemUserId;

    @Column(name = "locked_user_ind", nullable = false)
    @Enumerated(STRING)
    @Builder.Default
    private YesNo locked = NA;

    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * Logic to retrieve a user's roles
     */
    public Set<Role> getRoles() {
        return userRoles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
    }

    /**
     * Logic to retrieve a user's permissions.
     */
    public Set<Role> getPermissions() {
        return userRoles.stream()
                .map(UserRole::getRole)
                .filter(role -> PERMISSION.equals(role.getRoleTypeCode()))
                .collect(Collectors.toSet());
    }

}
