INSERT OR IGNORE INTO etudiant (id, matricule, lastname, firstname, ufr, telephone, email_institutionnel) VALUES
 (1, '2300001', 'Diop',   'Maodo',    'SATIC', '771234567', 'maodo.diop@uadb.edu.sn'),
 (2, '2300002', 'Ndiaye', 'Aminata',  'SEFS',  '781234567', 'aminata.ndiaye@uadb.edu.sn'),
 (3, '2200003', 'Fall',   'Cheikh',   'SJP',   '761234567', 'cheikh.fall@uadb.edu.sn');

INSERT OR IGNORE INTO logement_crous (etudiant_id, pavillon, numero_chambre, lit) VALUES
 (1, 'B', '102', 'A'),
 (2, 'A', '215', 'B');