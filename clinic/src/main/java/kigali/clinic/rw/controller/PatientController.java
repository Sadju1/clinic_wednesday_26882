package kigali.clinic.rw.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.PatientService;

@RestController
@RequestMapping({"/api/patient", "/api/patients"})
public class PatientController {

    @Autowired
    private PatientService patientServ;

    @PostMapping("/save")
    public ResponseEntity<?> savePatient(@RequestBody Patient patient) {
        String message = patientServ.savePatient(patient);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return new ResponseEntity<>(
                patientServ.getAllPatients(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable UUID id) {
        Patient patient = patientServ.getPatientById(id);

        if (patient == null) {
            return new ResponseEntity<>(
                    "Patient not found",
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable UUID id,
            @RequestBody Patient patient) {

        String message = patientServ.updatePatient(id, patient);

        if (message.equals("Patient updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable UUID id) {
        String message = patientServ.deletePatient(id);

        if (message.equals("Patient deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/by-last-name")
    public ResponseEntity<List<Patient>> getPatientsByLastName(
            @RequestParam String lastName) {

        return new ResponseEntity<>(
                patientServ.getPatientsByLastName(lastName),
                HttpStatus.OK
        );
    }

    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> getPatientsOfDoctor(
            @PathVariable UUID doctorId) {

        Optional<List<Patient>> patients =
                patientServ.getPatientsOfDoctor(doctorId);

        if (patients.isEmpty()) {
            return new ResponseEntity<>(
                    "The doctor with that id does not exist",
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                patients.get(),
                HttpStatus.OK
        );
    }

    @GetMapping("/frequent")
    public ResponseEntity<List<Patient>> getFrequentPatients(
            @RequestParam int min) {

        return new ResponseEntity<>(
                patientServ.getFrequentPatients(min),
                HttpStatus.OK
        );
    }
}