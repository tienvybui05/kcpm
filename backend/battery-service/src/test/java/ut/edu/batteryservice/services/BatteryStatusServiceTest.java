package ut.edu.batteryservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ut.edu.batteryservice.dtos.BatteryStatusDTO;
import ut.edu.batteryservice.models.LichSuPinTram;
import ut.edu.batteryservice.models.Pin;
import ut.edu.batteryservice.repositories.ILichSuPinTramRepository;
import ut.edu.batteryservice.repositories.IPinRepository;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BatteryStatusServiceTest {

    @Mock
    private IPinRepository pinRepository;

    @Mock
    private ILichSuPinTramRepository lichSuPinTramRepository;

    @InjectMocks
    private BatteryStatusService batteryStatusService;

    private Pin createPin(Long id, Pin.TrangThaiSoHuu soHuu, Pin.TinhTrang tinhTrang) {
        Pin p = new Pin();
        p.setMaPin(id);
        p.setTrangThaiSoHuu(soHuu);
        p.setTinhTrang(tinhTrang);
        return p;
    }

    // ========== TCS: Tổng hợp toàn hệ thống ==========
    @Test
    void TC_STATUS_001_GlobalSummary_ShouldCountAllValidPins() {
        Pin p1 = createPin(1L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.DAY);
        Pin p2 = createPin(2L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.DANG_SAC);
        Pin p3 = createPin(3L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.BAO_TRI);

        Mockito.when(pinRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3));

        LichSuPinTram hist = new LichSuPinTram();
        hist.setMaTram(10L);
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(1L)).thenReturn(hist);
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(2L)).thenReturn(hist);
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(3L)).thenReturn(hist);

        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(null);
        assertEquals(3, result.getTongSoPin());
        assertEquals(1, result.getDay());
        assertEquals(1, result.getDangSac());
        assertEquals(1, result.getBaoTri());
    }

    // ========== TCS: Bỏ qua pin đang sử dụng ==========
    @Test
    void TC_STATUS_002_SkipDangSuDung() {
        Pin pSkip = createPin(1L, Pin.TrangThaiSoHuu.DANG_SU_DUNG, Pin.TinhTrang.DAY);
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.singletonList(pSkip));
        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(null);
        assertEquals(0, result.getTongSoPin());
    }

    // ========== TCS: Bỏ qua pin đang vận chuyển ==========
    @Test
    void TC_STATUS_003_SkipDangVanChuyen() {
        Pin pSkip = createPin(1L, Pin.TrangThaiSoHuu.DANG_VAN_CHUYEN, Pin.TinhTrang.DAY);
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.singletonList(pSkip));
        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(null);
        assertEquals(0, result.getTongSoPin());
    }

    // ========== TCS: Bỏ qua pin khi lịch sử null ==========
    @Test
    void TC_STATUS_004_HistoryNull_Skip() {
        Pin p = createPin(1L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.DAY);
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.singletonList(p));
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(1L)).thenReturn(null);
        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(null);
        assertEquals(0, result.getTongSoPin());
    }

    // ========== TCS: Lọc theo tramId khớp ==========
    @Test
    void TC_STATUS_005_FilterByTramId_Match() {
        Pin p = createPin(1L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.DAY);
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.singletonList(p));

        LichSuPinTram hist = new LichSuPinTram();
        hist.setMaTram(10L);
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(1L)).thenReturn(hist);

        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(10L);
        assertEquals(1, result.getTongSoPin());
        assertEquals(1, result.getDay());
    }

    // ========== TCS: Lọc theo tramId không khớp ==========
    @Test
    void TC_STATUS_006_FilterByTramId_NotMatch() {
        Pin p = createPin(1L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.DAY);
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.singletonList(p));

        LichSuPinTram hist = new LichSuPinTram();
        hist.setMaTram(10L);
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(1L)).thenReturn(hist);

        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(20L);
        assertEquals(0, result.getTongSoPin());
    }

    // ========== TCS: Danh sách pin rỗng ==========
    @Test
    void TC_STATUS_007_EmptyList_ShouldReturnZero() {
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.emptyList());
        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(null);
        assertEquals(0, result.getTongSoPin());
        assertEquals(0, result.getDay());
        assertEquals(0, result.getDangSac());
        assertEquals(0, result.getBaoTri());
    }

    // ========== TCS: Đếm đúng số pin đầy (DAY) ==========
    @Test
    void TC_STATUS_008_CountDayStatus() {
        Pin p = createPin(1L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.DAY);
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.singletonList(p));

        LichSuPinTram hist = new LichSuPinTram();
        hist.setMaTram(10L);
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(1L)).thenReturn(hist);

        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(null);
        assertEquals(1, result.getDay());
        assertEquals(0, result.getDangSac());
        assertEquals(0, result.getBaoTri());
    }

    // ========== TCS: Đếm đúng số pin đang sạc (DANG_SAC) ==========
    @Test
    void TC_STATUS_009_CountDangSacStatus() {
        Pin p = createPin(1L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.DANG_SAC);
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.singletonList(p));

        LichSuPinTram hist = new LichSuPinTram();
        hist.setMaTram(10L);
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(1L)).thenReturn(hist);

        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(null);
        assertEquals(0, result.getDay());
        assertEquals(1, result.getDangSac());
        assertEquals(0, result.getBaoTri());
    }

    // ========== TCS: Đếm đúng số pin bảo trì (BAO_TRI) ==========
    @Test
    void TC_STATUS_010_CountBaoTriStatus() {
        Pin p = createPin(1L, Pin.TrangThaiSoHuu.SAN_SANG, Pin.TinhTrang.BAO_TRI);
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.singletonList(p));

        LichSuPinTram hist = new LichSuPinTram();
        hist.setMaTram(10L);
        Mockito.when(lichSuPinTramRepository.findTopByMaPinOrderByNgayThayDoiDesc(1L)).thenReturn(hist);

        BatteryStatusDTO result = batteryStatusService.getBatteryStatusSummary(null);
        assertEquals(0, result.getDay());
        assertEquals(0, result.getDangSac());
        assertEquals(1, result.getBaoTri());
    }
}