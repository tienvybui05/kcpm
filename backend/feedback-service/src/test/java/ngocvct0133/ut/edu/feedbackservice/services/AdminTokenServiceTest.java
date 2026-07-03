package ngocvct0133.ut.edu.feedbackservice.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminTokenService - Whitebox Tests")
@SuppressWarnings({"rawtypes", "unchecked"})
class AdminTokenServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AdminTokenService adminTokenService;

    // =========================================================================
    // layTokenAdmin
    // =========================================================================
    @Nested
    @DisplayName("layTokenAdmin()")
    class LayTokenAdmin {

        @Test
        @DisplayName("TC01 - Gọi API thành công, trả về danh sách token")
        void layTokenAdmin_thanhCong() {
            // Arrange
            List<String> mockTokens = List.of("adminToken1", "adminToken2");
            ResponseEntity<List<String>> responseEntity = new ResponseEntity<>(mockTokens, HttpStatus.OK);

            when(restTemplate.exchange(
                    anyString(),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            // Act
            List<String> result = adminTokenService.layTokenAdmin();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("adminToken1", result.get(0));
            assertEquals("adminToken2", result.get(1));
        }

        @Test
        @DisplayName("TC02 - Gọi API thành công nhưng body = null → return List.of()")
        void layTokenAdmin_bodyNull() {
            // Arrange
            ResponseEntity<List<String>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

            when(restTemplate.exchange(
                    anyString(),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            // Act
            List<String> result = adminTokenService.layTokenAdmin();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("TC03 - Exception xảy ra → return List.of()")
        void layTokenAdmin_exception() {
            // Arrange
            when(restTemplate.exchange(
                    anyString(),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenThrow(new RuntimeException("Connection timeout"));

            // Act
            List<String> result = adminTokenService.layTokenAdmin();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // =========================================================================
    // layTokenTaiXe
    // =========================================================================
    @Nested
    @DisplayName("layTokenTaiXe()")
    class LayTokenTaiXe {

        @Test
        @DisplayName("TC04 - Gọi API thành công, trả về danh sách token tài xế")
        void layTokenTaiXe_thanhCong() {
            // Arrange
            LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
            map1.put("token", "driverToken1");
            List<Object> mockBody = List.of(map1);
            ResponseEntity<List> responseEntity = new ResponseEntity<>(mockBody, HttpStatus.OK);

            when(restTemplate.getForEntity(anyString(), eq(List.class))).thenReturn(responseEntity);

            // Act
            List<String> result = adminTokenService.layTokenTaiXe(100L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("driverToken1", result.get(0));
        }

        @Test
        @DisplayName("TC05 - Body null/empty → return List.of()")
        void layTokenTaiXe_bodyNullOrEmpty() {
            // Arrange (Trường hợp body null)
            ResponseEntity<List> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
            when(restTemplate.getForEntity(anyString(), eq(List.class))).thenReturn(responseEntity);

            // Act
            List<String> resultNull = adminTokenService.layTokenTaiXe(99L);

            // Assert
            assertNotNull(resultNull);
            assertTrue(resultNull.isEmpty());

            // Arrange (Trường hợp body empty)
            ResponseEntity<List> responseEntityEmpty = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            when(restTemplate.getForEntity(anyString(), eq(List.class))).thenReturn(responseEntityEmpty);

            // Act
            List<String> resultEmpty = adminTokenService.layTokenTaiXe(99L);

            // Assert
            assertNotNull(resultEmpty);
            assertTrue(resultEmpty.isEmpty());
        }

        @Test
        @DisplayName("TC06 - Exception xảy ra → return List.of()")
        void layTokenTaiXe_exception() {
            // Arrange
            when(restTemplate.getForEntity(anyString(), eq(List.class)))
                    .thenThrow(new RuntimeException("Internal Server Error"));

            // Act
            List<String> result = adminTokenService.layTokenTaiXe(500L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
