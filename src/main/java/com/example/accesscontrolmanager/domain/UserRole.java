package com.example.accesscontrolmanager.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.dialect.H2Dialect;

import java.time.OffsetDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

/**
 * Represents the User Roles database table.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@Entity
@Table(name = "user_roles")
public class UserRole extends JpaAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ure_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usr_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "roe_id", nullable = false)
    private Role role;
}
