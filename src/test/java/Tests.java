import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;


public class Tests {
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    @Test
    public void checkBloodPressureIfItsWrong() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById("1")).thenReturn(new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))));
        SendAlertServiceImpl sendAlertService = Mockito.spy(SendAlertServiceImpl.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
        BloodPressure currentPressure = new BloodPressure(60, 120);
        medicalService.checkBloodPressure("1", currentPressure);
        String expectedResult = "Warning, patient with id: null, need help, wrong Blood Pressure";
        assertThat(outContent.toString(), StringContains.containsString(expectedResult));

    }
    @Test
    public void checkTemperatureTestIfItsHigh() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById("1")).thenReturn(new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));
        SendAlertServiceImpl sendAlertService = Mockito.spy(SendAlertServiceImpl.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
        BigDecimal currentTemperature = new BigDecimal("39.1");
        String expectedResult = "Warning, patient with id: null, need help, high temperature";
        medicalService.checkTemperature("1", currentTemperature);
        assertThat(outContent.toString(), StringContains.containsString(expectedResult));
    }
    @Test
    public void checkTemperatureTestIfItsLow() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById("1")).thenReturn(new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));
        SendAlertServiceImpl sendAlertService = Mockito.spy(SendAlertServiceImpl.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
        BigDecimal currentTemperature = new BigDecimal("35.1");
        String expectedResult = "Warning, patient with id: null, need help, low temperature";
        medicalService.checkTemperature("1", currentTemperature);
        assertThat(outContent.toString(), StringContains.containsString(expectedResult));
    }
    @Test
    public void checkTemperatureAndBloodPressureIfItsOk() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById("1")).thenReturn(new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));
        SendAlertServiceImpl sendAlertService = Mockito.spy(SendAlertServiceImpl.class);
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
        BigDecimal currentTemperature = new BigDecimal("36.6");
        BloodPressure currentPressure = new BloodPressure(125, 78);
        medicalService.checkTemperature("1", currentTemperature);
        medicalService.checkBloodPressure("1", currentPressure);
        Assertions.assertEquals(outContent.toString(), "");
    }
    @After
    public void restoreStreams() {
        System.setOut(null);
    }
}

