package kigali.clinic.rw.controller;

import java.util.List;
import java.util.Optional;
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

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;

@RestController
@RequestMapping(value = {"/api/office", "/api/offices"})
public class OfficeController {

    @Autowired
    private OfficeService officeServ;

    // CREATE
    @PostMapping(value = "/save")
    public ResponseEntity<?> saveOffice(@RequestBody Office office) {

        String returnedMessage = officeServ.saveOffice(office);

        if (returnedMessage.equals("Office saved successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Office already exists", HttpStatus.CONFLICT);
        }
    }

    // READ ALL
    @GetMapping(value = "/all")
    public ResponseEntity<List<Office>> getAllOffices() {

        List<Office> offices = officeServ.getAllOffices();

        return new ResponseEntity<>(offices, HttpStatus.OK);
    }

    // READ ONE
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id) {

        Office office = officeServ.getOfficeById(id);

        if (office == null) {
            return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(office, HttpStatus.OK);
    }

    // UPDATE
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateOffice(
            @PathVariable UUID id,
            @RequestBody Office office) {

        String returnedMessage = officeServ.updateOffice(id, office);

        if (returnedMessage.equals("Office updated successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    // DELETE
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable UUID id) {

        String returnedMessage = officeServ.deleteOffice(id);

        if (returnedMessage.equals("Office deleted successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/free")
    public ResponseEntity<List<Office>> getFreeOffices() {

        return new ResponseEntity<>(officeServ.getFreeOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/busiest")
    public ResponseEntity<?> getBusiestOffice() {

        Optional<Object[]> busiest = officeServ.getBusiestOffice();

        if (busiest.isEmpty()) {
            return new ResponseEntity<>("No appointments yet", HttpStatus.OK);
        }

        return new ResponseEntity<>(busiest.get(), HttpStatus.OK);
    }
}