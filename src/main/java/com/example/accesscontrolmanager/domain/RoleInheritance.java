package com.example.accesscontrolmanager.domain;

import com.example.accesscontrolmanager.domain.enums.InheritanceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents an entry in the Role Inheritance database table.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
@Table(name = "role_inheritances")
public class RoleInheritance extends JpaAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rie_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inherits_roe_id", nullable = false)
    private Role childRole;

    @Column(name = "inheritance_type_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private InheritanceType inheritanceType;

    @ManyToOne
    @JoinColumn(name = "roe_id", nullable = false)
    private Role parentRole;
}
