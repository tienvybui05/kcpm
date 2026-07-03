package ut.edu.stationservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import ut.edu.stationservice.models.LichSuDatPin;
import ut.edu.stationservice.models.Tram;
import ut.edu.stationservice.repositories.ILichSuDatPinRepository;
import ut.edu.stationservice.repositories.ITramRepository;

@ExtendWith(MockitoExtension.class)
public class LichSuDatPinServiceTest {

    @Mock
    private ILichSuDatPinRepository lichSuDatPinRepository;

    @Mock
    private ITramRepository tramRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LichSuDatPinService lichSuDatPinService;

    private LichSuDatPin validLichSu;
    private Tram validTram;

    @BeforeEach
    void setUp() {
        validTram = new Tram();
        validTram.setMaTram(1L);
        validTram.setTenTram("Trạm A");

        validLichSu = new LichSuDatPin();
        validLichSu.setMaTaiXe(10L);
        validLichSu.setTram(validTram);
        validLichSu.setNgayDat(LocalDateTime.now());
        validLichSu.setTrangThaiXacNhan("Chờ xác nhận");
        validLichSu.setTrangThaiDoiPin("Chưa đổi pin");
        validLichSu.setMaXeGiaoDich(50L);
        validLichSu.setMaPinDuocGiu(99L);
    }

    // ==========================================
    // KHỐI TEST CRUD CƠ BẢN
    // ==========================================

    @Test
    void TC_FindAll() {
        when(lichSuDatPinRepository.findAll()).thenReturn(List.of(validLichSu));
        List<LichSuDatPin> result = lichSuDatPinService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void TC_FindById_Found() {
        when(lichSuDatPinRepository.findById(100L)).thenReturn(Optional.of(validLichSu));
        LichSuDatPin result = lichSuDatPinService.findById(100L);
        assertNotNull(result);
    }

    @Test
    void TC_FindById_NotFound() {
        when(lichSuDatPinRepository.findById(100L)).thenReturn(Optional.empty());
        LichSuDatPin result = lichSuDatPinService.findById(100L);
        assertNull(result);
    }

    @Test
    void TC_Save() {
        when(lichSuDatPinRepository.save(any(LichSuDatPin.class))).thenReturn(validLichSu);
        LichSuDatPin result = lichSuDatPinService.save(validLichSu);
        assertEquals(validLichSu, result);
    }

    @Test
    void TC_DeleteById_Exists() {
        when(lichSuDatPinRepository.existsById(100L)).thenReturn(true);
        boolean result = lichSuDatPinService.deleteById(100L);
        assertTrue(result);
        verify(lichSuDatPinRepository, times(1)).deleteById(100L);
    }

    @Test
    void TC_DeleteById_NotExists() {
        when(lichSuDatPinRepository.existsById(100L)).thenReturn(false);
        boolean result = lichSuDatPinService.deleteById(100L);
        assertFalse(result);
        verify(lichSuDatPinRepository, never()).deleteById(anyLong());
    }

    @Test
    void TC_FindByMaTram() {
        when(lichSuDatPinRepository.findByTram_MaTram(1L)).thenReturn(List.of(validLichSu));
        List<LichSuDatPin> result = lichSuDatPinService.findByMaTram(1L);
        assertEquals(1, result.size());
    }

    @Test
    void TC_FindByMaTramAndTrangThai() {
        when(lichSuDatPinRepository.findActiveQueueByTramAndStatus(1L, "Chờ xác nhận")).thenReturn(List.of(validLichSu));
        List<LichSuDatPin> result = lichSuDatPinService.findByMaTramAndTrangThai(1L, "Chờ xác nhận");
        assertEquals(1, result.size());
    }

    // ==========================================
    // KHỐI TEST NGHIỆP VỤ: ĐẶT LỊCH
    // ==========================================

    @Test
    void TC_DatLich_LoiTrungDonChuaXong() {
        // Giả lập DB trả về 1 đơn cũ đang "Chờ xác nhận", cùng trạm, cùng xe
        when(lichSuDatPinRepository.findByMaTaiXe(10L)).thenReturn(List.of(validLichSu));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> lichSuDatPinService.datLich(10L, 1L, 50L, 99L));
        assertEquals("Bạn đang có đơn đổi pin chưa hoàn thành cùng một trạm.", ex.getMessage());
    }

    @Test
    void TC_DatLich_LoiKhongTimThayTram() {
        // DB trả về rỗng (tài xế không có đơn trùng) để lọt qua ải 1
        when(lichSuDatPinRepository.findByMaTaiXe(10L)).thenReturn(new ArrayList<>());
        // Giả lập không tìm thấy trạm
        when(tramRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> lichSuDatPinService.datLich(10L, 1L, 50L, 99L));
        assertEquals("Không tìm thấy trạm ID: 1", ex.getMessage());
    }

    @Test
    void TC_DatLich_LoiThieuMaPin() {
        when(lichSuDatPinRepository.findByMaTaiXe(10L)).thenReturn(new ArrayList<>());
        when(tramRepository.findById(1L)).thenReturn(Optional.of(validTram));

        // Truyền null vào maPinDuocGiu
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> lichSuDatPinService.datLich(10L, 1L, 50L, null));
        assertEquals("Thiếu mã pin được giữ chỗ!", ex.getMessage());
    }

    @Test
    void TC_DatLich_Success() {
        when(lichSuDatPinRepository.findByMaTaiXe(10L)).thenReturn(new ArrayList<>());
        when(tramRepository.findById(1L)).thenReturn(Optional.of(validTram));
        when(lichSuDatPinRepository.save(any(LichSuDatPin.class))).thenReturn(validLichSu);

        LichSuDatPin result = lichSuDatPinService.datLich(10L, 1L, 50L, 99L);
        assertNotNull(result);
        assertEquals("Chờ xác nhận", result.getTrangThaiXacNhan());
    }

    // ==========================================
    // KHỐI TEST NGHIỆP VỤ: CẬP NHẬT TRẠNG THÁI
    // ==========================================

    @Test
    void TC_CapNhatTrangThai_NotFound() {
        when(lichSuDatPinRepository.findById(100L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> lichSuDatPinService.capNhatTrangThai(100L, "X", "Y", 1L));
        assertEquals("Không tìm thấy lịch sử đặt pin với ID: 100", ex.getMessage());
    }

    @Test
    void TC_CapNhatTrangThai_Success_AllFields() {
        when(lichSuDatPinRepository.findById(100L)).thenReturn(Optional.of(validLichSu));
        when(lichSuDatPinRepository.save(any(LichSuDatPin.class))).thenReturn(validLichSu);

        lichSuDatPinService.capNhatTrangThai(100L, "Đã xác nhận", "Đã đổi pin", 200L);

        verify(lichSuDatPinRepository, times(1)).save(validLichSu);
        assertEquals("Đã xác nhận", validLichSu.getTrangThaiXacNhan());
        assertEquals("Đã đổi pin", validLichSu.getTrangThaiDoiPin());
        assertEquals(200L, validLichSu.getMaGiaoDichDoiPin());
    }

    @Test
    void TC_CapNhatTrangThai_Success_PartialNullFields() {
        // Kịch bản test 3 lệnh if (trường == null thì không cập nhật)
        when(lichSuDatPinRepository.findById(100L)).thenReturn(Optional.of(validLichSu));
        when(lichSuDatPinRepository.save(any(LichSuDatPin.class))).thenReturn(validLichSu);

        lichSuDatPinService.capNhatTrangThai(100L, null, null, null);

        // Trạng thái giữ nguyên như lúc khởi tạo
        verify(lichSuDatPinRepository, times(1)).save(validLichSu);
        assertEquals("Chờ xác nhận", validLichSu.getTrangThaiXacNhan());
    }

    // ==========================================
    // KHỐI TEST NGHIỆP VỤ: FIND BY MÃ TÀI XẾ (STREAM FILTER)
    // ==========================================

    @Test
    void TC_FindByMaTaiXe_StreamFilter_Coverage() {
        // Tạo 4 object để vét cạn các nhánh của biểu thức Lambda filter:
        // !Hủy && (trangThaiDoiPin == null || !Hoàn thành)

        // 1. Nhánh: Bị "Hủy" -> Lọc bỏ (Short-circuit False)
        LichSuDatPin ls1 = new LichSuDatPin();
        ls1.setTrangThaiXacNhan("Hủy");

        // 2. Nhánh: Không hủy, trạng thái đổi pin là NULL -> Giữ lại (Pass đk 1, đk 2.1)
        LichSuDatPin ls2 = new LichSuDatPin();
        ls2.setTrangThaiXacNhan("Đã xác nhận");
        ls2.setTrangThaiDoiPin(null);

        // 3. Nhánh: Không hủy, trạng thái đổi pin là "Hoàn thành" -> Lọc bỏ (Pass đk 1, đk 2.1 fail, đk 2.2 fail)
        LichSuDatPin ls3 = new LichSuDatPin();
        ls3.setTrangThaiXacNhan("Chờ xác nhận");
        ls3.setTrangThaiDoiPin("Hoàn thành");

        // 4. Nhánh: Không hủy, trạng thái đổi pin khác "Hoàn thành" -> Giữ lại (Pass hết)
        LichSuDatPin ls4 = new LichSuDatPin();
        ls4.setTrangThaiXacNhan("Đã xác nhận");
        ls4.setTrangThaiDoiPin("Chưa đổi pin");

        when(lichSuDatPinRepository.findByMaTaiXe(10L)).thenReturn(List.of(ls1, ls2, ls3, ls4));

        List<LichSuDatPin> result = lichSuDatPinService.findByMaTaiXe(10L);

        // Phải lọc ra được ls2 và ls4 (size = 2)
        assertEquals(2, result.size());
    }

    // ==========================================
    // KHỐI TEST NGHIỆP VỤ: HỦY ĐƠN & AUTO CANCEL
    // ==========================================

    @Test
    void TC_HuyDon_NotFound() {
        when(lichSuDatPinRepository.findById(100L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> lichSuDatPinService.huyDon(100L));
        assertEquals("Không tìm thấy đơn", ex.getMessage());
    }

    @Test
    void TC_HuyDon_Success() {
        when(lichSuDatPinRepository.findById(100L)).thenReturn(Optional.of(validLichSu));

        lichSuDatPinService.huyDon(100L);

        verify(lichSuDatPinRepository, times(1)).save(validLichSu);
        assertEquals("Hủy", validLichSu.getTrangThaiXacNhan());
        assertEquals("Tài xế hủy", validLichSu.getTrangThaiDoiPin());
    }

    @Test
    void TC_AutoCancelExpiredBookings() {
        // Giả lập có 1 đơn quá hạn cần bị hủy
        when(lichSuDatPinRepository.findByTrangThaiXacNhanAndNgayDatBefore(
                eq("Chờ xác nhận"), any(LocalDateTime.class))).thenReturn(List.of(validLichSu));

        // Mock restTemplate.patchForObject để nó không ném lỗi kết nối
        when(restTemplate.patchForObject(anyString(), any(Map.class), eq(Void.class))).thenReturn(null);

        lichSuDatPinService.autoCancelExpiredBookings();

        // Kiểm tra rest template đã được gọi
        verify(restTemplate, times(1)).patchForObject(
                eq("http://gateway:8080/api/battery-service/pins/99/state"),
                any(Map.class),
                eq(Void.class)
        );

        // Kiểm tra đã lưu lại trạng thái Hủy
        verify(lichSuDatPinRepository, times(1)).save(validLichSu);
        assertEquals("Hủy", validLichSu.getTrangThaiXacNhan());
        assertEquals("Quá hạn", validLichSu.getTrangThaiDoiPin());
    }

    @Test
    void TC_AutoCancelExpiredBookings_EmptyList() {
        // Giả lập vòng lặp forEach không chạy nhánh nào (list rỗng)
        when(lichSuDatPinRepository.findByTrangThaiXacNhanAndNgayDatBefore(
                anyString(), any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        lichSuDatPinService.autoCancelExpiredBookings();

        // Không có ai gọi API hay save DB cả
        verify(restTemplate, never()).patchForObject(anyString(), any(), any());
        verify(lichSuDatPinRepository, never()).save(any());
    }

    @Test
    void TC_DatLich_LambdaFilter_Coverage() {
        // Mục tiêu: Bắn tỉa 4 dấu vàng trong biểu thức (A || B) && C && D

        // 1. Ép A sai, B sai -> Ngắt mạch không chạy C, D
        LichSuDatPin ls1 = new LichSuDatPin();
        ls1.setTrangThaiXacNhan("Đã xác nhận"); // Khác "Chờ xác nhận" (A sai)
        ls1.setTrangThaiDoiPin("Đã đổi pin");   // Khác "Chưa đổi pin" (B sai)

        // 2. Ép A sai, B đúng, C sai -> Ngắt mạch không chạy D
        LichSuDatPin ls2 = new LichSuDatPin();
        ls2.setTrangThaiXacNhan("Đã xác nhận"); // A sai
        ls2.setTrangThaiDoiPin("Chưa đổi pin"); // B đúng -> Chạy tiếp sang C
        Tram tram2 = new Tram();
        tram2.setMaTram(99L);                   // Mã trạm khác 1L (C sai)
        ls2.setTram(tram2);

        // 3. Ép A đúng (bỏ qua B), C đúng, D sai
        LichSuDatPin ls3 = new LichSuDatPin();
        ls3.setTrangThaiXacNhan("Chờ xác nhận"); // A đúng -> Bỏ qua check B, chạy thẳng tới C
        Tram tram3 = new Tram();
        tram3.setMaTram(1L);                     // C đúng -> Chạy tiếp sang D
        ls3.setTram(tram3);
        ls3.setMaXeGiaoDich(99L);                // Mã xe khác 50L (D sai)

        // Bơm cả 3 đơn này vào DB giả
        when(lichSuDatPinRepository.findByMaTaiXe(10L)).thenReturn(List.of(ls1, ls2, ls3));
        when(tramRepository.findById(1L)).thenReturn(Optional.of(validTram));
        when(lichSuDatPinRepository.save(any(LichSuDatPin.class))).thenReturn(validLichSu);

        // Chạy hàm. Cả 3 đơn đều bị filter loại bỏ (vì D sai), list rỗng, không bị throw Exception
        LichSuDatPin result = lichSuDatPinService.datLich(10L, 1L, 50L, 99L);

        assertNotNull(result);
    }
}