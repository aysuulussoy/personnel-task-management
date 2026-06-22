package com.yurticicargo.personnel_task_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Personel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adSoyad;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
}