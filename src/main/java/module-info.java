module uniquindio.edu.co.proyectofinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens uniquindio.edu.co.proyectofinal to javafx.fxml;
    opens uniquindio.edu.co.proyectofinal.modelo to com.google.gson;
    opens uniquindio.edu.co.proyectofinal.persistencia to com.google.gson;
    exports uniquindio.edu.co.proyectofinal;
}