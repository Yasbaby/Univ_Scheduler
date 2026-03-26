package view;

import dao.CoursDAO;
import model.Cours;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class EmploiDuTempsView {

    private static final String BLEU_FONCE = "#1A3A5C";
    private static final String BLEU_MID   = "#2471A3";
    private static final String BLANC      = "#FFFFFF";
    private static final String GRIS       = "#F2F3F4";

    private static final String[] JOURS = {
            "LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI"
    };
    private static final String[] CRENEAUX = {
            "08:00-10:00", "10:00-12:00", "12:00-14:00",
            "14:00-16:00", "16:00-18:00"
    };

    public VBox getView() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        // Titre
        Label titre = new Label("🗓️ Emploi du Temps");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        Label sousTitre = new Label("Vue hebdomadaire de tous les cours");
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setTextFill(Color.GRAY);

        // Grille
        GridPane grille = createGrille();

        ScrollPane scroll = new ScrollPane(grille);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: white;");

        panel.getChildren().addAll(titre, sousTitre, scroll);
        return panel;
    }

    private GridPane createGrille() {
        GridPane grille = new GridPane();
        grille.setHgap(2);
        grille.setVgap(2);
        grille.setPadding(new Insets(10));
        grille.setStyle("-fx-background-color: white;");

        // Colonne créneaux
        grille.getColumnConstraints().add(columnConstraint(100));
        for (int i = 0; i < JOURS.length; i++) {
            grille.getColumnConstraints().add(columnConstraint(160));
        }

        // En-tête : cellule vide en haut à gauche
        Label vide = new Label("");
        grille.add(vide, 0, 0);

        // En-tête : jours
        for (int j = 0; j < JOURS.length; j++) {
            Label lblJour = new Label(JOURS[j]);
            lblJour.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lblJour.setTextFill(Color.WHITE);
            lblJour.setAlignment(Pos.CENTER);
            lblJour.setMaxWidth(Double.MAX_VALUE);
            lblJour.setPadding(new Insets(8));
            lblJour.setStyle(
                    "-fx-background-color: " + BLEU_FONCE + ";" +
                            "-fx-background-radius: 4;"
            );
            grille.add(lblJour, j + 1, 0);
        }

        // Créneaux horaires
        for (int c = 0; c < CRENEAUX.length; c++) {
            Label lblHeure = new Label(CRENEAUX[c]);
            lblHeure.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            lblHeure.setTextFill(Color.web(BLEU_MID));
            lblHeure.setPadding(new Insets(8));
            lblHeure.setAlignment(Pos.CENTER);
            lblHeure.setMaxWidth(Double.MAX_VALUE);
            lblHeure.setStyle(
                    "-fx-background-color: #EBF5FB;" +
                            "-fx-background-radius: 4;"
            );
            grille.add(lblHeure, 0, c + 1);

            // Cellules vides pour chaque jour
            for (int j = 0; j < JOURS.length; j++) {
                Label cellule = new Label("");
                cellule.setMaxWidth(Double.MAX_VALUE);
                cellule.setMaxHeight(Double.MAX_VALUE);
                cellule.setMinHeight(60);
                cellule.setStyle(
                        "-fx-background-color: " + GRIS + ";" +
                                "-fx-background-radius: 4;"
                );
                grille.add(cellule, j + 1, c + 1);
            }
        }

        // Remplir avec les cours depuis MySQL
        CoursDAO dao = new CoursDAO();
        List<Cours> coursList = dao.getTousLesCours();

        for (Cours cours : coursList) {
            if (cours.getCreneau() == null) continue;

            String jourCours  = cours.getCreneau().getJour().name();
            String heureDeb   = cours.getCreneau().getHeureDebut().toString();

            int col = getColonne(jourCours);
            int row = getLigne(heureDeb);

            if (col == -1 || row == -1) continue;

            VBox card = createCarteCoursm(cours);
            grille.add(card, col, row);
        }

        return grille;
    }

    private VBox createCarteCoursm(Cours cours) {
        VBox card = new VBox(3);
        card.setPadding(new Insets(6));
        card.setMinHeight(60);
        card.setMaxWidth(Double.MAX_VALUE);

        // Couleur selon type
        String couleur;
        switch (cours.getType()) {
            case "CM":     couleur = "#2471A3"; break;
            case "TD":     couleur = "#1E8449"; break;
            case "TP":     couleur = "#D35400"; break;
            case "EXAMEN": couleur = "#922B21"; break;
            default:       couleur = "#7D3C98";
        }

        card.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        );

        Label lblMatiere = new Label(cours.getMatiere());
        lblMatiere.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblMatiere.setTextFill(Color.WHITE);
        lblMatiere.setWrapText(true);

        Label lblGroupe = new Label(cours.getGroupe() + " • " + cours.getType());
        lblGroupe.setFont(Font.font("Arial", 10));
        lblGroupe.setTextFill(Color.web("#D6EAF8"));

        Label lblSalle = new Label("🏫 " + cours.getNumeroSalle());
        lblSalle.setFont(Font.font("Arial", 10));
        lblSalle.setTextFill(Color.web("#D6EAF8"));

        card.getChildren().addAll(lblMatiere, lblGroupe, lblSalle);
        return card;
    }

    private int getColonne(String jour) {
        for (int i = 0; i < JOURS.length; i++) {
            if (JOURS[i].equals(jour)) return i + 1;
        }
        return -1;
    }

    private int getLigne(String heureDebut) {
        if (heureDebut.startsWith("8") || heureDebut.startsWith("08")) return 1;
        if (heureDebut.startsWith("10")) return 2;
        if (heureDebut.startsWith("12")) return 3;
        if (heureDebut.startsWith("14")) return 4;
        if (heureDebut.startsWith("16")) return 5;
        return -1;
    }

    private ColumnConstraints columnConstraint(double width) {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPrefWidth(width);
        return cc;
    }
}