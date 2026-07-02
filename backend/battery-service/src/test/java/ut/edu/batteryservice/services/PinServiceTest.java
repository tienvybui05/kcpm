package ut.edu.batteryservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ut.edu.batteryservice.models.Pin;
import ut.edu.batteryservice.repositories.IPinRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PinServiceTest {

    @Mock
    private IPinRepository pinRepository;

    @InjectMocks
    private PinService pinService;

    // ========== TCS: Lấy danh sách pin ==========
    @Test
    void TC_PIN_001_GetAllPins_ShouldReturnList() {
        Mockito.when(pinRepository.findAll()).thenReturn(Arrays.asList(new Pin(), new Pin()));
        List<Pin> list = pinService.getAllPinTypes();
        assertEquals(2, list.size());
    }

    // ========== TCS: Lấy pin theo ID ==========
    @Test
    void TC_PIN_002_GetPinById_Found() {
        Pin p = new Pin();
        Mockito.when(pinRepository.findById(1L)).thenReturn(Optional.of(p));
        assertNotNull(pinService.getPinTypeById(1L));
    }

    @Test
    void TC_PIN_003_GetPinById_NotFound() {
        Mockito.when(pinRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(pinService.getPinTypeById(2L));
    }

    // ========== TCS: Tạo mới pin ==========
    @Test
    void TC_PIN_004_CreatePin_Success() {
        Pin p = new Pin("Lithium", 50.0, Pin.TinhTrang.DAY, Pin.TrangThaiSoHuu.SAN_SANG, 90.0, null, null);
        Mockito.when(pinRepository.save(p)).thenReturn(p);
        Pin result = pinService.createPinType(p);
        assertNotNull(result);
    }

    // ========== TCS: Cập nhật pin ==========
    @Test
    void TC_PIN_005_UpdatePin_Success() {
        Pin validPin = new Pin("Lithium", 50.0, Pin.TinhTrang.DAY, Pin.TrangThaiSoHuu.SAN_SANG, 90.0, null, null);
        Pin existing = new Pin();
        Mockito.when(pinRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(pinRepository.save(Mockito.any(Pin.class))).thenReturn(existing);
        assertNotNull(pinService.updatePinType(1L, validPin));
    }

    @Test
    void TC_PIN_006_UpdatePin_NotFound() {
        Pin validPin = new Pin("Lithium", 50.0, Pin.TinhTrang.DAY, Pin.TrangThaiSoHuu.SAN_SANG, 90.0, null, null);
        Mockito.when(pinRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> pinService.updatePinType(2L, validPin));
    }

    // ========== TCS: Cập nhật trạng thái pin ==========
    @Test
    void TC_PIN_007_UpdatePinState_Success() {
        Pin pin = new Pin();
        Mockito.when(pinRepository.findById(1L)).thenReturn(Optional.of(pin));
        Mockito.when(pinRepository.save(Mockito.any(Pin.class))).thenReturn(pin);

        Pin result = pinService.updatePinState(1L, "DAY", "SAN_SANG");
        assertNotNull(result);
        assertEquals(Pin.TinhTrang.DAY, pin.getTinhTrang());
        assertEquals(Pin.TrangThaiSoHuu.SAN_SANG, pin.getTrangThaiSoHuu());
    }

    @Test
    void TC_PIN_008_UpdatePinState_InvalidTinhTrang() {
        Pin pin = new Pin();
        Mockito.when(pinRepository.findById(1L)).thenReturn(Optional.of(pin));
        assertThrows(RuntimeException.class, () -> pinService.updatePinState(1L, "SAI_TINH_TRANG", "SAN_SANG"));
    }

    @Test
    void TC_PIN_009_UpdatePinState_InvalidTrangThaiSoHuu() {
        Pin pin = new Pin();
        Mockito.when(pinRepository.findById(1L)).thenReturn(Optional.of(pin));
        assertThrows(RuntimeException.class, () -> pinService.updatePinState(1L, "DAY", "SAI_SO_HUU"));
    }

    @Test
    void TC_PIN_010_UpdatePinState_NullParams() {
        Pin pin = new Pin();
        Mockito.when(pinRepository.findById(1L)).thenReturn(Optional.of(pin));
        Mockito.when(pinRepository.save(Mockito.any(Pin.class))).thenReturn(pin);
        assertDoesNotThrow(() -> pinService.updatePinState(1L, null, null));
    }

    @Test
    void TC_PIN_011_UpdatePinState_NotFound() {
        Mockito.when(pinRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> pinService.updatePinState(99L, "DAY", "SAN_SANG"));
    }

    // ========== TCS: Xóa pin ==========
    @Test
    void TC_PIN_012_DeletePin_Found() {
        Mockito.when(pinRepository.existsById(1L)).thenReturn(true);
        assertTrue(pinService.deletePinType(1L));
    }

    @Test
    void TC_PIN_013_DeletePin_NotFound() {
        Mockito.when(pinRepository.existsById(2L)).thenReturn(false);
        assertFalse(pinService.deletePinType(2L));
    }

    // ========== TCS: Thêm pin (addPin) - validation ==========
    @Test
    void TC_PIN_014_AddPin_Success() {
        Pin p = new Pin("Lithium", 50.0, Pin.TinhTrang.DAY, Pin.TrangThaiSoHuu.SAN_SANG, 90.0, null, null);
        Mockito.when(pinRepository.save(p)).thenReturn(p);
        assertNotNull(pinService.addPin(p));
    }

    @Test
    void TC_PIN_015_AddPin_NullPin_ThrowsException() {
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(null));
    }

    @Test
    void TC_PIN_016_AddPin_EmptyLoaiPin_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin("");
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }

    @Test
    void TC_PIN_017_AddPin_BlankLoaiPin_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin("   ");
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }

    // ----- BỔ SUNG: test loaiPin = null -----
    @Test
    void TC_PIN_022_AddPin_NullLoaiPin_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin(null);
        // Cần set các trường khác hợp lệ để chỉ kiểm tra loaiPin
        p.setDungLuong(50.0);
        p.setSucKhoe(90.0);
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }

    @Test
    void TC_PIN_018_AddPin_DungLuongZero_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin("Lithium");
        p.setDungLuong(0.0);
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }

    @Test
    void TC_PIN_019_AddPin_DungLuongNegative_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin("Lithium");
        p.setDungLuong(-5.0);
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }

    // ----- BỔ SUNG: test dungLuong = null -----
    @Test
    void TC_PIN_023_AddPin_NullDungLuong_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin("Lithium");
        p.setDungLuong(null);
        p.setSucKhoe(90.0);
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }

    @Test
    void TC_PIN_020_AddPin_SucKhoeNegative_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin("Lithium");
        p.setDungLuong(60.0);
        p.setSucKhoe(-10.0);
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }

    @Test
    void TC_PIN_021_AddPin_SucKhoeGreaterThan100_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin("Lithium");
        p.setDungLuong(60.0);
        p.setSucKhoe(150.0);
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }

    // ----- BỔ SUNG: test sucKhoe = null -----
    @Test
    void TC_PIN_024_AddPin_NullSucKhoe_ThrowsException() {
        Pin p = new Pin();
        p.setLoaiPin("Lithium");
        p.setDungLuong(60.0);
        p.setSucKhoe(null);
        assertThrows(ResponseStatusException.class, () -> pinService.addPin(p));
    }
}