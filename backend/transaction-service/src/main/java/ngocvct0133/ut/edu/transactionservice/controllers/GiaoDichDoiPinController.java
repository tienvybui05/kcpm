package ngocvct0133.ut.edu.transactionservice.controllers;

import jakarta.validation.Valid;
import ngocvct0133.ut.edu.transactionservice.modules.GiaoDichDoiPin;
import ngocvct0133.ut.edu.transactionservice.services.IGiaoDichDoiPinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-service/giaodichdoipin")
public class GiaoDichDoiPinController {

    private final IGiaoDichDoiPinService giaoDichDoiPinService;

    public GiaoDichDoiPinController(IGiaoDichDoiPinService giaoDichDoiPinService) {
        this.giaoDichDoiPinService = giaoDichDoiPinService;
    }

    @GetMapping
    public ResponseEntity<List<GiaoDichDoiPin>> layDanhSachGiaoDich() {
        return ResponseEntity.ok(giaoDichDoiPinService.danhSachGiaoDichDoiPin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiaoDichDoiPin> layGiaoDichTheoId(@PathVariable Long id) {
        return ResponseEntity.ok(giaoDichDoiPinService.layGiaoDichDoiPinTheoId(id));
    }

    @PostMapping
    public ResponseEntity<GiaoDichDoiPin> themGiaoDich(@Valid @RequestBody GiaoDichDoiPin doiPin) {
        GiaoDichDoiPin saved = giaoDichDoiPinService.themGiaoDichDoiPin(doiPin);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GiaoDichDoiPin> suaGiaoDich(
            @PathVariable Long id,
            @Valid @RequestBody GiaoDichDoiPin giaoDich) {

        return ResponseEntity.ok(
                giaoDichDoiPinService.suaGiaoDichDoiPinTheoId(id, giaoDich)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaGiaoDich(@PathVariable Long id) {
        boolean deleted = giaoDichDoiPinService.xoaGiaoDichDoiPinTheoId(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tai-xe/{maTaiXe}")
    public ResponseEntity<List<GiaoDichDoiPin>> getByTaiXe(@PathVariable Long maTaiXe) {
        List<GiaoDichDoiPin> list = giaoDichDoiPinService.layTheoTaiXe(maTaiXe);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/tram/{maTram}")
    public ResponseEntity<List<GiaoDichDoiPin>> getByTram(@PathVariable Long maTram) {
        return ResponseEntity.ok(giaoDichDoiPinService.layTheoTram(maTram));
    }
}