package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public String savePatient(Patient patient) {
        patientRepo.save(patient);
        return "Patient saved successfully";
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Patient getPatientById(UUID id) {
        return patientRepo.findById(id).orElse(null);
    }

    public String updatePatient(UUID id, Patient patient) {
        Patient existingPatient = patientRepo.findById(id).orElse(null);

        if (existingPatient == null) {
            return "Patient not found";
        }

        existingPatient.setFirstName(patient.getFirstName());
        existingPatient.setLastName(patient.getLastName());
        existingPatient.setDateOfBirth(patient.getDateOfBirth());
        existingPatient.setSsNumber(patient.getSsNumber());

        patientRepo.save(existingPatient);

        return "Patient updated successfully";
    }

    public String deletePatient(UUID id) {
        Patient existingPatient = patientRepo.findById(id).orElse(null);

        if (existingPatient == null) {
            return "Patient not found";
        }

        patientRepo.delete(existingPatient);

        return "Patient deleted successfully";
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public Optional<List<Patient>> getPatientsOfDoctor(UUID doctorId) {
        if (!doctorRepo.existsById(doctorId)) {
            return Optional.empty();
        }

        return Optional.of(patientRepo.findPatientsOfDoctor(doctorId));
    }

    public List<Patient> getFrequentPatients(int min) {
        return patientRepo.findFrequentPatients(min);
    }
}