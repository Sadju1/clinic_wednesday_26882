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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping({"/api/prescription", "/api/prescriptions"})
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionServ;

    @PostMapping("/save")
    public ResponseEntity<?> savePrescription(
            @RequestParam UUID appointmentId,
            @RequestBody Prescription prescription) {

        String message =
                prescriptionServ.savePrescription(appointmentId, prescription);

        if (message.equals("Prescription saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else if (message.equals(
                "The appointment with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionsByAppointment(
            @PathVariable UUID appointmentId) {

        Optional<List<Prescription>> prescriptions =
                prescriptionServ.getPrescriptionsByAppointment(appointmentId);

        if (prescriptions.isEmpty()) {
            return new ResponseEntity<>(
                    "The appointment with that id does not exist",
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(prescriptions.get(), HttpStatus.OK);
    }

    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<List<Prescription>> getPrescriptionHistoryByPatient(
            @PathVariable String ssNumber) {

        return new ResponseEntity<>(
                prescriptionServ.getPrescriptionHistoryByPatient(ssNumber),
                HttpStatus.OK
        );
    }

    @GetMapping("/stats/top-medicines")
    public ResponseEntity<List<Object[]>> getTopMedicines() {

        return new ResponseEntity<>(
                prescriptionServ.getTopMedicines(),
                HttpStatus.OK
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePrescription(
            @PathVariable UUID id,
            @RequestBody Prescription prescription) {

        String message =
                prescriptionServ.updatePrescription(id, prescription);

        if (message.equals("Prescription updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePrescription(@PathVariable UUID id) {

        String message = prescriptionServ.deletePrescription(id);

        if (message.equals("Prescription deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
