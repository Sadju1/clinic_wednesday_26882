package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    Office findByOfficeNumber(int offNumber);

    List<Office> findByName(String name);

    @Query("""
        SELECT o
        FROM Office o
        WHERE NOT EXISTS (
            SELECT d FROM Doctor d WHERE d.office = o
        )
        ORDER BY o.officeNumber ASC
        """)
    List<Office> findFreeOffices();

    @Query("""
        SELECT o.name, o.officeNumber, COUNT(a)
        FROM Appointment a
        JOIN a.doctor d
        JOIN d.office o
        GROUP BY o.name, o.officeNumber
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> findBusiestOffice();
}