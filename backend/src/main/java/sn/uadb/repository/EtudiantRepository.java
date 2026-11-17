package sn.uadb.repository;

import java.util.Optional;

import org.openapitools.model.Etudiant;
import org.openapitools.model.LogementCrous;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class EtudiantRepository {

    private static final String SQL_BY_MATRICULE = """
            SELECT e.id, e.matricule, e.lastname, e.firstname, e.ufr, e.telephone,
                   e.email_institutionnel, e.id_card, e.id_driver,
                   l.pavillon, l.numero_chambre, l.lit
            FROM etudiant e
            LEFT JOIN logement_crous l ON l.etudiant_id = e.id
            WHERE e.matricule = :matricule
            """;

    private final JdbcClient jdbc;

    public EtudiantRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Etudiant> findByMatricule(String matricule) {
        return jdbc.sql(SQL_BY_MATRICULE)
                .param("matricule", matricule)   // requête paramétrée : pas d'injection SQL
                .query((rs, rowNum) -> {
                    Etudiant e = new Etudiant();
                    e.setId(String.valueOf(rs.getLong("id")));
                    e.setMatricule(rs.getString("matricule"));
                    e.setLastname(rs.getString("lastname"));
                    e.setFirstname(rs.getString("firstname"));
                    e.setUfr(rs.getString("ufr"));
                    e.setTelephone(rs.getString("telephone"));
                    e.setEmailInstitutionnel(rs.getString("email_institutionnel"));
                    e.setIdCard(rs.getString("id_card"));
                    e.setIdDriver(rs.getString("id_driver"));

                    String pavillon = rs.getString("pavillon");
                    if (pavillon != null) {          // LEFT JOIN : null si non logé
                        LogementCrous l = new LogementCrous();
                        l.setPavillon(pavillon);
                        l.setNumeroChambre(rs.getString("numero_chambre"));
                        l.setLit(rs.getString("lit"));
                        e.setLogementCrous(l);
                    }
                    return e;
                })
                .optional();
    }
}