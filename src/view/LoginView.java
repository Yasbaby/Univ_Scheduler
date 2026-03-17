package view;

import dao.UtilisateurDAO;
import model.Utilisateur;
import service.SessionManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView extends Application {

    private static final String BLEU_FONCE = "#1A3A5C";
    private static final String BLEU_MID   = "#2471A3";
    private static final String GRIS       = "#F2F3F4";

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + GRIS + ";");

        // ── Panneau gauche (bleu) ──
        VBox panneauGauche = new VBox(20);
        panneauGauche.setPrefWidth(380);
        panneauGauche.setStyle("-fx-background-color: " + BLEU_FONCE + ";");
        panneauGauche.setAlignment(Pos.CENTER);
        panneauGauche.setPadding(new Insets(60));

        Label icone = new Label("🎓");
        icone.setFont(Font.font("Arial", 60));

        Label titre = new Label("UNIV-SCHEDULER");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setTextFill(Color.WHITE);

        Label sous = new Label("Gestion des Salles\net Emplois du Temps");
        sous.setFont(Font.font("Arial", 14));
        sous.setTextFill(Color.web("#AED6F1"));
        sous.setAlignment(Pos.CENTER);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2E6DA4;");

        Label info = new Label("Licence 2 Informatique\nYacine & Amina\nMars 2026");
        info.setFont(Font.font("Arial", 12));
        info.setTextFill(Color.web("#7FB3D3"));
        info.setAlignment(Pos.CENTER);

        // Comptes de test
        Label lblComptes = new Label(
                "Comptes de test :\n" +
                        "admin@univ.sn / admin123\n" +
                        "gestionnaire@univ.sn / gestionnaire123\n" +
                        "diallo@univ.sn / enseignant123\n" +
                        "ba@univ.sn / etudiant123"
        );
        lblComptes.setFont(Font.font("Arial", 10));
        lblComptes.setTextFill(Color.web("#7FB3D3"));
        lblComptes.setAlignment(Pos.CENTER);

        panneauGauche.getChildren().addAll(
                icone, titre, sous, sep, info, lblComptes);

        // ── Panneau droit (formulaire) ──
        VBox panneauDroit = new VBox(15);
        panneauDroit.setAlignment(Pos.CENTER);
        panneauDroit.setPadding(new Insets(60, 50, 60, 50));

        Label titreCo = new Label("Connexion");
        titreCo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titreCo.setTextFill(Color.web(BLEU_FONCE));

        Label sousCo = new Label("Connectez-vous à votre compte");
        sousCo.setFont(Font.font("Arial", 13));
        sousCo.setTextFill(Color.GRAY);

        // Champ email
        Label lblEmail = new Label("Adresse email");
        lblEmail.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblEmail.setTextFill(Color.web(BLEU_FONCE));

        TextField tfEmail = new TextField();
        tfEmail.setPromptText("exemple@univ.sn");
        tfEmail.setPrefHeight(40);
        tfEmail.setStyle(
                "-fx-font-size: 13;" +
                        "-fx-padding: 8;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #BDC3C7;" +
                        "-fx-border-radius: 6;"
        );

        // Champ mot de passe
        Label lblMdp = new Label("Mot de passe");
        lblMdp.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblMdp.setTextFill(Color.web(BLEU_FONCE));

        PasswordField pfMdp = new PasswordField();
        pfMdp.setPromptText("Votre mot de passe");
        pfMdp.setPrefHeight(40);
        pfMdp.setStyle(
                "-fx-font-size: 13;" +
                        "-fx-padding: 8;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #BDC3C7;" +
                        "-fx-border-radius: 6;"
        );

        // Message erreur
        Label lblErreur = new Label("");
        lblErreur.setTextFill(Color.RED);
        lblErreur.setFont(Font.font("Arial", 12));

        // Bouton connexion
        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setPrefWidth(Double.MAX_VALUE);
        btnConnexion.setPrefHeight(45);
        btnConnexion.setStyle(
                "-fx-background-color: " + BLEU_MID + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );

        // Action connexion
        btnConnexion.setOnAction(e -> {
            String email = tfEmail.getText().trim();
            String mdp   = pfMdp.getText().trim();

            if (email.isEmpty() || mdp.isEmpty()) {
                lblErreur.setText("⚠️ Remplis tous les champs !");
                return;
            }

            // Vérification avec la base
            UtilisateurDAO dao = new UtilisateurDAO();
            Utilisateur u = dao.seConnecter(email, mdp);

            if (u == null) {
                lblErreur.setText("❌ Email ou mot de passe incorrect !");
                tfEmail.setStyle(
                        "-fx-font-size: 13; -fx-padding: 8;" +
                                "-fx-background-radius: 6;" +
                                "-fx-border-color: red; -fx-border-radius: 6;"
                );
                pfMdp.setStyle(
                        "-fx-font-size: 13; -fx-padding: 8;" +
                                "-fx-background-radius: 6;" +
                                "-fx-border-color: red; -fx-border-radius: 6;"
                );
            } else {
                // ✅ Sauvegarde la session RBAC
                SessionManager.getInstance().connecter(u);

                // Ferme le login
                stage.close();

                // Redirige selon le rôle
                Stage mainStage = new Stage();
                try {
                    String role = SessionManager.getInstance().getRole();
                    switch (role) {
                        case "ADMINISTRATEUR":
                        case "GESTIONNAIRE":
                            new MainView().start(mainStage);
                            break;
                        case "ENSEIGNANT":
                            // Amina va créer EnseignantView
                            new MainView().start(mainStage);
                            break;
                        case "ETUDIANT":
                            // Amina va créer EtudiantView
                            new MainView().start(mainStage);
                            break;
                        default:
                            new MainView().start(mainStage);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        panneauDroit.getChildren().addAll(
                titreCo, sousCo,
                new Label(""),
                lblEmail, tfEmail,
                lblMdp, pfMdp,
                lblErreur,
                btnConnexion
        );

        root.setLeft(panneauGauche);
        root.setCenter(panneauDroit);

        Scene scene = new Scene(root, 750, 500);
        stage.setTitle("UNIV-SCHEDULER — Connexion");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}