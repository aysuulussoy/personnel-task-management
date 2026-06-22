package com.yurticicargo.personnel_task_management.controller;

import com.yurticicargo.personnel_task_management.entity.Personel;
import com.yurticicargo.personnel_task_management.service.PersonelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/personel")
@RequiredArgsConstructor
public class PersonelController {

    private final PersonelService personelService;

    @PostMapping
    public ResponseEntity<Personel> ekle(@RequestBody Personel personel) {
        return ResponseEntity.ok(personelService.kaydet(personel));
    }

    @GetMapping
    public ResponseEntity<List<Personel>> hepsiniGetir() {
        return ResponseEntity.ok(personelService.hepsiniGetir());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Personel> idIleGetir(@PathVariable Long id) {
        return ResponseEntity.ok(personelService.idIleGetir(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Personel> guncelle(@PathVariable Long id, @RequestBody Personel personel) {
        return ResponseEntity.ok(personelService.guncelle(id, personel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> sil(@PathVariable Long id) {
        personelService.sil(id);
        return ResponseEntity.noContent().build();
    }
}