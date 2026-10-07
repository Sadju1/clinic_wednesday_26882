package kigali.clinic.rw.controller;

import java.util.List;
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

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping({"/api/doctor", "/api/doctors"})
public class DoctorController {

    @Autowired
    private DoctorService doctorServ;

    @PostMapping("/save")
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor) {

        String message = doctorServ.saveDoctor(doctor);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(
                doctorServ.getAllDoctors(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(
            @PathVariable UUID id) {

        Doctor doctor = doctorServ.getDoctorById(id);

        if (doctor == null) {
            return new ResponseEntity<>(
                    "Doctor not found",
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable UUID id,
            @RequestBody Doctor doctor) {

        String message =
                doctorServ.updateDoctor(id, doctor);

        if (message.equals("Doctor updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable UUID id) {

        String message =
                doctorServ.deleteDoctor(id);

        if (message.equals("Doctor deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }

    @PutMapping("/{doctorId}/specialization/{specializationId}")
    public ResponseEntity<?> addSpecialization(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message =
                doctorServ.addSpecialization(
                        doctorId,
                        specializationId
                );

        if (message.equals(
                "Specialization added to doctor successfully")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }

    @GetMapping("/by-specialization")
    public ResponseEntity<List<Doctor>>
    getDoctorsBySpecialization(
            @RequestParam String name) {

        return new ResponseEntity<>(
                doctorServ.getDoctorsBySpecialization(name),
                HttpStatus.OK
        );
    }

    @GetMapping("/without-office")
    public ResponseEntity<List<Doctor>>
    getDoctorsWithoutOffice() {

        return new ResponseEntity<>(
                doctorServ.getDoctorsWithoutOffice(),
                HttpStatus.OK
        );
    }
}