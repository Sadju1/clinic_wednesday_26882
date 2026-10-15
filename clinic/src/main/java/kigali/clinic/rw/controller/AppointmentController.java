package kigali.clinic.rw.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping({"/api/appointment", "/api/appointments"})
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentServ;

    @PostMapping("/save")
    public ResponseEntity<?> saveAppointment(
            @RequestBody Appointment appointment) {

        String message = appointmentServ.saveAppointment(appointment);

        if (message.equals("Appointment saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("Doctor is already booked on that date")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(
                appointmentServ.getAllAppointments(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(
            @PathVariable UUID id) {

        Appointment appointment =
                appointmentServ.getAppointmentById(id);

        if (appointment == null) {
            return new ResponseEntity<>(
                    "Appointment not found",
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable UUID id,
            @RequestBody Appointment appointment) {

        String message =
                appointmentServ.updateAppointment(id, appointment);

        if (message.equals("Appointment updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(
            @PathVariable UUID id) {

        String message =
                appointmentServ.deleteAppointment(id);

        if (message.equals("Appointment deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals(
                "That appointment has prescriptions, delete them first")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/doctor/{doctorId}/pending")
    public ResponseEntity<List<Appointment>>
    getPendingAppointmentsByDoctor(
            @PathVariable UUID doctorId) {

        return new ResponseEntity<>(
                appointmentServ.getPendingAppointmentsByDoctor(doctorId),
                HttpStatus.OK
        );
    }

    @GetMapping("/pending/past")
    public ResponseEntity<List<Appointment>>
    getPastPendingAppointments() {

        return new ResponseEntity<>(
                appointmentServ.getPastPendingAppointments(),
                HttpStatus.OK
        );
    }

    @GetMapping("/statistics/doctor-count")
    public ResponseEntity<List<Object[]>>
    getAppointmentCountPerDoctor() {

        return new ResponseEntity<>(
                appointmentServ.getAppointmentCountPerDoctor(),
                HttpStatus.OK
        );
    }

    @GetMapping("/specialization/{specializationName}")
    public ResponseEntity<List<Appointment>>
    getAppointmentsBySpecialization(
            @PathVariable String specializationName) {

        return new ResponseEntity<>(
                appointmentServ.getAppointmentsBySpecialization(
                        specializationName
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/doctor/{doctorId}/page")
    public ResponseEntity<Page<Appointment>>
    getDoctorAppointments(
            @PathVariable UUID doctorId,
            @RequestParam(defaultValue = "0") int page) {

        return new ResponseEntity<>(
                appointmentServ.getDoctorAppointments(doctorId, page),
                HttpStatus.OK
        );
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<Appointment>>
    getAppointmentsByStatus(
            @RequestParam AppointmentStatus status) {

        return new ResponseEntity<>(
                appointmentServ.getAppointmentsByStatus(status),
                HttpStatus.OK
        );
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>>
    getAppointmentCountByStatus() {

        return new ResponseEntity<>(
                appointmentServ.getAppointmentCountByStatus(),
                HttpStatus.OK
        );
    }

    @GetMapping("/between")
    public ResponseEntity<List<Appointment>>
    getAppointmentsBetween(
            @RequestParam String start,
            @RequestParam String end) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        Date startSqlDate = Date.valueOf(startDate);
        Date endSqlDate = Date.valueOf(endDate);

        return new ResponseEntity<>(
                appointmentServ.getAppointmentsBetween(
                        startSqlDate,
                        endSqlDate
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Appointment>> getAppointmentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate,asc") String sort) {

        String[] sortParams = sort.split(",");

        Sort.Direction direction =
                sortParams.length > 1
                        && sortParams[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortParams[0])
        );

        return new ResponseEntity<>(
                appointmentServ.getAllAppointmentsPaged(pageable),
                HttpStatus.OK
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID id,
            @RequestParam AppointmentStatus status) {

        String message = appointmentServ.updateStatus(id, status);

        if (message.equals("Appointment status updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals(
                "The appointment with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable UUID id,
            @RequestParam String newDate) {

        LocalDate parsedDate = LocalDate.parse(newDate);

        String message =
                appointmentServ.rescheduleAppointment(id, parsedDate);

        if (message.equals("Appointment rescheduled successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals(
                "The appointment with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else if (message.equals("Doctor is already booked on that date")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDoctorAppointmentsOnDate(
            @RequestParam UUID doctorId,
            @RequestParam String date) {

        LocalDate parsedDate = LocalDate.parse(date);
        Date sqlDate = Date.valueOf(parsedDate);

        String message = appointmentServ.cancelDoctorAppointmentsOnDate(
                doctorId,
                sqlDate
        );

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/cancelled-before")
    public ResponseEntity<String> deleteCancelledBefore(
            @RequestParam String date) {

        LocalDate parsedDate = LocalDate.parse(date);
        Date sqlDate = Date.valueOf(parsedDate);

        String message = appointmentServ.deleteCancelledBefore(sqlDate);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}