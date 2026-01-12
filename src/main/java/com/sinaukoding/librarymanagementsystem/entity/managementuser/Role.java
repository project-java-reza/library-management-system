package com.sinaukoding.librarymanagementsystem.entity.managementuser;

import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name = "m_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")
    private String id;

    @Enumerated(EnumType.STRING)
    private ERole role;
}
