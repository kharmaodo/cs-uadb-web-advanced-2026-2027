package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.model.LogementCrous;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Etudiant
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-07T15:27:12.796389883Z[Etc/UTC]", comments = "Generator version: 7.26.0")
public class Etudiant {

  private String id;

  private String matricule;

  private @Nullable String lastname;

  private @Nullable String firstname;

  private @Nullable String idCard;

  private @Nullable String idDriver;

  private @Nullable String telephone;

  private @Nullable String emailInstitutionnel;

  private String ufr;

  private @Nullable LogementCrous logementCrous;

  public Etudiant() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Etudiant(String id, String matricule, String ufr) {
    this.id = id;
    this.matricule = matricule;
    this.ufr = ufr;
  }

  public Etudiant id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Code permanent UADB à 7 chiffres.
   * @return id
   */
  @NotNull @Pattern(regexp = "^[0-9]{7}$") 
  @Schema(name = "id", example = "204", description = "Code permanent UADB à 7 chiffres.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public Etudiant matricule(String matricule) {
    this.matricule = matricule;
    return this;
  }

  /**
   * Code permanent UADB à 7 chiffres.
   * @return matricule
   */
  @NotNull @Pattern(regexp = "^[0-9]{7}$") 
  @Schema(name = "matricule", example = "2300123", description = "Code permanent UADB à 7 chiffres.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("matricule")
  public String getMatricule() {
    return matricule;
  }

  @JsonProperty("matricule")
  public void setMatricule(String matricule) {
    this.matricule = matricule;
  }

  public Etudiant lastname(@Nullable String lastname) {
    this.lastname = lastname;
    return this;
  }

  /**
   * Prénom de l'étudiant.
   * @return lastname
   */
  
  @Schema(name = "lastname", example = "Moussa", description = "Prénom de l'étudiant.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastname")
  public @Nullable String getLastname() {
    return lastname;
  }

  @JsonProperty("lastname")
  public void setLastname(@Nullable String lastname) {
    this.lastname = lastname;
  }

  public Etudiant firstname(@Nullable String firstname) {
    this.firstname = firstname;
    return this;
  }

  /**
   * Nom de famille de l'étudiant.
   * @return firstname
   */
  
  @Schema(name = "firstname", example = "Diop", description = "Nom de famille de l'étudiant.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstname")
  public @Nullable String getFirstname() {
    return firstname;
  }

  @JsonProperty("firstname")
  public void setFirstname(@Nullable String firstname) {
    this.firstname = firstname;
  }

  public Etudiant idCard(@Nullable String idCard) {
    this.idCard = idCard;
    return this;
  }

  /**
   * Numéro de Carte Nationale d'Identité.
   * @return idCard
   */
  
  @Schema(name = "idCard", example = "1234199801234", description = "Numéro de Carte Nationale d'Identité.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idCard")
  public @Nullable String getIdCard() {
    return idCard;
  }

  @JsonProperty("idCard")
  public void setIdCard(@Nullable String idCard) {
    this.idCard = idCard;
  }

  public Etudiant idDriver(@Nullable String idDriver) {
    this.idDriver = idDriver;
    return this;
  }

  /**
   * Numéro de permis de conduire.
   * @return idDriver
   */
  
  @Schema(name = "idDriver", example = "DK-123456", description = "Numéro de permis de conduire.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idDriver")
  public @Nullable String getIdDriver() {
    return idDriver;
  }

  @JsonProperty("idDriver")
  public void setIdDriver(@Nullable String idDriver) {
    this.idDriver = idDriver;
  }

  public Etudiant telephone(@Nullable String telephone) {
    this.telephone = telephone;
    return this;
  }

  /**
   * Numéro de téléphone portable.
   * @return telephone
   */
  
  @Schema(name = "telephone", example = "+221771234567", description = "Numéro de téléphone portable.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephone")
  public @Nullable String getTelephone() {
    return telephone;
  }

  @JsonProperty("telephone")
  public void setTelephone(@Nullable String telephone) {
    this.telephone = telephone;
  }

  public Etudiant emailInstitutionnel(@Nullable String emailInstitutionnel) {
    this.emailInstitutionnel = emailInstitutionnel;
    return this;
  }

  /**
   * Adresse email UADB.
   * @return emailInstitutionnel
   */
  @jakarta.validation.constraints.Email 
  @Schema(name = "emailInstitutionnel", example = "moussa.diop@uadb.edu.sn", description = "Adresse email UADB.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailInstitutionnel")
  public @Nullable String getEmailInstitutionnel() {
    return emailInstitutionnel;
  }

  @JsonProperty("emailInstitutionnel")
  public void setEmailInstitutionnel(@Nullable String emailInstitutionnel) {
    this.emailInstitutionnel = emailInstitutionnel;
  }

  public Etudiant ufr(String ufr) {
    this.ufr = ufr;
    return this;
  }

  /**
   * Unité de Formation et de Recherche (UFR).
   * @return ufr
   */
  @NotNull 
  @Schema(name = "ufr", example = "SAT (Sciences Appliquées et Technologies)", description = "Unité de Formation et de Recherche (UFR).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ufr")
  public String getUfr() {
    return ufr;
  }

  @JsonProperty("ufr")
  public void setUfr(String ufr) {
    this.ufr = ufr;
  }

  public Etudiant logementCrous(@Nullable LogementCrous logementCrous) {
    this.logementCrous = logementCrous;
    return this;
  }

  /**
   * Get logementCrous
   * @return logementCrous
   */
  @Valid 
  @Schema(name = "logementCrous", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logementCrous")
  public @Nullable LogementCrous getLogementCrous() {
    return logementCrous;
  }

  @JsonProperty("logementCrous")
  public void setLogementCrous(@Nullable LogementCrous logementCrous) {
    this.logementCrous = logementCrous;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Etudiant etudiant = (Etudiant) o;
    return Objects.equals(this.id, etudiant.id) &&
        Objects.equals(this.matricule, etudiant.matricule) &&
        Objects.equals(this.lastname, etudiant.lastname) &&
        Objects.equals(this.firstname, etudiant.firstname) &&
        Objects.equals(this.idCard, etudiant.idCard) &&
        Objects.equals(this.idDriver, etudiant.idDriver) &&
        Objects.equals(this.telephone, etudiant.telephone) &&
        Objects.equals(this.emailInstitutionnel, etudiant.emailInstitutionnel) &&
        Objects.equals(this.ufr, etudiant.ufr) &&
        Objects.equals(this.logementCrous, etudiant.logementCrous);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, matricule, lastname, firstname, idCard, idDriver, telephone, emailInstitutionnel, ufr, logementCrous);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Etudiant {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    matricule: ").append(toIndentedString(matricule)).append("\n");
    sb.append("    lastname: ").append(toIndentedString(lastname)).append("\n");
    sb.append("    firstname: ").append(toIndentedString(firstname)).append("\n");
    sb.append("    idCard: ").append(toIndentedString(idCard)).append("\n");
    sb.append("    idDriver: ").append(toIndentedString(idDriver)).append("\n");
    sb.append("    telephone: ").append(toIndentedString(telephone)).append("\n");
    sb.append("    emailInstitutionnel: ").append(toIndentedString(emailInstitutionnel)).append("\n");
    sb.append("    ufr: ").append(toIndentedString(ufr)).append("\n");
    sb.append("    logementCrous: ").append(toIndentedString(logementCrous)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}

