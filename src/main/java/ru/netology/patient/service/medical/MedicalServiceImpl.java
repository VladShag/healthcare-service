package ru.netology.patient.service.medical;

import java.math.BigDecimal;

import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

public class MedicalServiceImpl implements MedicalService {

    private final PatientInfoRepository patientInfoRepository;
    private final SendAlertService alertService;

    public MedicalServiceImpl(PatientInfoRepository patientInfoRepository, SendAlertService alertService) {
        this.patientInfoRepository = patientInfoRepository;
        this.alertService = alertService;
    }

    @Override
    public void checkBloodPressure(String patientId, BloodPressure bloodPressure) {
        PatientInfo patientInfo = getPatientInfo(patientId);
        if (!patientInfo.getHealthInfo().getBloodPressure().equals(bloodPressure)) {
            String message = String.format("Warning, patient with id: %s, need help, wrong Blood Pressure", patientInfo.getId());
            alertService.send(message);
        }
    }

    @Override
    public void checkTemperature(String patientId, BigDecimal temperature) {
        PatientInfo patientInfo = getPatientInfo(patientId);
        if (patientInfo.getHealthInfo().getNormalTemperature().compareTo(temperature) > 0) {
            String message = String.format("Warning, patient with id: %s, need help, low temperature", patientInfo.getId());
            alertService.send(message);
        } else if(patientInfo.getHealthInfo().getNormalTemperature().compareTo(temperature) < 0) {
            String message = String.format("Warning, patient with id: %s, need help, high temperature", patientInfo.getId());
            alertService.send(message);
        }
    }

    private PatientInfo getPatientInfo(String patientId) {
        PatientInfo patientInfo = patientInfoRepository.getById(patientId);
        if (patientInfo == null) {
            throw new RuntimeException("Patient not found");
        }
        return patientInfo;
    }
}
