package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;

@RestController
@RequestMapping({
        "/api/specialization",
        "/api/specializations"
})
public class SpecializationController {

    @Autowired
    private SpecializationService specializationServ;

    @PostMapping("/save")
    public ResponseEntity<?> saveSpecialization(
            @RequestBody Specialization specialization) {

        String message =
                specializationServ.saveSpecialization(
                        specialization
                );

        return new ResponseEntity<>(
                message,
                HttpStatus.OK
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<Specialization>>
    getAllSpecializations() {

        return new ResponseEntity<>(
                specializationServ.getAllSpecializations(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSpecializationById(
            @PathVariable UUID id) {

        Specialization specialization =
                specializationServ.getSpecializationById(id);

        if (specialization == null) {
            return new ResponseEntity<>(
                    "Specialization not found",
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                specialization,
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpecialization(
            @PathVariable UUID id,
            @RequestBody Specialization specialization) {

        String message =
                specializationServ.updateSpecialization(
                        id,
                        specialization
                );

        if (message.equals(
                "Specialization updated successfully")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpecialization(
            @PathVariable UUID id) {

        String message =
                specializationServ.deleteSpecialization(id);

        if (message.equals(
                "Specialization deleted successfully")) {

            return new ResponseEntity<>(
                    message,
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                message,
                HttpStatus.NOT_FOUND
        );
    }

    @GetMapping("/unused")
    public ResponseEntity<List<Specialization>>
    getUnusedSpecializations() {

        return new ResponseEntity<>(
                specializationServ.getUnusedSpecializations(),
                HttpStatus.OK
        );
    }
}