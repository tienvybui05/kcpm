package ut.edu.batteryservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ut.edu.batteryservice.dtos.BatterySummaryDTO;
import ut.edu.batteryservice.models.Pin;
import ut.edu.batteryservice.repositories.IPinRepository;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BatterySummaryServiceTest {

    @Mock
    private IPinRepository pinRepository;

    @InjectMocks
    private BatterySummaryService batterySummaryService;

    // ========== TCS: Tổng hợp có pin thuộc cả 3 loại sức khỏe ==========
    @Test
    void TC_SUMMARY_001_MixHealthCategories_ShouldCountCorrectly() {
        Pin p1 = new Pin(); p1.setSucKhoe(95.0);   // healthy
        Pin p2 = new Pin(); p2.setSucKhoe(80.0);   // degraded
        Pin p3 = new Pin(); p3.setSucKhoe(60.0);   // critical
        Pin p4 = new Pin(); p4.setSucKhoe(null);   // bỏ qua

        Mockito.when(pinRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3, p4));

        BatterySummaryDTO result = batterySummaryService.getBatterySummary();
        assertEquals(4, result.getTotalBatteries());
        assertEquals(1, result.getHealthy());
        assertEquals(1, result.getDegraded());
        assertEquals(1, result.getCritical());
    }

    // ========== TCS: Tất cả pin healthy ==========
    @Test
    void TC_SUMMARY_002_AllHealthy() {
        Pin p1 = new Pin(); p1.setSucKhoe(100.0);
        Pin p2 = new Pin(); p2.setSucKhoe(91.0);
        Mockito.when(pinRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        BatterySummaryDTO result = batterySummaryService.getBatterySummary();
        assertEquals(2, result.getTotalBatteries());
        assertEquals(2, result.getHealthy());
        assertEquals(0, result.getDegraded());
        assertEquals(0, result.getCritical());
    }

    // ========== TCS: Tất cả pin degraded ==========
    @Test
    void TC_SUMMARY_003_AllDegraded() {
        Pin p1 = new Pin(); p1.setSucKhoe(90.0);
        Pin p2 = new Pin(); p2.setSucKhoe(70.0);
        Mockito.when(pinRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        BatterySummaryDTO result = batterySummaryService.getBatterySummary();
        assertEquals(2, result.getTotalBatteries());
        assertEquals(0, result.getHealthy());
        assertEquals(2, result.getDegraded());
        assertEquals(0, result.getCritical());
    }

    // ========== TCS: Tất cả pin critical ==========
    @Test
    void TC_SUMMARY_004_AllCritical() {
        Pin p1 = new Pin(); p1.setSucKhoe(69.0);
        Pin p2 = new Pin(); p2.setSucKhoe(0.0);
        Mockito.when(pinRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        BatterySummaryDTO result = batterySummaryService.getBatterySummary();
        assertEquals(2, result.getTotalBatteries());
        assertEquals(0, result.getHealthy());
        assertEquals(0, result.getDegraded());
        assertEquals(2, result.getCritical());
    }

    // ========== TCS: Tất cả pin có sucKhoe null ==========
    @Test
    void TC_SUMMARY_005_AllNullHealth_ShouldCountZeroForAllCategories() {
        Pin p1 = new Pin(); p1.setSucKhoe(null);
        Pin p2 = new Pin(); p2.setSucKhoe(null);
        Mockito.when(pinRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        BatterySummaryDTO result = batterySummaryService.getBatterySummary();
        assertEquals(2, result.getTotalBatteries());
        assertEquals(0, result.getHealthy());
        assertEquals(0, result.getDegraded());
        assertEquals(0, result.getCritical());
    }

    // ========== TCS: Danh sách pin rỗng ==========
    @Test
    void TC_SUMMARY_006_EmptyList_ShouldReturnAllZero() {
        Mockito.when(pinRepository.findAll()).thenReturn(Collections.emptyList());

        BatterySummaryDTO result = batterySummaryService.getBatterySummary();
        assertEquals(0, result.getTotalBatteries());
        assertEquals(0, result.getHealthy());
        assertEquals(0, result.getDegraded());
        assertEquals(0, result.getCritical());
    }
}