package uniquindio.edu.co.proyectofinal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.*;

/** Calendario mensual: una columna por noche; la fecha de salida no ocupa noche. */
public class VistaCalendarioOcupacionController {
    @FXML private Button btnMenuInicio, btnMenuReservas, btnMenuCalendario,
        btnMenuUsuario, btnMenuPreparacion, btnMenuMapa, btnMenuEstadisticas,
        btnMenuCargaDatos, btnMesAnterior, btnMesSiguiente, btnIrMesActual,
        btnActualizarCalendario;
    @FXML private Label lblMesActual, lblTotalApartamentosCalendario, lblNochesLibres,
        lblNochesOcupadas, lblNochesBloqueadas, lblDetalleApartamentoCalendario,
        lblDetalleFechaCalendario, lblDetalleEstadoCalendario, lblDetalleReservaCalendario;
    @FXML private ComboBox<String> cmbBloqueCalendario, cmbEstadoNoche;
    @FXML private TextField txtBuscarApartamentoCalendario;
    @FXML private ScrollPane scrollCalendario;
    @FXML private GridPane gridCalendario;
    private SistemaSGA sistema;
    private YearMonth mes = YearMonth.now();
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es","CO"));
    private enum Tipo { LIBRE, OCUPADA, BLOQUEADA, FUERA_SERVICIO }
    private record Noche(Tipo tipo, Reserva reserva) {}

    @FXML private void initialize() {
        cmbBloqueCalendario.getItems().add("Todos");
        cmbBloqueCalendario.setValue("Todos");
        cmbEstadoNoche.getItems().addAll("Todos","Libre","Ocupada","Bloqueada","Fuera de servicio");
        cmbEstadoNoche.setValue("Todos");
        btnMesAnterior.setOnAction(e->{mes=mes.minusMonths(1); actualizar();});
        btnMesSiguiente.setOnAction(e->{mes=mes.plusMonths(1); actualizar();});
        btnIrMesActual.setOnAction(e->{mes=YearMonth.now(); actualizar();});
        btnActualizarCalendario.setOnAction(e->actualizar());
        cmbBloqueCalendario.valueProperty().addListener((o,a,b)->actualizar());
        cmbEstadoNoche.valueProperty().addListener((o,a,b)->actualizar());
        txtBuscarApartamentoCalendario.textProperty().addListener((o,a,b)->actualizar());
        btnMenuInicio.setOnAction(e->navegar("VentanaPrincipal.fxml",true));
        btnMenuReservas.setOnAction(e->navegar("VistaReservas.fxml",false));
        btnMenuCalendario.setOnAction(e->actualizar());
        btnMenuUsuario.setOnAction(e->abrirUsuario());
        btnMenuPreparacion.setOnAction(e->abrirPreparacion());
        btnMenuMapa.setOnAction(e->abrirMapa());
        for(Button b:new Button[]{btnMenuEstadisticas,btnMenuCargaDatos})
            b.setOnAction(e->new Alert(Alert.AlertType.INFORMATION,
                    "Vista pendiente: "+b.getText()).showAndWait());
        actualizar();
    }
    public void setSistema(SistemaSGA sistema) {
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

        this.sistema=Objects.requireNonNull(sistema);
        actualizar();
    }
    private void abrirMapa() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VistaMapaInteractivo.fxml"));
            Scene scene=new Scene(loader.load());
            ((VistaMapaInteractivoController)loader.getController()).setSistema(sistema);
            ((Stage)btnMenuMapa.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){new Alert(Alert.AlertType.ERROR,ex.toString()).showAndWait();}
    }

    private void abrirPreparacion() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VistaPanelPreparacion.fxml"));
            Scene scene=new Scene(loader.load());
            ((VistaPanelPreparacionController)loader.getController()).setSistema(sistema);
            ((Stage)btnMenuPreparacion.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){new Alert(Alert.AlertType.ERROR,ex.toString()).showAndWait();}
    }

    private void abrirUsuario() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VistaPanelUsuario.fxml"));
            Scene scene=new Scene(loader.load());
            ((VistaPanelUsuarioController)loader.getController()).setSistema(sistema);
            ((Stage)btnMenuUsuario.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){
            new Alert(Alert.AlertType.ERROR,"No se pudo abrir Panel de usuario: "+ex.getMessage()).showAndWait();
        }
    }

    private void navegar(String archivo, boolean inicio) {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource(archivo));
            Scene escena=new Scene(loader.load());
            if(inicio) ((VentanaPrincipalController)loader.getController()).setSistema(sistema);
            else ((VistaReservasController)loader.getController()).setSistema(sistema);
            ((Stage)btnMenuInicio.getScene().getWindow()).setScene(escena);
        }catch(Exception ex){
            new Alert(Alert.AlertType.ERROR,"No se pudo abrir "+archivo+": "+ex.getMessage()).showAndWait();
        }
    }
    private Noche noche(Apartamento apartamento,LocalDate fecha) {
        // Una unidad fuera de servicio no se ofrece aunque tenga reservas antiguas.
        if(apartamento.getEstado()==EstadoApartamento.FUERA_DE_SERVICIO)
            return new Noche(Tipo.FUERA_SERVICIO,null);
        for(Reserva reserva:sistema.getServicioReserva().listarReservas()) {
            if(!reserva.getApartamento().getCodigo().equalsIgnoreCase(apartamento.getCodigo()))continue;
            if(fecha.isBefore(reserva.getEstancia().getFechaEntrada())
                    || !fecha.isBefore(reserva.getEstancia().getFechaSalida()))continue;
            switch(reserva.getEstado()) {
                case CONFIRMADA, EN_CURSO -> { return new Noche(Tipo.OCUPADA,reserva); }
                case PENDIENTE -> { return new Noche(Tipo.BLOQUEADA,reserva); }
                default -> { /* Cancelada, finalizada o no-show no bloquean fechas. */ }
            }
        }
        return new Noche(Tipo.LIBRE,null);
    }
    private String nombre(Tipo t) {
        return switch(t) {
            case LIBRE->"Libre"; case OCUPADA->"Ocupada";
            case BLOQUEADA->"Bloqueada"; case FUERA_SERVICIO->"Fuera de servicio";
        };
    }
    private String color(Tipo t) {
        return switch(t) {
            case LIBRE->"#DDF5E6"; case OCUPADA->"#BFD8F7";
            case BLOQUEADA->"#FFE7A8"; case FUERA_SERVICIO->"#F8C9C9";
        };
    }
    private Label celda(String texto,int ancho,boolean cabecera) {
        Label label=new Label(texto);
        label.setAlignment(Pos.CENTER);
        label.setMinSize(ancho,37);
        label.setPrefSize(ancho,37);
        label.setMaxWidth(ancho);
        label.setStyle(cabecera?"-fx-background-color:#25466E;-fx-text-fill:white;-fx-font-weight:bold;"
                :"-fx-background-color:white;-fx-text-fill:#172B48;");
        return label;
    }
    public void actualizar() {
        lblMesActual.setText(MES.format(mes.atDay(1)).toUpperCase(new Locale("es","CO")));
        gridCalendario.getChildren().clear();
        gridCalendario.getColumnConstraints().clear();
        if(sistema==null) {
            lblTotalApartamentosCalendario.setText("0");
            lblNochesLibres.setText("0");lblNochesOcupadas.setText("0");lblNochesBloqueadas.setText("0");
            return;
        }
        gridCalendario.add(celda("Apartamento / Noche",165,true),0,0);
        for(int d=1;d<=mes.lengthOfMonth();d++) {
            LocalDate fecha=mes.atDay(d);
            gridCalendario.add(celda(String.format("%02d",d),37,true),d,0);
        }
        int fila=1,libres=0,ocupadas=0,bloqueadas=0;
        String texto=txtBuscarApartamentoCalendario.getText()==null?""
                :txtBuscarApartamentoCalendario.getText().strip().toLowerCase(Locale.ROOT);
        String filtroBloque=cmbBloqueCalendario.getValue();
        String filtroEstado=cmbEstadoNoche.getValue();
        for(Apartamento a:sistema.getServicioApartamento().listarApartamentos()) {
            if(!cmbBloqueCalendario.getItems().contains(a.getBloque()))
                cmbBloqueCalendario.getItems().add(a.getBloque());
            if(!"Todos".equals(filtroBloque)&&!a.getBloque().equals(filtroBloque))continue;
            if(!texto.isEmpty()&&!a.getCodigo().toLowerCase(Locale.ROOT).contains(texto)
                    &&!a.getNombre().toLowerCase(Locale.ROOT).contains(texto))continue;
            boolean mostrar="Todos".equals(filtroEstado);
            if(!mostrar)for(int d=1;d<=mes.lengthOfMonth();d++)
                if(nombre(noche(a,mes.atDay(d)).tipo()).equals(filtroEstado)){mostrar=true;break;}
            if(!mostrar)continue;
            gridCalendario.add(celda(a.getCodigo()+" · "+a.getNombre(),165,false),0,fila);
            for(int d=1;d<=mes.lengthOfMonth();d++) {
                LocalDate fecha=mes.atDay(d);
                Noche n=noche(a,fecha);
                switch(n.tipo()) {
                    case LIBRE->libres++;
                    case OCUPADA->ocupadas++;
                    case BLOQUEADA, FUERA_SERVICIO->bloqueadas++;
                }
                Label celda=celda("●",37,false);
                celda.setStyle("-fx-background-color:"+color(n.tipo())
                        +";-fx-text-fill:#172B48;-fx-font-size:15px;-fx-cursor:hand;");
                celda.setTooltip(new Tooltip(a.getCodigo()+" · "+fecha+" · "+nombre(n.tipo())));
                celda.setOnMouseClicked(e->detalle(a,fecha,n));
                gridCalendario.add(celda,d,fila);
            }
            fila++;
        }
        lblTotalApartamentosCalendario.setText(""+(fila-1));
        lblNochesLibres.setText(""+libres);
        lblNochesOcupadas.setText(""+ocupadas);
        lblNochesBloqueadas.setText(""+bloqueadas);
    }
    private void detalle(Apartamento a,LocalDate fecha,Noche n) {
        lblDetalleApartamentoCalendario.setText(a.getCodigo()+" · "+a.getNombre());
        lblDetalleFechaCalendario.setText(fecha.toString());
        lblDetalleEstadoCalendario.setText(nombre(n.tipo()));
        lblDetalleReservaCalendario.setText(n.reserva()==null?"—":n.reserva().getCodigo());
    }
}
