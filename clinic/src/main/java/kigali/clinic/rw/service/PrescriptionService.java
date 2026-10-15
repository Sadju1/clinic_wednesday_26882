package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String savePrescription(UUID appointmentId, Prescription prescription) {

        Appointment appointment = appointmentRepo.findById(appointmentId).orElse(null);

        if (appointment == null) {
            return "The appointment with that id does not exist";
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment);
        prescriptionRepo.save(prescription);

        return "Prescription saved successfully";
    }

    public Optional<List<Prescription>> getPrescriptionsByAppointment(
            UUID appointmentId) {

        Appointment appointment = appointmentRepo.findById(appointmentId).orElse(null);

        if (appointment == null) {
            return Optional.empty();
        }

        return Optional.of(prescriptionRepo.findByAppointment(appointment));
    }

    public List<Prescription> getPrescriptionHistoryByPatient(String ssNumber) {
        return prescriptionRepo.findPrescriptionHistoryByPatientSsNumber(ssNumber);
    }

    public List<Object[]> getTopMedicines() {
        return prescriptionRepo.findTopMedicines();
    }

    public String updatePrescription(UUID id, Prescription prescription) {

        Prescription existing = prescriptionRepo.findById(id).orElse(null);

        if (existing == null) {
            return "The prescription with that id does not exist";
        }

        existing.setMedicineName(prescription.getMedicineName());
        existing.setDosage(prescription.getDosage());
        existing.setDurationDays(prescription.getDurationDays());

        prescriptionRepo.save(existing);

        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {

        Prescription existing = prescriptionRepo.findById(id).orElse(null);

        if (existing == null) {
            return "The prescription with that id does not exist";
        }

        prescriptionRepo.delete(existing);

        return "Prescription deleted successfully";
    }
}
