package ut.edu.stationservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import ut.edu.stationservice.models.Tram;
import ut.edu.stationservice.repositories.ITramRepository;

@ExtendWith(MockitoExtension.class)
public class TrafficServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ITramRepository tramRepository;

    @InjectMocks
    private TrafficService trafficService;

    private Tram tram1;
    private Tram tram2;

    @BeforeEach
    void setUp() {
        // Bơm API key giả vào biến @Value bằng Reflection
        ReflectionTestUtils.setField(trafficService, "apiKey", "fake-api-key");

        // Tạo 2 trạm để test vòng lặp và logic khoảng cách
        tram1 = new Tram();
        tram1.setMaTram(1L);
        tram1.setTenTram("Trạm 1");
        tram1.setTrangThai("Hoạt động");
        tram1.setViDo(10.0);
        tram1.setKinhDo(106.0);

        tram2 = new Tram();
        tram2.setMaTram(2L);
        tram2.setTenTram("Trạm 2");
        tram2.setTrangThai("Bảo trì"); // Trạm này sẽ bị filter loại bỏ
        tram2.setViDo(10.1);
        tram2.setKinhDo(106.1);
    }

    // =========================================================================
    // HÀM HELPER ĐỂ MOCK DỮ LIỆU JSON TỪ TOMTOM API
    // =========================================================================

    private ResponseEntity<Map<String, Object>> mockMatrixResponse() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("travelTimeInSeconds", 600);
        summary.put("lengthInMeters", 5000);

        Map<String, Object> entry = new HashMap<>();
        entry.put("destinationIndex", 0);
        entry.put("routeSummary", summary);

        Map<String, Object> response = new HashMap<>();
        response.put("data", List.of(entry));

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> mockRouteResponse() {
        Map<String, Object> p1 = Map.of("latitude", 10.0, "longitude", 106.0);
        Map<String, Object> p2 = Map.of("latitude", 10.01, "longitude", 106.01);

        Map<String, Object> leg = Map.of("points", List.of(p1, p2));
        Map<String, Object> route = Map.of("legs", List.of(leg));
        Map<String, Object> response = Map.of("routes", List.of(route));

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> mockRouteDetailResponse() {
        // Mock trả về 2 route (1 chính, 1 thay thế) để test nhánh alternatives
        Map<String, Object> p1 = Map.of("latitude", 10.0, "longitude", 106.0);
        Map<String, Object> p2 = Map.of("latitude", 10.01, "longitude", 106.01);
        Map<String, Object> p3 = Map.of("latitude", 10.02, "longitude", 106.02);

        Map<String, Object> summary = Map.of("travelTimeInSeconds", 1200, "lengthInMeters", 6000);
        Map<String, Object> leg = Map.of("points", List.of(p1, p2, p3));

        Map<String, Object> route0 = Map.of("legs", List.of(leg), "summary", summary);
        Map<String, Object> route1 = Map.of("legs", List.of(leg), "summary", summary); // Tuyến thay thế

        Map<String, Object> response = Map.of("routes", List.of(route0, route1));
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> mockFlowResponse(double currentSpeed) {
        Map<String, Object> fsData = new HashMap<>();
        fsData.put("currentSpeed", currentSpeed);
        fsData.put("freeFlowSpeed", 50.0); // Free flow cố định 50km/h

        Map<String, Object> response = new HashMap<>();
        response.put("flowSegmentData", fsData);

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> mockIncidentResponse(boolean closeToRoute) {
        double lat = closeToRoute ? 10.005 : 20.0;
        double lng = closeToRoute ? 106.005 : 120.0;

        List<Double> coords = List.of(lng, lat);
        Map<String, Object> geometry = Map.of("coordinates", List.of(coords));
        Map<String, Object> incident = Map.of("geometry", geometry);

        Map<String, Object> response = Map.of("incidents", List.of(incident));

        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // PHẦN 1: TEST HÀM getTravelTimesForAllStations
    // =========================================================================

    @Test
    void TC_GetTravelTimes_EmptyStations() {
        // FIX: Trả về ArrayList trống thay vì list bất biến
        when(tramRepository.findAll()).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = trafficService.getTravelTimesForAllStations(10.0, 106.0);
        assertTrue(result.isEmpty());
    }

    @Test
    void TC_GetTravelTimes_NoActiveStations() {
        // FIX: Bọc trong ArrayList
        when(tramRepository.findAll()).thenReturn(new ArrayList<>(List.of(tram2)));

        List<Map<String, Object>> result = trafficService.getTravelTimesForAllStations(10.0, 106.0);
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void TC_GetTravelTimes_Success_WithIncidentOnRoute() {
        // FIX: Bọc trong ArrayList
        when(tramRepository.findAll()).thenReturn(new ArrayList<>(List.of(tram1, tram2)));

        when(restTemplate.exchange(contains("/routing/matrix/2"), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(mockMatrixResponse());

        when(restTemplate.exchange(contains("/routing/1/calculateRoute/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockRouteResponse());

        when(restTemplate.exchange(contains("/flowSegmentData/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockFlowResponse(20.0));

        when(restTemplate.exchange(contains("/incidentDetails"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockIncidentResponse(true)); // Sự cố nằm trên tuyến đường

        List<Map<String, Object>> result = trafficService.getTravelTimesForAllStations(10.0, 106.0);

        assertEquals(1, result.size());
        assertTrue((Boolean) result.get(0).get("best"));
        assertEquals(1L, result.get(0).get("stationId"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void TC_GetTravelTimes_Success_IncidentFarAway() {
        // FIX: Bọc trong ArrayList
        when(tramRepository.findAll()).thenReturn(new ArrayList<>(List.of(tram1)));

        when(restTemplate.exchange(contains("/routing/matrix/2"), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(mockMatrixResponse());

        when(restTemplate.exchange(contains("/routing/1/calculateRoute/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockRouteResponse());

        when(restTemplate.exchange(contains("/flowSegmentData/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockFlowResponse(20.0));

        when(restTemplate.exchange(contains("/incidentDetails"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockIncidentResponse(false)); // Sự cố nằm xa lơ xa lắc

        List<Map<String, Object>> result = trafficService.getTravelTimesForAllStations(10.0, 106.0);
        assertEquals(1, result.size());
    }

    // =========================================================================
    // PHẦN 2: TEST HÀM getRouteDetail (CHI TIẾT MỘT TRẠM, KẸT XE, MÀU SẮC)
    // =========================================================================

    @Test
    void TC_GetRouteDetail_NotFound() {
        when(tramRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> trafficService.getRouteDetail(10.0, 106.0, 99L));
        assertEquals("Station not found", ex.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void TC_GetRouteDetail_Traffic_Red() {
        // Nhánh ĐỎ (Red): ratio < 0.4. Ví dụ: current = 10, free = 50 -> 0.2
        when(tramRepository.findById(1L)).thenReturn(Optional.of(tram1));

        when(restTemplate.exchange(contains("/routing/1/calculateRoute/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockRouteDetailResponse());

        when(restTemplate.exchange(contains("/flowSegmentData/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockFlowResponse(10.0));

        when(restTemplate.exchange(contains("/incidentDetails"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockIncidentResponse(false));

        Map<String, Object> result = trafficService.getRouteDetail(10.0, 106.0, 1L);

        List<Map<String, Object>> coloredSegments = (List<Map<String, Object>>) result.get("coloredSegments");
        assertFalse(coloredSegments.isEmpty());
        assertEquals("red", coloredSegments.get(0).get("color")); // Đoạn đường màu đỏ

        List<Map<String, Object>> alternatives = (List<Map<String, Object>>) result.get("alternatives");
        assertEquals(1, alternatives.size()); // Có tuyến đường thay thế
    }

    @Test
    @SuppressWarnings("unchecked")
    void TC_GetRouteDetail_Traffic_Orange() {
        // Nhánh CAM (Orange): 0.4 <= ratio < 0.7. Ví dụ: current = 30, free = 50 -> 0.6
        when(tramRepository.findById(1L)).thenReturn(Optional.of(tram1));

        when(restTemplate.exchange(contains("/routing/1/calculateRoute/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockRouteDetailResponse());

        when(restTemplate.exchange(contains("/flowSegmentData/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockFlowResponse(30.0));

        when(restTemplate.exchange(contains("/incidentDetails"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockIncidentResponse(false));

        Map<String, Object> result = trafficService.getRouteDetail(10.0, 106.0, 1L);

        List<Map<String, Object>> coloredSegments = (List<Map<String, Object>>) result.get("coloredSegments");
        assertEquals("orange", coloredSegments.get(0).get("color"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void TC_GetRouteDetail_Traffic_Green() {
        // Nhánh XANH (Green): ratio >= 0.7. Ví dụ: current = 50, free = 50 -> 1.0
        when(tramRepository.findById(1L)).thenReturn(Optional.of(tram1));

        when(restTemplate.exchange(contains("/routing/1/calculateRoute/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockRouteDetailResponse());

        when(restTemplate.exchange(contains("/flowSegmentData/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockFlowResponse(50.0));

        when(restTemplate.exchange(contains("/incidentDetails"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockIncidentResponse(false));

        Map<String, Object> result = trafficService.getRouteDetail(10.0, 106.0, 1L);

        List<Map<String, Object>> coloredSegments = (List<Map<String, Object>>) result.get("coloredSegments");
        assertEquals("green", coloredSegments.get(0).get("color"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void TC_GetRouteDetail_EmptyRoute_Coverage() {
        // Quét nhánh JSON trả về route rỗng (không có tuyến đường nào)
        when(tramRepository.findById(1L)).thenReturn(Optional.of(tram1));

        when(restTemplate.exchange(contains("/routing/1/calculateRoute/"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(new HashMap<>())); // JSON rỗng

        when(restTemplate.exchange(contains("/incidentDetails"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockIncidentResponse(false));

        Map<String, Object> result = trafficService.getRouteDetail(10.0, 106.0, 1L);

        List<Map<String, Object>> coloredSegments = (List<Map<String, Object>>) result.get("coloredSegments");
        assertTrue(coloredSegments.isEmpty()); // Sẽ không có đoạn vẽ màu nào
    }
}