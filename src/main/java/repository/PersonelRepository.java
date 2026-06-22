package com.yurticicargo.personnel_task_management.repository;

import com.yurticicargo.personnel_task_management.entity.Personel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonelRepository extends JpaRepository<Personel, Long> {
}