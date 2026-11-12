package org.openapitools.controllers;

import org.openapitools.api.ApiApi;
import org.openapitools.model.Etudiant;
import org.openapitools.model.LogementCrous;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class ApiController implements ApiApi {

    private static final Map<String, Etudiant> ETUDIANTS = Map.of(
        "2300001", etudiant("1", "2300001", "Diop", "Maodo", "SATIC", "771234567",
                "maodo.diop@uadb.edu.sn", logement("B", "102", "A")),
        "2300002", etudiant("2", "2300002", "Ndiaye", "Aminata", "SEFS", "781234567",
                "aminata.ndiaye@uadb.edu.sn", logement("A", "215", "B")),
        "2200003", etudiant("3", "2200003", "Fall", "Cheikh", "SJP", "761234567",
                "cheikh.fall@uadb.edu.sn", null) // étudiant non logé
    );

    @Override
    public ResponseEntity<Etudiant> getEtudiantByMatricule(String matricule) {
        if (matricule == null || !matricule.matches("^[0-9]{7}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Format de matricule invalide (doit comporter exactement 7 chiffres).");
        }
        Etudiant etudiant = ETUDIANTS.get(matricule);
        if (etudiant == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aucun étudiant trouvé avec ce code permanent.");
        }
        return ResponseEntity.ok(etudiant);
    }

    private static LogementCrous logement(String pavillon, String numeroChambre, String lit) {
        LogementCrous l = new LogementCrous();
        l.setPavillon(pavillon);
        l.setNumeroChambre(numeroChambre);
        l.setLit(lit);
        return l;
    }

    private static Etudiant etudiant(String id, String matricule, String lastname, String firstname,
                                     String ufr, String telephone, String emailInstitutionnel,
                                     LogementCrous logementCrous) {
        Etudiant e = new Etudiant();
        e.setId(id);
        e.setMatricule(matricule);
        e.setLastname(lastname);
        e.setFirstname(firstname);
        e.setUfr(ufr);
        e.setTelephone(telephone);
        e.setEmailInstitutionnel(emailInstitutionnel);
        e.setLogementCrous(logementCrous);
        return e;
    }
}