package com.yurticicargo.personnel_task_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gorev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Gorev {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baslik;
    private String aciklama;

    @Enumerated(EnumType.STRING)
    private GorevStatu statu;

    @ManyToOne
    @JoinColumn(name = "atanan_personel_id")
    private Personel atananPersonel;

    @ManyToOne
    @JoinColumn(name = "atayan_yonetici_id")
    private Personel atayanYonetici;

    private LocalDateTime olusturulmaTarihi;
}