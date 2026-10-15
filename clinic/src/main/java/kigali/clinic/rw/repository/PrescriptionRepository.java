package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByAppointment(Appointment appointment);

    boolean existsByAppointment_Id(UUID appointmentId);

    @Query("""
        SELECT p
        FROM Prescription p
        WHERE p.appointment.patient.ssNumber = :ssNumber
        ORDER BY p.issuedDate DESC
        """)
    List<Prescription> findPrescriptionHistoryByPatientSsNumber(
            @Param("ssNumber") String ssNumber
    );

    @Query("""
        SELECT p.medicineName, COUNT(p)
        FROM Prescription p
        GROUP BY p.medicineName
        ORDER BY COUNT(p) DESC
        """)
    List<Object[]> findTopMedicines();
}
