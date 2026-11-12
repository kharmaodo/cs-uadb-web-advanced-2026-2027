package sn.uadb.controller;

import org.openapitools.api.ApiApi;
import org.openapitools.model.Etudiant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import sn.uadb.repository.EtudiantRepository;

@RestController
public class ApiController implements ApiApi {

    private final EtudiantRepository repository;

    public ApiController(EtudiantRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResponseEntity<Etudiant> getEtudiantByMatricule(String matricule) {
        if (matricule == null || !matricule.matches("^[0-9]{7}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Format de matricule invalide (doit comporter exactement 7 chiffres).");
        }
        return repository.findByMatricule(matricule)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aucun étudiant trouvé avec ce code permanent."));
    }
}