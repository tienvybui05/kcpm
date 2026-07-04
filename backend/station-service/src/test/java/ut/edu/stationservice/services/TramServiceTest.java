package ut.edu.stationservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ut.edu.stationservice.models.Tram;
import ut.edu.stationservice.repositories.ITramRepository;

@ExtendWith(MockitoExtension.class)
public class TramServiceTest {

    @Mock
    private ITramRepository tramRepository;

    @InjectMocks
    private TramService tramService;

    private Tram validTram;

    @BeforeEach
    void setUp() {
        // Tạo một Trạm chuẩn, qua được mọi Validation để làm base cho các test
        validTram = new Tram();
        validTram.setMaTram(1L);
        validTram.setTenTram("Trạm chuẩn mực");
        validTram.setDiaChi("123 Đường BVA");
        validTram.setKinhDo(106.6297);
        validTram.setViDo(10.8231);
        validTram.setSoLuongPinToiDa(100);
        validTram.setSoDT("0901234567");
        validTram.setTrangThai("Hoạt động");
    }

    // ==========================================
    // KHỐI TEST CHO HÀM addPin() (Dựa trên BVA)
    // ==========================================

    @Test
    void testAddPin_Success() {
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        Tram savedTram = tramService.addPin(validTram);
        assertNotNull(savedTram);
        verify(tramRepository, times(1)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: tenTram (HÀM addPin)
    // ==========================================

    @Test
    void TC_AddPin_TenTram_Null() {
        validTram.setTenTram(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("Tên trạm rỗng", ex.getMessage());
    }

    @Test
    void TC_AddPin_TenTram_EmptyOrWhitespace() {
        // Nhánh 1: Chuỗi rỗng hoàn toàn
        validTram.setTenTram("");
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("Tên trạm rỗng", ex1.getMessage());

        // Nhánh 2: Chuỗi toàn khoảng trắng (để test lệnh .trim().isEmpty())
        validTram.setTenTram("     ");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("Tên trạm rỗng", ex2.getMessage());
    }

    @Test
    void TC_AddPin_TenTram_TrungLap() {
        validTram.setTenTram("Trạm Ngã Tư Sở");
        // Mock DB trả về true -> giả lập trường hợp tên đã tồn tại
        when(tramRepository.existsByTenTram("Trạm Ngã Tư Sở")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("❌ Tên trạm trùng", ex.getMessage());
    }

    @Test
    void TC_AddPin_TenTram_Vuot150KiTu() {
        // Tạo chuỗi 151 kí tự (Biên ngoài - Invalid)
        String longName = "A".repeat(151);
        validTram.setTenTram(longName);

        // Phải mock DB trả về false để nó đi qua được ải trùng tên, lọt xuống ải check length
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("Tên trạm lố 150 kí tự", ex.getMessage());
    }

    @Test
    void TC_AddPin_TenTram_Bien150KiTu_HopLe() {
        // Tạo chuỗi đúng 150 kí tự (Biên trong - Valid) để chắc chắn logic <= 150 là chạy đúng
        String exact150Name = "A".repeat(150);
        validTram.setTenTram(exact150Name);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        // Không throw ra lỗi nào là pass
        assertDoesNotThrow(() -> tramService.addPin(validTram));
        verify(tramRepository, times(1)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: diaChi (HÀM addPin)
    // ==========================================

    @Test
    void TC_AddPin_DiaChi_Null() {
        validTram.setDiaChi(null);

        // Cần lọt qua ải trùng tên trạm trước khi tới ải địa chỉ
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("Địa chỉ rỗng", ex.getMessage());
    }

    @Test
    void TC_AddPin_DiaChi_EmptyOrWhitespace() {
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        // Nhánh 1: Chuỗi rỗng hoàn toàn
        validTram.setDiaChi("");
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("Địa chỉ rỗng", ex1.getMessage());

        // Nhánh 2: Chuỗi toàn khoảng trắng (để test lệnh .trim().isEmpty())
        validTram.setDiaChi("     ");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("Địa chỉ rỗng", ex2.getMessage());
    }

    @Test
    void TC_AddPin_DiaChi_Vuot250KiTu() {
        // Tạo chuỗi 251 kí tự (Biên ngoài - Invalid)
        String longAddress = "A".repeat(251);
        validTram.setDiaChi(longAddress);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("Địa chỉ lố 250 kí tự", ex.getMessage());
    }

    @Test
    void TC_AddPin_DiaChi_Bien250KiTu_HopLe() {
        // Tạo chuỗi đúng 250 kí tự (Biên trong - Valid) để test điều kiện > 250
        String exact250Address = "A".repeat(250);
        validTram.setDiaChi(exact250Address);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        // Phải pass mượt mà, không quăng lỗi
        assertDoesNotThrow(() -> tramService.addPin(validTram));
        verify(tramRepository, times(1)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: kinhDo (HÀM addPin)
    // ==========================================

    @Test
    void TC_AddPin_KinhDo_Null() {
        validTram.setKinhDo(null);

        // Vẫn phải vượt ải trùng tên trạm
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("kinh độ bị rỗng", ex.getMessage());
    }

    @Test
    void TC_AddPin_KinhDo_VuotBienDuoi() {
        // Giá trị nhỏ hơn -180 (Biên ngoài - Invalid)
        validTram.setKinhDo(-180.0001);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("kinh độ vượt biên (<-180)", ex.getMessage());
    }

    @Test
    void TC_AddPin_KinhDo_VuotBienTren() {
        // Giá trị lớn hơn 180 (Biên ngoài - Invalid)
        validTram.setKinhDo(180.0001);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("kinh độ vượt biên (>180)", ex.getMessage());
    }

    @Test
    void TC_AddPin_KinhDo_CacBienHopLe() {
        // Test các giá trị ngay tại biên và giữa biên (Valid)
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        // 1. Ngay vạch biên dưới (-180.0)
        validTram.setKinhDo(-180.0);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 2. Ngay vạch biên trên (180.0)
        validTram.setKinhDo(180.0);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 3. Giá trị bình thường ở giữa (ví dụ TP.HCM)
        validTram.setKinhDo(106.6297);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // Chạy 3 lần thành công thì hàm save phải được gọi 3 lần
        verify(tramRepository, times(3)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: viDo (HÀM addPin)
    // ==========================================

    @Test
    void TC_AddPin_ViDo_Null() {
        validTram.setViDo(null);

        // Vượt ải trùng tên trạm
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        // Lưu ý: Chữ "V" viết hoa theo đúng code gốc của ông
        assertEquals("Vĩ độ bị rỗng", ex.getMessage());
    }

    @Test
    void TC_AddPin_ViDo_VuotBienDuoi() {
        // Giá trị nhỏ hơn -90 (Biên ngoài - Invalid)
        validTram.setViDo(-90.0001);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("vĩ độ vượt biên (<-90)", ex.getMessage());
    }

    @Test
    void TC_AddPin_ViDo_VuotBienTren() {
        // Giá trị lớn hơn 90 (Biên ngoài - Invalid)
        validTram.setViDo(90.0001);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("vĩ độ vượt biên (>90)", ex.getMessage());
    }

    @Test
    void TC_AddPin_ViDo_CacBienHopLe() {
        // Test các giá trị ngay tại biên và giữa biên (Valid)
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        // 1. Ngay vạch biên dưới (-90.0)
        validTram.setViDo(-90.0);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 2. Ngay vạch biên trên (90.0)
        validTram.setViDo(90.0);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 3. Giá trị bình thường ở giữa
        validTram.setViDo(10.8231);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // Chạy 3 lần thành công thì hàm save phải được gọi 3 lần
        verify(tramRepository, times(3)).save(validTram);
    }


    // ==========================================
    // TEST THUỘC TÍNH: soLuongPinToiDa (HÀM addPin)
    // ==========================================

    @Test
    void TC_AddPin_SoLuongPin_Null() {
        validTram.setSoLuongPinToiDa(null);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("pin rỗng", ex.getMessage());
    }

    @Test
    void TC_AddPin_SoLuongPin_Am() {
        // Biên ngoài (Invalid) - Nhỏ hơn 0
        validTram.setSoLuongPinToiDa(-1);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("pin âm", ex.getMessage());
    }

    @Test
    void TC_AddPin_SoLuongPin_Vuot200() {
        // Biên ngoài (Invalid) - Lớn hơn 200
        validTram.setSoLuongPinToiDa(201);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("số lượng pin tối đa > 200", ex.getMessage());
    }

    @Test
    void TC_AddPin_SoLuongPin_CacBienHopLe() {
        // Test các giá trị ngay tại biên và giữa biên (Valid)
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        // 1. Ngay vạch biên dưới (0 pin)
        validTram.setSoLuongPinToiDa(0);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 2. Ngay vạch biên trên (200 pin)
        validTram.setSoLuongPinToiDa(200);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 3. Giá trị bình thường ở giữa
        validTram.setSoLuongPinToiDa(100);
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // Chạy 3 lần thành công thì hàm save phải được gọi 3 lần
        verify(tramRepository, times(3)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: soDT (HÀM addPin)
    // ==========================================

    @Test
    void TC_AddPin_SoDT_Null() {
        validTram.setSoDT(null);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("số điện thoại rỗng", ex.getMessage());
    }

    @Test
    void TC_AddPin_SoDT_EmptyOrWhitespace() {
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        // Nhánh 1: Rỗng hoàn toàn
        validTram.setSoDT("");
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("số điện thoại rỗng", ex1.getMessage());

        // Nhánh 2: Chỉ chứa khoảng trắng
        validTram.setSoDT("     ");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("số điện thoại rỗng", ex2.getMessage());
    }

    @Test
    void TC_AddPin_SoDT_SaiKieuDuLieu() {
        // Chứa chữ cái thay vì số (test hàm .matches("^[0-9]+$"))
        validTram.setSoDT("090ABCDEFG");

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("số điện thoại sai kiểu dữ liệu", ex.getMessage());
    }

    @Test
    void TC_AddPin_SoDT_NhoHon10() {
        // Chuỗi có 9 số (Biên ngoài - Invalid)
        validTram.setSoDT("123456789");

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("số điện thoại nhỏ hơn 10 số", ex.getMessage());
    }

    @Test
    void TC_AddPin_SoDT_LonHon11() {
        // Chuỗi có 12 số (Biên ngoài - Invalid)
        validTram.setSoDT("123456789012");

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("số điện thoại lớn hơn 11 số", ex.getMessage());
    }

    @Test
    void TC_AddPin_SoDT_CacBienHopLe() {
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        // 1. Biên 10 số (Valid)
        validTram.setSoDT("0123456789");
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 2. Biên 11 số (Valid)
        validTram.setSoDT("01234567890");
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        verify(tramRepository, times(2)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: trangThai (HÀM addPin)
    // ==========================================

    @Test
    void TC_AddPin_TrangThai_Null() {
        validTram.setTrangThai(null);

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("trạng thái rỗng", ex.getMessage());
    }

    @Test
    void TC_AddPin_TrangThai_EmptyOrWhitespace() {
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        // Nhánh 1: Rỗng hoàn toàn
        validTram.setTrangThai("");
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("trạng thái rỗng", ex1.getMessage());

        // Nhánh 2: Chỉ chứa khoảng trắng
        validTram.setTrangThai("     ");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("trạng thái rỗng", ex2.getMessage());
    }

    @Test
    void TC_AddPin_TrangThai_SaiDuLieu() {
        // Trạng thái bậy bạ không nằm trong 3 giá trị cho phép
        validTram.setTrangThai("Đang cháy");

        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.addPin(validTram));
        assertEquals("trangThai sai dữ liệu", ex.getMessage());
    }

    @Test
    void TC_AddPin_TrangThai_CacBienHopLe() {
        when(tramRepository.existsByTenTram(anyString())).thenReturn(false);
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        // 1. Trạng thái hợp lệ 1: Hoạt động
        validTram.setTrangThai("Hoạt động");
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 2. Trạng thái hợp lệ 2: Bảo trì
        validTram.setTrangThai("Bảo trì");
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // 3. Trạng thái hợp lệ 3: Tạm dừng
        validTram.setTrangThai("Tạm dừng");
        assertDoesNotThrow(() -> tramService.addPin(validTram));

        // Chạy 3 lần thành công thì hàm save phải được gọi 3 lần
        verify(tramRepository, times(3)).save(validTram);
    }

    // ====================================================================
    // KHỐI TEST CHO HÀM updatePin()
    // ====================================================================

    @Test
    void TC_UpdatePin_Success() {
        Tram existingTram = new Tram();
        existingTram.setTenTram("Trạm cũ");

        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(existingTram));
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        Tram updatedTram = tramService.updatePin(validTram);
        assertNotNull(updatedTram);
        verify(tramRepository, times(1)).save(existingTram);
    }

    @Test
    void TC_UpdatePin_NotFound() {
        // Mock không tìm thấy ID trong DB (Trường hợp ném lỗi NoSuchElementException)
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> tramService.updatePin(validTram));
        assertEquals("Không tìm thấy trạm", ex.getMessage());
    }

    // ==========================================
    // TEST THUỘC TÍNH: tenTram (HÀM updatePin)
    // ==========================================

    @Test
    void TC_UpdatePin_TenTram_Null() {
        Tram existingTram = new Tram();
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(existingTram));

        validTram.setTenTram(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("Tên trạm rỗng", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_TenTram_EmptyOrWhitespace() {
        Tram existingTram = new Tram();
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(existingTram));

        // Nhánh 1: Chuỗi rỗng hoàn toàn
        validTram.setTenTram("");
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("Tên trạm rỗng", ex1.getMessage());

        // Nhánh 2: Chuỗi toàn khoảng trắng
        validTram.setTenTram("     ");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("Tên trạm rỗng", ex2.getMessage());
    }

    @Test
    void TC_UpdatePin_TenTram_Vuot150KiTu() {
        Tram existingTram = new Tram();
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(existingTram));

        // Tạo chuỗi 151 kí tự (Biên ngoài - Invalid)
        String longName = "A".repeat(151);
        validTram.setTenTram(longName);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("Tên trạm lố 150 kí tự", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_TenTram_TrungLap() {
        Tram existingTram = new Tram();
        existingTram.setTenTram("Trạm Cũ A"); // Tên hiện tại trong DB đang là Trạm Cũ A

        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(existingTram));

        // Cố tình đổi sang tên mới là Trạm Cũ B, nhưng tên này lại bị trùng với 1 trạm khác dưới DB
        validTram.setTenTram("Trạm Cũ B");
        when(tramRepository.existsByTenTram("Trạm Cũ B")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("Tên trạm trùng", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_TenTram_GiuNguyenTenCu_HopLe() {
        Tram existingTram = new Tram();
        existingTram.setTenTram("Trạm Cũ A");

        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(existingTram));

        // Test nhánh logic phức tạp: Không đổi tên (giữ nguyên tên cũ) thì không được báo lỗi trùng lặp
        validTram.setTenTram("Trạm Cũ A");
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        assertDoesNotThrow(() -> tramService.updatePin(validTram));
        verify(tramRepository, times(1)).save(existingTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: diaChi (HÀM updatePin)
    // ==========================================

    @Test
    void TC_UpdatePin_DiaChi_Null() {
        // DÙNG validTram ĐỂ MOCK ĐỠ BỊ NULL POINTER EXCEPTION
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setDiaChi(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("Địa chỉ rỗng", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_DiaChi_EmptyOrWhitespace() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setDiaChi("");
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("Địa chỉ rỗng", ex1.getMessage());

        validTram.setDiaChi("     ");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("Địa chỉ rỗng", ex2.getMessage());
    }

    @Test
    void TC_UpdatePin_DiaChi_Vuot250KiTu() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setDiaChi("A".repeat(251));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("địa chỉ lố 250 kí tự", ex.getMessage());
    }

    // ==========================================
    // TEST THUỘC TÍNH: kinhDo (HÀM updatePin)
    // ==========================================

    @Test
    void TC_UpdatePin_KinhDo_Null() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setKinhDo(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("kinh độ bị rỗng", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_KinhDo_VuotBienDuoi() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setKinhDo(-180.0001);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("kinh độ vượt biên (<-180)", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_KinhDo_VuotBienTren() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setKinhDo(180.0001);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("kinh độ vượt biên (>180)", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_KinhDo_CacBienHopLe() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        validTram.setKinhDo(-180.0);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setKinhDo(180.0);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setKinhDo(106.6297);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        verify(tramRepository, times(3)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: viDo (HÀM updatePin)
    // ==========================================

    @Test
    void TC_UpdatePin_ViDo_Null() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setViDo(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("vĩ độ bị rỗng", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_ViDo_VuotBienDuoi() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setViDo(-90.0001);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("vĩ độ vượt biên (<-90)", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_ViDo_VuotBienTren() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setViDo(90.0001);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("vĩ độ vượt biên (>90)", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_ViDo_CacBienHopLe() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        validTram.setViDo(-90.0);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setViDo(90.0);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setViDo(10.8231);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        verify(tramRepository, times(3)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: soLuongPinToiDa (HÀM updatePin)
    // ==========================================

    @Test
    void TC_UpdatePin_SoLuongPin_Null() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setSoLuongPinToiDa(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("pin rỗng", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_SoLuongPin_Am() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setSoLuongPinToiDa(-1);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("pin âm", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_SoLuongPin_Vuot200() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setSoLuongPinToiDa(201);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("số lượng pin tối đa > 200", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_SoLuongPin_CacBienHopLe() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        validTram.setSoLuongPinToiDa(0);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setSoLuongPinToiDa(200);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setSoLuongPinToiDa(100);
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        verify(tramRepository, times(3)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: soDT (HÀM updatePin)
    // ==========================================

    @Test
    void TC_UpdatePin_SoDT_Null() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setSoDT(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("số điện thoại rỗng", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_SoDT_EmptyOrWhitespace() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setSoDT("");
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("số điện thoại rỗng", ex1.getMessage());

        validTram.setSoDT("     ");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("số điện thoại rỗng", ex2.getMessage());
    }

    @Test
    void TC_UpdatePin_SoDT_SaiKieuDuLieu() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setSoDT("090ABCDEFG");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("số điện thoại sai kiểu dữ liệu", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_SoDT_NhoHon10() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setSoDT("123456789");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("số điện thoại nhỏ hơn 10 số", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_SoDT_LonHon11() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setSoDT("123456789012");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("số điện thoại lớn hơn 11 số", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_SoDT_CacBienHopLe() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        validTram.setSoDT("0123456789");
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setSoDT("01234567890");
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        verify(tramRepository, times(2)).save(validTram);
    }

    // ==========================================
    // TEST THUỘC TÍNH: trangThai (HÀM updatePin)
    // ==========================================

    @Test
    void TC_UpdatePin_TrangThai_Null() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setTrangThai(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("trạng thái rỗng", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_TrangThai_EmptyOrWhitespace() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setTrangThai("");
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("trạng thái rỗng", ex1.getMessage());

        validTram.setTrangThai("     ");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("trạng thái rỗng", ex2.getMessage());
    }

    @Test
    void TC_UpdatePin_TrangThai_SaiDuLieu() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));

        validTram.setTrangThai("Đang cháy");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tramService.updatePin(validTram));
        assertEquals("trangThai sai dữ liệu", ex.getMessage());
    }

    @Test
    void TC_UpdatePin_TrangThai_CacBienHopLe() {
        when(tramRepository.findById(validTram.getMaTram())).thenReturn(Optional.of(validTram));
        when(tramRepository.save(any(Tram.class))).thenReturn(validTram);

        validTram.setTrangThai("Hoạt động");
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setTrangThai("Bảo trì");
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        validTram.setTrangThai("Tạm dừng");
        assertDoesNotThrow(() -> tramService.updatePin(validTram));

        verify(tramRepository, times(3)).save(validTram);
    }

    // ==========================================
    // TEST CHO CÁC HÀM CRUD CÒN LẠI
    // ==========================================

    @Test
    void TC_DeleteById_True() {
        when(tramRepository.existsById(1L)).thenReturn(true);
        boolean result = tramService.deleteById(1L);
        assertTrue(result);
        verify(tramRepository, times(1)).deleteById(1L);
    }

    @Test
    void TC_DeleteById_False() {
        when(tramRepository.existsById(1L)).thenReturn(false);
        boolean result = tramService.deleteById(1L);
        assertFalse(result);
        verify(tramRepository, never()).deleteById(anyLong());
    }

    @Test
    void TC_FindByTramId() {
        when(tramRepository.findAll()).thenReturn(List.of(validTram));
        List<Tram> result = tramService.findByTramId(null);
        assertEquals(1, result.size());
    }

    @Test
    void TC_FindById_Found() {
        when(tramRepository.findById(1L)).thenReturn(Optional.of(validTram));
        Tram result = tramService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getMaTram());
    }

    @Test
    void TC_FindById_NotFound() {
        when(tramRepository.findById(1L)).thenReturn(Optional.empty());
        Tram result = tramService.findById(1L);
        assertNull(result);
    }

    @Test
    void TC_Save_Success() {
        when(tramRepository.save(validTram)).thenReturn(validTram);
        Tram result = tramService.save(validTram);
        assertEquals(validTram, result);
    }

    @Test
    void TC_AddNhieuTram_Success() {
        List<Tram> list = List.of(validTram);
        when(tramRepository.saveAll(list)).thenReturn(list);
        List<Tram> result = tramService.addNhieuTram(list);
        assertEquals(1, result.size());
    }
}