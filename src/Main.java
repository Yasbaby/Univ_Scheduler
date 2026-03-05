import dao.SalleDAO;
import model.Salle;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        SalleDAO salleDAO = new SalleDAO();

        // Récupère toutes les salles de la base
        List<Salle> salles = salleDAO.getToutesLesSalles();

        // Affiche chaque salle dans la console
        System.out.println("=== Liste des salles ===");
        for (Salle s : salles) {
            System.out.println(s);
        }
    }
}
