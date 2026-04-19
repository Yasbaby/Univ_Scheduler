package view;

import dao.SalleDAO;
import dao.CoursDAO;
import dao.BatimentDAO;
import dao.NotificationDAO;
import model.Notification;
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
        String roleActuel = session.getRole();
        switch (roleActuel) {
            case "ADMINISTRATEUR":
                showDashboard();
                break;
            case "GESTIONNAIRE":
                showCours();
                break;
            case "ENSEIGNANT":
                showEmploiDuTempsEnseignant();
                break;
            case "ETUDIANT":
                showEmploiDuTempsEtudiant();

                break;
            default:
                contentArea.getChildren().setAll(new EmploiDuTempsView().getView());
                break;
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

        Label icone = new Label("\uD83C\uDF93");
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
                session.getUtilisateurConnecte().getNomComplet() + " - " + session.getRole() : "";
        Label lblUser = new Label("\uD83D\uDC64 " + nomUser);
        lblUser.setFont(Font.font("Arial", 11));
        lblUser.setTextFill(Color.web("#7FB3D3"));

        header.getChildren().addAll(icone, titre, sep, sous, spacer, lblUser);
        return header;
    }

    // ── Menu gauche ──
    private VBox createMenu() {
        VBox menu = new VBox(0);
        menu.setStyle("-fx-background-color: " + BLEU_FONCE + ";");
        menu.setPadding(new Insets(0));
        menu.setPrefWidth(200);

        // ── Logo / Titre ──
        VBox logoBox = new VBox(4);
        logoBox.setPadding(new Insets(20, 15, 20, 15));
        logoBox.setStyle("-fx-background-color: #0F2840;");
        Label logoIcone = new Label("🎓");
        logoIcone.setFont(Font.font("Arial", 28));
        Label logoTitre = new Label("UNIV-SCHEDULER");
        logoTitre.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        logoTitre.setTextFill(Color.WHITE);
        Label logoSous = new Label("Gestion universitaire");
        logoSous.setFont(Font.font("Arial", 10));
        logoSous.setTextFill(Color.web("#7FB3D3"));
        logoBox.getChildren().addAll( logoSous);

        // ── Profil utilisateur ──
        SessionManager session = SessionManager.getInstance();
        String nomUser  = session.estConnecte()
                ? session.getUtilisateurConnecte().getNomComplet() : "Inconnu";
        String roleUser = session.getRole();

        String avatarIcon = switch (roleUser) {
            case "ADMINISTRATEUR" -> "⚙️";
            case "GESTIONNAIRE"   -> "📋";
            case "ENSEIGNANT"     -> "👨‍🏫";
            case "ETUDIANT"       -> "🎓";
            default               -> "👤";
        };
        String roleCouleur = switch (roleUser) {
            case "ADMINISTRATEUR" -> "#E74C3C";
            case "GESTIONNAIRE"   -> "#2471A3";
            case "ENSEIGNANT"     -> "#1E8449";
            case "ETUDIANT"       -> "#D35400";
            default               -> "#7F8C8D";
        };

        VBox profilBox = new VBox(5);
        profilBox.setPadding(new Insets(15, 15, 15, 15));
        profilBox.setStyle(
                "-fx-background-color: #162E44;" +
                        "-fx-border-color: #2E6DA4;" +
                        "-fx-border-width: 1 0 1 0;"
        );

        HBox profilHBox = new HBox(10);
        profilHBox.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(avatarIcon);
        avatar.setFont(Font.font("Arial", 22));

        VBox profilInfo = new VBox(2);
        Label lblNomUser = new Label(nomUser);
        lblNomUser.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblNomUser.setTextFill(Color.WHITE);
        lblNomUser.setWrapText(true);
        lblNomUser.setMaxWidth(130);

        Label lblRoleBadge = new Label(" " + roleUser + " ");
        lblRoleBadge.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        lblRoleBadge.setTextFill(Color.WHITE);
        lblRoleBadge.setStyle(
                "-fx-background-color: " + roleCouleur + ";" +
                        "-fx-background-radius: 3;" +
                        "-fx-padding: 2 6 2 6;"
        );

        profilInfo.getChildren().addAll(lblNomUser, lblRoleBadge);
        profilHBox.getChildren().addAll(avatar, profilInfo);
        profilBox.getChildren().add(profilHBox);

        // ── Contenu scrollable du menu ──
        VBox menuContenu = new VBox(2);
        menuContenu.setPadding(new Insets(10, 8, 10, 8));

        // Notifications badge
        dao.NotificationDAO notifDAO = new dao.NotificationDAO();
        int nbNotifs = session.estConnecte()
                ? notifDAO.getNombreNonLues(session.getUtilisateurConnecte().getId()) : 0;

        // ── Création des boutons ──
        Button btnDash        = createMenuBtn("🏠", "Tableau de bord",  false);
        Button btnCours       = createMenuBtn("📚", "Cours",             false);
        Button btnSalles      = createMenuBtn("🏫", "Salles",            false);
        Button btnBatiments   = createMenuBtn("🏢", "Bâtiments",         false);
        Button btnUsers       = createMenuBtn("👥", "Utilisateurs",      false);
        Button btnEquipements = createMenuBtn("🔧", "Équipements",       false);
        Button btnEmploi      = createMenuBtn("🗓️", "Emploi du temps",   false);
        Button btnReservations= createMenuBtn("📋", "Réservations",      false);
        Button btnGenererEdt  = createMenuBtn("⚡", "Générer EDT",        false);
        Button btnConflits    = createMenuBtn("⚠️", "Conflits",           false);
        Button btnRecherche   = createMenuBtn("🔍", "Rechercher salle",  false);
        Button btnNotifs      = createMenuBtn("🔔",
                "Notifications" + (nbNotifs > 0 ? "  ●" + nbNotifs : ""), nbNotifs > 0);

        // ── Actions ──
        btnDash.setOnAction(e        -> { setActif(btnDash);         showDashboard(); });
        btnCours.setOnAction(e       -> { setActif(btnCours);        showCours(); });
        btnSalles.setOnAction(e      -> { setActif(btnSalles);       showSalles(); });
        btnBatiments.setOnAction(e   -> { setActif(btnBatiments);    showBatiments(); });
        btnUsers.setOnAction(e       -> { setActif(btnUsers);        showUtilisateurs(); });
        btnEquipements.setOnAction(e -> { setActif(btnEquipements);  showEquipements(); });
        btnReservations.setOnAction(e-> { setActif(btnReservations); showReservations(); });
        btnGenererEdt.setOnAction(e  -> { setActif(btnGenererEdt);   showGenererEmploiDuTemps(); });
        btnConflits.setOnAction(e    -> { setActif(btnConflits);     showConflits(); });
        btnRecherche.setOnAction(e   -> { setActif(btnRecherche);    showRechercherSalle(); });
        btnNotifs.setOnAction(e      -> { setActif(btnNotifs);       showNotifications(); });
        btnEmploi.setOnAction(e      -> {
            setActif(btnEmploi);
            switch (session.getRole()) {
                case "ENSEIGNANT": showEmploiDuTempsEnseignant(); break;
                case "ETUDIANT":   showEmploiDuTempsEtudiant();   break;
                default: contentArea.getChildren().setAll(new EmploiDuTempsView().getView());
            }
        });

        // ── Visibilité selon le rôle ──
        // Tout cacher par défaut
        for (Button b : new Button[]{btnDash, btnCours, btnSalles, btnBatiments,
                btnUsers, btnEquipements, btnEmploi, btnReservations,
                btnGenererEdt, btnConflits, btnRecherche, btnNotifs}) {
            b.setVisible(false);
            b.setManaged(false);
        }

        // Sections selon le rôle
        switch (roleUser) {

            case "ADMINISTRATEUR":
                ajouterSectionMenu(menuContenu, "ADMINISTRATION");
                afficher(btnDash, btnUsers, btnSalles, btnBatiments);
                menuContenu.getChildren().addAll(
                        btnDash, btnUsers, btnSalles, btnBatiments);
                break;

            case "GESTIONNAIRE":
                ajouterSectionMenu(menuContenu, "PLANIFICATION");
                afficher(btnCours, btnGenererEdt, btnReservations, btnEquipements);
                menuContenu.getChildren().addAll(btnCours, btnGenererEdt, btnReservations, btnEquipements);
                ajouterSectionMenu(menuContenu, "OUTILS");
                afficher(btnNotifs);
                menuContenu.getChildren().add(btnNotifs);
                break;

            case "ENSEIGNANT":
                ajouterSectionMenu(menuContenu, "MON ESPACE");
                afficher(btnEmploi, btnRecherche);
                menuContenu.getChildren().addAll(btnEmploi, btnRecherche);
                ajouterSectionMenu(menuContenu, "OUTILS");
                afficher(btnNotifs);
                menuContenu.getChildren().add(btnNotifs);
                break;

            case "ETUDIANT":
                ajouterSectionMenu(menuContenu, "MON ESPACE");
                afficher(btnEmploi, btnRecherche);
                menuContenu.getChildren().addAll(btnEmploi, btnRecherche);
                ajouterSectionMenu(menuContenu, "OUTILS");
                afficher(btnNotifs);
                menuContenu.getChildren().add(btnNotifs);
                break;
        }

        // ── Séparateur + Déconnexion ──
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2E6DA4; -fx-opacity: 0.5;");
        sep.setPadding(new Insets(5, 0, 5, 0));

        // Infos université
        VBox infoBox = new VBox(6);
        infoBox.setPadding(new Insets(10, 15, 10, 15));
        infoBox.setStyle("-fx-background-color: #162E44;");
        String[] infos = {
                "📍 Univ. Iba Der Thiam",
                "🎓 Licence 2 Informatique",
                "📅 Semestre 4 — 2026"
        };
        for (String info : infos) {
            Label lbl = new Label(info);
            lbl.setFont(Font.font("Arial", 10));
            lbl.setTextFill(Color.web("#7FB3D3"));
            infoBox.getChildren().add(lbl);
        }

        Button btnDeconnexion = new Button("🚪  Déconnexion");
        btnDeconnexion.setMaxWidth(Double.MAX_VALUE);
        btnDeconnexion.setStyle(
                "-fx-background-color: #C0392B;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 0;" +
                        "-fx-font-size: 12;" +
                        "-fx-font-weight: bold;"
        );
        btnDeconnexion.setOnMouseEntered(e ->
                btnDeconnexion.setStyle(btnDeconnexion.getStyle()
                        .replace("#C0392B", "#E74C3C")));
        btnDeconnexion.setOnMouseExited(e ->
                btnDeconnexion.setStyle(btnDeconnexion.getStyle()
                        .replace("#E74C3C", "#C0392B")));
        btnDeconnexion.setOnAction(e -> {
            SessionManager.getInstance().deconnecter();
            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            stage.close();
            try { new LoginView().start(new Stage()); }
            catch (Exception ex) { ex.printStackTrace(); }
        });

        ScrollPane scrollMenu = new ScrollPane(menuContenu);
        scrollMenu.setFitToWidth(true);
        scrollMenu.setStyle(
                "-fx-background-color: " + BLEU_FONCE + ";" +
                        "-fx-background: " + BLEU_FONCE + ";"
        );
        VBox.setVgrow(scrollMenu, Priority.ALWAYS);

        // ── Activer le bouton par défaut ──
        switch (roleUser) {
            case "ADMINISTRATEUR": setActif(btnDash);   break;
            case "GESTIONNAIRE":   setActif(btnCours);  break;
            case "ENSEIGNANT":
            case "ETUDIANT":       setActif(btnEmploi); break;
        }

        menu.getChildren().addAll(logoBox, profilBox, scrollMenu, infoBox, btnDeconnexion);
        return menu;
    }

    // ── Helpers menu ──
    private Button createMenuBtn(String icone, String texte, boolean badge) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label lblIcone = new Label(icone);
        lblIcone.setFont(Font.font("Arial", 14));
        lblIcone.setMinWidth(20);

        Label lblTexte = new Label(texte);
        lblTexte.setFont(Font.font("Arial", 12));

        content.getChildren().addAll(lblIcone, lblTexte);
        btn.setGraphic(content);
        btn.setStyle(styleMenuNormal());

        btn.setOnMouseEntered(e -> {
            if (btn != btnActif) btn.setStyle(styleMenuHover());
        });
        btn.setOnMouseExited(e -> {
            if (btn != btnActif) btn.setStyle(styleMenuNormal());
        });

        return btn;
    }

    private void ajouterSectionMenu(VBox parent, String titre) {
        Label lbl = new Label(titre);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web("#5D8AA8"));
        lbl.setPadding(new Insets(12, 5, 4, 8));
        parent.getChildren().add(lbl);
    }

    private void afficher(Button... boutons) {
        for (Button b : boutons) {
            b.setVisible(true);
            b.setManaged(true);
        }
    }

    private String styleMenuNormal() {
        return  "-fx-background-color: transparent;" +
                "-fx-text-fill: #AED6F1;" +
                "-fx-padding: 9 12;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 6;";
    }

    private String styleMenuHover() {
        return  "-fx-background-color: #1F4F72;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 9 12;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 6;";
    }

    private String styleMenuActif() {
        return  "-fx-background-color: #2471A3;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 9 12;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 6;";
    }

    private void setActif(Button btn) {
        if (btnActif != null) btnActif.setStyle(styleMenuNormal());
        btn.setStyle(styleMenuActif());
        btnActif = btn;
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

        Label titre = new Label("\uD83C\uDFE0 Tableau de bord");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        Label sousTitre = new Label("Vue d\'ensemble du syst\u00E8me UNIV-SCHEDULER");
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
        barChart.setTitle("\uD83D\uDCCA Cours par salle");
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
        pieChart.setTitle("\uD83E\uDD67 Types de cours");
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
        chartJour.setTitle("\uD83D\uDCC5 Cours par jour");
        chartJour.setPrefWidth(300);
        chartJour.setPrefHeight(280);
        chartJour.setLegendVisible(false);
        chartJour.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        XYChart.Series<String, Number> seriesJour = new XYChart.Series<>();
        String[] jours = {"LUNDI","MARDI","MERCREDI","JEUDI","VENDREDI","SAMEDI"};
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
        int nbConflits = 0;
        for (int i = 0; i < coursList.size(); i++) {
            for (int j = i + 1; j < coursList.size(); j++) {
                Cours c1 = coursList.get(i);
                Cours c2 = coursList.get(j);
                if (c1.getCreneauId() == c2.getCreneauId() &&
                        (c1.getSalleId() == c2.getSalleId() ||
                                c1.getEnseignantId() == c2.getEnseignantId())) {
                    nbConflits++;
                }
            }
        }

        graphiques.getChildren().addAll(barChart, pieChart, chartJour);

        panel.getChildren().addAll(titre, sousTitre, cartes, graphiques);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + GRIS_CLAIR + ";");
        contentArea.getChildren().setAll(scroll);
    }

    private VBox createCarte(String icone, String label, String valeur, String couleur) {
        VBox carte = new VBox(0);
        carte.setPrefWidth(210);
        carte.setPrefHeight(120);
        carte.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 4);"
        );
        carte.setCursor(javafx.scene.Cursor.HAND);

        // ── Bande supérieure (label + icône) ──
        HBox bandeau = new HBox();
        bandeau.setPadding(new Insets(12, 15, 8, 15));
        bandeau.setAlignment(Pos.CENTER_LEFT);
        bandeau.setStyle(
                "-fx-background-color: rgba(0,0,0,0.12);" +
                        "-fx-background-radius: 12 12 0 0;"
        );

        Label lblCategorie = new Label(label.toUpperCase());
        lblCategorie.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        lblCategorie.setTextFill(Color.web("#D6EAF8"));

        Region spacerBandeau = new Region();
        HBox.setHgrow(spacerBandeau, Priority.ALWAYS);

        Label ico = new Label(icone);
        ico.setFont(Font.font("Arial", 18));
        ico.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 5 8 5 8;"
        );

        bandeau.getChildren().addAll(lblCategorie, spacerBandeau, ico);

        // ── Corps (valeur + tendance) ──
        HBox corps = new HBox(10);
        corps.setPadding(new Insets(10, 15, 12, 15));
        corps.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setVgrow(corps, Priority.ALWAYS);

        Label val = new Label(valeur);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        val.setTextFill(Color.WHITE);

        VBox tendance = new VBox(2);
        tendance.setAlignment(Pos.BOTTOM_LEFT);

        // Sous-label contextuel selon le type de carte
        String sousLabel = getSousLabel(label);
        Label lblSous = new Label(sousLabel);
        lblSous.setFont(Font.font("Arial", 10));
        lblSous.setTextFill(Color.web("#D6EAF8"));

        // Barre de progression visuelle
        HBox barre = new HBox(2);
        barre.setAlignment(Pos.CENTER_LEFT);
        int nbPoints = getNbPoints(label, valeur);
        for (int i = 0; i < 5; i++) {
            Label point = new Label("●");
            point.setFont(Font.font("Arial", 8));
            point.setTextFill(i < nbPoints
                    ? Color.WHITE
                    : Color.web("rgba(255,255,255,0.25)"));
            barre.getChildren().add(point);
        }

        tendance.getChildren().addAll(lblSous, barre);
        corps.getChildren().addAll(val, tendance);

        // ── Pied (ligne de statut) ──
        HBox pied = new HBox(5);
        pied.setPadding(new Insets(6, 15, 6, 15));
        pied.setAlignment(Pos.CENTER_LEFT);
        pied.setStyle(
                "-fx-background-color: rgba(0,0,0,0.15);" +
                        "-fx-background-radius: 0 0 12 12;"
        );

        Label pointStatut = new Label("●");
        pointStatut.setFont(Font.font("Arial", 8));
        pointStatut.setTextFill(Color.web("#A9DFBF"));

        Label lblStatut = new Label(getStatut(label, valeur));
        lblStatut.setFont(Font.font("Arial", 9));
        lblStatut.setTextFill(Color.web("#D6EAF8"));

        pied.getChildren().addAll(pointStatut, lblStatut);

        // ── Hover effect ──
        carte.setOnMouseEntered(e -> carte.setStyle(
                "-fx-background-color: derive(" + couleur + ", -10%);" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 14, 0, 0, 6);" +
                        "-fx-scale-x: 1.02;" +
                        "-fx-scale-y: 1.02;"
        ));
        carte.setOnMouseExited(e -> carte.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 4);"
        ));

        carte.getChildren().addAll(bandeau, corps, pied);
        return carte;
    }

    // ── Sous-label selon le type ──
    private String getSousLabel(String label) {
        switch (label.toLowerCase()) {
            case "salles":      return "capacité totale";
            case "cours":       return "planifiés";
            case "bâtiments":
            case "batiments":   return "sur le campus";
            case "conflits":    return "détectés";
            case "mes cours":   return "cette semaine";
            case "groupes":     return "distincts";
            case "salles utilisées":
            case "salles utilisees": return "différentes";
            default:            return "total";
        }
    }

    // ── Nb de points allumés (sur 5) ──
    private int getNbPoints(String label, String valeur) {
        try {
            int v = Integer.parseInt(valeur);
            if (v == 0) return 0;
            if (v <= 2)  return 1;
            if (v <= 5)  return 2;
            if (v <= 10) return 3;
            if (v <= 20) return 4;
            return 5;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ── Texte de statut dans le pied ──
    private String getStatut(String label, String valeur) {
        try {
            int v = Integer.parseInt(valeur);
            switch (label.toLowerCase()) {
                case "conflits":
                    return v == 0 ? "Aucun conflit détecté" : v + " à résoudre";
                case "salles":
                    return "Disponibles dans le système";
                case "cours":
                    return "Cours enregistrés";
                case "bâtiments":
                case "batiments":
                    return "Bâtiments actifs";
                default:
                    return v > 0 ? "Données à jour" : "Aucune donnée";
            }
        } catch (NumberFormatException e) {
            return "Données à jour";
        }
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
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(450);

        TableColumn<Salle, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Salle, String> colNumero = new TableColumn<>("Numéro");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colNumero.setPrefWidth(100);

        TableColumn<Salle, Integer> colCapacite = new TableColumn<>("Capacité");
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colCapacite.setPrefWidth(90);

        TableColumn<Salle, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<Salle, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                String couleur = switch (item) {
                    case "AMPHI"   -> "#2471A3";
                    case "TD"      -> "#1E8449";
                    case "TP"      -> "#D35400";
                    case "REUNION" -> "#7D3C98";
                    default        -> "#7F8C8D";
                };
                setText(item);
                setStyle("-fx-text-fill: " + couleur + "; -fx-font-weight: bold;");
            }
        });
        colType.setPrefWidth(90);

        // ✅ Colonne Bâtiment
        TableColumn<Salle, String> colBatiment = new TableColumn<>("Bâtiment");
        colBatiment.setCellValueFactory(data -> {
            BatimentDAO batDAO = new BatimentDAO();
            String nomBat = batDAO.getTousLesBatiments().stream()
                    .filter(b -> b.getId() == data.getValue().getBatimentId())
                    .map(model.Batiment::getNom)
                    .findFirst()
                    .orElse("—");
            return new javafx.beans.property.SimpleStringProperty(nomBat);
        });
        colBatiment.setPrefWidth(120);

        // ✅ Colonne Disponible avec visuel
        TableColumn<Salle, Boolean> colDispo = new TableColumn<>("Disponible");
        colDispo.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        colDispo.setCellFactory(col -> new TableCell<Salle, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                if (item) {
                    setText("✅ Disponible");
                    setStyle("-fx-text-fill: #1E8449; -fx-font-weight: bold;");
                } else {
                    setText("🔴 Occupée");
                    setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
                }
            }
        });
        colDispo.setPrefWidth(110);

        table.getColumns().addAll(colId, colNumero, colCapacite, colType, colBatiment, colDispo);

        SalleDAO dao = new SalleDAO();
        ObservableList<Salle> data = FXCollections.observableArrayList(dao.getToutesLesSalles());
        table.setItems(data);

        btnAjouter.setOnAction(e -> showFormulaireAjoutSalle(table, data));
        btnSupprimer.setOnAction(e -> {
            Salle selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("⚠️ Sélectionne une salle !"); return; }
            if (dao.supprimer(selected.getId())) data.setAll(dao.getToutesLesSalles());
        });

        panel.getChildren().addAll(titre, actions, table);
        contentArea.getChildren().setAll(panel);
    }
    // ── Formulaire ajout salle ──
    private void showFormulaireAjoutSalle(TableView<Salle> table,
                                          ObservableList<Salle> data) {
        Stage popup = new Stage();
        popup.setTitle("Ajouter une salle");

        VBox form = new VBox(10);
        form.setPadding(new Insets(25));
        form.setPrefWidth(340);
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

        BatimentDAO batDAO = new BatimentDAO();
        java.util.List<model.Batiment> batiments = batDAO.getTousLesBatiments();
        ComboBox<String> cbBatiment = new ComboBox<>();
        batiments.forEach(b -> cbBatiment.getItems().add(b.getId() + " — " + b.getNom()));
        cbBatiment.setPromptText("Choisir un bâtiment");
        cbBatiment.setPrefWidth(Double.MAX_VALUE);

        CheckBox chkDispo = new CheckBox("Disponible");
        chkDispo.setSelected(true);
        chkDispo.setFont(Font.font("Arial", 13));

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("💾 Enregistrer", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        // ✅ Action simple et correcte — uniquement pour les salles
        btnSave.setOnAction(e -> {
            if (tfNumero.getText().isEmpty() || tfCapacite.getText().isEmpty()
                    || cbType.getValue() == null || cbBatiment.getValue() == null) {
                lblMsg.setText("⚠️ Remplis tous les champs !");
                return;
            }
            try {
                int batimentId = Integer.parseInt(
                        cbBatiment.getValue().split(" — ")[0].trim()
                );

                Salle s = new Salle();
                s.setNumero(tfNumero.getText());
                s.setCapacite(Integer.parseInt(tfCapacite.getText()));
                s.setType(cbType.getValue());
                s.setBatimentId(batimentId);
                s.setDisponible(chkDispo.isSelected());

                SalleDAO salleDAO = new SalleDAO();
                if (salleDAO.ajouterSalle(s)) {
                    data.setAll(salleDAO.getToutesLesSalles());
                    popup.close();
                    showAlert("✅ Salle ajoutée avec succès !");
                } else {
                    lblMsg.setText("❌ Erreur lors de l'ajout !");
                }
            } catch (NumberFormatException ex) {
                lblMsg.setText("⚠️ La capacité doit être un nombre !");
            }
        });

        form.getChildren().addAll(
                titre,
                new Label("Numéro :"),   tfNumero,
                new Label("Capacité :"), tfCapacite,
                new Label("Type :"),     cbType,
                new Label("Bâtiment :"), cbBatiment,
                chkDispo,
                lblMsg, btnSave
        );

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
        Button btnModifier = createBouton("✏️ Modifier", ORANGE);
        actions.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer);




        TableView<Cours> table = createTableCours();
        table.setPrefHeight(450);

        CoursDAO dao = new CoursDAO();
        ObservableList<Cours> data = FXCollections.observableArrayList(
                dao.getTousLesCours());
        table.setItems(data);

        btnAjouter.setOnAction(e -> showFormulaireAjoutCours(data));
        btnModifier.setOnAction(e -> {
            Cours selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne un cours à modifier !");
                return;
            }
            showFormulaireModifierCours(selected, data);
        });
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

        TextField tfMatiere    = createField("Matière (ex: POO Java)");
        TextField tfGroupe     = createField("Groupe (ex: G1)");
        TextField tfEnseignant = createField("ID Enseignant (ex: 3)");
        TextField tfSalle      = createField("ID Salle (ex: 1)");
        TextField tfHeureDebut = createField("Heure début (ex: 08:00:00)");
        TextField tfHeureFin   = createField("Heure fin (ex: 10:00:00)");

        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("CM", "TD", "TP", "EXAMEN");
        cbType.setPromptText("Type de cours");
        cbType.setPrefWidth(Double.MAX_VALUE);

        ComboBox<String> cbJour = new ComboBox<>();
        cbJour.getItems().addAll("LUNDI","MARDI","MERCREDI","JEUDI","VENDREDI","SAMEDI");
        cbJour.setPromptText("Jour");
        cbJour.setPrefWidth(Double.MAX_VALUE);

        Label lblMsg = new Label("");
        lblMsg.setWrapText(true);

        Button btnSave = createBouton("💾 Enregistrer", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfMatiere.getText().isEmpty() || tfGroupe.getText().isEmpty()
                    || cbType.getValue() == null || tfEnseignant.getText().isEmpty()
                    || tfSalle.getText().isEmpty() || cbJour.getValue() == null
                    || tfHeureDebut.getText().isEmpty() || tfHeureFin.getText().isEmpty()) {
                lblMsg.setStyle("-fx-text-fill: #E74C3C;");
                lblMsg.setText("⚠️ Remplis tous les champs !");
                return;
            }
            try {
                java.sql.Connection conn = database.DatabaseConnection.getConnection();

                // ── 1. Créer le créneau ──
                java.sql.PreparedStatement psCreneau = conn.prepareStatement(
                        "INSERT INTO creneau (jour, heure_debut, heure_fin) VALUES (?, ?, ?)",
                        java.sql.Statement.RETURN_GENERATED_KEYS);
                psCreneau.setString(1, cbJour.getValue());
                psCreneau.setString(2, tfHeureDebut.getText());
                psCreneau.setString(3, tfHeureFin.getText());
                psCreneau.executeUpdate();

                java.sql.ResultSet rs = psCreneau.getGeneratedKeys();
                int creneauId = rs.next() ? rs.getInt(1) : 0;

                int salleId      = Integer.parseInt(tfSalle.getText());
                int enseignantId = Integer.parseInt(tfEnseignant.getText());

                dao.ConflitDAO conflitDAO = new dao.ConflitDAO();

                // ── 2. Vérifier conflit SALLE ──
                if (conflitDAO.salleDejaOccupee(salleId, creneauId)) {
                    String infoConflit = getInfoConflitSalle(conn, salleId, creneauId);
                    notifierConflitSalle(conn, salleId, tfMatiere.getText(),
                            tfGroupe.getText(), cbJour.getValue(),
                            tfHeureDebut.getText(), infoConflit);

                    lblMsg.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                    lblMsg.setText("❌ CONFLIT SALLE détecté !\n📬 Gestionnaire notifié.");

                    showConflitAlert(
                            "Conflit de Salle Détecté",
                            "🏫 Salle ID " + salleId + " déjà occupée",
                            cbJour.getValue() + " de " + tfHeureDebut.getText()
                                    + " à " + tfHeureFin.getText(),
                            infoConflit,
                            tfMatiere.getText() + " (" + tfGroupe.getText() + ")"
                    );
                    return;
                }

                // ── 3. Vérifier conflit ENSEIGNANT ──
                if (conflitDAO.enseignantDejaOccupe(enseignantId, creneauId)) {
                    lblMsg.setStyle("-fx-text-fill: #E67E22; -fx-font-weight: bold;");
                    lblMsg.setText("❌ CONFLIT : enseignant déjà occupé à ce créneau !");
                    return;
                }

                // ── 4. Aucun conflit → ajouter ──
                Cours c = new Cours();
                c.setMatiere(tfMatiere.getText());
                c.setGroupe(tfGroupe.getText());
                c.setType(cbType.getValue());
                c.setEnseignantId(enseignantId);
                c.setSalleId(salleId);
                c.setCreneauId(creneauId);
                c.setEmploiDuTempsId(1);

                CoursDAO coursDAO = new CoursDAO();
                if (coursDAO.ajouter(c)) {
                    data.setAll(coursDAO.getTousLesCours());
                    popup.close();
                    showAlert("✅ Cours ajouté avec succès !");
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

        HBox actions = new HBox(10);
        Button btnAjouter   = createBouton("+ Ajouter", BLEU_MID);
        Button btnSupprimer = createBouton("🗑 Supprimer", "#C0392B");
        Button btnCarte     = createBouton("🗺️ Carte interactive", VIOLET);
        actions.getChildren().addAll(btnAjouter, btnSupprimer, btnCarte);

        TableView<model.Batiment> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );
        table.setPrefHeight(400);

        // ── Colonnes ──
        TableColumn<model.Batiment, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<model.Batiment, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(160);

        TableColumn<model.Batiment, String> colLoc = new TableColumn<>("Localisation");
        colLoc.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colLoc.setPrefWidth(180);

        TableColumn<model.Batiment, Integer> colEtages = new TableColumn<>("Étages");
        colEtages.setCellValueFactory(new PropertyValueFactory<>("nombreEtages"));
        colEtages.setPrefWidth(70);

        // ✅ Colonne Salles avec compteur coloré
        TableColumn<model.Batiment, String> colSalles = new TableColumn<>("Salles");
        colSalles.setCellValueFactory(data -> {
            SalleDAO salleDAO = new SalleDAO();
            long nb = salleDAO.getToutesLesSalles().stream()
                    .filter(s -> s.getBatimentId() == data.getValue().getId())
                    .count();
            return new javafx.beans.property.SimpleStringProperty(nb + " salle(s)");
        });
        colSalles.setCellFactory(col -> new TableCell<model.Batiment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                int nb = Integer.parseInt(item.split(" ")[0]);
                setStyle(nb > 0
                        ? "-fx-text-fill: #1E8449; -fx-font-weight: bold;"
                        : "-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
            }
        });
        colSalles.setPrefWidth(90);

        // ✅ Colonne Capacité totale
        TableColumn<model.Batiment, String> colCapTotale = new TableColumn<>("Capacité totale");
        colCapTotale.setCellValueFactory(data -> {
            SalleDAO salleDAO = new SalleDAO();
            int total = salleDAO.getToutesLesSalles().stream()
                    .filter(s -> s.getBatimentId() == data.getValue().getId())
                    .mapToInt(Salle::getCapacite)
                    .sum();
            return new javafx.beans.property.SimpleStringProperty(
                    total > 0 ? total + " places" : "—");
        });
        colCapTotale.setPrefWidth(110);

        table.getColumns().addAll(colId, colNom, colLoc, colEtages, colSalles, colCapTotale);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        BatimentDAO dao = new BatimentDAO();
        ObservableList<model.Batiment> data =
                FXCollections.observableArrayList(dao.getTousLesBatiments());
        table.setItems(data);

        // ── Actions ──
        btnAjouter.setOnAction(e -> showFormulaireAjoutBatiment(data));

        btnSupprimer.setOnAction(e -> {
            model.Batiment selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne un bâtiment !");
                return;
            }
            if (dao.supprimer(selected.getId())) {
                data.setAll(dao.getTousLesBatiments());
            }
        });

        btnCarte.setOnAction(e -> showCarteBatiments());

        panel.getChildren().addAll(titre, actions, table);
        contentArea.getChildren().setAll(panel);
    }

    private void showFormulaireAjoutBatiment(ObservableList<model.Batiment> data) {
        Stage popup = new Stage();
        popup.setTitle("Ajouter un bâtiment");

        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setPrefWidth(320);
        form.setStyle("-fx-background-color: white;");

        Label titre = new Label("🏢 Nouveau Bâtiment");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TextField tfNom    = createField("Nom (ex: Bâtiment C)");
        TextField tfLoc    = createField("Localisation (ex: Campus Sud)");
        TextField tfEtages = createField("Nombre d'étages (ex: 3)");

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("💾 Enregistrer", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfNom.getText().isEmpty() || tfEtages.getText().isEmpty()) {
                lblMsg.setText("⚠️ Remplis au moins le nom et les étages !");
                return;
            }
            try {
                model.Batiment b = new model.Batiment();
                b.setNom(tfNom.getText());
                b.setLocalisation(tfLoc.getText());
                b.setNombreEtages(Integer.parseInt(tfEtages.getText()));

                BatimentDAO batDAO = new BatimentDAO();
                if (batDAO.ajouter(b)) {
                    data.setAll(batDAO.getTousLesBatiments());
                    popup.close();
                } else {
                    lblMsg.setText("❌ Erreur lors de l'ajout !");
                }
            } catch (NumberFormatException ex) {
                lblMsg.setText("⚠️ Le nombre d'étages doit être un nombre !");
            }
        });

        form.getChildren().addAll(
                titre,
                new Label("Nom :"),           tfNom,
                new Label("Localisation :"),  tfLoc,
                new Label("Nb étages :"),     tfEtages,
                lblMsg, btnSave
        );

        popup.setScene(new Scene(form));
        popup.show();
    }

    private void showCarteBatiments() {
        Stage popup = new Stage();
        popup.setTitle("🗺️ Carte des Bâtiments");
        popup.setWidth(860);
        popup.setHeight(640);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + GRIS_CLAIR + ";");

        Label titre = new Label("🗺️ Carte Interactive des Bâtiments");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titre.setTextFill(Color.web(BLEU_FONCE));

        // Récupère les données fraîches
        BatimentDAO batDAO = new BatimentDAO();
        SalleDAO salleDAO  = new SalleDAO();
        List<model.Batiment> batiments = batDAO.getTousLesBatiments();
        List<Salle> salles = salleDAO.getToutesLesSalles();

        // ── Zone carte ──
        javafx.scene.layout.FlowPane carte = new javafx.scene.layout.FlowPane();
        carte.setHgap(20);
        carte.setVgap(20);
        carte.setPadding(new Insets(25));
        carte.setStyle(
                "-fx-background-color: #E8F4FD;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #BDC3C7;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        for (model.Batiment b : batiments) {
            // Salles de ce bâtiment
            List<Salle> sallesBat = salles.stream()
                    .filter(s -> s.getBatimentId() == b.getId())
                    .collect(java.util.stream.Collectors.toList());

            long nbSalles   = sallesBat.size();
            int  capTotale  = sallesBat.stream().mapToInt(Salle::getCapacite).sum();
            long nbDispo    = sallesBat.stream().filter(Salle::isDisponible).count();

            // Couleur selon occupation
            String couleurBat = nbSalles == 0 ? "#7F8C8D"
                    : nbDispo == nbSalles ? "#1A3A5C"
                    : "#2471A3";
            String couleurStat = nbSalles == 0 ? "#E74C3C"
                    : nbDispo > 0    ? "#F1C40F"
                    : "#E74C3C";

            // ── Carte bâtiment ──
            VBox card = new VBox(0);
            card.setPrefWidth(170);
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setStyle(
                    "-fx-background-color: " + couleurBat + ";" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
            );

            // En-tête de la carte
            VBox header = new VBox(4);
            header.setPadding(new Insets(12, 12, 8, 12));
            header.setStyle(
                    "-fx-background-color: rgba(0,0,0,0.15);" +
                            "-fx-background-radius: 10 10 0 0;"
            );

            Label lblNom = new Label(b.getNom());
            lblNom.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lblNom.setTextFill(Color.WHITE);
            lblNom.setWrapText(true);

            Label lblLoc = new Label(b.getLocalisation() != null
                    ? "📍 " + b.getLocalisation() : "");
            lblLoc.setFont(Font.font("Arial", 10));
            lblLoc.setTextFill(Color.web("#D6EAF8"));
            lblLoc.setWrapText(true);

            header.getChildren().addAll(lblNom, lblLoc);

            // Corps de la carte
            VBox body = new VBox(6);
            body.setPadding(new Insets(10, 12, 10, 12));

            Label lblEtages = new Label("🏗️ " + b.getNombreEtages() + " étage(s)");
            lblEtages.setFont(Font.font("Arial", 11));
            lblEtages.setTextFill(Color.web("#D6EAF8"));

            Label lblNbSalles = new Label("🏫 " + nbSalles + " salle(s)");
            lblNbSalles.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lblNbSalles.setTextFill(
                    nbSalles == 0 ? Color.web("#E74C3C") : Color.web("#F9E79F"));

            Label lblCap = new Label("👥 " + (capTotale > 0
                    ? capTotale + " places" : "—"));
            lblCap.setFont(Font.font("Arial", 11));
            lblCap.setTextFill(Color.web("#D6EAF8"));

            body.getChildren().addAll(lblEtages, lblNbSalles, lblCap);

            // Liste des salles
            if (!sallesBat.isEmpty()) {
                Separator sepSalles = new Separator();
                sepSalles.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
                body.getChildren().add(sepSalles);

                for (Salle s : sallesBat) {
                    HBox ligneSalle = new HBox(5);
                    ligneSalle.setAlignment(Pos.CENTER_LEFT);
                    Label ptDispo = new Label("●");
                    ptDispo.setFont(Font.font("Arial", 8));
                    ptDispo.setTextFill(s.isDisponible()
                            ? Color.web("#A9DFBF") : Color.web("#E74C3C"));
                    Label lblSalle = new Label(s.getNumero() + " · " + s.getType());
                    lblSalle.setFont(Font.font("Arial", 10));
                    lblSalle.setTextFill(Color.web("#D6EAF8"));
                    ligneSalle.getChildren().addAll(ptDispo, lblSalle);
                    body.getChildren().add(ligneSalle);
                }
            }

            // Pied de carte
            HBox pied = new HBox(5);
            pied.setPadding(new Insets(6, 12, 6, 12));
            pied.setAlignment(Pos.CENTER_LEFT);
            pied.setStyle(
                    "-fx-background-color: rgba(0,0,0,0.15);" +
                            "-fx-background-radius: 0 0 10 10;"
            );
            Label ptStat = new Label("●");
            ptStat.setFont(Font.font("Arial", 8));
            ptStat.setTextFill(Color.web(couleurStat));
            Label lblStat = new Label(nbSalles == 0 ? "Aucune salle"
                    : nbDispo + "/" + nbSalles + " disponible(s)");
            lblStat.setFont(Font.font("Arial", 9));
            lblStat.setTextFill(Color.web("#D6EAF8"));
            pied.getChildren().addAll(ptStat, lblStat);

            card.getChildren().addAll(header, body, pied);

            // Tooltip
            javafx.scene.control.Tooltip tip = new javafx.scene.control.Tooltip(
                    b.getNom() + "\n" +
                            "Localisation : " + b.getLocalisation() + "\n" +
                            "Étages : " + b.getNombreEtages() + "\n" +
                            "Salles : " + nbSalles + " | Capacité : " + capTotale + " places\n" +
                            "Disponibles : " + nbDispo + "/" + nbSalles
            );
            javafx.scene.control.Tooltip.install(card, tip);

            // Hover
            card.setOnMouseEntered(ev -> card.setStyle(
                    "-fx-background-color: #0F2840;" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 12, 0, 0, 5);" +
                            "-fx-scale-x: 1.03; -fx-scale-y: 1.03;"
            ));
            card.setOnMouseExited(ev -> card.setStyle(
                    "-fx-background-color: " + couleurBat + ";" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
            ));

            carte.getChildren().add(card);
        }

        // ── Légende ──
        HBox legende = new HBox(20);
        legende.setAlignment(Pos.CENTER_LEFT);
        legende.setPadding(new Insets(5, 0, 0, 0));

        String[][] legendeItems = {
                {"#1A3A5C", "Toutes salles disponibles"},
                {"#2471A3", "Partiellement disponible"},
                {"#7F8C8D", "Aucune salle assignée"}
        };
        for (String[] item : legendeItems) {
            HBox ligne = new HBox(6);
            ligne.setAlignment(Pos.CENTER_LEFT);
            javafx.scene.shape.Rectangle rect =
                    new javafx.scene.shape.Rectangle(12, 12,
                            javafx.scene.paint.Color.web(item[0]));
            rect.setArcWidth(3); rect.setArcHeight(3);
            Label lbl = new Label(item[1]);
            lbl.setFont(Font.font("Arial", 11));
            lbl.setTextFill(Color.GRAY);
            ligne.getChildren().addAll(rect, lbl);
            legende.getChildren().add(ligne);
        }

        root.getChildren().addAll(titre, carte, legende);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        popup.setScene(new Scene(scroll));
        popup.show();
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

                if (c1.getCreneau() != null && c2.getCreneau() != null
                        && c1.getCreneau().getJour() == c2.getCreneau().getJour()
                        && c1.getCreneau().getHeureDebut().equals(c2.getCreneau().getHeureDebut())
                        && c1.getSalleId() == c2.getSalleId()
                        && c1.getId() != c2.getId()) {
                    listeConflits.getChildren().add(createConflitBox(
                            "🏫 SALLE OCCUPÉE",
                            "Salle " + c1.getNumeroSalle(),
                            c1.getMatiere() + " (" + c1.getGroupe() + ")",
                            c2.getMatiere() + " (" + c2.getGroupe() + ")",
                            "#E74C3C"
                    ));
                    conflitTrouve = true;
                }
                if (c1.getCreneau() != null && c2.getCreneau() != null
                        && c1.getCreneau().getJour() == c2.getCreneau().getJour()
                        && c1.getCreneau().getHeureDebut().equals(c2.getCreneau().getHeureDebut())
                        && c1.getEnseignantId() == c2.getEnseignantId()
                        && c1.getId() != c2.getId()) {
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

        Label icone = new Label("\uD83C\uDF93");
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
            if (selected.getId() == SessionManager.getInstance().getUtilisateurConnecte().getId()) {
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
            if (selected.getId() == SessionManager.getInstance().getUtilisateurConnecte().getId()) {
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

    private void showEquipements() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        Label titre = new Label("\uD83D\uDD27 Gestion des \u00C9quipements");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        HBox actions = new HBox(10);
        Button btnAjouter   = createBouton("+ Ajouter", BLEU_MID);
        Button btnSupprimer = createBouton("\uD83D\uDDD1 Supprimer", "#C0392B");
        Button btnPanne     = createBouton("\u26A0\uFE0F Signaler panne", ORANGE);
        Button btnReparer   = createBouton("\u2705 R\u00E9parer", VERT);
        actions.getChildren().addAll(btnAjouter, btnSupprimer, btnPanne, btnReparer);

        TableView<model.Equipement> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );
        table.setPrefHeight(450);

        TableColumn<model.Equipement, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<model.Equipement, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);

        TableColumn<model.Equipement, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(200);

        TableColumn<model.Equipement, Integer> colSalle = new TableColumn<>("Salle ID");
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salleId"));
        colSalle.setPrefWidth(80);

        TableColumn<model.Equipement, String> colEtat = new TableColumn<>("\u00C9tat");
        colEtat.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().isFonctionnel() ? "Fonctionnel" : "En panne"
                )
        );
        colEtat.setCellFactory(col -> new TableCell<model.Equipement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else if (item.equals("Fonctionnel")) {
                    setText("\u25CF Fonctionnel");
                    setStyle("-fx-text-fill: #1E8449; -fx-font-weight: bold;");
                } else {
                    setText("\u25CF En panne");
                    setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
                }
            }
        });
        colEtat.setPrefWidth(120);

        table.getColumns().addAll(colId, colNom, colDesc, colSalle, colEtat);

        dao.EquipementDAO dao = new dao.EquipementDAO();
        ObservableList<model.Equipement> data =
                FXCollections.observableArrayList(dao.getTousLesEquipements());
        table.setItems(data);

        btnAjouter.setOnAction(e -> showFormulaireAjoutEquipement(data));

        btnSupprimer.setOnAction(e -> {
            model.Equipement selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("\u26A0\uFE0F S\u00E9lectionne un \u00E9quipement !");
                return;
            }
            if (dao.supprimer(selected.getId())) {
                data.setAll(dao.getTousLesEquipements());
            }
        });

        btnPanne.setOnAction(e -> {
            model.Equipement selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("\u26A0\uFE0F S\u00E9lectionne un \u00E9quipement !");
                return;
            }
            if (dao.setFonctionnel(selected.getId(), false)) {
                data.setAll(dao.getTousLesEquipements());
            }
        });

        btnReparer.setOnAction(e -> {
            model.Equipement selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("\u26A0\uFE0F S\u00E9lectionne un \u00E9quipement !");
                return;
            }
            if (dao.setFonctionnel(selected.getId(), true)) {
                data.setAll(dao.getTousLesEquipements());
            }
        });

        panel.getChildren().addAll(titre, actions, table);
        contentArea.getChildren().setAll(panel);
    }

    private void showFormulaireAjoutEquipement(
            ObservableList<model.Equipement> data) {
        Stage popup = new Stage();
        popup.setTitle("Ajouter un \u00E9quipement");

        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setPrefWidth(320);
        form.setStyle("-fx-background-color: white;");

        Label titre = new Label("\uD83D\uDD27 Nouvel \u00C9quipement");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TextField tfNom   = createField("Nom (ex: Vid\u00E9oprojecteur)");
        TextField tfDesc  = createField("Description (ex: Epson EB-X41)");
        TextField tfSalle = createField("ID Salle (ex: 1)");

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("\uD83D\uDCBE Enregistrer", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfNom.getText().isEmpty() || tfSalle.getText().isEmpty()) {
                lblMsg.setText("\u26A0\uFE0F Remplis au moins le nom et l'ID salle !");
                return;
            }
            try {
                model.Equipement eq = new model.Equipement();
                eq.setNom(tfNom.getText());
                eq.setDescription(tfDesc.getText());
                eq.setSalleId(Integer.parseInt(tfSalle.getText()));
                eq.setFonctionnel(true);

                dao.EquipementDAO equipDAO = new dao.EquipementDAO();
                if (equipDAO.ajouter(eq)) {
                    data.setAll(equipDAO.getTousLesEquipements());
                    popup.close();
                } else {
                    lblMsg.setText("\u274C Erreur lors de l'ajout !");
                }
            } catch (NumberFormatException ex) {
                lblMsg.setText("\u26A0\uFE0F L'ID salle doit \u00EAtre un nombre !");
            }
        });

        form.getChildren().addAll(
                titre,
                new Label("Nom :"),         tfNom,
                new Label("Description :"), tfDesc,
                new Label("ID Salle :"),    tfSalle,
                lblMsg, btnSave
        );

        popup.setScene(new Scene(form));
        popup.show();
    }
    private void showEmploiDuTempsEnseignant() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        // ── En-tête ──
        SessionManager session = SessionManager.getInstance();
        String nomEnseignant = session.getUtilisateurConnecte().getNomComplet();
        int enseignantId     = session.getUtilisateurConnecte().getId();

        Label titre = new Label("🗓️ Mon Emploi du Temps");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        Label sousTitre = new Label("Cours de " + nomEnseignant);
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setTextFill(Color.GRAY);

        // ── Récupère uniquement les cours de cet enseignant ──
        CoursDAO dao = new CoursDAO();
        List<Cours> tousLesCours = dao.getTousLesCours();
        List<Cours> mesCours = tousLesCours.stream()
                .filter(c -> c.getEnseignantId() == enseignantId)
                .collect(java.util.stream.Collectors.toList());

        // ── Statistiques rapides ──
        HBox stats = new HBox(15);
        stats.getChildren().addAll(
                createCarte("📚", "Mes cours",
                        String.valueOf(mesCours.size()), BLEU_MID),
                createCarte("👥", "Groupes",
                        String.valueOf(mesCours.stream()
                                .map(Cours::getGroupe)
                                .distinct().count()), VERT),
                createCarte("🏫", "Salles utilisées",
                        String.valueOf(mesCours.stream()
                                .map(Cours::getSalleId)
                                .distinct().count()), ORANGE)
        );

        // ── Tableau de mes cours ──
        TableView<Cours> table = createTableCours();
        table.setItems(javafx.collections.FXCollections
                .observableArrayList(mesCours));
        table.setPrefHeight(350);

        // ── Actions ──
        Label titreActions = new Label("⚡ Actions");
        titreActions.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        titreActions.setTextFill(Color.web(BLEU_FONCE));

        HBox actions = new HBox(15);
        Button btnReserver = createBouton("📅 Réserver une salle", BLEU_MID);
        Button btnSignaler = createBouton("⚠️ Signaler un problème", ORANGE);
        actions.getChildren().addAll(btnReserver, btnSignaler);

        btnReserver.setOnAction(e -> showFormulaireReservation());
        btnSignaler.setOnAction(e -> showFormulaireSignalement());

        panel.getChildren().addAll(
                titre, sousTitre, stats, table, titreActions, actions);
        contentArea.getChildren().setAll(panel);
    }

    private void showFormulaireReservation() {
        Stage popup = new Stage();
        popup.setTitle("Réserver une salle");

        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setPrefWidth(350);
        form.setStyle("-fx-background-color: white;");

        Label titre = new Label("📅 Nouvelle Réservation");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TextField tfSalle  = createField("ID Salle (ex: 1)");
        TextField tfMotif  = createField("Motif (ex: Soutenance)");

        ComboBox<String> cbJour = new ComboBox<>();
        cbJour.getItems().addAll("LUNDI","MARDI","MERCREDI","JEUDI","VENDREDI","SAMEDI");
        cbJour.setPromptText("Jour");
        cbJour.setPrefWidth(Double.MAX_VALUE);

        TextField tfHeureDebut = createField("Heure début (ex: 08:00:00)");
        TextField tfHeureFin   = createField("Heure fin (ex: 10:00:00)");

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("💾 Réserver", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfSalle.getText().isEmpty() || cbJour.getValue() == null
                    || tfHeureDebut.getText().isEmpty() || tfHeureFin.getText().isEmpty()) {
                lblMsg.setText("⚠️ Remplis tous les champs !");
                return;
            }
            try {
                // Créer le créneau
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

                // Vérifier conflit
                dao.ConflitDAO conflitDAO = new dao.ConflitDAO();
                if (conflitDAO.salleDejaOccupee(Integer.parseInt(tfSalle.getText()), creneauId)) {
                    lblMsg.setText("❌ Cette salle est déjà occupée !");
                    return;
                }

                // Créer la réservation
                String sqlRes = "INSERT INTO reservation (demandeur_id, salle_id, creneau_id, motif, statut) " +
                        "VALUES (?, ?, ?, ?, 'EN_ATTENTE')";
                java.sql.PreparedStatement psRes = conn.prepareStatement(sqlRes);
                psRes.setInt(1, SessionManager.getInstance().getUtilisateurConnecte().getId());
                psRes.setInt(2, Integer.parseInt(tfSalle.getText()));
                psRes.setInt(3, creneauId);
                psRes.setString(4, tfMotif.getText());

                if (psRes.executeUpdate() > 0) {
                    showAlert("✅ Réservation soumise avec succès !\nEn attente de validation.");
                    popup.close();
                }
            } catch (Exception ex) {
                lblMsg.setText("❌ Erreur : " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
                titre,
                new Label("ID Salle :"),    tfSalle,
                new Label("Motif :"),       tfMotif,
                new Label("Jour :"),        cbJour,
                new Label("Heure début :"), tfHeureDebut,
                new Label("Heure fin :"),   tfHeureFin,
                lblMsg, btnSave
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        popup.setScene(new Scene(scroll, 370, 480));
        popup.show();
    }

    private void showFormulaireSignalement() {
        Stage popup = new Stage();
        popup.setTitle("Signaler un problème");

        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setPrefWidth(350);
        form.setStyle("-fx-background-color: white;");

        Label titre = new Label("⚠️ Signaler un Problème");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web(BLEU_FONCE));

        TextField tfSalle = createField("ID Salle concernée (ex: 1)");

        TextArea taDesc = new TextArea();
        taDesc.setPromptText("Décrivez le problème...");
        taDesc.setPrefRowCount(4);
        taDesc.setStyle("-fx-font-size: 13; -fx-background-radius: 6;");

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("📤 Envoyer", ORANGE);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfSalle.getText().isEmpty() || taDesc.getText().isEmpty()) {
                lblMsg.setText("⚠️ Remplis tous les champs !");
                return;
            }
            try {
                // Marquer l'équipement comme en panne
                String sql = "UPDATE equipement SET fonctionnel = FALSE WHERE salle_id = ?";
                java.sql.Connection conn = database.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(tfSalle.getText()));
                ps.executeUpdate();

                // ✅ APRÈS — envoie au gestionnaire dynamiquement
                dao.UtilisateurDAO utilisateurDAO = new dao.UtilisateurDAO();
                int gestionnaireId = utilisateurDAO.getIdGestionnaire();

                if (gestionnaireId == -1) {
                    lblMsg.setText("❌ Aucun gestionnaire trouvé !");
                    return;
                }

                String sqlNotif = "INSERT INTO notification (destinataire_id, message, type) " +
                        "VALUES (?, ?, 'PROBLEME_TECHNIQUE')";
                java.sql.PreparedStatement psNotif = conn.prepareStatement(sqlNotif);
                psNotif.setInt(1, gestionnaireId);
                psNotif.setString(2, "Problème signalé en salle " + tfSalle.getText() +
                        " par " + SessionManager.getInstance().getUtilisateurConnecte().getNomComplet() +
                        " : " + taDesc.getText());
                psNotif.executeUpdate();

                showAlert("✅ Problème signalé avec succès !\nLe gestionnaire a été notifié.");
                popup.close();
            } catch (Exception ex) {
                lblMsg.setText("❌ Erreur : " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
                titre,
                new Label("ID Salle :"),     tfSalle,
                new Label("Description :"),  taDesc,
                lblMsg, btnSave
        );

        popup.setScene(new Scene(form));
        popup.show();
    }
    private void showEmploiDuTempsEtudiant() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        SessionManager session = SessionManager.getInstance();
        String classe = session.getUtilisateurConnecte().getClasse();
        if (classe == null || classe.isEmpty()) classe = "Toutes";



        // ── En-tête ──
        Label titre = new Label("🗓️ Emploi du Temps — " + classe);
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        Label sousTitre = new Label("Cours de ta classe · Semaine en cours");
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setTextFill(Color.GRAY);

        // ── Filtre par groupe/classe ──
        CoursDAO dao = new CoursDAO();
        List<Cours> tousLesCours = dao.getTousLesCours();
        final String classeFinale = classe;

        // Affiche tous les cours par défaut
        List<Cours> mesCours = new java.util.ArrayList<>(tousLesCours);
        // ── Statistiques ──
        HBox stats = new HBox(15);
        long nbCours  = mesCours.size();
        long nbTD     = mesCours.stream().filter(c -> "TD".equals(c.getType())).count();
        long nbTP     = mesCours.stream().filter(c -> "TP".equals(c.getType())).count();
        stats.getChildren().addAll(
                createCarte("📚", "Cours cette semaine", String.valueOf(nbCours), BLEU_MID),
                createCarte("📝", "TD", String.valueOf(nbTD), VERT),
                createCarte("🔬", "TP", String.valueOf(nbTP), ORANGE)
        );

        // ── Grille hebdomadaire ──
        GridPane grille = buildGrilleEtudiant(mesCours);
        ScrollPane scroll = new ScrollPane(grille);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: white;");

        // ── Sélecteur de groupe (si l'étudiant veut filtrer) ──
        HBox filtres = new HBox(10);
        filtres.setAlignment(Pos.CENTER_LEFT);
        Label lblFiltrer = new Label("Filtrer par groupe :");
        lblFiltrer.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        ComboBox<String> cbGroupe = new ComboBox<>();
        cbGroupe.getItems().add("Tous");
        tousLesCours.stream()
                .map(Cours::getGroupe)
                .distinct()
                .sorted()
                .forEach(cbGroupe.getItems()::add);
        cbGroupe.setValue(classeFinale.equals("Toutes") ? "Tous" : classeFinale);
        cbGroupe.setPrefWidth(150);

        Button btnFiltrer = createBouton("🔍 Filtrer", BLEU_MID);
        btnFiltrer.setOnAction(e -> {
            String groupeChoisi = cbGroupe.getValue();
            List<Cours> filtrés = tousLesCours.stream()
                    .filter(c -> "Tous".equals(groupeChoisi) ||
                            c.getGroupe().equals(groupeChoisi))
                    .collect(java.util.stream.Collectors.toList());
            GridPane nouvelleGrille = buildGrilleEtudiant(filtrés);
            scroll.setContent(nouvelleGrille);

            // Mise à jour stats
            stats.getChildren().setAll(
                    createCarte("📚", "Cours", String.valueOf(filtrés.size()), BLEU_MID),
                    createCarte("📝", "TD", String.valueOf(
                            filtrés.stream().filter(c -> "TD".equals(c.getType())).count()), VERT),
                    createCarte("🔬", "TP", String.valueOf(
                            filtrés.stream().filter(c -> "TP".equals(c.getType())).count()), ORANGE)
            );
        });

        filtres.getChildren().addAll(lblFiltrer, cbGroupe, btnFiltrer);

        // ── Recherche salle libre ──
        Button btnRechercherSalle = createBouton("🔍 Trouver une salle libre", VIOLET);
        btnRechercherSalle.setOnAction(e -> {
            setActif(btnRechercherSalle); // pas idéal mais fonctionnel
            showRechercherSalle();
        });

        panel.getChildren().addAll(
                titre, sousTitre, stats, filtres, scroll, btnRechercherSalle);
        contentArea.getChildren().setAll(panel);
    }

    // ── Grille hebdomadaire pour l'étudiant ──
    private GridPane buildGrilleEtudiant(List<Cours> coursList) {
        GridPane grille = new GridPane();
        grille.setHgap(2);
        grille.setVgap(2);
        grille.setPadding(new Insets(10));
        grille.setStyle("-fx-background-color: white;");

        String[] JOURS       = {"LUNDI","MARDI","MERCREDI","JEUDI","VENDREDI","SAMEDI"};
        String[] JOURS_EN    = {"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
        String[] CRENEAUX    = {"08:00-10:00","10:00-12:00","12:00-14:00","14:00-16:00","16:00-18:00"};
        String[] HEURES_DEB  = {"08","10","12","14","16"};

        // Contraintes colonnes
        ColumnConstraints cc0 = new ColumnConstraints(); cc0.setPrefWidth(100);
        grille.getColumnConstraints().add(cc0);
        for (int i = 0; i < JOURS.length; i++) {
            ColumnConstraints cc = new ColumnConstraints(); cc.setPrefWidth(150);
            grille.getColumnConstraints().add(cc);
        }

        // En-tête vide
        grille.add(new Label(""), 0, 0);

        // En-têtes jours
        for (int j = 0; j < JOURS.length; j++) {
            Label lblJour = new Label(JOURS[j]);
            lblJour.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lblJour.setTextFill(Color.WHITE);
            lblJour.setAlignment(Pos.CENTER);
            lblJour.setMaxWidth(Double.MAX_VALUE);
            lblJour.setPadding(new Insets(8));
            lblJour.setStyle("-fx-background-color: #1A3A5C; -fx-background-radius: 4;");
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
            lblHeure.setStyle("-fx-background-color: #EBF5FB; -fx-background-radius: 4;");
            grille.add(lblHeure, 0, c + 1);

            // Cellules vides
            for (int j = 0; j < JOURS.length; j++) {
                Label vide = new Label("");
                vide.setMaxWidth(Double.MAX_VALUE);
                vide.setMinHeight(60);
                vide.setStyle("-fx-background-color: #F2F3F4; -fx-background-radius: 4;");
                grille.add(vide, j + 1, c + 1);
            }
        }

        // Remplir avec les cours
        for (Cours cours : coursList) {
            if (cours.getCreneau() == null) continue;

            String jourCours = cours.getCreneau().getJour().name();
            String heureDeb  = cours.getCreneau().getHeureDebut().toString();

            int col = -1;
            for (int j = 0; j < JOURS_EN.length; j++) {
                if (JOURS_EN[j].equals(jourCours)) { col = j + 1; break; }
            }
            int row = -1;
            for (int h = 0; h < HEURES_DEB.length; h++) {
                if (heureDeb.startsWith(HEURES_DEB[h])) { row = h + 1; break; }
            }

            if (col == -1 || row == -1) continue;

            VBox card = new VBox(3);
            card.setPadding(new Insets(6));
            card.setMinHeight(60);
            card.setMaxWidth(Double.MAX_VALUE);

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

            Label lblInfo = new Label(cours.getGroupe() + " • " + cours.getType());
            lblInfo.setFont(Font.font("Arial", 10));
            lblInfo.setTextFill(Color.web("#D6EAF8"));

            Label lblSalle = new Label("🏫 " + cours.getNumeroSalle());
            lblSalle.setFont(Font.font("Arial", 10));
            lblSalle.setTextFill(Color.web("#D6EAF8"));

            card.getChildren().addAll(lblMatiere, lblInfo, lblSalle);
            grille.add(card, col, row);
        }

        return grille;
    }

    private int getLigneGrille(String heureDebut) {
        if (heureDebut.startsWith("8") || heureDebut.startsWith("08")) return 1;
        if (heureDebut.startsWith("10")) return 2;
        if (heureDebut.startsWith("12")) return 3;
        if (heureDebut.startsWith("14")) return 4;
        if (heureDebut.startsWith("16")) return 5;
        return -1;
    }
    private void showReservations() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        Label titre = new Label("📋 Gestion des Réservations");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        Label sousTitre = new Label("Demandes de réservation en attente de validation");
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setTextFill(Color.GRAY);

        // Tableau
        TableView<model.Reservation> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );
        table.setPrefHeight(400);

        TableColumn<model.Reservation, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<model.Reservation, String> colDemandeur = new TableColumn<>("Demandeur");
        colDemandeur.setCellValueFactory(new PropertyValueFactory<>("nomDemandeur"));
        colDemandeur.setPrefWidth(150);

        TableColumn<model.Reservation, String> colSalle = new TableColumn<>("Salle");
        colSalle.setCellValueFactory(new PropertyValueFactory<>("numeroSalle"));
        colSalle.setPrefWidth(80);

        TableColumn<model.Reservation, String> colMotif = new TableColumn<>("Motif");
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colMotif.setPrefWidth(200);

        TableColumn<model.Reservation, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(col -> new TableCell<model.Reservation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else if (item.equals("EN_ATTENTE")) {
                    setText("⏳ En attente");
                    setStyle("-fx-text-fill: #E67E22; -fx-font-weight: bold;");
                } else if (item.equals("VALIDEE")) {
                    setText("✅ Validée");
                    setStyle("-fx-text-fill: #1E8449; -fx-font-weight: bold;");
                } else {
                    setText("❌ Annulée");
                    setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
                }
            }
        });
        colStatut.setPrefWidth(120);

        table.getColumns().addAll(colId, colDemandeur, colSalle, colMotif, colStatut);

        // Charger les réservations
        dao.ReservationDAO reservationDAO = new dao.ReservationDAO();
        ObservableList<model.Reservation> data =
                FXCollections.observableArrayList(reservationDAO.getToutesLesReservations());
        table.setItems(data);

        // Boutons
        HBox actions = new HBox(10);
        Button btnValider  = createBouton("✅ Valider", VERT);
        Button btnRefuser  = createBouton("❌ Refuser", "#C0392B");
        actions.getChildren().addAll(btnValider, btnRefuser);

        btnValider.setOnAction(e -> {
            model.Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne une réservation !");
                return;
            }
            if (reservationDAO.changerStatut(selected.getId(), "VALIDEE")) {
                data.setAll(reservationDAO.getToutesLesReservations());
            }
        });

        btnRefuser.setOnAction(e -> {
            model.Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("⚠️ Sélectionne une réservation !");
                return;
            }
            if (reservationDAO.changerStatut(selected.getId(), "ANNULEE")) {
                data.setAll(reservationDAO.getToutesLesReservations());
            }
        });

        panel.getChildren().addAll(titre, sousTitre, actions, table);
        contentArea.getChildren().setAll(panel);
    }

    private void showGenererEmploiDuTemps() {
        contentArea.getChildren().setAll(new EmploiDuTempsView().getView());
    }

    private void showFormulaireModifierCours(Cours cours, ObservableList<Cours> data) {
        Stage popup = new Stage();
        popup.setTitle("Modifier un cours");

        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setPrefWidth(350);
        form.setStyle("-fx-background-color: white;");

        Label titre = new Label("✏️ Modifier le Cours");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web(BLEU_FONCE));

        // Pré-remplir avec les valeurs actuelles
        TextField tfMatiere = createField("Matière");
        tfMatiere.setText(cours.getMatiere());

        TextField tfGroupe = createField("Groupe");
        tfGroupe.setText(cours.getGroupe());

        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("CM", "TD", "TP", "EXAMEN");
        cbType.setValue(cours.getType());
        cbType.setPrefWidth(Double.MAX_VALUE);

        TextField tfEnseignant = createField("ID Enseignant");
        tfEnseignant.setText(String.valueOf(cours.getEnseignantId()));

        TextField tfSalle = createField("ID Salle");
        tfSalle.setText(String.valueOf(cours.getSalleId()));

        Label lblMsg = new Label("");
        lblMsg.setTextFill(Color.RED);

        Button btnSave = createBouton("💾 Enregistrer", BLEU_MID);
        btnSave.setPrefWidth(Double.MAX_VALUE);

        btnSave.setOnAction(e -> {
            if (tfMatiere.getText().isEmpty() || tfGroupe.getText().isEmpty()
                    || cbType.getValue() == null) {
                lblMsg.setText("⚠️ Remplis tous les champs !");
                return;
            }
            try {
                // Vérifier conflit salle
                int newSalleId = Integer.parseInt(tfSalle.getText());
                int newEnseignantId = Integer.parseInt(tfEnseignant.getText());

                dao.ConflitDAO conflitDAO = new dao.ConflitDAO();
                if (newSalleId != cours.getSalleId() &&
                        conflitDAO.salleDejaOccupee(newSalleId, cours.getCreneauId())) {
                    lblMsg.setText("❌ CONFLIT : cette salle est déjà occupée !");
                    return;
                }

                // Mettre à jour en base
                String sql = "UPDATE cours SET matiere=?, groupe=?, type=?, " +
                        "enseignant_id=?, salle_id=? WHERE id=?";
                java.sql.Connection conn = database.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, tfMatiere.getText());
                ps.setString(2, tfGroupe.getText());
                ps.setString(3, cbType.getValue());
                ps.setInt   (4, newEnseignantId);
                ps.setInt   (5, newSalleId);
                ps.setInt   (6, cours.getId());

                if (ps.executeUpdate() > 0) {
                    CoursDAO coursDAO = new CoursDAO();
                    data.setAll(coursDAO.getTousLesCours());
                    popup.close();
                    showAlert("✅ Cours modifié avec succès !");
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
                lblMsg, btnSave
        );

        popup.setScene(new Scene(form));
        popup.show();
    }
    private void showNotifications() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(5));

        int userId = SessionManager.getInstance().getUtilisateurConnecte().getId();
        dao.NotificationDAO notifDAO = new dao.NotificationDAO();

        Label titre = new Label("🔔 Mes Notifications");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setTextFill(Color.web(BLEU_FONCE));

        // Bouton tout marquer comme lu
        Button btnLuTout = createBouton("✅ Tout marquer comme lu", BLEU_MID);
        btnLuTout.setOnAction(e -> {
            notifDAO.toutMarquerLues(userId);
            showNotifications();
        });

        // Liste des notifications
        VBox listeNotifs = new VBox(10);
        List<Notification> notifications = notifDAO.getMesNotifications(userId);

        if (notifications.isEmpty()) {
            HBox vide = new HBox();
            vide.setPadding(new Insets(40));
            vide.setAlignment(Pos.CENTER);
            vide.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
            Label lblVide = new Label("🔔 Aucune notification");
            lblVide.setFont(Font.font("Arial", 15));
            lblVide.setTextFill(Color.GRAY);
            vide.getChildren().add(lblVide);
            listeNotifs.getChildren().add(vide);
        } else {
            for (Notification n : notifications) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(15));
                card.setAlignment(Pos.CENTER_LEFT);

                String bgColor = n.isLue() ? "white" : "#EBF5FB";
                String borderColor = n.isLue() ? "#BDC3C7" : BLEU_MID;

                card.setStyle(
                        "-fx-background-color: " + bgColor + ";" +
                                "-fx-background-radius: 8;" +
                                "-fx-border-color: " + borderColor + ";" +
                                "-fx-border-width: 0 0 0 4;" +
                                "-fx-border-radius: 8;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
                );

                Label icone = new Label(n.isLue() ? "📭" : "📬");
                icone.setFont(Font.font("Arial", 24));

                VBox details = new VBox(5);
                HBox.setHgrow(details, Priority.ALWAYS);

                Label lblMessage = new Label(n.getMessage());
                lblMessage.setFont(Font.font("Arial",
                        n.isLue() ? FontWeight.NORMAL : FontWeight.BOLD, 13));
                lblMessage.setTextFill(Color.web(BLEU_FONCE));
                lblMessage.setWrapText(true);

                Label lblDate = new Label(n.getDateEnvoi() != null ?
                        n.getDateEnvoi().toString().replace("T", " à ").substring(0, 19) : "");
                lblDate.setFont(Font.font("Arial", 11));
                lblDate.setTextFill(Color.GRAY);

                details.getChildren().addAll(lblMessage, lblDate);

                // Bouton marquer comme lu
                Button btnLu = createBouton("Lu", "#7F8C8D");
                btnLu.setStyle(
                        "-fx-background-color: #ECF0F1;" +
                                "-fx-text-fill: #7F8C8D;" +
                                "-fx-font-size: 11;" +
                                "-fx-padding: 4 10 4 10;" +
                                "-fx-cursor: hand;" +
                                "-fx-background-radius: 4;"
                );
                if (!n.isLue()) {
                    btnLu.setOnAction(e -> {
                        notifDAO.marquerLue(n.getId());
                        showNotifications();
                    });
                }

                card.getChildren().addAll(icone, details, btnLu);
                listeNotifs.getChildren().add(card);
            }
        }

        ScrollPane scroll = new ScrollPane(listeNotifs);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setPrefHeight(500);

        panel.getChildren().addAll(titre, btnLuTout, scroll);
        contentArea.getChildren().setAll(panel);
    }
    // ── Récupère les infos du cours qui occupe déjà la salle ──
    private String getInfoConflitSalle(java.sql.Connection conn,
                                       int salleId, int creneauId) {
        String info = "cours inconnu";
        String sql  = "SELECT c.matiere, c.groupe, c.type, " +
                "CONCAT(u.prenom, ' ', u.nom) AS enseignant " +
                "FROM cours c " +
                "LEFT JOIN utilisateur u ON c.enseignant_id = u.id " +
                "WHERE c.salle_id = ? AND c.creneau_id = ? LIMIT 1";
        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, salleId);
            ps.setInt(2, creneauId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                info = rs.getString("matiere") +
                        " (" + rs.getString("groupe") + ")" +
                        " — " + rs.getString("type") +
                        " — " + rs.getString("enseignant");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("❌ getInfoConflitSalle : " + e.getMessage());
        }
        return info;
    }

    // ── Crée la notification de conflit salle ──
    private void notifierConflitSalle(java.sql.Connection conn,
                                      int salleId,
                                      String nouvelleMatiere,
                                      String nouveauGroupe,
                                      String jour,
                                      String heureDebut,
                                      String coursExistant) {
        try {
            // Récupérer l'ID du gestionnaire connecté
            int gestionnaireId = SessionManager.getInstance()
                    .getUtilisateurConnecte()
                    .getId();

            // Récupérer le numéro de la salle
            String numeroSalle = "Salle ID " + salleId;
            String sqlSalle = "SELECT numero FROM salle WHERE id = ?";
            java.sql.PreparedStatement psSalle = conn.prepareStatement(sqlSalle);
            psSalle.setInt(1, salleId);
            java.sql.ResultSet rsSalle = psSalle.executeQuery();
            if (rsSalle.next()) {
                numeroSalle = "Salle " + rsSalle.getString("numero");
            }

            // Construire le message de notification
            String message = "🚨 CONFLIT DE SALLE DÉTECTÉ\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "📍 " + numeroSalle + "\n" +
                    "📅 " + jour + " à " + heureDebut + "\n\n" +
                    "Cours existant :\n  • " + coursExistant + "\n\n" +
                    "Nouveau cours refusé :\n  • " +
                    nouvelleMatiere + " (" + nouveauGroupe + ")\n\n" +
                    "⚠️ Action requise : changer la salle ou le créneau.";

            String sqlNotif = "INSERT INTO notification " +
                    "(destinataire_id, message, type, lue) " +
                    "VALUES (?, ?, 'CONFLIT_SALLE', FALSE)";
            java.sql.PreparedStatement psNotif = conn.prepareStatement(sqlNotif);
            psNotif.setInt   (1, gestionnaireId);
            psNotif.setString(2, message);
            psNotif.executeUpdate();

            System.out.println("✅ Notification conflit envoyée au gestionnaire ID "
                    + gestionnaireId);

        } catch (java.sql.SQLException e) {
            System.err.println("❌ Erreur notification conflit : " + e.getMessage());
        }
    }

    // ── Popup visuel de conflit ──
    private void showConflitAlert(String titrePopup, String ressource,
                                  String creneau, String coursExistant,
                                  String nouveauCours) {
        Stage popup = new Stage();
        popup.setTitle(titrePopup);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setPrefWidth(420);
        root.setStyle("-fx-background-color: white;");

        // ── Bandeau rouge ──
        HBox bandeau = new HBox(12);
        bandeau.setPadding(new Insets(15));
        bandeau.setAlignment(Pos.CENTER_LEFT);
        bandeau.setStyle(
                "-fx-background-color: #FDEDEC;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #E74C3C;" +
                        "-fx-border-width: 0 0 0 5;" +
                        "-fx-border-radius: 8;"
        );

        Label iconeAlert = new Label("🚨");
        iconeAlert.setFont(Font.font("Arial", 32));

        VBox infoAlert = new VBox(4);
        Label lblTitre = new Label("CONFLIT DE SALLE DÉTECTÉ");
        lblTitre.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblTitre.setTextFill(Color.web("#E74C3C"));
        Label lblRessource = new Label(ressource);
        lblRessource.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblRessource.setTextFill(Color.web("#1A3A5C"));
        Label lblCreneau = new Label("📅 " + creneau);
        lblCreneau.setFont(Font.font("Arial", 11));
        lblCreneau.setTextFill(Color.GRAY);
        infoAlert.getChildren().addAll(lblTitre, lblRessource, lblCreneau);
        bandeau.getChildren().addAll(iconeAlert, infoAlert);

        // ── Comparaison des cours ──
        GridPane comparaison = new GridPane();
        comparaison.setHgap(10);
        comparaison.setVgap(8);
        comparaison.setPadding(new Insets(15));
        comparaison.setStyle(
                "-fx-background-color: #F8F9FA;" +
                        "-fx-background-radius: 8;"
        );

        Label lblCol1 = new Label("Cours existant");
        lblCol1.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblCol1.setTextFill(Color.web("#1E8449"));

        Label lblCol2 = new Label("Nouveau cours (refusé)");
        lblCol2.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblCol2.setTextFill(Color.web("#E74C3C"));

        Label valExistant = new Label(coursExistant);
        valExistant.setWrapText(true);
        valExistant.setFont(Font.font("Arial", 12));
        valExistant.setTextFill(Color.web("#1A3A5C"));
        valExistant.setStyle(
                "-fx-background-color: #D5F5E3;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8;"
        );

        Label valNouveau = new Label(nouveauCours);
        valNouveau.setWrapText(true);
        valNouveau.setFont(Font.font("Arial", 12));
        valNouveau.setTextFill(Color.web("#1A3A5C"));
        valNouveau.setStyle(
                "-fx-background-color: #FADBD8;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8;"
        );

        comparaison.add(lblCol1,    0, 0);
        comparaison.add(lblCol2,    1, 0);
        comparaison.add(valExistant,0, 1);
        comparaison.add(valNouveau, 1, 1);

        GridPane.setHgrow(valExistant, Priority.ALWAYS);
        GridPane.setHgrow(valNouveau,  Priority.ALWAYS);

        // ── Notification confirmée ──
        HBox notifConfirm = new HBox(8);
        notifConfirm.setAlignment(Pos.CENTER_LEFT);
        notifConfirm.setPadding(new Insets(10));
        notifConfirm.setStyle(
                "-fx-background-color: #EBF5FB;" +
                        "-fx-background-radius: 6;"
        );
        Label icoNotif = new Label("📬");
        icoNotif.setFont(Font.font("Arial", 16));
        Label lblNotif = new Label(
                "Une notification a été envoyée au gestionnaire.");
        lblNotif.setFont(Font.font("Arial", 12));
        lblNotif.setTextFill(Color.web("#2471A3"));
        notifConfirm.getChildren().addAll(icoNotif, lblNotif);

        // ── Bouton fermer ──
        Button btnFermer = createBouton("✖ Fermer", "#E74C3C");
        btnFermer.setPrefWidth(Double.MAX_VALUE);
        btnFermer.setOnAction(ev -> popup.close());

        root.getChildren().addAll(bandeau, comparaison, notifConfirm, btnFermer);

        popup.setScene(new Scene(root));
        popup.show();
    }
    public static void main(String[] args) {
        Application.launch(MainView.class, args);
    }
}