package com.sinaukoding.librarymanagementsystem.entity.managementuser;


import jakarta.persistence.*;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name = "m_status_buku")
public class StatusBuku{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "status_buku_id")
    private String id;

    @Enumerated(EnumType.STRING)
    private EStatusBuku statusBuku;
}
