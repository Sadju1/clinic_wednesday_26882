package kigali.clinic.rw.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private SpecializationRepository specializationRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String saveDoctor(Doctor doctor) {
        doctorRepo.save(doctor);
        return "Doctor saved successfully";
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Doctor getDoctorById(UUID id) {
        return doctorRepo.findById(id).orElse(null);
    }

    public String updateDoctor(UUID id, Doctor doctor) {
        Doctor existingDoctor = doctorRepo.findById(id).orElse(null);

        if (existingDoctor == null) {
            return "Doctor not found";
        }

        existingDoctor.setFirstName(doctor.getFirstName());
        existingDoctor.setLastName(doctor.getLastName());
        existingDoctor.setDateOfBirth(doctor.getDateOfBirth());
        existingDoctor.setOffice(doctor.getOffice());
        existingDoctor.setSpecializations(doctor.getSpecializations());

        doctorRepo.save(existingDoctor);

        return "Doctor updated successfully";
    }

    public String deleteDoctor(UUID id) {
        Doctor existingDoctor = doctorRepo.findById(id).orElse(null);

        if (existingDoctor == null) {
            return "Doctor not found";
        }

        doctorRepo.delete(existingDoctor);

        return "Doctor deleted successfully";
    }

    public String addSpecialization(
            UUID doctorId,
            UUID specializationId) {

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        Specialization specialization =
                specializationRepo.findById(specializationId).orElse(null);

        if (specialization == null) {
            return "That specialization does not exist";
        }

        if (doctor.getSpecializations().contains(specialization)) {
            return "The doctor already has that specialization";
        }

        doctor.getSpecializations().add(specialization);
        doctorRepo.save(doctor);

        return "Specialization added to the doctor";
    }

    public String removeSpecialization(
            UUID doctorId,
            UUID specializationId) {

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        Specialization specialization =
                specializationRepo.findById(specializationId).orElse(null);

        if (specialization == null) {
            return "That specialization does not exist";
        }

        if (!doctor.getSpecializations().contains(specialization)) {
            return "The doctor does not have that specialization";
        }

        doctor.getSpecializations().remove(specialization);
        doctorRepo.save(doctor);

        return "Specialization removed from the doctor";
    }

    public String changeOffice(UUID doctorId, int officeNumber) {

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        Office office = officeRepo.findByOfficeNumber(officeNumber);

        if (office == null) {
            return "The office with that office number does not exist";
        }

        if (doctorRepo.existsByOfficeAndIdNot(office, doctorId)) {
            return "That office is taken by other doctor";
        }

        doctor.setOffice(office);
        doctorRepo.save(doctor);

        return "Office changed successfully";
    }

    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepo.findDoctorsBySpecialization(name);
    }

    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }

    public Optional<List<Appointment>> getDoctorAgenda(
            UUID doctorId,
            LocalDate date) {

        if (!doctorRepo.existsById(doctorId)) {
            return Optional.empty();
        }

        Date sqlDate = Date.valueOf(date);

        return Optional.of(
                appointmentRepo.findByDoctor_IdAndAppointmentDateAndStatusNot(
                        doctorId,
                        sqlDate,
                        AppointmentStatus.CANCELLED
                )
        );
    }

    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepo.searchDoctorsByName(name);
    }

    public List<Object[]> getDoctorWorkload(LocalDate start, LocalDate end) {

        Date startDate = Date.valueOf(start);
        Date endDate = Date.valueOf(end);

        return appointmentRepo.findDoctorWorkload(
                AppointmentStatus.COMPLETED,
                startDate,
                endDate
        );
    }
}