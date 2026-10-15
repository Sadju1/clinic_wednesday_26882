package kigali.clinic.rw.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String saveAppointment(Appointment appointment) {

        if (appointment.getAppointmentDate().toLocalDate()
                .isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        if (appointment.getDoctor() != null
                && appointment.getDoctor().getId() != null) {

            boolean booked = appointmentRepo
                    .existsByDoctor_IdAndAppointmentDateAndStatusNot(
                            appointment.getDoctor().getId(),
                            appointment.getAppointmentDate(),
                            AppointmentStatus.CANCELLED
                    );

            if (booked) {
                return "Doctor is already booked on that date";
            }
        }

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);

        return "Appointment saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public Appointment getAppointmentById(UUID id) {
        return appointmentRepo.findById(id).orElse(null);
    }

    public String updateAppointment(UUID id, Appointment appointment) {
        Appointment existingAppointment =
                appointmentRepo.findById(id).orElse(null);

        if (existingAppointment == null) {
            return "Appointment not found";
        }

        existingAppointment.setAppointmentDate(
                appointment.getAppointmentDate()
        );
        existingAppointment.setReason(appointment.getReason());
        existingAppointment.setPatient(appointment.getPatient());
        existingAppointment.setDoctor(appointment.getDoctor());

        appointmentRepo.save(existingAppointment);

        return "Appointment updated successfully";
    }

    public String updateStatus(UUID id, AppointmentStatus newStatus) {

        Appointment appointment = appointmentRepo.findById(id).orElse(null);

        if (appointment == null) {
            return "The appointment with that id does not exist";
        }

        AppointmentStatus currentStatus = appointment.getStatus();

        boolean allowed =
                (currentStatus == AppointmentStatus.SCHEDULED
                        && (newStatus == AppointmentStatus.CONFIRMED
                            || newStatus == AppointmentStatus.CANCELLED))
                || (currentStatus == AppointmentStatus.CONFIRMED
                        && (newStatus == AppointmentStatus.COMPLETED
                            || newStatus == AppointmentStatus.CANCELLED));

        if (!allowed) {
            return "Cannot change status from "
                    + currentStatus + " to " + newStatus;
        }

        appointment.setStatus(newStatus);
        appointmentRepo.save(appointment);

        return "Appointment status updated successfully";
    }

    public String rescheduleAppointment(UUID id, LocalDate newDate) {

        Appointment appointment = appointmentRepo.findById(id).orElse(null);

        if (appointment == null) {
            return "The appointment with that id does not exist";
        }

        AppointmentStatus currentStatus = appointment.getStatus();

        if (currentStatus == AppointmentStatus.COMPLETED
                || currentStatus == AppointmentStatus.CANCELLED) {
            return "A " + currentStatus + " appointment cannot be rescheduled";
        }

        if (newDate.isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        Date newSqlDate = Date.valueOf(newDate);

        boolean booked = appointmentRepo
                .existsByDoctor_IdAndAppointmentDateAndStatusNot(
                        appointment.getDoctor().getId(),
                        newSqlDate,
                        AppointmentStatus.CANCELLED
                );

        if (booked) {
            return "Doctor is already booked on that date";
        }

        appointment.setAppointmentDate(newSqlDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);

        return "Appointment rescheduled successfully";
    }

    public String deleteAppointment(UUID id) {
        Appointment existingAppointment =
                appointmentRepo.findById(id).orElse(null);

        if (existingAppointment == null) {
            return "Appointment not found";
        }

        appointmentRepo.delete(existingAppointment);

        return "Appointment deleted successfully";
    }

    public List<Appointment> getPendingAppointmentsByDoctor(UUID doctorId) {
        return appointmentRepo
                .findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
                        doctorId,
                        AppointmentStatus.PENDING
                );
    }

    public List<Appointment> getPastPendingAppointments() {
        return appointmentRepo.findPastPendingAppointments(
                AppointmentStatus.PENDING
        );
    }

    public List<Object[]> getAppointmentCountPerDoctor() {
        return appointmentRepo.countAppointmentsPerDoctor();
    }

    public List<Object[]> getAppointmentCountByStatus() {
        return appointmentRepo.countAppointmentsByStatus();
    }

    public List<Appointment> getAppointmentsBySpecialization(
            String specializationName) {

        return appointmentRepo.findAppointmentsBySpecialization(
                specializationName
        );
    }

    public Page<Appointment> getDoctorAppointments(
            UUID doctorId,
            int page) {

        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by(
                        Sort.Direction.ASC,
                        "appointmentDate"
                )
        );

        return appointmentRepo.findByDoctor_Id(
                doctorId,
                pageable
        );
    }

    public List<Appointment> getAppointmentsByStatus(
            AppointmentStatus status) {

        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(
                status
        );
    }

    public List<Appointment> getAppointmentsBetween(
            Date start,
            Date end) {

        return appointmentRepo
                .findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                        start,
                        end
                );
    }

    public Page<Appointment> getAllAppointmentsPaged(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    @Transactional
    public String cancelDoctorAppointmentsOnDate(
            UUID doctorId,
            Date date) {

        int cancelled = appointmentRepo
                .cancelDoctorAppointmentsOnDate(doctorId, date);

        return cancelled + " appointments cancelled";
    }

    @Transactional
    public String deleteCancelledBefore(Date date) {
        int deleted = appointmentRepo.deleteCancelledBefore(date);
        return deleted + " appointments deleted";
    }
}