package com.yurticicargo.personnel_task_management.repository;

import com.yurticicargo.personnel_task_management.entity.Gorev;
import com.yurticicargo.personnel_task_management.entity.GorevStatu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface GorevRepository extends JpaRepository<Gorev, Long> {
    List<Gorev> findByStatuAndOlusturulmaTarihiBetween(GorevStatu statu, LocalDateTime baslangic, LocalDateTime bitis);
}