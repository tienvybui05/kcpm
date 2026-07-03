package ut.edu.stationservice.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        // Dùng MockHttpServletRequest của Spring để giả lập request
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/trams");
    }

    @Test
    void TC_HandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Lỗi nghiệp vụ test");
        ResponseEntity<Object> response = exceptionHandler.handleRuntimeException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Lỗi nghiệp vụ test", body.get("message"));
    }

    @Test
    void TC_HandleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Không tìm thấy dữ liệu");
        ResponseEntity<Object> response = exceptionHandler.handleNoSuchElementException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Không tìm thấy dữ liệu", body.get("message"));
    }

    @Test
    void TC_HandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        ResponseEntity<Object> response = exceptionHandler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Sai kiểu dữ liệu đầu vào", body.get("message"));
    }

    // ========================================================
    // KHỐI TEST CHO SAI KIỂU DỮ LIỆU (HttpMessageNotReadable)
    // ========================================================

    @Test
    void TC_HandleMessageNotReadable_KinhDo() {
        // Giả lập lỗi parse JSON cho field "kinhDo"
        InvalidFormatException cause = mock(InvalidFormatException.class);
        JsonMappingException.Reference ref = new JsonMappingException.Reference(null, "kinhDo");
        when(cause.getPath()).thenReturn(List.of(ref));

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Lỗi", cause, null);

        ResponseEntity<Object> response = exceptionHandler.handleMessageNotReadable(ex, request);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Kinh độ sai kiểu dữ liệu", body.get("message"));
    }

    @Test
    void TC_HandleMessageNotReadable_ViDo() {
        // Giả lập lỗi parse JSON cho field "viDo"
        InvalidFormatException cause = mock(InvalidFormatException.class);
        JsonMappingException.Reference ref = new JsonMappingException.Reference(null, "viDo");
        when(cause.getPath()).thenReturn(List.of(ref));

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Lỗi", cause, null);

        ResponseEntity<Object> response = exceptionHandler.handleMessageNotReadable(ex, request);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Vĩ độ sai kiểu dữ liệu", body.get("message"));
    }

    @Test
    void TC_HandleMessageNotReadable_SoLuongPinToiDa() {
        // Giả lập lỗi parse JSON cho field "soLuongPinToiDa"
        InvalidFormatException cause = mock(InvalidFormatException.class);
        JsonMappingException.Reference ref = new JsonMappingException.Reference(null, "soLuongPinToiDa");
        when(cause.getPath()).thenReturn(List.of(ref));

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Lỗi", cause, null);

        ResponseEntity<Object> response = exceptionHandler.handleMessageNotReadable(ex, request);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("pin sai kiểu dữ liệu", body.get("message"));
    }

    @Test
    void TC_HandleMessageNotReadable_KhongPhaiInvalidFormat() {
        // Giả lập lỗi đọc JSON nhưng không phải do sai kiểu dữ liệu (vd: thiếu dấu phẩy)
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Lỗi cú pháp", (Throwable) null, null);

        ResponseEntity<Object> response = exceptionHandler.handleMessageNotReadable(ex, request);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Dữ liệu đầu vào không hợp lệ", body.get("message"));
    }

    @Test
    void TC_HandleMessageNotReadable_EmptyPath() {
        // Giả lập lỗi InvalidFormatException nhưng getPath() trả về danh sách RỖNG
        // Để ép nó đi vào nhánh False của dòng: if (!cause.getPath().isEmpty())
        InvalidFormatException cause = mock(InvalidFormatException.class);
        when(cause.getPath()).thenReturn(List.of()); // List rỗng

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Lỗi", cause, null);

        ResponseEntity<Object> response = exceptionHandler.handleMessageNotReadable(ex, request);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Dữ liệu đầu vào không hợp lệ", body.get("message"));
    }

    @Test
    void TC_HandleMessageNotReadable_UnknownField() {
        // Giả lập lỗi parse JSON cho một field LẠ (không phải kinhDo, viDo hay soLuongPinToiDa)
        // Để ép nó đi vào nhánh False của dòng else if cuối cùng
        InvalidFormatException cause = mock(InvalidFormatException.class);
        JsonMappingException.Reference ref = new JsonMappingException.Reference(null, "motTruongBatKy");
        when(cause.getPath()).thenReturn(List.of(ref));

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Lỗi", cause, null);

        ResponseEntity<Object> response = exceptionHandler.handleMessageNotReadable(ex, request);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Dữ liệu đầu vào không hợp lệ", body.get("message"));
    }
}