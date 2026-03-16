package view;

import dao.SalleDAO;
import dao.CoursDAO;
import dao.BatimentDAO;
import model.Salle;
import model.Cours;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;

public class MainView extends Application {

    private static final String BLEU_FONCE  = "#1A3A5C";
    private static final String BLEU_MID    = "#2471A3";
    private static final String GRIS_CLAIR  = "#F2F3F4";
    private static final String VERT        = "#1E8449";
    private static final String ORANGE      = "#D35400";
    private static final String VIOLET      = "#7D3C98";

    private StackPane contentArea;
    private Button btnActif = null;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setLeft(createMenu());
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: " + GRIS_CLAIR + ";");
        contentArea.setPadding(new Insets(25));
        root.setCenter(contentArea);

        showDashboard();

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("UNIV-SCHEDULER");
        stage.setScene(scene);
        stage.show();
    }

    // ── En-tête ──
    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + BLEU_FONCE + ";");
        header.setPadding(new Insets(14, 25, 14, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Label icone = new Label("🎓");
        icone.setFont(Font.font("Arial", 24));

        Label titre = new Label("UNIV-SCHEDULER");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.WHITE);

        Label sep = new Label("  |  ");
        sep.setTextFill(Color.LIGHTGRAY);

        Label sous = new Label("Gestion des Salles et Emplois du Temps");
        sous.setFont(Font.font("Arial", 13));
        sous.setTextFill(Color.LIGHTGRAY);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label version = new Label("Licence 2 Informatique — 2026");
        version.setFont(Font.font("Arial", 11));
        version.setTextFill(Color.web("#7FB3D3"));

        header.getChildren().addAll(icone, titre, sep, sous, spacer, version);
        return header;
    }

    // ── Menu gauche ──
    private VBox createMenu() {
        VBox menu = new VBox(4);
        menu.setStyle("-fx-background-color: " + BLEU_FONCE + ";");
        menu.setPadding(new Insets(20, 10, 20, 10));
        menu.setPrefWidth(190);

        Label menuTitre = new Label("  MENU PRINCIPAL");
        menuTitre.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        menuTitre.setTextFill(Color.web("#7FB3D3"));
        menuTitre.setPadding(new Insets(0, 0, 15, 0));

        Button btnDash      = createMenuButton("🏠  Tableau de bord");
        Button btnSalles    = createMenuButton("🏫  Salles");
        Button btnCours     = createMenuButton("📚  Cours");
        Button btnBatiments = createMenuButton("🏢  Bâtiments");
        Button btnEmploi    = createMenuButton("🗓️  Emploi du temps");

        btnDash.setOnAction(e -> { setActif(btnDash); showDashboard(); });
        btnSalles.setOnAction(e -> { setActif(btnSalles); showSalles(); });
        btnCours.setOnAction(e -> { setActif(btnCours); showCours(); });
        btnBatiments.setOnAction(e -> { setActif(btnBatiments); showBatiments(); });
        btnEmploi.setOnAction(e -> {
            setActif(btnEmploi);
            contentArea.getChildren().setAll(new EmploiDuTempsView().getView());
        });

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2E6DA4;");

        Label infoTitre = new Label("  INFORMATIONS");
        infoTitre.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        infoTitre.setTextFill(Color.web("#7FB3D3"));
        infoTitre.setPadding(new Insets(15, 0, 10, 0));

        Label info = new Label("  Yacine & Amina\n  L2 Informatique\n  Mars 2026");
        info.setFont(Font.font("Arial", 11));
        info.setTextFill(Color.web("#AED6F1"));
        info.setPadding(new Insets(0, 0, 0, 5));

        setActif(btnDash);
        menu.getChildren().addAll(menuTitre, btnDash, btnSalles,
                btnCours, btnBatiments, btnEmploi, sep, infoTitre, info);
        return menu;
    }

    private void setActif(Button btn) {
        if (btnActif != null) btnActif.setStyle(styleMenuNormal());
        btn.setStyle(styleMenuActif());
        btnActif = btn;
    }

    private String styleMenuNormal() {
        return "-fx-background-color: transparent;" +
                "-fx-text-fill: #AED6F1;" +
                "-fx-padding: 10 15 10 15;" +
                "-fx-cursor: hand;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-font-size: 13;";
    }

    private String styleMenuActif() {
        return "-fx-background-color: #2471A3;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10 15 10 15;" +
                "-fx-cursor: hand;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-font-size: 13;" +
                "-fx-background-radius: 6;";
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(styleMenuNormal());
        btn.setOnMouseEntered(e -> {
            if (btn != btnActif) btn.setStyle(
                    "-fx-background-color: #1F5C8A;" +
                            "-fx-text-fill: white;" +
                            "-fx-padding: 10 15 10 15;" +
                            "-fx-cursor: hand;" +
                            "-fx-alignment: CENTER_LEFT;" +
                            "-fx-font-size: 13;" +
                            "-fx-background-radius: 6;"
            );
        });
        btn.setOnMouseExited(e -> {
            if (btn != btnActif) btn.setStyle(styleMenuNormal());
        });
        return btn;
    }

    // ── Dashboard ──
    private void showDashboard() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(5));

        Label titre = new Label("🏠 Tableau de bord");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        Label sousTitre = new Label("Vue d'ensemble du système UNIV-SCHEDULER");
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setTextFill(Color.GRAY);

        SalleDAO salleDAO = new SalleDAO();
        CoursDAO coursDAO = new CoursDAO();
        BatimentDAO batDAO = new BatimentDAO();

        int nbSalles    = salleDAO.getToutesLesSalles().size();
        int nbCours     = coursDAO.getTousLesCours().size();
        int nbBatiments = batDAO.getTousLesBatiments().size();

        HBox cartes = new HBox(20);
        cartes.getChildren().addAll(
                createCarte("🏫", "Salles",    String.valueOf(nbSalles),    BLEU_MID),
                createCarte("📚", "Cours",     String.valueOf(nbCours),     VERT),
                createCarte("🏢", "Bâtiments", String.valueOf(nbBatiments), ORANGE),
                createCarte("👥", "Étudiants", "1",                        VIOLET)
        );

        Label titreCours = new Label("📋 Derniers cours planifiés");
        titreCours.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        titreCours.setTextFill(Color.web(BLEU_FONCE));

        TableView<Cours> table = createTableCours();
        table.setPrefHeight(250);

        panel.getChildren().addAll(titre, sousTitre, cartes, titreCours, table);
        contentArea.getChildren().setAll(panel);
    }

    private VBox createCarte(String icone, String label, String valeur, String couleur) {
        VBox carte = new VBox(8);
        carte.setPadding(new Insets(20));
        carte.setPrefWidth(180);
        carte.setPrefHeight(110);
        carte.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );
        carte.setAlignment(Pos.CENTER_LEFT);

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label(icone);
        ico.setFont(Font.font("Arial", 22));
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#D6EAF8"));
        top.getChildren().addAll(ico, lbl);

        Label val = new Label(valeur);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        val.setTextFill(Color.WHITE);

        carte.getChildren().addAll(top, val);
        return carte;
    }

    // ── Vue Salles ──
    private void showSalles() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        Label titre = new Label("🏫 Gestion des Salles");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        HBox actions = new HBox(10);
        Button btnAjouter   = createBouton("+ Ajouter", BLEU_MID);
        Button btnSupprimer = createBouton("🗑 Supprimer", "#C0392B");
        actions.getChildren().addAll(btnAjouter, btnSupprimer);

        TableView<Salle> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );
        table.setPrefHeight(450);

        TableColumn<Salle, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<Salle, String> colNumero = new TableColumn<>("Numéro");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colNumero.setPrefWidth(110);

        TableColumn<Salle, Integer> colCapacite = new TableColumn<>("Capacité");
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colCapacite.setPrefWidth(110);

        TableColumn<Salle, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(110);

        TableColumn<Salle, Boolean> colDispo = new TableColumn<>("Disponible");
        colDispo.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        colDispo.setPrefWidth(110);

        table.getColumns().addAll(colId, colNumero, colCapacite, colType, colDispo);

        SalleDAO dao = new SalleDAO();
        ObservableList<Salle> data = FXCollections.observableArrayList(
                dao.getToutesLesSalles());
        table.setItems(data);

        btnAjouter.setOnAction(e -> showFormulaireAjoutSalle(table, data));
        btnSupprimer.setOnAction(e -> {
            Salle selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne une salle à supprimer !");
                return;
            }
            if (dao.supprimer(selected.getId())) {
                data.setAll(dao.getToutesLesSalles());
            }
        });

        panel.getChildren().addAll(titre, actions, table);
        contentArea.getChildren().setAll(panel);
    }

    // ── Formulaire ajout salle ──
    private void showFormulaireAjoutSalle(TableView<Salle> table,
                                          ObservableList<Salle> data) {
        Stage popup = new Stage();
        popup.setTitle("Ajouter une salle");

        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setPrefWidth(320);
        form.setStyle("-fx-background-color: white;");

        Label titre = new Label("🏫 Nouvelle Salle");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TextField tfNumero   = createField("Numéro (ex: C301)");
        TextField tfCapacite = createField("Capacité (ex: 30)");

        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("TD", "TP", "AMPHI", "REUNION");
        cbType.setPromptText("Type de salle");
        cbType.setPrefWidth(Double.MAX_VALUE);

        TextField tfBatiment = createField("ID Bâtiment (ex: 1)");

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("💾 Enregistrer", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfNumero.getText().isEmpty() || tfCapacite.getText().isEmpty()
                    || cbType.getValue() == null || tfBatiment.getText().isEmpty()) {
                lblMsg.setText("⚠️ Remplis tous les champs !");
                return;
            }
            try {
                Salle s = new Salle();
                s.setNumero(tfNumero.getText());
                s.setCapacite(Integer.parseInt(tfCapacite.getText()));
                s.setType(cbType.getValue());
                s.setBatimentId(Integer.parseInt(tfBatiment.getText()));

                SalleDAO dao = new SalleDAO();
                if (dao.ajouterSalle(s)) {
                    data.setAll(dao.getToutesLesSalles());
                    popup.close();
                } else {
                    lblMsg.setText("❌ Erreur lors de l'ajout !");
                }
            } catch (NumberFormatException ex) {
                lblMsg.setText("⚠️ Capacité et ID doivent être des nombres !");
            }
        });

        form.getChildren().addAll(titre,
                new Label("Numéro :"), tfNumero,
                new Label("Capacité :"), tfCapacite,
                new Label("Type :"), cbType,
                new Label("ID Bâtiment :"), tfBatiment,
                lblMsg, btnSave);
        popup.setScene(new Scene(form));
        popup.show();
    }

    // ── Vue Cours ──
    private void showCours() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        Label titre = new Label("📚 Gestion des Cours");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TableView<Cours> table = createTableCours();
        table.setPrefHeight(500);

        panel.getChildren().addAll(titre, table);
        contentArea.getChildren().setAll(panel);
    }

    private TableView<Cours> createTableCours() {
        TableView<Cours> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );

        TableColumn<Cours, String> colMatiere = new TableColumn<>("Matière");
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("matiere"));
        colMatiere.setPrefWidth(160);

        TableColumn<Cours, String> colGroupe = new TableColumn<>("Groupe");
        colGroupe.setCellValueFactory(new PropertyValueFactory<>("groupe"));
        colGroupe.setPrefWidth(90);

        TableColumn<Cours, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(80);

        TableColumn<Cours, String> colEnseignant = new TableColumn<>("Enseignant");
        colEnseignant.setCellValueFactory(new PropertyValueFactory<>("nomEnseignant"));
        colEnseignant.setPrefWidth(160);

        TableColumn<Cours, String> colSalle = new TableColumn<>("Salle");
        colSalle.setCellValueFactory(new PropertyValueFactory<>("numeroSalle"));
        colSalle.setPrefWidth(90);

        table.getColumns().addAll(colMatiere, colGroupe, colType,
                colEnseignant, colSalle);

        CoursDAO dao = new CoursDAO();
        table.setItems(FXCollections.observableArrayList(dao.getTousLesCours()));
        return table;
    }

    // ── Vue Bâtiments ──
    private void showBatiments() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        Label titre = new Label("🏢 Gestion des Bâtiments");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TableView<model.Batiment> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );
        table.setPrefHeight(500);

        TableColumn<model.Batiment, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<model.Batiment, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(200);

        TableColumn<model.Batiment, String> colLoc = new TableColumn<>("Localisation");
        colLoc.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colLoc.setPrefWidth(200);

        TableColumn<model.Batiment, Integer> colEtages = new TableColumn<>("Étages");
        colEtages.setCellValueFactory(new PropertyValueFactory<>("nombreEtages"));
        colEtages.setPrefWidth(100);

        table.getColumns().addAll(colId, colNom, colLoc, colEtages);

        BatimentDAO dao = new BatimentDAO();
        table.setItems(FXCollections.observableArrayList(dao.getTousLesBatiments()));

        panel.getChildren().addAll(titre, table);
        contentArea.getChildren().setAll(panel);
    }

    // ── Helpers ──
    private Button createBouton(String text, String couleur) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13;" +
                        "-fx-padding: 8 18 8 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 6;"
        );
        return btn;
    }

    private TextField createField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
                "-fx-font-size: 13;" +
                        "-fx-padding: 8;" +
                        "-fx-background-radius: 6;"
        );
        return tf;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}