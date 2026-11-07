package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * LogementCrous
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-07T15:27:12.796389883Z[Etc/UTC]", comments = "Generator version: 7.26.0")
public class LogementCrous {

  private @Nullable String pavillon;

  private @Nullable String numeroChambre;

  private @Nullable String lit;

  public LogementCrous pavillon(@Nullable String pavillon) {
    this.pavillon = pavillon;
    return this;
  }

  /**
   * Nom ou lettre du pavillon au campus social.
   * @return pavillon
   */
  
  @Schema(name = "pavillon", example = "Pavillon A", description = "Nom ou lettre du pavillon au campus social.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pavillon")
  public @Nullable String getPavillon() {
    return pavillon;
  }

  @JsonProperty("pavillon")
  public void setPavillon(@Nullable String pavillon) {
    this.pavillon = pavillon;
  }

  public LogementCrous numeroChambre(@Nullable String numeroChambre) {
    this.numeroChambre = numeroChambre;
    return this;
  }

  /**
   * Numéro de chambre.
   * @return numeroChambre
   */
  
  @Schema(name = "numeroChambre", example = "14", description = "Numéro de chambre.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numeroChambre")
  public @Nullable String getNumeroChambre() {
    return numeroChambre;
  }

  @JsonProperty("numeroChambre")
  public void setNumeroChambre(@Nullable String numeroChambre) {
    this.numeroChambre = numeroChambre;
  }

  public LogementCrous lit(@Nullable String lit) {
    this.lit = lit;
    return this;
  }

  /**
   * Identifiant du lit (ex: A, B).
   * @return lit
   */
  
  @Schema(name = "lit", example = "B", description = "Identifiant du lit (ex: A, B).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lit")
  public @Nullable String getLit() {
    return lit;
  }

  @JsonProperty("lit")
  public void setLit(@Nullable String lit) {
    this.lit = lit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogementCrous logementCrous = (LogementCrous) o;
    return Objects.equals(this.pavillon, logementCrous.pavillon) &&
        Objects.equals(this.numeroChambre, logementCrous.numeroChambre) &&
        Objects.equals(this.lit, logementCrous.lit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pavillon, numeroChambre, lit);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogementCrous {\n");
    sb.append("    pavillon: ").append(toIndentedString(pavillon)).append("\n");
    sb.append("    numeroChambre: ").append(toIndentedString(numeroChambre)).append("\n");
    sb.append("    lit: ").append(toIndentedString(lit)).append("\n");
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

