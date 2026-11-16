package uniquindio.edu.co.proyectofinal;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.EstadoApartamento;
import java.util.Locale;

/** Controlador del tablero principal, conectado a los apartamentos reales del SGA. */
public class VentanaPrincipalController {
    @FXML private Button btnCambiarRol;
    @FXML private Label lblTotalApartamentos, lblPreparados, lblOcupados,
            lblEnPreparacion, lblFueraServicio, lblDetalleCodigo, lblDetalleBloque,
            lblDetallePiso, lblDetalleCapacidad, lblDetalleEstado, lblMotivoFueraServicio;
    @FXML private TextField txtBuscarApartamento;
    @FXML private ComboBox<String> cmbFiltrarEstado, cmbFiltrarBloque;
    @FXML private FlowPane panelApartamentos;
    @FXML private Button btnActualizarTablero, btnLimpiarFiltros, btnMenuInicio,
            btnMenuReservas, btnMenuCalendario, btnMenuUsuario, btnMenuPreparacion,
            btnMenuMapa, btnMenuEstadisticas, btnMenuCargaDatos;

    private SistemaSGA sistema;
    private Apartamento seleccionado;

    @FXML private void initialize() {
        cmbFiltrarEstado.getItems().add("Todos");
        for (EstadoApartamento estado : EstadoApartamento.values())
            cmbFiltrarEstado.getItems().add(estado.name());
        cmbFiltrarEstado.setValue("Todos");
        cmbFiltrarBloque.getItems().add("Todos");
        cmbFiltrarBloque.setValue("Todos");
        txtBuscarApartamento.textProperty().addListener((obs, a, b) -> actualizarTablero());
        cmbFiltrarEstado.valueProperty().addListener((obs, a, b) -> actualizarTablero());
        cmbFiltrarBloque.valueProperty().addListener((obs, a, b) -> actualizarTablero());
        btnActualizarTablero.setOnAction(e -> actualizarTablero());
        btnLimpiarFiltros.setOnAction(e -> {
            txtBuscarApartamento.clear();
            cmbFiltrarEstado.setValue("Todos");
            cmbFiltrarBloque.setValue("Todos");
            actualizarTablero();
        });
        btnMenuInicio.setOnAction(e -> actualizarTablero());
        btnMenuReservas.setOnAction(e -> abrirReservas());
        btnMenuCalendario.setOnAction(e -> abrirCalendario());
        btnMenuUsuario.setOnAction(e -> abrirUsuario());
        btnMenuPreparacion.setOnAction(e -> abrirPreparacion());
        btnMenuMapa.setOnAction(e -> abrirMapa());
        for (Button boton : new Button[]{btnMenuEstadisticas, btnMenuCargaDatos}) {
            boton.setOnAction(e -> avisoPendiente(boton.getText()));
        }
        actualizarTablero();
    }

    /** La aplicación inyecta la instancia compartida, no crea otro sistema. */
    public void setSistema(SistemaSGA sistema) {
        btnCambiarRol.setOnAction(e -> {
            try {
                sistema.guardar();
                SesionActual.cerrar();
                new SeleccionPerfil(sistema,(javafx.stage.Stage)btnCambiarRol.getScene().getWindow()).mostrar();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,"No se pudo cambiar de rol: "+ex.getMessage()).showAndWait();
            }
        });
        SesionActual.exigirPersonal();
        SesionActual.ocultarSoloAdministrador(btnMenuEstadisticas, btnMenuCargaDatos);
        if (btnMenuEstadisticas != null) btnMenuEstadisticas.setOnAction(event -> {
            try {
                javafx.fxml.FXMLLoader statsLoader = new javafx.fxml.FXMLLoader(getClass().getResource("VistaEstadisticas.fxml"));
                javafx.scene.Scene statsScene = new javafx.scene.Scene(statsLoader.load());
                ((VistaEstadisticasController) statsLoader.getController()).setSistema(this.sistema);
                ((javafx.stage.Stage) btnMenuEstadisticas.getScene().getWindow()).setScene(statsScene);
            } catch (Exception ex) {
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                        "No se pudo abrir Estadísticas: " + ex.getMessage()).showAndWait();
            }
        });

        btnMenuCargaDatos.setText("Gestión administrativa");
        btnMenuCargaDatos.setOnAction(event -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("VistaGestionAdministrativa.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                ((VistaGestionAdministrativaController) loader.getController()).setSistema(this.sistema);
                ((javafx.stage.Stage) btnMenuCargaDatos.getScene().getWindow()).setScene(scene);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "No se pudo abrir Gestión administrativa: " + ex.getMessage()).showAndWait();
            }
        });

        if (sistema == null) throw new IllegalArgumentException("Sistema requerido");
        this.sistema = sistema;
        actualizarTablero();
    }

    private void abrirMapa() {
        try {
            javafx.fxml.FXMLLoader loader=new javafx.fxml.FXMLLoader(getClass().getResource("VistaMapaInteractivo.fxml"));
            javafx.scene.Scene scene=new javafx.scene.Scene(loader.load());
            ((VistaMapaInteractivoController)loader.getController()).setSistema(sistema);
            ((javafx.stage.Stage)btnMenuMapa.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,ex.toString()).showAndWait();}
    }

    private void abrirPreparacion() {
        try {
            javafx.fxml.FXMLLoader loader=new javafx.fxml.FXMLLoader(getClass().getResource("VistaPanelPreparacion.fxml"));
            javafx.scene.Scene scene=new javafx.scene.Scene(loader.load());
            ((VistaPanelPreparacionController)loader.getController()).setSistema(sistema);
            ((javafx.stage.Stage)btnMenuPreparacion.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,ex.toString()).showAndWait();}
    }

    private void abrirUsuario() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("VistaPanelUsuario.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            ((VistaPanelUsuarioController)loader.getController()).setSistema(sistema);
            ((javafx.stage.Stage)btnMenuUsuario.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,ex.toString()).showAndWait();
        }
    }

    private void abrirCalendario() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("VistaCalendarioOcupacion.fxml"));
            javafx.scene.Scene escena = new javafx.scene.Scene(loader.load());
            ((VistaCalendarioOcupacionController)loader.getController()).setSistema(sistema);
            ((javafx.stage.Stage)btnMenuCalendario.getScene().getWindow()).setScene(escena);
        } catch (Exception ex) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "No se pudo abrir calendario: " + ex.getMessage()).showAndWait();
        }
    }

    private void abrirReservas() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("VistaReservas.fxml"));
            javafx.scene.Scene escena = new javafx.scene.Scene(loader.load());
            VistaReservasController controller = loader.getController();
            controller.setSistema(sistema);
            javafx.stage.Stage ventana = (javafx.stage.Stage) btnMenuReservas.getScene().getWindow();
            ventana.setScene(escena);
        } catch (Exception ex) {
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    ex.getMessage() == null ? ex.toString() : ex.getMessage());
            alerta.setTitle("Error al abrir reservas");
            alerta.showAndWait();
        }
    }

    private void avisoPendiente(String nombre) {
        Alert aviso = new Alert(Alert.AlertType.INFORMATION);
        aviso.setTitle("Módulo pendiente");
        aviso.setHeaderText(nombre);
        aviso.setContentText("Esta sección tendrá su propia ventana y controlador. "
                + "El tablero principal ya está conectado al SGA.");
        aviso.showAndWait();
    }

    public void actualizarTablero() {
        panelApartamentos.getChildren().clear();
        if (sistema == null) {
            lblTotalApartamentos.setText("0");
            lblPreparados.setText("0");
            lblOcupados.setText("0");
            lblEnPreparacion.setText("0");
            lblFueraServicio.setText("0");
            return;
        }
        int total=0, preparados=0, ocupados=0, preparando=0, fuera=0;
        String filtro = txtBuscarApartamento.getText() == null ? ""
                : txtBuscarApartamento.getText().strip().toLowerCase(Locale.ROOT);
        String estado = cmbFiltrarEstado.getValue();
        String bloque = cmbFiltrarBloque.getValue();
        String codigoSeleccionado = seleccionado == null ? null : seleccionado.getCodigo();
        Apartamento actualizado = null;
        for (Apartamento a : sistema.getServicioApartamento().listarApartamentos()) {
            total++;
            switch (a.getEstado()) {
                case PREPARADO -> preparados++;
                case OCUPADO -> ocupados++;
                case EN_PREPARACION -> preparando++;
                case FUERA_DE_SERVICIO -> fuera++;
                default -> {}
            }
            if (!cmbFiltrarBloque.getItems().contains(a.getBloque()))
                cmbFiltrarBloque.getItems().add(a.getBloque());
            if (a.getCodigo().equals(codigoSeleccionado)) actualizado = a;
            if (!filtro.isEmpty() && !a.getCodigo().toLowerCase(Locale.ROOT).contains(filtro)
                    && !a.getNombre().toLowerCase(Locale.ROOT).contains(filtro)) continue;
            if (estado != null && !estado.equals("Todos") && !a.getEstado().name().equals(estado)) continue;
            if (bloque != null && !bloque.equals("Todos") && !a.getBloque().equals(bloque)) continue;
            panelApartamentos.getChildren().add(tarjeta(a));
        }
        lblTotalApartamentos.setText(String.valueOf(total));
        lblPreparados.setText(String.valueOf(preparados));
        lblOcupados.setText(String.valueOf(ocupados));
        lblEnPreparacion.setText(String.valueOf(preparando));
        lblFueraServicio.setText(String.valueOf(fuera));
        if (actualizado != null) mostrarDetalle(actualizado);
        else limpiarDetalle();
    }

    private VBox tarjeta(Apartamento a) {
        Label titulo = new Label(a.getCodigo() + " · " + a.getNombre());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label ubicacion = new Label("Bloque " + a.getBloque() + " · Piso " + a.getPiso());
        Label estado = new Label(a.getEstado().name().replace('_', ' '));
        estado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color(a.getEstado()) + ";");
        Label capacidad = new Label("Capacidad: " + a.getCapacidad() + " personas");
        VBox caja = new VBox(8, titulo, ubicacion, estado, capacidad);
        caja.setPadding(new Insets(15));
        caja.setPrefWidth(245);
        caja.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DDE5F0;"
                + "-fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
        caja.setOnMouseClicked(e -> {
            seleccionado = a;
            mostrarDetalle(a);
        });
        return caja;
    }

    private String color(EstadoApartamento estado) {
        return switch (estado) {
            case PREPARADO -> "#23965D";
            case OCUPADO -> "#2463A6";
            case FUERA_DE_SERVICIO -> "#D44B4B";
            default -> "#C9981B";
        };
    }

    private void mostrarDetalle(Apartamento a) {
        lblDetalleCodigo.setText(a.getCodigo());
        lblDetalleBloque.setText(a.getBloque());
        lblDetallePiso.setText(String.valueOf(a.getPiso()));
        lblDetalleCapacidad.setText(String.valueOf(a.getCapacidad()));
        lblDetalleEstado.setText(a.getEstado().name().replace('_',' '));
        lblMotivoFueraServicio.setText(a.getMotivoFueraServicio() == null
                ? "—" : a.getMotivoFueraServicio());
    }
    private void limpiarDetalle() {
        lblDetalleCodigo.setText("—");
        lblDetalleBloque.setText("—");
        lblDetallePiso.setText("—");
        lblDetalleCapacidad.setText("—");
        lblDetalleEstado.setText("—");
        lblMotivoFueraServicio.setText("—");
    }
}
