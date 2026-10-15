package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
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

    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> addSpecialization(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message =
                doctorServ.addSpecialization(
                        doctorId,
                        specializationId
                );

        if (message.equals("Specialization added to the doctor")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals(
                "The doctor already has that specialization")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> removeSpecialization(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message =
                doctorServ.removeSpecialization(
                        doctorId,
                        specializationId
                );

        if (message.equals("Specialization removed from the doctor")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals(
                "The doctor does not have that specialization")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<?> changeOffice(
            @PathVariable UUID doctorId,
            @RequestParam int officeNumber) {

        String message =
                doctorServ.changeOffice(doctorId, officeNumber);

        if (message.equals("Office changed successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("That office is taken by other doctor")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
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

    @GetMapping("/{doctorId}/agenda")
    public ResponseEntity<?> getDoctorAgenda(
            @PathVariable UUID doctorId,
            @RequestParam String date) {

        LocalDate parsedDate = LocalDate.parse(date);

        Optional<List<Appointment>> agenda =
                doctorServ.getDoctorAgenda(doctorId, parsedDate);

        if (agenda.isEmpty()) {
            return new ResponseEntity<>(
                    "The doctor with that id does not exist",
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(agenda.get(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctorsByName(
            @RequestParam String name) {

        return new ResponseEntity<>(
                doctorServ.searchDoctorsByName(name),
                HttpStatus.OK
        );
    }

    @GetMapping("/workload")
    public ResponseEntity<List<Object[]>> getDoctorWorkload(
            @RequestParam String start,
            @RequestParam String end) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        return new ResponseEntity<>(
                doctorServ.getDoctorWorkload(startDate, endDate),
                HttpStatus.OK
        );
    }
}