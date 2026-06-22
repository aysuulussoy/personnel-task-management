package com.yurticicargo.personnel_task_management.controller;

import com.yurticicargo.personnel_task_management.entity.Gorev;
import com.yurticicargo.personnel_task_management.entity.GorevStatu;
import com.yurticicargo.personnel_task_management.service.GorevService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/gorev")
@RequiredArgsConstructor
public class GorevController {

    private final GorevService gorevService;

    @PostMapping("/ata")
    public ResponseEntity<Gorev> gorevAta(
            @RequestHeader("yonetici-id") Long yoneticiId,
            @RequestParam Long personelId,
            @RequestBody Gorev gorev) {
        return ResponseEntity.ok(gorevService.gorevAta(yoneticiId, personelId, gorev));
    }

    @PatchMapping("/{id}/statu")
    public ResponseEntity<Gorev> statuGuncelle(
            @PathVariable Long id,
            @RequestHeader("personel-id") Long personelId,
            @RequestParam GorevStatu yeniStatu) {
        return ResponseEntity.ok(gorevService.statuGuncelle(id, personelId, yeniStatu));
    }

    @GetMapping
    public ResponseEntity<List<Gorev>> hepsiniGetir() {
        return ResponseEntity.ok(gorevService.hepsiniGetir());
    }

    @GetMapping("/rapor")
    public ResponseEntity<List<Gorev>> tamamlananGorevler(
            @RequestParam LocalDateTime baslangic,
            @RequestParam LocalDateTime bitis) {
        return ResponseEntity.ok(gorevService.tamamlananGorevleriGetir(baslangic, bitis));
    }
}