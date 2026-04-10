package com.hotel;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardScreen {

    private static HotelService service;
    private static Stage mainStage;
    private static Scene mainScene;

    private static ObservableList<Room>    roomList;
    private static ObservableList<Booking> bookingList;

    private static Label statTotal, statAvail, statOcc, statRev;

    // ── THEME ─────────────────────────────────────────────────────────────
    private static boolean isDark = false;

    // Light
    private static final String L_BG      = "#f0f0eb";
    private static final String L_CARD    = "white";
    private static final String L_TEXT    = "#1a1a2e";
    private static final String L_SUB     = "#666666";
    private static final String L_BORDER  = "#dddddd";
    private static final String L_TABLE   = "white";
    private static final String L_INPUT   = "white";
    private static final String L_HEADER  = "#1a1a2e";

    // Dark
    private static final String D_BG      = "#0f0f1a";
    private static final String D_CARD    = "#1e1e30";
    private static final String D_TEXT    = "#e8e8f0";
    private static final String D_SUB     = "#9090aa";
    private static final String D_BORDER  = "#333355";
    private static final String D_TABLE   = "#181828";
    private static final String D_INPUT   = "#1e1e30";
    private static final String D_HEADER  = "#0a0a14";

    // Nodes to restyle
    private static HBox  headerBox;
    private static TabPane tabPane;
    private static Button themeBtn;
    private static Label  dateLabel;
    private static VBox   dashContent, roomsContent, bookingsContent, billingContent, billRecContent;
    private static TableView<Booking> dashRecentTable, bookingsTable, billingTable, billRecTable;
    private static TableView<Room>    roomsTable;
    private static TextArea billArea, detailArea;
    private static VBox cardTotal, cardAvail, cardOcc, cardRev;

    private static final List<Label>     headingLabels = new ArrayList<>();
    private static final List<Label>     subLabels     = new ArrayList<>();
    private static final List<Label>     formLabels    = new ArrayList<>();
    private static final List<TextField> allFields     = new ArrayList<>();

    // ═════════════════════════════════════════════════════════════════════
    public static void show(Stage stage, HotelService svc) {
        mainStage   = stage;
        service     = svc;
        roomList    = FXCollections.observableArrayList(service.getAllRooms());
        bookingList = FXCollections.observableArrayList(service.getAllBookings());

        // Clear stale references between logins
        headingLabels.clear(); subLabels.clear(); formLabels.clear(); allFields.clear();

        headerBox = buildHeader();
        tabPane   = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
            buildDashboardTab(),
            buildRoomsTab(),
            buildBookingsTab(),
            buildCheckoutTab(), 
            buildBillingTab(),
            buildBillRecordsTab()
        );

        VBox root = new VBox(headerBox, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        mainScene = new Scene(root, 1090, 710);
        applyTheme();
        stage.setTitle("Grand Horizon – Hotel Management");
        stage.setScene(mainScene);
        stage.setResizable(true);
        stage.show();
    }

    // ── HEADER ────────────────────────────────────────────────────────────
    private static HBox buildHeader() {
        HBox hdr = new HBox(12);
        hdr.setPadding(new Insets(12, 24, 12, 24));
        hdr.setAlignment(Pos.CENTER_LEFT);

        Label logo = new Label("🏨");
        logo.setStyle("-fx-font-size: 24px;");

        Label title = new Label("GRAND HORIZON");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e0c97f; -fx-font-family: Georgia;");
        Label sub = new Label("Hotel Management System");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #7070a0;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        dateLabel = new Label("📅  " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #a0a0c0;");

        themeBtn = new Button("🌙  Dark Mode");
        themeBtn.setStyle("-fx-background-color: #e0c97f; -fx-text-fill: #1a1a2e;"
                + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 20; -fx-padding: 6 16;");
        themeBtn.setOnAction(e -> {
            isDark = !isDark;
            themeBtn.setText(isDark ? "☀️  Light Mode" : "🌙  Dark Mode");
            applyTheme();
        });

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e0c97f;"
                + "-fx-text-fill: #e0c97f; -fx-cursor: hand; -fx-border-radius: 4;"
                + "-fx-background-radius: 4; -fx-padding: 6 14;");
        logoutBtn.setOnAction(e -> LoginScreen.show(mainStage));

        hdr.getChildren().addAll(logo, new VBox(title, sub), spacer, dateLabel, themeBtn, logoutBtn);
        return hdr;
    }

    // ── APPLY THEME ───────────────────────────────────────────────────────
    private static void applyTheme() {
        String bg     = isDark ? D_BG     : L_BG;
        String card   = isDark ? D_CARD   : L_CARD;
        String text   = isDark ? D_TEXT   : L_TEXT;
        String sub    = isDark ? D_SUB    : L_SUB;
        String border = isDark ? D_BORDER : L_BORDER;
        String table  = isDark ? D_TABLE  : L_TABLE;
        String header = isDark ? D_HEADER : L_HEADER;
        String input  = isDark ? D_INPUT  : L_INPUT;

        if (headerBox != null) headerBox.setStyle("-fx-background-color: " + header + ";");
        tabPane.setStyle(
    "-fx-background-color: " + bg + ";" +
    "-fx-tab-pane-background-color: " + bg + ";"
);

        String contentStyle = "-fx-background-color: " + bg + ";";
        for (VBox v : new VBox[]{dashContent, roomsContent, bookingsContent, billingContent, billRecContent})
            if (v != null) v.setStyle(contentStyle);

        String cardStyle = "-fx-background-color: " + card + "; -fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.15),8,0,0,2);";
        for (VBox c : new VBox[]{cardTotal, cardAvail, cardOcc, cardRev})
            if (c != null) c.setStyle(cardStyle);

        String tableStyle = "-fx-background-color: " + table + "; -fx-control-inner-background: " + table + ";";
        for (TableView<?> t : new TableView[]{dashRecentTable, roomsTable, bookingsTable, billingTable, billRecTable})
            if (t != null) t.setStyle(tableStyle);

        for (Label l : headingLabels)
            l.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + text + "; -fx-font-family: Georgia;");
        for (Label l : subLabels)
            l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + text + ";");
        for (Label l : formLabels)
            l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + sub + ";");

        String fieldStyle = "-fx-background-color: " + input + "; -fx-text-fill: " + text
                + "; -fx-border-color: " + border + "; -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-padding: 6 10; -fx-font-size: 13px;";
        for (TextField tf : allFields) if (tf != null) tf.setStyle(fieldStyle);
        for (TableView<?> t : new TableView[]{dashRecentTable, roomsTable, bookingsTable, billingTable, billRecTable}) {
    if (t != null) {
        t.setStyle(
            "-fx-background-color: " + table + ";" +
            "-fx-control-inner-background: " + table + ";" +
            "-fx-table-cell-border-color: transparent;"
        );
    }
}
        String taStyle = "-fx-font-family: 'Courier New'; -fx-font-size: 13px;"
                + "-fx-control-inner-background: " + (isDark ? D_INPUT : "white")
                + "; -fx-text-fill: " + text + ";";
        if (billArea   != null) billArea.setStyle(taStyle);
        if (detailArea != null) detailArea.setStyle(taStyle);
    }

    // ═════════════════════════════════════════════════════════════════════
    // TAB 1 – DASHBOARD
    // ═════════════════════════════════════════════════════════════════════
    private static Tab buildDashboardTab() {
        Tab tab = new Tab("  📊  Dashboard  ");
        dashContent = new VBox(24);
        dashContent.setPadding(new Insets(28));

        Label heading = h1("Overview");

        statTotal = statNum("0",  "#3498db");
        statAvail = statNum("0",  "#27ae60");
        statOcc   = statNum("0",  "#e67e22");
        statRev   = statNum("₹0", "#8e44ad");

        cardTotal = card(statTotal, "Total Rooms");
        cardAvail = card(statAvail, "Available");
        cardOcc   = card(statOcc,   "Occupied");
        cardRev   = card(statRev,   "Total Revenue");
        HBox cards = new HBox(16, cardTotal, cardAvail, cardOcc, cardRev);

        Label recLbl = h2("Recent Bookings");
        dashRecentTable = bookingTable();
        dashRecentTable.setItems(bookingList);
        dashRecentTable.setPrefHeight(280);

        dashContent.getChildren().addAll(heading, cards, recLbl, dashRecentTable);
        tab.setOnSelectionChanged(e -> { if (tab.isSelected()) { refreshStats(); applyTheme(); } });
        refreshStats();
        tab.setContent(scroll(dashContent));
        return tab;
    }
    
    private static void refreshStats() {
        if (statTotal == null) return;
        statTotal.setText(String.valueOf(service.totalRooms()));
        statAvail.setText(String.valueOf(service.availableRooms()));
        statOcc.setText(String.valueOf(service.occupiedRooms()));
        statRev.setText("₹" + String.format("%,.0f", service.totalRevenue()));
        roomList.setAll(service.getAllRooms());
        bookingList.setAll(service.getAllBookings());
    }

    // ═════════════════════════════════════════════════════════════════════
    // TAB 2 – ROOMS
    // ═════════════════════════════════════════════════════════════════════
    private static Tab buildRoomsTab() {
        Tab tab = new Tab("  🛏  Rooms  ");
        roomsContent = new VBox(20);
        roomsContent.setPadding(new Insets(24));

        Label heading = h1("Room Management");

        TitledPane addPane = new TitledPane("  ➕  Add New Room", null);
        addPane.setExpanded(false);
        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10); form.setPadding(new Insets(16));

        TextField numF   = fld("e.g. 303");
        ComboBox<String> typeC = new ComboBox<>();
        typeC.getItems().addAll("Single", "Double", "Suite"); typeC.setValue("Single");
        TextField priceF = fld("e.g. 2500");

        form.add(lbl("Room No:"),    0, 0); form.add(numF,   1, 0);
        form.add(lbl("Type:"),       0, 1); form.add(typeC,  1, 1);
        form.add(lbl("Price/Day:"),  0, 2); form.add(priceF, 1, 2);

        Label addMsg = new Label(); formLabels.add(addMsg);
        Button addBtn = new Button("Add Room"); btn(addBtn, "#27ae60");
        addBtn.setOnAction(e -> {
            try {
                int no = Integer.parseInt(numF.getText().trim());
                double pr = Double.parseDouble(priceF.getText().trim());
                if (service.addRoom(no, typeC.getValue(), pr)) {
                    addMsg.setStyle("-fx-text-fill: #27ae60;");
                    addMsg.setText("✔ Room " + no + " added.");
                    refreshStats(); applyTheme(); numF.clear(); priceF.clear();
                } else { addMsg.setStyle("-fx-text-fill:#e74c3c;"); addMsg.setText("✘ Room already exists."); }
            } catch (NumberFormatException ex) { addMsg.setStyle("-fx-text-fill:#e74c3c;"); addMsg.setText("✘ Invalid numbers."); }
        });
        form.add(addBtn, 0, 3, 2, 1); form.add(addMsg, 0, 4, 2, 1);
        addPane.setContent(form);

        roomsTable = new TableView<>(); roomsTable.setItems(roomList); roomsTable.setPrefHeight(380);

        TableColumn<Room,Integer> rc1=new TableColumn<>("Room No");  rc1.setCellValueFactory(new PropertyValueFactory<>("roomNumber")); rc1.setPrefWidth(100);
        TableColumn<Room,String>  rc2=new TableColumn<>("Type");     rc2.setCellValueFactory(new PropertyValueFactory<>("type"));       rc2.setPrefWidth(120);
        TableColumn<Room,Double>  rc3=new TableColumn<>("₹/Day");    rc3.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));rc3.setPrefWidth(120);
        rc3.setCellFactory(c->new TableCell<>(){@Override protected void updateItem(Double v,boolean e){super.updateItem(v,e);setText(e||v==null?null:String.format("₹%.0f",v));}});
        TableColumn<Room,Boolean> rc4=new TableColumn<>("Status");   rc4.setCellValueFactory(new PropertyValueFactory<>("available"));  rc4.setPrefWidth(130);
        rc4.setCellFactory(c->new TableCell<>(){@Override protected void updateItem(Boolean v,boolean e){super.updateItem(v,e);if(e||v==null){setText(null);return;}setText(v?"✅ Available":"🔴 Occupied");setStyle(v?"-fx-text-fill:#27ae60;":"-fx-text-fill:#e74c3c;");}});
        TableColumn<Room,Void> rdel=new TableColumn<>("Action"); rdel.setPrefWidth(100);
        rdel.setCellFactory(c->new TableCell<>(){final Button b=new Button("Delete");{btn(b,"#e74c3c");b.setOnAction(ev->{Room r=getTableView().getItems().get(getIndex());if(service.deleteRoom(r.getRoomNumber())){refreshStats();applyTheme();}else showAlert("Cannot Delete","Room is occupied.");});}@Override protected void updateItem(Void v,boolean e){super.updateItem(v,e);setGraphic(e?null:b);}});
        roomsTable.getColumns().addAll(rc1,rc2,rc3,rc4,rdel);

        tab.setOnSelectionChanged(e->{if(tab.isSelected()){refreshStats();applyTheme();}});
        roomsContent.getChildren().addAll(heading, addPane, roomsTable);
        tab.setContent(scroll(roomsContent));
        return tab;
    }

    // ═════════════════════════════════════════════════════════════════════
    // TAB 3 – BOOKINGS
    // ═════════════════════════════════════════════════════════════════════
    private static Tab buildBookingsTab() {
        Tab tab = new Tab("  📋  Bookings  ");
        bookingsContent = new VBox(20); bookingsContent.setPadding(new Insets(24));
        Label heading = h1("Booking Management");

        TitledPane bookPane = new TitledPane("  📝  Book a Room", null);
        GridPane form = new GridPane(); form.setHgap(12); form.setVgap(10); form.setPadding(new Insets(16));
        TextField nameF=fld("Customer Full Name"), phoneF=fld("Phone Number");
        ComboBox<Integer> roomC=new ComboBox<>(); roomC.setPromptText("Select room");
        roomC.setOnShowing(e->roomC.getItems().setAll(service.getAvailableRooms().stream().map(Room::getRoomNumber).toList()));
        roomC.getItems().setAll(service.getAvailableRooms().stream().map(Room::getRoomNumber).toList());
        DatePicker ci=new DatePicker(LocalDate.now()), co=new DatePicker(LocalDate.now().plusDays(1));
        form.add(lbl("Customer Name:"),0,0); form.add(nameF,1,0);
        form.add(lbl("Phone:"),0,1);         form.add(phoneF,1,1);
        form.add(lbl("Room No:"),0,2);       form.add(roomC,1,2);
        form.add(lbl("Check-In:"),0,3);      form.add(ci,1,3);
        form.add(lbl("Check-Out:"),0,4);     form.add(co,1,4);
        Label bookMsg=new Label(); Button bookBtn=new Button("✔  Confirm Booking"); btn(bookBtn,"#1a1a2e");
        bookBtn.setOnAction(e->{
            try {
                if(nameF.getText().isBlank()||phoneF.getText().isBlank()||roomC.getValue()==null){bookMsg.setStyle("-fx-text-fill:#e74c3c;");bookMsg.setText("✘ Fill all fields.");return;}
                Booking b=service.bookRoom(nameF.getText().trim(),phoneF.getText().trim(),roomC.getValue(),ci.getValue(),co.getValue());
                bookMsg.setStyle("-fx-text-fill:#27ae60;"); bookMsg.setText("✔ Booking #"+b.getBookingId()+" confirmed! Bill: ₹"+String.format("%.2f",b.getTotalBill()));
                nameF.clear(); phoneF.clear(); roomC.getItems().setAll(service.getAvailableRooms().stream().map(Room::getRoomNumber).toList());
                refreshStats(); applyTheme();
            } catch(Exception ex){bookMsg.setStyle("-fx-text-fill:#e74c3c;");bookMsg.setText("✘ "+ex.getMessage());}
        });
        form.add(bookBtn,0,5,2,1); form.add(bookMsg,0,6,2,1);
        bookPane.setContent(form);

        

        // ── Cancel Booking panel ──────────────────────────────────────────
        TitledPane cancelPane = new TitledPane("  ❌  Cancel a Booking", null);
        cancelPane.setExpanded(false);
        VBox cancelBox = new VBox(12);
        cancelBox.setPadding(new Insets(16));

        Label cancelInfo = new Label("Select an active booking to cancel. The room will be freed immediately.");
        cancelInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        ComboBox<String> cancelCombo = new ComboBox<>();
        cancelCombo.setPromptText("Select Active Booking");
        cancelCombo.setPrefWidth(420);

        Runnable refreshCancelCombo = () -> cancelCombo.getItems().setAll(
            service.getActiveBookings().stream()
                .map(b -> "#" + b.getBookingId() + "  –  " + b.getCustomerName()
                          + "  (Room " + b.getRoomNumber() + ")")
                .toList()
        );
        cancelPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) refreshCancelCombo.run();
        });
        cancelCombo.setOnShowing(e -> refreshCancelCombo.run());

        Label cancelMsg = new Label();
        Button cancelBtn = new Button("❌  Cancel Booking");
        btn(cancelBtn, "#c0392b");

        cancelBtn.setOnAction(e -> {
            String selected = cancelCombo.getValue();
            if (selected == null) {
                cancelMsg.setStyle("-fx-text-fill:#e74c3c;");
                cancelMsg.setText("✘ Please select a booking to cancel.");
                return;
            }
            try {
                int id = Integer.parseInt(selected.substring(1, selected.indexOf("  –")).trim());
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Cancel Booking");
                confirm.setHeaderText(null);
                confirm.setContentText("Are you sure you want to cancel booking " + selected + "?\nThis cannot be undone.");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Booking cancelled = service.cancelBooking(id);
                            cancelMsg.setStyle("-fx-text-fill:#27ae60;");
                            cancelMsg.setText("✔ Booking #" + cancelled.getBookingId()
                                + " for " + cancelled.getCustomerName() + " cancelled. Room freed.");
                            cancelCombo.getItems().clear();
                            cancelCombo.setValue(null);
                            refreshStats();
                            applyTheme();
                        } catch (Exception ex) {
                            cancelMsg.setStyle("-fx-text-fill:#e74c3c;");
                            cancelMsg.setText("✘ " + ex.getMessage());
                        }
                    }
                });
            } catch (NumberFormatException ex) {
                cancelMsg.setStyle("-fx-text-fill:#e74c3c;");
                cancelMsg.setText("✘ Could not parse booking ID.");
            }
        });

        cancelBox.getChildren().addAll(cancelInfo, cancelCombo, cancelBtn, cancelMsg);
        cancelPane.setContent(cancelBox);

        Label tLbl=h2("All Bookings");
        bookingsTable=bookingTable(); bookingsTable.setItems(bookingList); bookingsTable.setPrefHeight(300);
        tab.setOnSelectionChanged(e->{if(tab.isSelected()){refreshStats();applyTheme();}});
        bookingsContent.getChildren().addAll(heading, bookPane, cancelPane, tLbl, bookingsTable);
        tab.setContent(scroll(bookingsContent));
        return tab;
    }

    // ═════════════════════════════════════════════════════════════════════
    // TAB 4 – BILLING (Generate Invoice)
    // ═════════════════════════════════════════════════════════════════════
    private static Tab buildBillingTab() {
        Tab tab = new Tab("  💰  Billing  ");
        billingContent = new VBox(20); billingContent.setPadding(new Insets(24));
        Label heading = h1("Billing & Invoice Generator");

        HBox searchBox = new HBox(12); searchBox.setAlignment(Pos.CENTER_LEFT);
        TextField searchF = fld("Enter Booking ID"); searchF.setPrefWidth(200);
        Button searchBtn=new Button("🧾  Generate Invoice"); btn(searchBtn,"#1a1a2e");
        Button clearBtn=new Button("Clear"); btn(clearBtn,"#666");
        searchBox.getChildren().addAll(lbl("Booking ID:"),searchF,searchBtn,clearBtn);

        billArea = new TextArea(); billArea.setPrefHeight(260); billArea.setEditable(false);
        billArea.setPromptText("Invoice will appear here. Click a row below or enter a Booking ID.");

        searchBtn.setOnAction(e->{
            try{int id=Integer.parseInt(searchF.getText().trim());
                service.getAllBookings().stream().filter(b->b.getBookingId()==id).findFirst()
                    .ifPresentOrElse(b -> {
                        if ("CANCELLED".equals(b.getStatus()))
                            billArea.setText("⚠ Booking #" + id + " was cancelled. No invoice available.");
                        else
                            billArea.setText(generateBill(b));
                    }, ()->billArea.setText("⚠ No booking found with ID: "+id));
            }catch(NumberFormatException ex){billArea.setText("⚠ Enter a valid numeric Booking ID.");}
        });
        searchF.setOnAction(e->searchBtn.fire());
        clearBtn.setOnAction(e->{billArea.clear();searchF.clear();});

        Label sumLbl=h2("All Billing Records — click a row to preview invoice");
        billingTable=bookingTable(); billingTable.setItems(bookingList); billingTable.setPrefHeight(240);
        billingTable.setOnMouseClicked(e->{
            Booking sel=billingTable.getSelectionModel().getSelectedItem();
            if(sel!=null){
                searchF.setText(String.valueOf(sel.getBookingId()));
                if ("CANCELLED".equals(sel.getStatus()))
                    billArea.setText("⚠ Booking #" + sel.getBookingId() + " was cancelled. No invoice available.");
                else
                    billArea.setText(generateBill(sel));
            }
        });

        tab.setOnSelectionChanged(e->{if(tab.isSelected()){refreshStats();applyTheme();}});
        billingContent.getChildren().addAll(heading,searchBox,billArea,sumLbl,billingTable);
        tab.setContent(scroll(billingContent));
        return tab;
    }
    
   private static Tab buildCheckoutTab() {
    Tab tab = new Tab("  🔑  Checkout  ");

    VBox content = new VBox(25);
    content.setPadding(new Insets(30));

    Label heading = h1("Room Checkout");

    VBox card = new VBox(15);
    card.setPadding(new Insets(20));
    card.setStyle(
        "-fx-background-color: " + (isDark ? "#1e1e30" : "white") + ";" +
        "-fx-background-radius: 12;" +
        "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.2),10,0,0,3);"
    );

    Label info = new Label("Select an occupied room to checkout");
    info.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

    ComboBox<Integer> roomBox = new ComboBox<>();
    roomBox.setPromptText("Choose Room");

    roomBox.setOnShowing(e -> roomBox.getItems().setAll(
        service.getAllRooms().stream()
            .filter(r -> !r.isAvailable())
            .map(Room::getRoomNumber)
            .toList()
    ));

    // 💳 Payment method
    ComboBox<String> paymentBox = new ComboBox<>();
    paymentBox.getItems().addAll("Cash", "UPI", "Card");
    paymentBox.setPromptText("Select Payment Method");

    Label result = new Label();

    Button checkoutBtn = new Button("🔑 Checkout Room");
    btn(checkoutBtn, "#e67e22");

    checkoutBtn.setOnAction(e -> {
        if (roomBox.getValue() == null) {
            result.setStyle("-fx-text-fill:#e74c3c;");
            result.setText("❌ Please select a room.");
            return;
        }

        if (paymentBox.getValue() == null) {
            result.setStyle("-fx-text-fill:#e74c3c;");
            result.setText("❌ Select payment method.");
            return;
        }

        try {
            Booking b = service.checkout(roomBox.getValue());

            result.setStyle("-fx-text-fill:#27ae60;");
            result.setText("✅ Checkout successful!");

            // 🧾 Show invoice popup
            showInvoicePopup(b, paymentBox.getValue());

            roomBox.getItems().clear();
            paymentBox.getSelectionModel().clearSelection();

            refreshStats();
            applyTheme();

        } catch (Exception ex) {
            result.setStyle("-fx-text-fill:#e74c3c;");
            result.setText("❌ " + ex.getMessage());
        }
    });

    card.getChildren().addAll(info, roomBox, paymentBox, checkoutBtn, result);
    content.getChildren().addAll(heading, card);

    tab.setContent(scroll(content));
    return tab;
}
    private static void showInvoicePopup(Booking b, String paymentMethod) {

    Stage popup = new Stage();
    popup.setTitle("Invoice");

    TextArea invoice = new TextArea();
    invoice.setEditable(false);

    invoice.setText(
        generateBill(b) +
        "\n\nPayment Method: " + paymentMethod +
        "\nStatus: PAID ✅"
    );

    Button closeBtn = new Button("Close");
    btn(closeBtn, "#1a1a2e");
    closeBtn.setOnAction(e -> popup.close());

    VBox layout = new VBox(15, invoice, closeBtn);
    layout.setPadding(new Insets(20));

    Scene scene = new Scene(layout, 500, 500);
    popup.setScene(scene);
    popup.show();
}
    // ═════════════════════════════════════════════════════════════════════
    // TAB 5 – BILL RECORDS (history + stats + search)
    // ═════════════════════════════════════════════════════════════════════
    private static Tab buildBillRecordsTab() {
        Tab tab = new Tab("  🗂  Bill Records  ");
        billRecContent = new VBox(20); billRecContent.setPadding(new Insets(24));
        Label heading = h1("Bill Records & Revenue Summary");

        // Revenue stat cards
        Label cntLbl=statNum("0","#3498db"), revLbl=statNum("₹0","#8e44ad"),
              avgLbl=statNum("₹0","#27ae60"), maxLbl=statNum("₹0","#e74c3c");
        VBox cCnt=card(cntLbl,"Total Bills"), cRev=card(revLbl,"Total Revenue"),
             cAvg=card(avgLbl,"Avg Bill"),    cMax=card(maxLbl,"Highest Bill");
        HBox revCards = new HBox(16, cCnt, cRev, cAvg, cMax);

        // Filter bar
        HBox filterBox=new HBox(12); filterBox.setAlignment(Pos.CENTER_LEFT);
        TextField filterF=fld("Search name / phone / room / ID"); filterF.setPrefWidth(280);
        Button filterBtn=new Button("🔍  Search"); btn(filterBtn,"#3498db");
        Button resetBtn=new Button("Reset");       btn(resetBtn,"#666");
        filterBox.getChildren().addAll(lbl("Filter:"),filterF,filterBtn,resetBtn);

        // Table
        Label tLbl = h2("Complete Billing History");
        ObservableList<Booking> filtered = FXCollections.observableArrayList(service.getAllBookings());
        billRecTable = billRecordTable();
        billRecTable.setItems(filtered); billRecTable.setPrefHeight(300);

        // Detail panel
        Label detLbl = h2("Selected Invoice");
        detailArea = new TextArea(); detailArea.setPrefHeight(220); detailArea.setEditable(false);
        detailArea.setPromptText("Click a row above to view full invoice here...");
        billRecTable.setOnMouseClicked(e->{
            Booking sel=billRecTable.getSelectionModel().getSelectedItem();
            if(sel!=null) {
                if ("CANCELLED".equals(sel.getStatus()))
                    detailArea.setText("⚠ Booking #" + sel.getBookingId() + " was cancelled. No invoice available.");
                else
                    detailArea.setText(generateBill(sel));
            }
        });

        // Filter logic
        Runnable doFilter = ()->{
            String q=filterF.getText().trim().toLowerCase();
            List<Booking> all=service.getAllBookings();
            filtered.setAll(q.isBlank() ? all : all.stream().filter(b->
                b.getCustomerName().toLowerCase().contains(q)||
                b.getPhone().contains(q)||
                String.valueOf(b.getRoomNumber()).contains(q)||
                String.valueOf(b.getBookingId()).contains(q)).toList());
            updateBillStats(cntLbl,revLbl,avgLbl,maxLbl,filtered);
        };
        filterBtn.setOnAction(e->doFilter.run());
        filterF.setOnAction(e->doFilter.run());
        resetBtn.setOnAction(e->{filterF.clear();doFilter.run();});

        tab.setOnSelectionChanged(e->{
            if(tab.isSelected()){
                filterF.clear(); filtered.setAll(service.getAllBookings());
                updateBillStats(cntLbl,revLbl,avgLbl,maxLbl,filtered);
                applyTheme();
            }
        });
        updateBillStats(cntLbl,revLbl,avgLbl,maxLbl,filtered);

        billRecContent.getChildren().addAll(heading,revCards,filterBox,tLbl,billRecTable,detLbl,detailArea);
        tab.setContent(scroll(billRecContent));
        return tab;
    }

    private static void updateBillStats(Label cnt,Label rev,Label avg,Label max,List<Booking> list){
        int n=list.size();
        double tot=list.stream().filter(b -> !"CANCELLED".equals(b.getStatus())).mapToDouble(Booking::getTotalBill).sum();
        long nonCancelled = list.stream().filter(b -> !"CANCELLED".equals(b.getStatus())).count();
        cnt.setText(String.valueOf(n));
        rev.setText("₹"+String.format("%,.0f",tot));
        avg.setText("₹"+String.format("%,.0f",nonCancelled>0?tot/nonCancelled:0));
        max.setText("₹"+String.format("%,.0f",list.stream().filter(b -> !"CANCELLED".equals(b.getStatus())).mapToDouble(Booking::getTotalBill).max().orElse(0)));
    }
    
    
    // ── Bill Records table (extra columns) ────────────────────────────────
    @SuppressWarnings("unchecked")
    private static TableView<Booking> billRecordTable(){
        TableView<Booking> t=new TableView<>();
        TableColumn<Booking,Integer>   c1=new TableColumn<>("#ID");    c1.setCellValueFactory(new PropertyValueFactory<>("bookingId"));    c1.setPrefWidth(55);
        TableColumn<Booking,String>    c2=new TableColumn<>("Customer");c2.setCellValueFactory(new PropertyValueFactory<>("customerName")); c2.setPrefWidth(150);
        TableColumn<Booking,String>    c3=new TableColumn<>("Phone");   c3.setCellValueFactory(new PropertyValueFactory<>("phone"));        c3.setPrefWidth(120);
        TableColumn<Booking,Integer>   c4=new TableColumn<>("Room");    c4.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));   c4.setPrefWidth(65);
        TableColumn<Booking,LocalDate> c5=new TableColumn<>("Check-In");c5.setCellValueFactory(new PropertyValueFactory<>("checkIn"));     c5.setPrefWidth(105);
        TableColumn<Booking,LocalDate> c6=new TableColumn<>("Check-Out");c6.setCellValueFactory(new PropertyValueFactory<>("checkOut"));   c6.setPrefWidth(105);
        TableColumn<Booking,Long>      c7=new TableColumn<>("Days");    c7.setCellValueFactory(new PropertyValueFactory<>("days"));        c7.setPrefWidth(55);
        TableColumn<Booking,Double>    c8=new TableColumn<>("Total Bill");c8.setCellValueFactory(new PropertyValueFactory<>("totalBill")); c8.setPrefWidth(115);
        c8.setCellFactory(col->new TableCell<>(){@Override protected void updateItem(Double v,boolean e){super.updateItem(v,e);if(e||v==null){setText(null);setStyle("");return;}setText(String.format("₹%.2f",v));setStyle(v>5000?"-fx-text-fill:#8e44ad;-fx-font-weight:bold;":"-fx-text-fill:#27ae60;");}});
        TableColumn<Booking,String>    c9=new TableColumn<>("Status");  c9.setPrefWidth(100);
        // NEW (correct) — reads actual status field from the Booking object
c9.setCellValueFactory(new PropertyValueFactory<>("status"));
c9.setCellFactory(col -> new TableCell<>() {
    protected void updateItem(String v, boolean e) {
        super.updateItem(v, e);
        if (e || v == null) { setText(null); return; }
        switch (v) {
            case "COMPLETED" -> { setText("✅ Completed"); setStyle("-fx-text-fill:#27ae60;"); }
            case "CANCELLED" -> { setText("🚫 Cancelled"); setStyle("-fx-text-fill:#e74c3c;"); }
            default          -> { setText("🟡 Active");    setStyle("-fx-text-fill:#e67e22;"); }
        }
    }
});      
        t.getColumns().addAll(c1,c2,c3,c4,c5,c6,c7,c8,c9);
        return t;
    }

    // ── Shared booking table ───────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private static TableView<Booking> bookingTable(){
        TableView<Booking> t=new TableView<>();
        TableColumn<Booking,Integer>   c1=new TableColumn<>("ID");     c1.setCellValueFactory(new PropertyValueFactory<>("bookingId"));    c1.setPrefWidth(55);
        TableColumn<Booking,String>    c2=new TableColumn<>("Customer");c2.setCellValueFactory(new PropertyValueFactory<>("customerName")); c2.setPrefWidth(150);
        TableColumn<Booking,String>    c3=new TableColumn<>("Phone");   c3.setCellValueFactory(new PropertyValueFactory<>("phone"));        c3.setPrefWidth(120);
        TableColumn<Booking,Integer>   c4=new TableColumn<>("Room");    c4.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));   c4.setPrefWidth(65);
        TableColumn<Booking,LocalDate> c5=new TableColumn<>("Check-In");c5.setCellValueFactory(new PropertyValueFactory<>("checkIn"));     c5.setPrefWidth(105);
        TableColumn<Booking,LocalDate> c6=new TableColumn<>("Check-Out");c6.setCellValueFactory(new PropertyValueFactory<>("checkOut"));   c6.setPrefWidth(105);
        TableColumn<Booking,Double>    c7=new TableColumn<>("Bill (₹)");c7.setCellValueFactory(new PropertyValueFactory<>("totalBill"));   c7.setPrefWidth(100);
        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
statusCol.setPrefWidth(120);
statusCol.setCellFactory(col -> new TableCell<>() {
    protected void updateItem(String v, boolean e) {
        super.updateItem(v, e);
        if (e || v == null) { setText(null); setStyle(""); return; }
        switch (v) {
            case "COMPLETED" -> { setText("✅ Completed"); setStyle("-fx-text-fill:#27ae60;"); }
            case "CANCELLED" -> { setText("🚫 Cancelled"); setStyle("-fx-text-fill:#e74c3c;"); }
            default          -> { setText("🟡 Active");    setStyle("-fx-text-fill:#e67e22;"); }
        }
    }
});
        c7.setCellFactory(c->new TableCell<>(){@Override protected void updateItem(Double v,boolean e){super.updateItem(v,e);setText(e||v==null?null:String.format("₹%.2f",v));}});
        t.getColumns().addAll(c1,c2,c3,c4,c5,c6,c7,statusCol);
        return t;
    }
    
    // ── Invoice text ──────────────────────────────────────────────────────
    private static String generateBill(Booking b){
        DateTimeFormatter fmt=DateTimeFormatter.ofPattern("dd MMM yyyy");
        double rate=b.getDays()>0?b.getTotalBill()/b.getDays():0;
        return  "╔══════════════════════════════════════════╗\n"
              + "║         GRAND HORIZON HOTEL              ║\n"
              + "║           OFFICIAL INVOICE               ║\n"
              + "╠══════════════════════════════════════════╣\n"
              + String.format("  Booking ID    : #%d%n",         b.getBookingId())
              + String.format("  Customer      : %s%n",          b.getCustomerName())
              + String.format("  Phone         : %s%n",          b.getPhone())
              + String.format("  Room No       : %d%n",          b.getRoomNumber())
              + String.format("  Check-In      : %s%n",          b.getCheckIn().format(fmt))
              + String.format("  Check-Out     : %s%n",          b.getCheckOut().format(fmt))
              + String.format("  Days Stayed   : %d%n",          b.getDays())
              + String.format("  Rate / Day    : ₹%.2f%n",       rate)
              + "  ──────────────────────────────────────\n"
              + String.format("  Sub Total     : ₹%.2f%n",       b.getTotalBill())
              + "  Tax (0%)       : ₹0.00\n"
              + "  ──────────────────────────────────────\n"
              + String.format("  TOTAL BILL    : ₹%.2f%n",       b.getTotalBill())
              + "╚══════════════════════════════════════════╝\n"
              + "  Thank you for staying with us! 🌟\n"
              + "  Grand Horizon Hotel  •  www.grandhorizon.in";
    }

    // ── Tiny builder helpers ───────────────────────────────────────────────
    private static Label h1(String t){ Label l=new Label(t); headingLabels.add(l); return l; }
    private static Label h2(String t){ Label l=new Label(t); subLabels.add(l);    return l; }
    private static Label lbl(String t){ Label l=new Label(t); formLabels.add(l);  return l; }
    private static TextField fld(String p){ TextField f=new TextField(); f.setPromptText(p); allFields.add(f); return f; }
    private static Label statNum(String v,String c){ Label l=new Label(v); l.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:"+c+";"); return l; }
    private static VBox card(Label num,String name){ Label n=new Label(name); n.setStyle("-fx-font-size:12px;-fx-text-fill:#888;"); formLabels.add(n); VBox c=new VBox(4,num,n); c.setPadding(new Insets(18,24,18,24)); c.setPrefWidth(185); return c; }
    private static void btn(Button b,String bg){ b.setStyle("-fx-background-color:"+bg+";-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;-fx-background-radius:6;-fx-padding:8 18;"); b.setOnMouseEntered(e->b.setOpacity(0.82)); b.setOnMouseExited(e->b.setOpacity(1)); }
    private static ScrollPane scroll(VBox v){
    ScrollPane s = new ScrollPane(v);
    s.setFitToWidth(true);

    s.setStyle(
        "-fx-background: transparent;" +
        "-fx-background-color: transparent;" +
        "-fx-control-inner-background: transparent;"
    );

    v.setStyle("-fx-background-color: transparent;");

    return s;
}
    private static void showAlert(String t,String m){ Alert a=new Alert(Alert.AlertType.WARNING); a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait(); }
}