package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private SpecializationRepository specializationRepo;

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
            return "Doctor not found";
        }

        Specialization specialization =
                specializationRepo.findById(specializationId).orElse(null);

        if (specialization == null) {
            return "Specialization not found";
        }

        if (!specialization.getDoctors().contains(doctor)) {
            specialization.getDoctors().add(doctor);
        }

        if (!doctor.getSpecializations().contains(specialization)) {
            doctor.getSpecializations().add(specialization);
        }

        specializationRepo.save(specialization);

        return "Specialization added to doctor successfully";
    }

    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepo.findDoctorsBySpecialization(name);
    }

    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }
}