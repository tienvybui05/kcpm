package ut.edu.batteryservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ut.edu.batteryservice.models.LichSuPinTram;
import ut.edu.batteryservice.models.Pin;
import ut.edu.batteryservice.repositories.ILichSuPinTramRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LichSuPinTramServiceTest {

    @Mock
    private ILichSuPinTramRepository lichSuPinTramRepository;

    @InjectMocks
    private LichSuPinTramService lichSuPinTramService;

    // ========== TCS: Lấy danh sách lịch sử ==========
    @Test
    void TC_LICHSU_001_FindAll_ShouldReturnList() {
        Mockito.when(lichSuPinTramRepository.findAll()).thenReturn(Arrays.asList(new LichSuPinTram()));
        List<LichSuPinTram> list = lichSuPinTramService.findAll();
        assertEquals(1, list.size());
    }

    // ========== TCS: Lấy lịch sử theo ID ==========
    @Test
    void TC_LICHSU_002_FindById_Found() {
        LichSuPinTram ls = new LichSuPinTram();
        Mockito.when(lichSuPinTramRepository.findById(1L)).thenReturn(Optional.of(ls));
        assertNotNull(lichSuPinTramService.findById(1L));
    }

    @Test
    void TC_LICHSU_003_FindById_NotFound() {
        Mockito.when(lichSuPinTramRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(lichSuPinTramService.findById(2L));
    }

    // ========== TCS: Lưu (save) lịch sử ==========
    @Test
    void TC_LICHSU_004_Save_Success() {
        LichSuPinTram ls = new LichSuPinTram("THEM_PIN", null, 1L, 1L);
        Mockito.when(lichSuPinTramRepository.save(ls)).thenReturn(ls);
        assertNotNull(lichSuPinTramService.save(ls));
    }

    // ========== TCS: Xóa lịch sử ==========
    @Test
    void TC_LICHSU_005_DeleteById_Found() {
        Mockito.when(lichSuPinTramRepository.existsById(1L)).thenReturn(true);
        assertTrue(lichSuPinTramService.deleteById(1L));
    }

    @Test
    void TC_LICHSU_006_DeleteById_NotFound() {
        Mockito.when(lichSuPinTramRepository.existsById(2L)).thenReturn(false);
        assertFalse(lichSuPinTramService.deleteById(2L));
    }

    // ========== TCS: Thêm mới lịch sử ==========
    @Test
    void TC_LICHSU_007_AddLichSuPinTram_Success() {
        LichSuPinTram ls = new LichSuPinTram("THEM_PIN", null, 1L, 1L);
        Mockito.when(lichSuPinTramRepository.save(ls)).thenReturn(ls);
        assertNotNull(lichSuPinTramService.addLichSuPinTram(ls));
    }

    // ========== TCS: Lấy pin khả dụng theo trạm và loại ==========
    @Test
    void TC_LICHSU_008_GetAvailablePins_ShouldReturnList() {
        Mockito.when(lichSuPinTramRepository.findAvailablePinsByTramAndLoai(1L, "Lithium"))
                .thenReturn(Arrays.asList(new Pin()));
        List<Pin> pins = lichSuPinTramService.getAvailablePins(1L, "Lithium");
        assertEquals(1, pins.size());
    }

    // ========== TCS: Validation khi lưu/ thêm mới ==========
    @Test
    void TC_LICHSU_009_AddLichSu_NullObject_ThrowsException() {
        assertThrows(ResponseStatusException.class, () -> lichSuPinTramService.save(null));
    }

    @Test
    void TC_LICHSU_010_AddLichSu_EmptyHanhDong_ThrowsException() {
        LichSuPinTram ls = new LichSuPinTram();
        assertThrows(ResponseStatusException.class, () -> lichSuPinTramService.save(ls));
    }

    @Test
    void TC_LICHSU_011_AddLichSu_BlankHanhDong_ThrowsException() {
        LichSuPinTram ls = new LichSuPinTram();
        ls.setHanhDong("   ");
        assertThrows(ResponseStatusException.class, () -> lichSuPinTramService.save(ls));
    }

    @Test
    void TC_LICHSU_012_AddLichSu_MaPinNull_ThrowsException() {
        LichSuPinTram ls = new LichSuPinTram();
        ls.setHanhDong("THEM_PIN");
        assertThrows(ResponseStatusException.class, () -> lichSuPinTramService.save(ls));
    }

    @Test
    void TC_LICHSU_013_AddLichSu_MaPinZero_ThrowsException() {
        LichSuPinTram ls = new LichSuPinTram();
        ls.setHanhDong("THEM_PIN");
        ls.setMaPin(0L);
        assertThrows(ResponseStatusException.class, () -> lichSuPinTramService.save(ls));
    }

    @Test
    void TC_LICHSU_014_AddLichSu_MaTramNull_ThrowsException() {
        LichSuPinTram ls = new LichSuPinTram();
        ls.setHanhDong("THEM_PIN");
        ls.setMaPin(1L);
        assertThrows(ResponseStatusException.class, () -> lichSuPinTramService.save(ls));
    }

    @Test
    void TC_LICHSU_015_AddLichSu_MaTramZero_ThrowsException() {
        LichSuPinTram ls = new LichSuPinTram();
        ls.setHanhDong("THEM_PIN");
        ls.setMaPin(1L);
        ls.setMaTram(0L);
        assertThrows(ResponseStatusException.class, () -> lichSuPinTramService.save(ls));
    }
}