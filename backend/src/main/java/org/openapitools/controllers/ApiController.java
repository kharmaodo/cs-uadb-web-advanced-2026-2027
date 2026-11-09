package org.openapitools.controllers;
import org.openapitools.api.ApiApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.openapitools.model.Etudiant;
import org.openapitools.model.LogementCrous;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.constraints.*;



@RestController
public class ApiController implements ApiApi {
    @Override ResponseEntity<Etudiant> getEtudiantByMatricule(@Pattern(regexp = "^[0-9]{7}$") @Parameter(name = "matricule", description = "Code permanent UADB (7 chiffres, ex: 23 pour la promo 2023).", required = true, in = ParameterIn.PATH) @PathVariable("matricule") String matricule) 
 {
        Etudiant etudiant = new Etudiant();
        etudiant.setId("1");
        etudiant.setMatricule(matricule);
        etudiant.setLastname("Doe");
        etudiant.setFirstname("John");
        etudiant.setUfr("Informatique");
        etudiant.setEmailInstitutionnel("john.doe@example.com");
        etudiant.setTelephone("123456789");
        LogementCrous l = new LogementCrous();
        l.setPavillon("A");
        l.setLit("B");
        l.setNumeroChambre("101");

        etudiant.setLogementCrous(l); // Vous pouvez définir un logement CROUS si nécessaire
    

        return ResponseEntity.ok(etudiant);
    
}
}