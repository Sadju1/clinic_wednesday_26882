package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepo;

    // CREATE
    public String saveOffice(Office office) {

        officeRepo.save(office);

        return "Office saved successfully";
    }

    // READ ALL
    public List<Office> getAllOffices() {

        return officeRepo.findAll();
    }

    // READ ONE
    public Office getOfficeById(UUID id) {

        return officeRepo.findById(id).orElse(null);
    }

    // UPDATE
    public String updateOffice(UUID id, Office office) {

        Office existingOffice = officeRepo.findById(id).orElse(null);

        if (existingOffice == null) {
            return "Office not found";
        }

        existingOffice.setOfficeNumber(office.getOfficeNumber());
        existingOffice.setName(office.getName());

        officeRepo.save(existingOffice);

        return "Office updated successfully";
    }

    // DELETE
    public String deleteOffice(UUID id) {

        Office existingOffice = officeRepo.findById(id).orElse(null);

        if (existingOffice == null) {
            return "Office not found";
        }

        officeRepo.delete(existingOffice);

        return "Office deleted successfully";
    }

    public Optional<Object[]> getBusiestOffice() {

        List<Object[]> results = officeRepo.findBusiestOffice();

        if (results.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(results.get(0));
    }
}