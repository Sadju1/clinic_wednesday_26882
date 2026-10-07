package kigali.clinic.rw.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
            UUID doctorId,
            AppointmentStatus status
    );

    @Query("""
        SELECT a
        FROM Appointment a
        WHERE a.status = :status
        AND a.appointmentDate < CURRENT_DATE
        """)
    List<Appointment> findPastPendingAppointments(
            @Param("status") AppointmentStatus status
    );

    @Query("""
        SELECT a.doctor.id,
               a.doctor.firstName,
               a.doctor.lastName,
               COUNT(a)
        FROM Appointment a
        GROUP BY a.doctor.id,
                 a.doctor.firstName,
                 a.doctor.lastName
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> countAppointmentsPerDoctor();

    @Query("""
        SELECT a.status, COUNT(a)
        FROM Appointment a
        GROUP BY a.status
        """)
    List<Object[]> countAppointmentsByStatus();

    @Query("""
        SELECT a
        FROM Appointment a
        JOIN a.doctor d
        JOIN d.specializations s
        WHERE s.name = :specializationName
        """)
    List<Appointment> findAppointmentsBySpecialization(
            @Param("specializationName") String specializationName
    );

    Page<Appointment> findByDoctor_Id(
            UUID doctorId,
            Pageable pageable
    );

    List<Appointment> findByStatusOrderByAppointmentDateAsc(
            AppointmentStatus status
    );

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            Date start,
            Date end
    );

    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(
            UUID doctorId,
            Date appointmentDate,
            AppointmentStatus status
    );

    @Modifying
    @Query("""
        UPDATE Appointment a
        SET a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED
        WHERE a.doctor.id = :doctorId
        AND a.appointmentDate = :date
        AND a.status <> kigali.clinic.rw.domain.AppointmentStatus.COMPLETED
        """)
    int cancelDoctorAppointmentsOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") Date date
    );

    @Modifying
    @Query("""
        DELETE FROM Appointment a
        WHERE a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED
        AND a.appointmentDate < :date
        """)
    int deleteCancelledBefore(@Param("date") Date date);
}