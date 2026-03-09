import dao.ConflitDAO;

public class Main {
    public static void main(String[] args) {

        ConflitDAO conflitDAO = new ConflitDAO();

        // Test 1 : salle 1, enseignant 3, créneau 1 → déjà utilisés !
        String resultat1 = conflitDAO.verifierConflits(1, 3, 1, 25);
        System.out.println("Test 1 : " + resultat1);

        // Test 2 : salle 3, enseignant 4, créneau 4 → disponibles
        String resultat2 = conflitDAO.verifierConflits(3, 4, 4, 20);
        System.out.println("Test 2 : " + resultat2);
    }
}