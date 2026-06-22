package com.yurticicargo.personnel_task_management.service;

import com.yurticicargo.personnel_task_management.entity.Gorev;
import com.yurticicargo.personnel_task_management.entity.GorevStatu;
import com.yurticicargo.personnel_task_management.entity.Personel;
import com.yurticicargo.personnel_task_management.entity.Role;
import com.yurticicargo.personnel_task_management.repository.GorevRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GorevService {

    private final GorevRepository gorevRepository;
    private final PersonelService personelService;

    public Gorev gorevAta(Long yoneticiId, Long personelId, Gorev gorev) {
        Personel yonetici = personelService.idIleGetir(yoneticiId);
        Personel personel = personelService.idIleGetir(personelId);

        if (yonetici.getRole() != Role.YONETICI) {
            throw new RuntimeException("Sadece yoneticiler gorev atayabilir!");
        }

        gorev.setAtayanYonetici(yonetici);
        gorev.setAtananPersonel(personel);
        gorev.setStatu(GorevStatu.YENI);
        gorev.setOlusturulmaTarihi(LocalDateTime.now());
        return gorevRepository.save(gorev);
    }

    public Gorev statuGuncelle(Long gorevId, Long personelId, GorevStatu yeniStatu) {
        Gorev gorev = gorevRepository.findById(gorevId)
                .orElseThrow(() -> new RuntimeException("Gorev bulunamadi: " + gorevId));

        if (!gorev.getAtananPersonel().getId().equals(personelId)) {
            throw new RuntimeException("Bu gorevi sadece atanan personel guncelleyebilir!");
        }

        gorev.setStatu(yeniStatu);
        return gorevRepository.save(gorev);
    }

    public List<Gorev> hepsiniGetir() {
        return gorevRepository.findAll();
    }

    public List<Gorev> tamamlananGorevleriGetir(LocalDateTime baslangic, LocalDateTime bitis) {
        return gorevRepository.findByStatuAndOlusturulmaTarihiBetween(GorevStatu.TAMAMLANDI, baslangic, bitis);
    }
}