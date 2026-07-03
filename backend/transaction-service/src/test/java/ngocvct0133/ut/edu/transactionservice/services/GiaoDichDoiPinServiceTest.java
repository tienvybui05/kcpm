package ngocvct0133.ut.edu.transactionservice.services;

import ngocvct0133.ut.edu.transactionservice.modules.GiaoDichDoiPin;
import ngocvct0133.ut.edu.transactionservice.repositories.IGiaoDichDoiPinRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GiaoDichDoiPinServiceTest {

    @Mock
    private IGiaoDichDoiPinRepository giaoDichDoiPinRepository;

    @InjectMocks
    private GiaoDichDoiPinService giaoDichDoiPinService;

    // ===================== themGiaoDichDoiPin =====================

    @Test
    void testThemGiaoDichDoiPin_Success() {
        GiaoDichDoiPin gd = new GiaoDichDoiPin();
        gd.setMaPinTra("PIN-001");
        gd.setMaPinNhan("PIN-002");
        gd.setNgayGiaoDich(LocalDateTime.of(2026, 5, 29, 10, 15, 30));
        gd.setTrangThaiGiaoDich("Đang xử lý");
        gd.setThanhtien(500000.0);
        gd.setPhuongThucThanhToan("cash");
        gd.setMaTram(1L);
        gd.setMaTaiXe(2L);

        GiaoDichDoiPin saved = new GiaoDichDoiPin();
        saved.setMaGiaoDichDoiPin(1L);
        saved.setMaPinTra("PIN-001");
        saved.setMaPinNhan("PIN-002");

        Mockito.when(giaoDichDoiPinRepository.save(gd)).thenReturn(saved);

        GiaoDichDoiPin result = giaoDichDoiPinService.themGiaoDichDoiPin(gd);

        assertNotNull(result);
        assertEquals(1L, result.getMaGiaoDichDoiPin());
        assertEquals("PIN-001", result.getMaPinTra());
        Mockito.verify(giaoDichDoiPinRepository).save(gd);
    }

    // ===================== danhSachGiaoDichDoiPin =====================

    @Test
    void testDanhSachGiaoDichDoiPin_ReturnsList() {
        GiaoDichDoiPin gd1 = new GiaoDichDoiPin();
        gd1.setMaGiaoDichDoiPin(1L);
        GiaoDichDoiPin gd2 = new GiaoDichDoiPin();
        gd2.setMaGiaoDichDoiPin(2L);

        Mockito.when(giaoDichDoiPinRepository.findAll()).thenReturn(Arrays.asList(gd1, gd2));

        List<GiaoDichDoiPin> result = giaoDichDoiPinService.danhSachGiaoDichDoiPin();

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(giaoDichDoiPinRepository).findAll();
    }

    @Test
    void testDanhSachGiaoDichDoiPin_EmptyList() {
        Mockito.when(giaoDichDoiPinRepository.findAll()).thenReturn(Collections.emptyList());

        List<GiaoDichDoiPin> result = giaoDichDoiPinService.danhSachGiaoDichDoiPin();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ===================== layGiaoDichDoiPinTheoId =====================

    @Test
    void testLayGiaoDichDoiPinTheoId_Found() {
        GiaoDichDoiPin gd = new GiaoDichDoiPin();
        gd.setMaGiaoDichDoiPin(1L);
        gd.setMaPinTra("PIN-001");

        Mockito.when(giaoDichDoiPinRepository.findById(1L)).thenReturn(Optional.of(gd));

        GiaoDichDoiPin result = giaoDichDoiPinService.layGiaoDichDoiPinTheoId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getMaGiaoDichDoiPin());
        assertEquals("PIN-001", result.getMaPinTra());
    }

    @Test
    void testLayGiaoDichDoiPinTheoId_NotFound() {
        Mockito.when(giaoDichDoiPinRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> giaoDichDoiPinService.layGiaoDichDoiPinTheoId(99L));

        assertEquals("GiaoDichDoiPin not found with id: 99", ex.getMessage());
    }

    // ===================== xoaGiaoDichDoiPinTheoId =====================

    @Test
    void testXoaGiaoDichDoiPinTheoId_Exists() {
        Mockito.when(giaoDichDoiPinRepository.existsById(1L)).thenReturn(true);

        boolean result = giaoDichDoiPinService.xoaGiaoDichDoiPinTheoId(1L);

        assertTrue(result);
        Mockito.verify(giaoDichDoiPinRepository).deleteById(1L);
    }

    @Test
    void testXoaGiaoDichDoiPinTheoId_NotExists() {
        Mockito.when(giaoDichDoiPinRepository.existsById(99L)).thenReturn(false);

        boolean result = giaoDichDoiPinService.xoaGiaoDichDoiPinTheoId(99L);

        assertFalse(result);
        Mockito.verify(giaoDichDoiPinRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    // ===================== suaGiaoDichDoiPinTheoId =====================

    @Test
    void testSuaGiaoDichDoiPinTheoId_Success() {
        GiaoDichDoiPin existing = new GiaoDichDoiPin();
        existing.setMaGiaoDichDoiPin(1L);
        existing.setTrangThaiGiaoDich("Đang xử lý");
        existing.setPhuongThucThanhToan("cash");
        existing.setThanhtien(100000.0);
        existing.setNgayGiaoDich(LocalDateTime.of(2026, 5, 29, 10, 0, 0));

        GiaoDichDoiPin updateData = new GiaoDichDoiPin();
        updateData.setTrangThaiGiaoDich("Đã hoàn thành");
        updateData.setPhuongThucThanhToan("card");
        updateData.setThanhtien(200000.0);
        updateData.setNgayGiaoDich(LocalDateTime.of(2026, 6, 1, 12, 0, 0));

        Mockito.when(giaoDichDoiPinRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(giaoDichDoiPinRepository.save(Mockito.any(GiaoDichDoiPin.class))).thenReturn(existing);

        GiaoDichDoiPin result = giaoDichDoiPinService.suaGiaoDichDoiPinTheoId(1L, updateData);

        assertNotNull(result);
        assertEquals("Đã hoàn thành", result.getTrangThaiGiaoDich());
        assertEquals("card", result.getPhuongThucThanhToan());
        assertEquals(200000.0, result.getThanhtien());
        assertEquals(LocalDateTime.of(2026, 6, 1, 12, 0, 0), result.getNgayGiaoDich());
        Mockito.verify(giaoDichDoiPinRepository).save(existing);
    }

    @Test
    void testSuaGiaoDichDoiPinTheoId_NotFound() {
        Mockito.when(giaoDichDoiPinRepository.findById(99L)).thenReturn(Optional.empty());

        GiaoDichDoiPin updateData = new GiaoDichDoiPin();
        updateData.setTrangThaiGiaoDich("Đã hoàn thành");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> giaoDichDoiPinService.suaGiaoDichDoiPinTheoId(99L, updateData));

        assertEquals("GiaoDichDoiPin not found with id: 99", ex.getMessage());
        Mockito.verify(giaoDichDoiPinRepository, Mockito.never()).save(Mockito.any());
    }

    // ===================== layTheoTaiXe =====================

    @Test
    void testLayTheoTaiXe_FilterOnlyCompleted() {
        GiaoDichDoiPin gd1 = new GiaoDichDoiPin();
        gd1.setMaGiaoDichDoiPin(1L);
        gd1.setTrangThaiGiaoDich("Đã hoàn thành");
        gd1.setMaTaiXe(10L);

        GiaoDichDoiPin gd2 = new GiaoDichDoiPin();
        gd2.setMaGiaoDichDoiPin(2L);
        gd2.setTrangThaiGiaoDich("Đang xử lý");
        gd2.setMaTaiXe(10L);

        GiaoDichDoiPin gd3 = new GiaoDichDoiPin();
        gd3.setMaGiaoDichDoiPin(3L);
        gd3.setTrangThaiGiaoDich("Đã hoàn thành");
        gd3.setMaTaiXe(10L);

        Mockito.when(giaoDichDoiPinRepository.findByMaTaiXe(10L))
                .thenReturn(Arrays.asList(gd1, gd2, gd3));

        List<GiaoDichDoiPin> result = giaoDichDoiPinService.layTheoTaiXe(10L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(gd -> "Đã hoàn thành".equals(gd.getTrangThaiGiaoDich())));
    }

    @Test
    void testLayTheoTaiXe_NoCompletedTransactions() {
        GiaoDichDoiPin gd1 = new GiaoDichDoiPin();
        gd1.setTrangThaiGiaoDich("Đang xử lý");

        Mockito.when(giaoDichDoiPinRepository.findByMaTaiXe(10L))
                .thenReturn(List.of(gd1));

        List<GiaoDichDoiPin> result = giaoDichDoiPinService.layTheoTaiXe(10L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testLayTheoTaiXe_EmptyList() {
        Mockito.when(giaoDichDoiPinRepository.findByMaTaiXe(10L))
                .thenReturn(Collections.emptyList());

        List<GiaoDichDoiPin> result = giaoDichDoiPinService.layTheoTaiXe(10L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ===================== layTheoTram =====================

    @Test
    void testLayTheoTram_FilterOnlyCompleted() {
        GiaoDichDoiPin gd1 = new GiaoDichDoiPin();
        gd1.setMaGiaoDichDoiPin(1L);
        gd1.setTrangThaiGiaoDich("Đã hoàn thành");
        gd1.setMaTram(5L);

        GiaoDichDoiPin gd2 = new GiaoDichDoiPin();
        gd2.setMaGiaoDichDoiPin(2L);
        gd2.setTrangThaiGiaoDich("Đang xử lý");
        gd2.setMaTram(5L);

        Mockito.when(giaoDichDoiPinRepository.findByMaTram(5L))
                .thenReturn(Arrays.asList(gd1, gd2));

        List<GiaoDichDoiPin> result = giaoDichDoiPinService.layTheoTram(5L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getMaGiaoDichDoiPin());
        assertEquals("Đã hoàn thành", result.get(0).getTrangThaiGiaoDich());
    }

    @Test
    void testLayTheoTram_NoCompletedTransactions() {
        GiaoDichDoiPin gd1 = new GiaoDichDoiPin();
        gd1.setTrangThaiGiaoDich("Đang xử lý");

        Mockito.when(giaoDichDoiPinRepository.findByMaTram(5L))
                .thenReturn(List.of(gd1));

        List<GiaoDichDoiPin> result = giaoDichDoiPinService.layTheoTram(5L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testLayTheoTram_EmptyList() {
        Mockito.when(giaoDichDoiPinRepository.findByMaTram(5L))
                .thenReturn(Collections.emptyList());

        List<GiaoDichDoiPin> result = giaoDichDoiPinService.layTheoTram(5L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
