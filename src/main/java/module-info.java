module org.example.cs203pz {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.cs203pz to javafx.fxml;
    exports org.example.cs203pz;
}