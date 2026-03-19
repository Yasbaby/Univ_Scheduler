package view;

import dao.SalleDAO;
import dao.CoursDAO;
import dao.BatimentDAO;
import dao.ConflitDAO;
import model.Salle;
import model.Cours;
import service.SessionManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;

public class MainView extends Application {

    private static final String BLEU_FONCE = "#1A3A5C";
    private static final String BLEU_MID   = "#2471A3";
    private static final String GRIS_CLAIR = "#F2F3F4";
    private static final String VERT       = "#1E8449";
    private static final String ORANGE     = "#D35400";
    private static final String VIOLET     = "#7D3C98";

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

        // Affiche la bonne page selon le rôle
        SessionManager session = SessionManager.getInstance();
        if (session.aLaPermission("CONSULTER_STATISTIQUES")) {
            showDashboard();
        } else {
            contentArea.getChildren().setAll(new EmploiDuTempsView().getView());
        }

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

        // Affiche l'utilisateur connecté et son rôle
        SessionManager session = SessionManager.getInstance();
        String nomUser = session.estConnecte() ?
                session.getUtilisateur().getNomComplet() + " — " + session.getRole() : "";
        Label lblUser = new Label("👤 " + nomUser);
        lblUser.setFont(Font.font("Arial", 11));
        lblUser.setTextFill(Color.web("#7FB3D3"));

        header.getChildren().addAll(icone, titre, sep, sous, spacer, lblUser);
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
        Button btnConflits  = createMenuButton("⚠️  Conflits");
        Button btnRecherche = createMenuButton("🔍  Rechercher salle");
        Button btnUsers = createMenuButton("👥  Utilisateurs");

        btnUsers.setOnAction(e -> { setActif(btnUsers); showUtilisateurs(); });
        btnDash.setOnAction(e -> { setActif(btnDash); showDashboard(); });
        btnSalles.setOnAction(e -> { setActif(btnSalles); showSalles(); });
        btnCours.setOnAction(e -> { setActif(btnCours); showCours(); });
        btnBatiments.setOnAction(e -> { setActif(btnBatiments); showBatiments(); });
        btnEmploi.setOnAction(e -> {
            setActif(btnEmploi);
            contentArea.getChildren().setAll(new EmploiDuTempsView().getView());
        });
        btnConflits.setOnAction(e -> { setActif(btnConflits); showConflits(); });
        btnRecherche.setOnAction(e -> { setActif(btnRecherche); showRechercherSalle(); });

        // ── RBAC : cache les boutons selon les permissions ──
        SessionManager session = SessionManager.getInstance();
        String role = session.getRole();

        // Par défaut on cache tout
        btnDash.setVisible(false);      btnDash.setManaged(false);
        btnSalles.setVisible(false);    btnSalles.setManaged(false);
        btnCours.setVisible(false);     btnCours.setManaged(false);
        btnBatiments.setVisible(false); btnBatiments.setManaged(false);
        btnEmploi.setVisible(false);    btnEmploi.setManaged(false);
        btnConflits.setVisible(false);  btnConflits.setManaged(false);
        btnRecherche.setVisible(false); btnRecherche.setManaged(false);

        // On affiche selon le rôle
        switch (role) {
            case "ADMINISTRATEUR":
                btnDash.setVisible(true);      btnDash.setManaged(true);
                btnSalles.setVisible(true);    btnSalles.setManaged(true);
                btnBatiments.setVisible(true); btnBatiments.setManaged(true);
                btnCours.setVisible(true);     btnCours.setManaged(true);
                btnConflits.setVisible(true);  btnConflits.setManaged(true);
                btnUsers.setVisible(true);     btnUsers.setManaged(true);  // ← ajoute cette ligne
                break;
            case "GESTIONNAIRE":
                btnCours.setVisible(true);     btnCours.setManaged(true);
                btnConflits.setVisible(true);  btnConflits.setManaged(true);
                btnEmploi.setVisible(true);    btnEmploi.setManaged(true);
                break;
            case "ENSEIGNANT":
            case "ETUDIANT":
                btnEmploi.setVisible(true);    btnEmploi.setManaged(true);
                btnRecherche.setVisible(true); btnRecherche.setManaged(true);
                break;
        }

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2E6DA4;");

        Label infoTitre = new Label("  INFORMATIONS");
        infoTitre.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        infoTitre.setTextFill(Color.web("#7FB3D3"));
        infoTitre.setPadding(new Insets(15, 0, 10, 0));

        Label info = new Label("  Yacine & Amina\n  L2 Informatique\n  Mars 2026");
        info.setFont(Font.font("Arial", 11));
        info.setTextFill(Color.web("#AED6F1"));

        // Bouton déconnexion
        Button btnDeconnexion = new Button("🚪 Déconnexion");
        btnDeconnexion.setMaxWidth(Double.MAX_VALUE);
        btnDeconnexion.setStyle(
                "-fx-background-color: #C0392B;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 15 8 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 6;" +
                        "-fx-font-size: 12;"
        );
        btnDeconnexion.setOnAction(e -> {
            SessionManager.getInstance().deconnecter();
            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            stage.close();
            try {
                new LoginView().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setActif(btnDash);
        menu.getChildren().addAll(menuTitre, btnDash, btnSalles,
                btnCours, btnBatiments, btnEmploi, btnConflits,
                btnRecherche, btnUsers, sep, infoTitre, info, btnDeconnexion);
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

        SalleDAO salleDAO  = new SalleDAO();
        CoursDAO coursDAO  = new CoursDAO();
        BatimentDAO batDAO = new BatimentDAO();

        List<Salle> salles     = salleDAO.getToutesLesSalles();
        List<Cours> coursList  = coursDAO.getTousLesCours();
        int nbBatiments        = batDAO.getTousLesBatiments().size();

        HBox cartes = new HBox(20);
        cartes.getChildren().addAll(
                createCarte("🏫", "Salles",    String.valueOf(salles.size()),    BLEU_MID),
                createCarte("📚", "Cours",     String.valueOf(coursList.size()), VERT),
                createCarte("🏢", "Bâtiments", String.valueOf(nbBatiments),      ORANGE),
                createCarte("⚠️", "Conflits",  "0",                              "#C0392B")
        );

        HBox graphiques = new HBox(20);

        // BarChart — cours par salle
        CategoryAxis xBar = new CategoryAxis();
        NumberAxis yBar   = new NumberAxis(0, 10, 1);
        xBar.setLabel("Salles");
        yBar.setLabel("Nb cours");
        BarChart<String, Number> barChart = new BarChart<>(xBar, yBar);
        barChart.setTitle("📊 Cours par salle");
        barChart.setPrefWidth(340);
        barChart.setPrefHeight(280);
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        XYChart.Series<String, Number> seriesBar = new XYChart.Series<>();
        for (Salle s : salles) {
            long count = coursList.stream()
                    .filter(c -> c.getSalleId() == s.getId()).count();
            seriesBar.getData().add(new XYChart.Data<>(s.getNumero(), count));
        }
        barChart.getData().add(seriesBar);

        // PieChart — types de cours
        PieChart pieChart = new PieChart();
        pieChart.setTitle("🥧 Types de cours");
        pieChart.setPrefWidth(300);
        pieChart.setPrefHeight(280);
        pieChart.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        long nbCM = coursList.stream().filter(c -> "CM".equals(c.getType())).count();
        long nbTD = coursList.stream().filter(c -> "TD".equals(c.getType())).count();
        long nbTP = coursList.stream().filter(c -> "TP".equals(c.getType())).count();
        long nbEx = coursList.stream().filter(c -> "EXAMEN".equals(c.getType())).count();
        if (nbCM > 0) pieChart.getData().add(new PieChart.Data("CM (" + nbCM + ")", nbCM));
        if (nbTD > 0) pieChart.getData().add(new PieChart.Data("TD (" + nbTD + ")", nbTD));
        if (nbTP > 0) pieChart.getData().add(new PieChart.Data("TP (" + nbTP + ")", nbTP));
        if (nbEx > 0) pieChart.getData().add(new PieChart.Data("Examens (" + nbEx + ")", nbEx));

        // BarChart — cours par jour
        CategoryAxis xJour = new CategoryAxis();
        NumberAxis yJour   = new NumberAxis(0, 5, 1);
        xJour.setLabel("Jour");
        yJour.setLabel("Nb cours");
        BarChart<String, Number> chartJour = new BarChart<>(xJour, yJour);
        chartJour.setTitle("📅 Cours par jour");
        chartJour.setPrefWidth(300);
        chartJour.setPrefHeight(280);
        chartJour.setLegendVisible(false);
        chartJour.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        XYChart.Series<String, Number> seriesJour = new XYChart.Series<>();
        String[] jours      = {"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
        String[] joursLabel = {"Lun","Mar","Mer","Jeu","Ven","Sam"};
        for (int i = 0; i < jours.length; i++) {
            final String j = jours[i];
            long count = coursList.stream()
                    .filter(c -> c.getCreneau() != null &&
                            c.getCreneau().getJour().name().equals(j))
                    .count();
            seriesJour.getData().add(new XYChart.Data<>(joursLabel[i], count));
        }
        chartJour.getData().add(seriesJour);

        graphiques.getChildren().addAll(barChart, pieChart, chartJour);

        panel.getChildren().addAll(titre, sousTitre, cartes, graphiques);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + GRIS_CLAIR + ";");
        contentArea.getChildren().setAll(scroll);
    }

    private VBox createCarte(String icone, String label, String valeur, String couleur) {
        VBox carte = new VBox(8);
        carte.setPadding(new Insets(20));
        carte.setPrefWidth(200);
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

        HBox actions = new HBox(10);
        Button btnAjouter   = createBouton("+ Ajouter", BLEU_MID);
        Button btnSupprimer = createBouton("🗑 Supprimer", "#C0392B");
        actions.getChildren().addAll(btnAjouter, btnSupprimer);

        TableView<Cours> table = createTableCours();
        table.setPrefHeight(450);

        CoursDAO dao = new CoursDAO();
        ObservableList<Cours> data = FXCollections.observableArrayList(
                dao.getTousLesCours());
        table.setItems(data);

        btnAjouter.setOnAction(e -> showFormulaireAjoutCours(data));
        btnSupprimer.setOnAction(e -> {
            Cours selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne un cours à supprimer !");
                return;
            }
            if (dao.supprimer(selected.getId())) {
                data.setAll(dao.getTousLesCours());
            }
        });

        panel.getChildren().addAll(titre, actions, table);
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

    // ── Formulaire ajout cours ──
    private void showFormulaireAjoutCours(ObservableList<Cours> data) {
        Stage popup = new Stage();
        popup.setTitle("Ajouter un cours");

        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setPrefWidth(350);
        form.setStyle("-fx-background-color: white;");

        Label titre = new Label("📚 Nouveau Cours");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TextField tfMatiere  = createField("Matière (ex: POO Java)");
        TextField tfGroupe   = createField("Groupe (ex: G1)");

        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("CM", "TD", "TP", "EXAMEN");
        cbType.setPromptText("Type de cours");
        cbType.setPrefWidth(Double.MAX_VALUE);

        TextField tfEnseignant = createField("ID Enseignant (ex: 3)");
        TextField tfSalle      = createField("ID Salle (ex: 1)");

        ComboBox<String> cbJour = new ComboBox<>();
        cbJour.getItems().addAll("LUNDI", "MARDI", "MERCREDI",
                "JEUDI", "VENDREDI", "SAMEDI");
        cbJour.setPromptText("Jour");
        cbJour.setPrefWidth(Double.MAX_VALUE);

        TextField tfHeureDebut = createField("Heure début (ex: 08:00:00)");
        TextField tfHeureFin   = createField("Heure fin (ex: 10:00:00)");

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("💾 Enregistrer", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfMatiere.getText().isEmpty() || tfGroupe.getText().isEmpty()
                    || cbType.getValue() == null || tfEnseignant.getText().isEmpty()
                    || tfSalle.getText().isEmpty() || cbJour.getValue() == null
                    || tfHeureDebut.getText().isEmpty() || tfHeureFin.getText().isEmpty()) {
                lblMsg.setText("⚠️ Remplis tous les champs !");
                return;
            }
            try {
                String sqlCreneau = "INSERT INTO creneau (jour, heure_debut, heure_fin) VALUES (?, ?, ?)";
                java.sql.Connection conn = database.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(
                        sqlCreneau, java.sql.Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, cbJour.getValue());
                ps.setString(2, tfHeureDebut.getText());
                ps.setString(3, tfHeureFin.getText());
                ps.executeUpdate();

                java.sql.ResultSet rs = ps.getGeneratedKeys();
                int creneauId = 0;
                if (rs.next()) creneauId = rs.getInt(1);

                dao.ConflitDAO conflitDAO = new dao.ConflitDAO();
                if (conflitDAO.salleDejaOccupee(
                        Integer.parseInt(tfSalle.getText()), creneauId)) {
                    lblMsg.setText("❌ CONFLIT : salle déjà occupée !");
                    return;
                }
                if (conflitDAO.enseignantDejaOccupe(
                        Integer.parseInt(tfEnseignant.getText()), creneauId)) {
                    lblMsg.setText("❌ CONFLIT : enseignant déjà occupé !");
                    return;
                }

                Cours c = new Cours();
                c.setMatiere(tfMatiere.getText());
                c.setGroupe(tfGroupe.getText());
                c.setType(cbType.getValue());
                c.setEnseignantId(Integer.parseInt(tfEnseignant.getText()));
                c.setSalleId(Integer.parseInt(tfSalle.getText()));
                c.setCreneauId(creneauId);
                c.setEmploiDuTempsId(1);

                CoursDAO coursDAO = new CoursDAO();
                if (coursDAO.ajouter(c)) {
                    data.setAll(coursDAO.getTousLesCours());
                    popup.close();
                } else {
                    lblMsg.setText("❌ Erreur lors de l'ajout !");
                }
            } catch (NumberFormatException ex) {
                lblMsg.setText("⚠️ Les IDs doivent être des nombres !");
            } catch (java.sql.SQLException ex) {
                lblMsg.setText("❌ Erreur SQL : " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
                titre,
                new Label("Matière :"),       tfMatiere,
                new Label("Groupe :"),        tfGroupe,
                new Label("Type :"),          cbType,
                new Label("ID Enseignant :"), tfEnseignant,
                new Label("ID Salle :"),      tfSalle,
                new Label("Jour :"),          cbJour,
                new Label("Heure début :"),   tfHeureDebut,
                new Label("Heure fin :"),     tfHeureFin,
                lblMsg, btnSave
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        popup.setScene(new Scene(scroll, 370, 550));
        popup.show();
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

    // ── Vue Conflits ──
    private void showConflits() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        Label titre = new Label("⚠️ Gestion des Conflits");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        Label sousTitre = new Label("Conflits détectés automatiquement lors de la planification");
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setTextFill(Color.GRAY);

        CoursDAO coursDAO = new CoursDAO();
        List<Cours> coursList = coursDAO.getTousLesCours();

        VBox listeConflits = new VBox(10);
        boolean conflitTrouve = false;

        for (int i = 0; i < coursList.size(); i++) {
            for (int j = i + 1; j < coursList.size(); j++) {
                Cours c1 = coursList.get(i);
                Cours c2 = coursList.get(j);

                if (c1.getCreneauId() == c2.getCreneauId()
                        && c1.getSalleId() == c2.getSalleId()) {
                    listeConflits.getChildren().add(createConflitBox(
                            "🏫 SALLE OCCUPÉE",
                            "Salle " + c1.getNumeroSalle(),
                            c1.getMatiere() + " (" + c1.getGroupe() + ")",
                            c2.getMatiere() + " (" + c2.getGroupe() + ")",
                            "#E74C3C"
                    ));
                    conflitTrouve = true;
                }

                if (c1.getCreneauId() == c2.getCreneauId()
                        && c1.getEnseignantId() == c2.getEnseignantId()) {
                    listeConflits.getChildren().add(createConflitBox(
                            "👨‍🏫 ENSEIGNANT INDISPONIBLE",
                            c1.getNomEnseignant(),
                            c1.getMatiere() + " (" + c1.getGroupe() + ")",
                            c2.getMatiere() + " (" + c2.getGroupe() + ")",
                            "#E67E22"
                    ));
                    conflitTrouve = true;
                }
            }
        }

        if (!conflitTrouve) {
            VBox ok = new VBox(10);
            ok.setAlignment(Pos.CENTER);
            ok.setPadding(new Insets(60));
            ok.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
            Label lblOk = new Label("✅ Aucun conflit détecté !");
            lblOk.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            lblOk.setTextFill(Color.web(VERT));
            Label lblSous = new Label("Tous les cours sont correctement planifiés.");
            lblSous.setTextFill(Color.GRAY);
            ok.getChildren().addAll(lblOk, lblSous);
            listeConflits.getChildren().add(ok);
        }

        ScrollPane scroll = new ScrollPane(listeConflits);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setPrefHeight(500);

        panel.getChildren().addAll(titre, sousTitre, scroll);
        contentArea.getChildren().setAll(panel);
    }

    private HBox createConflitBox(String typeConflit, String ressource,
                                  String cours1, String cours2, String couleur) {
        HBox box = new HBox(15);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: " + couleur + ";" +
                        "-fx-border-width: 0 0 0 5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );

        Label icone = new Label("⚠️");
        icone.setFont(Font.font("Arial", 28));

        VBox details = new VBox(5);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label lblType = new Label(typeConflit);
        lblType.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lblType.setTextFill(Color.web(couleur));

        Label lblRessource = new Label("Ressource : " + ressource);
        lblRessource.setFont(Font.font("Arial", 12));
        lblRessource.setTextFill(Color.web(BLEU_FONCE));

        Label lblCours1 = new Label("• " + cours1);
        lblCours1.setFont(Font.font("Arial", 12));
        lblCours1.setTextFill(Color.DARKGRAY);

        Label lblCours2 = new Label("• " + cours2);
        lblCours2.setFont(Font.font("Arial", 12));
        lblCours2.setTextFill(Color.DARKGRAY);

        details.getChildren().addAll(lblType, lblRessource, lblCours1, lblCours2);
        box.getChildren().addAll(icone, details);
        return box;
    }

    // ── Vue Rechercher Salle ──
    private void showRechercherSalle() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        Label titre = new Label("🔍 Rechercher une Salle Disponible");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        Label sousTitre = new Label("Trouvez une salle libre selon vos critères");
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setTextFill(Color.GRAY);

        HBox formulaire = new HBox(15);
        formulaire.setPadding(new Insets(20));
        formulaire.setAlignment(Pos.CENTER_LEFT);
        formulaire.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );

        VBox boxJour = new VBox(5);
        Label lblJour = new Label("Jour :");
        lblJour.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ComboBox<String> cbJour = new ComboBox<>();
        cbJour.getItems().addAll("LUNDI","MARDI","MERCREDI","JEUDI","VENDREDI","SAMEDI");
        cbJour.setPromptText("Choisir un jour");
        cbJour.setPrefWidth(150);
        boxJour.getChildren().addAll(lblJour, cbJour);

        VBox boxHeure = new VBox(5);
        Label lblHeure = new Label("Heure début :");
        lblHeure.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ComboBox<String> cbHeure = new ComboBox<>();
        cbHeure.getItems().addAll("08:00:00","10:00:00","12:00:00","14:00:00","16:00:00");
        cbHeure.setPromptText("Choisir une heure");
        cbHeure.setPrefWidth(150);
        boxHeure.getChildren().addAll(lblHeure, cbHeure);

        VBox boxCapacite = new VBox(5);
        Label lblCapacite = new Label("Capacité minimale :");
        lblCapacite.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField tfCapacite = new TextField();
        tfCapacite.setPromptText("ex: 30");
        tfCapacite.setPrefWidth(120);
        boxCapacite.getChildren().addAll(lblCapacite, tfCapacite);

        VBox boxType = new VBox(5);
        Label lblType = new Label("Type de salle :");
        lblType.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("Tous","TD","TP","AMPHI","REUNION");
        cbType.setValue("Tous");
        cbType.setPrefWidth(130);
        boxType.getChildren().addAll(lblType, cbType);

        VBox boxBtn = new VBox(5);
        Label lblVide = new Label(" ");
        Button btnRechercher = createBouton("🔍 Rechercher", BLEU_MID);
        boxBtn.getChildren().addAll(lblVide, btnRechercher);

        formulaire.getChildren().addAll(boxJour, boxHeure, boxCapacite, boxType, boxBtn);

        Label titreResultats = new Label("Résultats :");
        titreResultats.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        titreResultats.setTextFill(Color.web(BLEU_FONCE));

        VBox resultats = new VBox(10);

        btnRechercher.setOnAction(e -> {
            resultats.getChildren().clear();

            if (cbJour.getValue() == null || cbHeure.getValue() == null) {
                Label lblErr = new Label("⚠️ Sélectionne un jour et une heure !");
                lblErr.setTextFill(Color.RED);
                resultats.getChildren().add(lblErr);
                return;
            }

            String jour  = cbJour.getValue();
            String heure = cbHeure.getValue();
            int capaciteMin = tfCapacite.getText().isEmpty() ? 0
                    : Integer.parseInt(tfCapacite.getText());
            String type = cbType.getValue().equals("Tous") ? null : cbType.getValue();

            CoursDAO coursDAO = new CoursDAO();
            List<Cours> coursList = coursDAO.getTousLesCours();

            java.util.Set<Integer> sallesOccupees = new java.util.HashSet<>();
            for (Cours c : coursList) {
                if (c.getCreneau() != null) {
                    String jourCours  = c.getCreneau().getJour().name();
                    String heureCours = c.getCreneau().getHeureDebut().toString();
                    if (jourCours.equals(jour) && (heureCours + ":00").equals(heure)) {
                        sallesOccupees.add(c.getSalleId());
                    }
                }
            }

            SalleDAO salleDAO = new SalleDAO();
            List<Salle> toutes = salleDAO.getToutesLesSalles();
            List<Salle> disponibles = new java.util.ArrayList<>();

            for (Salle s : toutes) {
                if (sallesOccupees.contains(s.getId())) continue;
                if (s.getCapacite() < capaciteMin) continue;
                if (type != null && !s.getType().equals(type)) continue;
                disponibles.add(s);
            }

            if (disponibles.isEmpty()) {
                HBox aucune = new HBox();
                aucune.setPadding(new Insets(20));
                aucune.setAlignment(Pos.CENTER);
                aucune.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
                Label lblAucune = new Label("❌ Aucune salle disponible pour ce créneau.");
                lblAucune.setTextFill(Color.RED);
                lblAucune.setFont(Font.font("Arial", 14));
                aucune.getChildren().add(lblAucune);
                resultats.getChildren().add(aucune);
            } else {
                for (Salle s : disponibles) {
                    HBox card = new HBox(15);
                    card.setPadding(new Insets(15));
                    card.setAlignment(Pos.CENTER_LEFT);
                    card.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-border-color: " + VERT + ";" +
                                    "-fx-border-width: 0 0 0 5;" +
                                    "-fx-border-radius: 8;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
                    );
                    Label ico = new Label("🏫");
                    ico.setFont(Font.font("Arial", 28));
                    VBox details = new VBox(4);
                    HBox.setHgrow(details, Priority.ALWAYS);
                    Label lblNom = new Label("Salle " + s.getNumero());
                    lblNom.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    lblNom.setTextFill(Color.web(BLEU_FONCE));
                    Label lblInfo = new Label(
                            "Type : " + s.getType() + "   |   Capacité : " + s.getCapacite() + " places");
                    lblInfo.setFont(Font.font("Arial", 12));
                    lblInfo.setTextFill(Color.GRAY);
                    Label lblDispo = new Label("✅ Disponible le " + jour +
                            " à " + heure.substring(0, 5));
                    lblDispo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    lblDispo.setTextFill(Color.web(VERT));
                    details.getChildren().addAll(lblNom, lblInfo, lblDispo);
                    card.getChildren().addAll(ico, details);
                    resultats.getChildren().add(card);
                }
            }
        });

        ScrollPane scroll = new ScrollPane(resultats);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setPrefHeight(400);

        panel.getChildren().addAll(titre, sousTitre, formulaire, titreResultats, scroll);
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
    private void showUtilisateurs() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        Label titre = new Label("👥 Gestion des Utilisateurs");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        // Barre d'actions
        HBox actions = new HBox(10);
        Button btnAjouter    = createBouton("+ Ajouter", BLEU_MID);
        Button btnSupprimer  = createBouton("🗑 Supprimer", "#C0392B");
        Button btnDesactiver = createBouton("🔒 Désactiver", ORANGE);
        Button btnActiver    = createBouton("✅ Activer", VERT);
        actions.getChildren().addAll(btnAjouter, btnSupprimer, btnDesactiver, btnActiver);

        // Tableau
        TableView<model.Utilisateur> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );
        table.setPrefHeight(450);

        TableColumn<model.Utilisateur, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<model.Utilisateur, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(120);

        TableColumn<model.Utilisateur, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(120);

        TableColumn<model.Utilisateur, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        TableColumn<model.Utilisateur, String> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getRole() != null ? data.getValue().getRole().getNom() : ""
                )
        );
        colRole.setPrefWidth(130);

        // Colonne statut avec voyant sobre
        TableColumn<model.Utilisateur, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().isActif() ? "Actif" : "Bloqué"
                )
        );
        colStatut.setCellFactory(col -> new TableCell<model.Utilisateur, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else if (item.equals("Actif")) {
                    setText("● Actif");
                    setStyle("-fx-text-fill: #1E8449; -fx-font-weight: bold;");
                } else {
                    setText("● Bloqué");
                    setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
                }
            }
        });
        colStatut.setPrefWidth(100);

        table.getColumns().addAll(colId, colNom, colPrenom, colEmail, colRole, colStatut);

        dao.UtilisateurDAO dao = new dao.UtilisateurDAO();
        ObservableList<model.Utilisateur> data =
                FXCollections.observableArrayList(dao.getTousLesUtilisateurs());
        table.setItems(data);

        // Actions
        btnAjouter.setOnAction(e -> showFormulaireAjoutUtilisateur(data));

        btnSupprimer.setOnAction(e -> {
            model.Utilisateur selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne un utilisateur !");
                return;
            }
            if (selected.getId() == SessionManager.getInstance().getUtilisateur().getId()) {
                showAlert("⚠️ Tu ne peux pas supprimer ton propre compte !");
                return;
            }
            if (dao.supprimer(selected.getId())) {
                data.setAll(dao.getTousLesUtilisateurs());
            }
        });

        btnDesactiver.setOnAction(e -> {
            model.Utilisateur selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne un utilisateur !");
                return;
            }
            if (selected.getId() == SessionManager.getInstance().getUtilisateur().getId()) {
                showAlert("⚠️ Tu ne peux pas bloquer ton propre compte !");
                return;
            }
            if (dao.toggleActif(selected.getId(), false)) {
                data.setAll(dao.getTousLesUtilisateurs());
            }
        });

        btnActiver.setOnAction(e -> {
            model.Utilisateur selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne un utilisateur !");
                return;
            }
            if (dao.toggleActif(selected.getId(), true)) {
                data.setAll(dao.getTousLesUtilisateurs());
            }
        });

        panel.getChildren().addAll(titre, actions, table);
        contentArea.getChildren().setAll(panel);
    }

    private void showFormulaireAjoutUtilisateur(
            ObservableList<model.Utilisateur> data) {
        Stage popup = new Stage();
        popup.setTitle("Ajouter un utilisateur");

        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setPrefWidth(350);
        form.setStyle("-fx-background-color: white;");

        Label titre = new Label("👤 Nouvel Utilisateur");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TextField tfNom    = createField("Nom");
        TextField tfPrenom = createField("Prénom");
        TextField tfEmail  = createField("Email");
        TextField tfMdp    = createField("Mot de passe");

        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("ADMINISTRATEUR", "GESTIONNAIRE", "ENSEIGNANT", "ETUDIANT");
        cbRole.setPromptText("Choisir un rôle");
        cbRole.setPrefWidth(Double.MAX_VALUE);

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("💾 Enregistrer", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfNom.getText().isEmpty() || tfPrenom.getText().isEmpty()
                    || tfEmail.getText().isEmpty() || tfMdp.getText().isEmpty()
                    || cbRole.getValue() == null) {
                lblMsg.setText("⚠️ Remplis tous les champs !");
                return;
            }

            // Trouver l'ID du rôle
            dao.RoleDAO roleDAO = new dao.RoleDAO();
            model.Role role = null;
            for (model.Role r : roleDAO.getTousLesRoles()) {
                if (r.getNom().equals(cbRole.getValue())) {
                    role = r;
                    break;
                }
            }

            if (role == null) {
                lblMsg.setText("❌ Rôle introuvable !");
                return;
            }

            model.Utilisateur u = new model.Utilisateur();
            u.setNom(tfNom.getText());
            u.setPrenom(tfPrenom.getText());
            u.setEmail(tfEmail.getText());
            u.setMotDePasse(tfMdp.getText());
            u.setRole(role);

            dao.UtilisateurDAO utilisateurDAO = new dao.UtilisateurDAO();
            if (utilisateurDAO.ajouter(u)) {
                data.setAll(utilisateurDAO.getTousLesUtilisateurs());
                popup.close();
            } else {
                lblMsg.setText("❌ Erreur lors de l'ajout !");
            }
        });

        form.getChildren().addAll(
                titre,
                new Label("Nom :"),       tfNom,
                new Label("Prénom :"),    tfPrenom,
                new Label("Email :"),     tfEmail,
                new Label("Mot de passe :"), tfMdp,
                new Label("Rôle :"),      cbRole,
                lblMsg, btnSave
        );

        popup.setScene(new Scene(form));
        popup.show();
    }

    public static void main(String[] args) {
        Application.launch(MainView.class, args);
    }
}