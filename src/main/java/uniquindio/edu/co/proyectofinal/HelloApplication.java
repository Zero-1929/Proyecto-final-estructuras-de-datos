package uniquindio.edu.co.proyectofinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import java.nio.file.Files;
import java.nio.file.Path;

/** Punto de entrada de la interfaz principal del SGA. */
public class HelloApplication extends Application {
    @Override public void start(Stage stage) throws Exception {
        Path ruta = SistemaSGA.rutaPorDefecto();
        SistemaSGA sistema = Files.exists(ruta) ? SistemaSGA.cargar(ruta) : SistemaSGA.crearVacio();
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("VentanaPrincipal.fxml"));
        Scene scene = new Scene(loader.load());
        VentanaPrincipalController controlador = loader.getController();
        controlador.setSistema(sistema);
        stage.setTitle("Aparta-Suites UQ · Sistema de Gestión");
        stage.setScene(scene);
        stage.setMinWidth(1050);
        stage.setMinHeight(650);
        stage.show();
    }
}
