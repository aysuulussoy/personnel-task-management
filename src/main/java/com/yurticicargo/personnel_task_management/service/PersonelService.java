package com.yurticicargo.personnel_task_management.service;

import com.yurticicargo.personnel_task_management.entity.Personel;
import com.yurticicargo.personnel_task_management.repository.PersonelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonelService {

    private final PersonelRepository personelRepository;

    public Personel kaydet(Personel personel) {
        return personelRepository.save(personel);
    }

    public List<Personel> hepsiniGetir() {
        return personelRepository.findAll();
    }

    public Personel idIleGetir(Long id) {
        return personelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personel bulunamadi: " + id));
    }

    public Personel guncelle(Long id, Personel yeniPersonel) {
        Personel mevcutPersonel = idIleGetir(id);
        mevcutPersonel.setAdSoyad(yeniPersonel.getAdSoyad());
        mevcutPersonel.setEmail(yeniPersonel.getEmail());
        mevcutPersonel.setRole(yeniPersonel.getRole());
        return personelRepository.save(mevcutPersonel);
    }

    public void sil(Long id) {
        personelRepository.deleteById(id);
    }
}