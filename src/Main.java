import dao.BatimentDAO;
import model.Batiment;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        BatimentDAO dao = new BatimentDAO();

        // Affiche tous les bâtiments
        List<Batiment> batiments = dao.getTousLesBatiments();
        System.out.println("=== Liste des bâtiments ===");
        for (Batiment b : batiments) {
            System.out.println(b);
        }
    }
}