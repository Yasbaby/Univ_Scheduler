import dao.CoursDAO;
import model.Cours;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        CoursDAO dao = new CoursDAO();

        List<Cours> cours = dao.getTousLesCours();
        System.out.println("=== Liste des cours ===");
        for (Cours c : cours) {
            System.out.println(c);
        }
    }
}