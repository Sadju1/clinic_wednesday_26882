package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specializationRepo;

    public String saveSpecialization(Specialization specialization) {
        specializationRepo.save(specialization);
        return "Specialization saved successfully";
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepo.findAll();
    }

    public Specialization getSpecializationById(UUID id) {
        return specializationRepo.findById(id).orElse(null);
    }

    public String updateSpecialization(
            UUID id,
            Specialization specialization) {

        Specialization existingSpecialization =
                specializationRepo.findById(id).orElse(null);

        if (existingSpecialization == null) {
            return "Specialization not found";
        }

        existingSpecialization.setName(specialization.getName());

        specializationRepo.save(existingSpecialization);

        return "Specialization updated successfully";
    }

    public String deleteSpecialization(UUID id) {
        Specialization existingSpecialization =
                specializationRepo.findById(id).orElse(null);

        if (existingSpecialization == null) {
            return "Specialization not found";
        }

        specializationRepo.delete(existingSpecialization);

        return "Specialization deleted successfully";
    }

    public List<Specialization> getUnusedSpecializations() {
        return specializationRepo.findUnusedSpecializations();
    }
}