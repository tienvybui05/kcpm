package ut.edu.stationservice.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import ut.edu.stationservice.models.Tram;
import ut.edu.stationservice.services.ITramService;

@RestController
@RequestMapping("/api/station-service/tram")
public class TramController {

    private final ITramService tramService;

    public TramController(ITramService tramService) {
        this.tramService = tramService;
    }

    @GetMapping
    public ResponseEntity<List<Tram>> layDanhSachTram() {
        List<Tram> trams = tramService.findByTramId(null);
        return ResponseEntity.ok(trams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tram> layTramTheoId(@PathVariable Long id) {
        Tram tram = tramService.findById(id);
        if (tram == null) {
            // Ném lỗi ra để GlobalExceptionHandler tạo cục JSON báo lỗi 404
            throw new NoSuchElementException("Không tìm thấy trạm");
        }
        return ResponseEntity.ok(tram);
    }

    @PostMapping
    public ResponseEntity<?> themTram(@Valid @RequestBody Tram tram) {
        return ResponseEntity.ok(tramService.addPin(tram));
    }

    @PostMapping("/batch")
    public ResponseEntity<?> themNhieuTram(@RequestBody List<Tram> dsTram) {
        try {
            List<Tram> newTrams = tramService.addNhieuTram(dsTram);
            return ResponseEntity.ok(newTrams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> suaTram(@PathVariable Long id, @RequestBody Tram tram) {
        // Đã xóa try-catch. Bất kỳ lỗi gì (400 hay 404) đều được đẩy ra Global Handler xử lý chuẩn JSON!
        tram.setMaTram(id);
        Tram updated = tramService.updatePin(tram);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoaTram(@PathVariable Long id) {
        boolean deleted = tramService.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok("Xóa trạm thành công!");
        } else {
            throw new NoSuchElementException("Không tìm thấy trạm");
        }
    }
}
