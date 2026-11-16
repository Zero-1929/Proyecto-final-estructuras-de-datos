package uniquindio.edu.co.proyectofinal;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.*;
import uniquindio.edu.co.proyectofinal.servicios.ServicioPreparacion;
import uniquindio.edu.co.proyectofinal.servicios.ServicioPreparacion.TareaPreparacion;
import uniquindio.edu.co.proyectofinal.servicios.ServicioRutas;
import uniquindio.edu.co.proyectofinal.estructuras.ResultadoRuta;

/** Tablero operativo de preparación conectado a la cola de prioridad real. */
public class VistaPanelPreparacionController {
    @FXML private Button btnMenuInicio;
    @FXML private Button btnMenuReservas;
    @FXML private Button btnMenuCalendario;
    @FXML private Button btnMenuUsuario;
    @FXML private Button btnMenuPreparacion;
    @FXML private Button btnMenuMapa;
    @FXML private Button btnMenuEstadisticas;
    @FXML private Button btnMenuCargaDatos;
    @FXML private Label lblTotalPendientes;
    @FXML private Label lblTotalEnPreparacion;
    @FXML private Label lblTotalPreparados;
    @FXML private Label lblSiguienteApartamento;
    @FXML private Button btnActualizarColaPreparacion;
    @FXML private TextField txtBuscarApartamentoPreparacion;
    @FXML private ComboBox<String> cmbFiltrarBloquePreparacion;
    @FXML private ComboBox<String> cmbFiltrarEstadoPreparacion;
    @FXML private Button btnLimpiarFiltrosPreparacion;
    @FXML private TableView<Fila> tablaColaPreparacion;
    @FXML private TableColumn<Fila,String> colPreparacionPrioridad;
    @FXML private TableColumn<Fila,String> colPreparacionApartamento;
    @FXML private TableColumn<Fila,String> colPreparacionUbicacion;
    @FXML private TableColumn<Fila,String> colPreparacionEstado;
    @FXML private TableColumn<Fila,String> colPreparacionSalida;
    @FXML private TableColumn<Fila,String> colPreparacionProximaEntrada;
    @FXML private TableColumn<Fila,String> colPreparacionMotivo;
    @FXML private Button btnIniciarPreparacion;
    @FXML private Button btnFinalizarPreparacion;
    @FXML private Label lblMensajeColaPreparacion;
    @FXML private Label lblDetalleApartamentoPreparacion;
    @FXML private Label lblDetalleEstadoPreparacion;
    @FXML private Label lblDetallePrioridadPreparacion;
    @FXML private Label lblDetalleProximaEntradaPreparacion;
    @FXML private TextArea txtObservacionesPreparacion;
    @FXML private Button btnCalcularRutaPreparacion;
    @FXML private ComboBox<String> cmbOrigenRutaPreparacion;
    @FXML private Label lblDistanciaRutaPreparacion;
    @FXML private ListView<String> listaParadasRutaPreparacion;
    @FXML private StackPane panelVistaRutaPreparacion;
    @FXML private Label lblMensajeRutaPreparacion;
    private record Fila(Apartamento apartamento, TareaPreparacion tarea, int posicion) {}
    private SistemaSGA sistema;
    private ServicioRutas<String> rutas;
    private final Map<String,String> observacionesSesion = new HashMap<>();
    private boolean cargando;
    @FXML private Button btnEjecutarRutaPreparacion;
    private final List<String> paradasCalculadas = new ArrayList<>();
    private boolean simulando;
    private PauseTransition pausaSimulacion;
    private int indiceSimulacion;


    private static void columna(TableColumn<Fila,String> c, Function<Fila,String> f) {
        c.setCellValueFactory(x->new ReadOnlyStringWrapper(f.apply(x.getValue())));
    }
    @FXML private void initialize() {
        cmbFiltrarBloquePreparacion.getItems().add("Todos");
        cmbFiltrarBloquePreparacion.setValue("Todos");
        cmbFiltrarEstadoPreparacion.getItems().addAll("Todos","PENDIENTE_PREPARACION","EN_PREPARACION");
        cmbFiltrarEstadoPreparacion.setValue("Todos");
        columna(colPreparacionPrioridad,x->x.tarea()==null?"En curso":""+x.posicion());
        columna(colPreparacionApartamento,x->x.apartamento().getCodigo());
        columna(colPreparacionUbicacion,x->x.apartamento().getBloque()+" / "+x.apartamento().getPiso());
        columna(colPreparacionEstado,x->x.apartamento().getEstado().name());
        columna(colPreparacionSalida,x->"—"); // El modelo no registra la fecha efectiva del último check-out.
        columna(colPreparacionProximaEntrada,x->proximaEntrada(x.apartamento()));
        columna(colPreparacionMotivo,x->x.tarea()==null?"En preparación":
                (llegadaHoy(x.apartamento())?"Llegada hoy; prioridad por hora":"Pendiente"));
        btnActualizarColaPreparacion.setOnAction(e->actualizar());
        btnLimpiarFiltrosPreparacion.setOnAction(e->{
            txtBuscarApartamentoPreparacion.clear();
            cmbFiltrarBloquePreparacion.setValue("Todos");
            cmbFiltrarEstadoPreparacion.setValue("Todos");actualizar();
        });
        txtBuscarApartamentoPreparacion.textProperty().addListener((o,a,b)->actualizar());
        cmbFiltrarBloquePreparacion.valueProperty().addListener((o,a,b)->actualizar());
        cmbFiltrarEstadoPreparacion.valueProperty().addListener((o,a,b)->actualizar());
        tablaColaPreparacion.getSelectionModel().selectedItemProperty().addListener((o,a,b)->detalle(b));
        btnIniciarPreparacion.setOnAction(e->iniciar());
        btnFinalizarPreparacion.setOnAction(e->finalizar());
        btnCalcularRutaPreparacion.setOnAction(e->calcularRuta());
        btnEjecutarRutaPreparacion.setOnAction(e->ejecutarRuta());
        btnEjecutarRutaPreparacion.setDisable(true);
        txtObservacionesPreparacion.focusedProperty().addListener((o,a,focused)->{
            if(!focused)guardarObservacionLocal();
        });
        btnMenuInicio.setOnAction(e->navegar("VentanaPrincipal.fxml"));
        btnMenuReservas.setOnAction(e->navegar("VistaReservas.fxml"));
        btnMenuCalendario.setOnAction(e->navegar("VistaCalendarioOcupacion.fxml"));
        btnMenuUsuario.setOnAction(e->navegar("VistaPanelUsuario.fxml"));
        btnMenuPreparacion.setOnAction(e->actualizar());
        btnMenuMapa.setOnAction(e->navegar("VistaMapaInteractivo.fxml"));
        for(Button b:new Button[]{btnMenuEstadisticas,btnMenuCargaDatos})
            b.setOnAction(e->mensaje("Vista pendiente",b.getText()));
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
        setRutas(MapaGrafoCompartido.obtener(sistema));
        actualizar();
    }
    /** Inyectar el grafo real de pasillos cuando se conecte el módulo de mapa. */
    public void setRutas(ServicioRutas<String> rutas) {
        this.rutas=rutas;
        cmbOrigenRutaPreparacion.getItems().clear();
        if(rutas!=null) {
            // El grafo propio no expone catálogo de ubicaciones; usar códigos conocidos.
            cmbOrigenRutaPreparacion.getItems().add("Recepcion");
            if(sistema!=null)for(Apartamento a:sistema.getServicioApartamento().listarApartamentos())
                cmbOrigenRutaPreparacion.getItems().add(a.getCodigo());
            cmbOrigenRutaPreparacion.setValue("Recepcion");
        }
    }
    private boolean llegadaHoy(Apartamento a) {
        if(sistema==null)return false;
        for(Reserva r:sistema.getServicioReserva().listarReservas())
            if(r.getApartamento().getCodigo().equalsIgnoreCase(a.getCodigo())
                &&r.getEstancia().getFechaEntrada().equals(LocalDate.now())
                &&(r.getEstado()==EstadoReserva.CONFIRMADA||r.getEstado()==EstadoReserva.PENDIENTE))
                return true;
        return false;
    }
    private String proximaEntrada(Apartamento a) {
        if(sistema==null)return "—";
        LocalDate mejor=null;
        LocalTime hora=null;
        for(Reserva r:sistema.getServicioReserva().listarReservas()){
            if(!r.getApartamento().getCodigo().equalsIgnoreCase(a.getCodigo()))continue;
            if(r.getEstado()!=EstadoReserva.CONFIRMADA&&r.getEstado()!=EstadoReserva.PENDIENTE)continue;
            LocalDate fecha=r.getEstancia().getFechaEntrada();
            if(fecha.isBefore(LocalDate.now()))continue;
            LocalTime h=r.getHoraLlegadaEstimada();
            if(mejor==null||fecha.isBefore(mejor)||(fecha.equals(mejor)
                    &&h!=null&&(hora==null||h.isBefore(hora)))){mejor=fecha;hora=h;}
        }
        return mejor==null?"—":mejor+(hora==null?"":" "+hora);
    }
    private void actualizar() {
        if(sistema==null)return;
        ServicioPreparacion sp=sistema.getServicioPreparacion();
        sp.reordenar();
        var filas=FXCollections.<Fila>observableArrayList();
        guardarObservacionLocal();
        String codigoSeleccionado=tablaColaPreparacion.getSelectionModel().getSelectedItem()==null
                ?null:tablaColaPreparacion.getSelectionModel().getSelectedItem().apartamento().getCodigo();
        int orden=0,preparando=0,preparados=0;
        for(TareaPreparacion tarea:sp.listarPendientes()) {
            orden++;
            Fila fila=new Fila(tarea.getApartamento(),tarea,orden);
            if(visible(fila))filas.add(fila);
        }
        for(Apartamento a:sistema.getServicioApartamento().listarApartamentos()) {
            if(!cmbFiltrarBloquePreparacion.getItems().contains(a.getBloque()))
                cmbFiltrarBloquePreparacion.getItems().add(a.getBloque());
            if(a.getEstado()==EstadoApartamento.EN_PREPARACION) {
                preparando++;
                Fila fila=new Fila(a,null,0);
                if(visible(fila))filas.add(fila);
            }
            if(a.getEstado()==EstadoApartamento.PREPARADO)preparados++;
        }
        tablaColaPreparacion.setItems(filas);
        lblTotalPendientes.setText(""+sp.cantidadPendientes());
        lblTotalEnPreparacion.setText(""+preparando);
        lblTotalPreparados.setText(""+preparados);
        TareaPreparacion siguiente=sp.verSiguientePreparacion();
        lblSiguienteApartamento.setText(siguiente==null?"Sin pendientes":siguiente.getApartamento().getCodigo());
        if(codigoSeleccionado!=null)for(Fila fila:filas)
            if(fila.apartamento().getCodigo().equals(codigoSeleccionado)){
                tablaColaPreparacion.getSelectionModel().select(fila);break;
            }
        if(tablaColaPreparacion.getSelectionModel().getSelectedItem()==null)detalle(null);
        lblMensajeColaPreparacion.setText("Pendientes: "+sp.cantidadPendientes()
                +" · Iniciar solo atiende la primera tarea de la cola.");
    }
    private boolean visible(Fila fila) {
        Apartamento a=fila.apartamento();
        String busqueda=txtBuscarApartamentoPreparacion.getText()==null?""
                :txtBuscarApartamentoPreparacion.getText().strip().toLowerCase(Locale.ROOT);
        return (busqueda.isEmpty()||a.getCodigo().toLowerCase(Locale.ROOT).contains(busqueda)
                ||a.getNombre().toLowerCase(Locale.ROOT).contains(busqueda))
                &&("Todos".equals(cmbFiltrarBloquePreparacion.getValue())
                    ||a.getBloque().equals(cmbFiltrarBloquePreparacion.getValue()))
                &&("Todos".equals(cmbFiltrarEstadoPreparacion.getValue())
                    ||a.getEstado().name().equals(cmbFiltrarEstadoPreparacion.getValue()));
    }
    private void guardarObservacionLocal() {
        if(cargando)return;
        Fila fila=tablaColaPreparacion.getSelectionModel().getSelectedItem();
        if(fila!=null)observacionesSesion.put(fila.apartamento().getCodigo(),txtObservacionesPreparacion.getText());
    }
    private void detalle(Fila fila) {
        cargando=true;
        try {
            lblDetalleApartamentoPreparacion.setText(fila==null?"—":fila.apartamento().getCodigo());
            lblDetalleEstadoPreparacion.setText(fila==null?"—":fila.apartamento().getEstado().name());
            lblDetallePrioridadPreparacion.setText(fila==null?"—":fila.tarea()==null?"En curso":""+fila.posicion());
            lblDetalleProximaEntradaPreparacion.setText(fila==null?"—":proximaEntrada(fila.apartamento()));
            txtObservacionesPreparacion.setText(fila==null?"":observacionesSesion.getOrDefault(fila.apartamento().getCodigo(),""));
        }finally{cargando=false;}
    }
    private boolean confirmar(String titulo,String texto) {
        return new Alert(Alert.AlertType.CONFIRMATION,texto,ButtonType.YES,ButtonType.NO)
                .showAndWait().orElse(ButtonType.NO)==ButtonType.YES;
    }
    private void iniciar() {
        if(simulando)return;
        try {
            TareaPreparacion siguiente=sistema.getServicioPreparacion().verSiguientePreparacion();
            if(siguiente==null){mensaje("Cola vacía","No hay preparaciones pendientes.");return;}
            Fila fila=tablaColaPreparacion.getSelectionModel().getSelectedItem();
            if(fila!=null&&!fila.apartamento().getCodigo().equals(siguiente.getApartamento().getCodigo()))
                throw new IllegalStateException("Solo se puede iniciar la primera tarea: "+siguiente.getApartamento().getCodigo());
            if(!confirmar("Iniciar preparación","¿Iniciar "+siguiente.getApartamento().getCodigo()+"?"))return;
            sistema.getServicioPreparacion().iniciarSiguientePreparacion();
            sistema.guardar();actualizar();
        }catch(Exception ex){error(ex);}
    }
    private void finalizar() {
        if(simulando)return;
        try {
            Fila fila=tablaColaPreparacion.getSelectionModel().getSelectedItem();
            if(fila==null)throw new IllegalArgumentException("Selecciona un apartamento en preparación.");
            Apartamento a=fila.apartamento();
            if(a.getEstado()!=EstadoApartamento.EN_PREPARACION)
                throw new IllegalStateException("El apartamento no está EN_PREPARACION.");
            if(!confirmar("Finalizar preparación","¿Marcar "+a.getCodigo()+" como preparado?"))return;
            sistema.getServicioPreparacion().finalizarPreparacion(a);
            sistema.guardar();actualizar();
        }catch(Exception ex){error(ex);}
    }
    private void calcularRuta() {
        if(simulando)return;
        paradasCalculadas.clear();
        btnEjecutarRutaPreparacion.setDisable(true);
        listaParadasRutaPreparacion.getItems().clear();
        panelVistaRutaPreparacion.getChildren().clear();
        lblDistanciaRutaPreparacion.setText("—");
        if(sistema==null)return;
        var pendientes=sistema.getServicioPreparacion().listarPendientes();
        if(pendientes.estaVacia()) {
            lblMensajeRutaPreparacion.setText("No hay apartamentos pendientes de preparación.");
            return;
        }
        if(rutas==null||rutas.mapaVacio()) {
            lblMensajeRutaPreparacion.setText("El mapa no tiene conexiones. Configura los pasillos en la vista Mapa.");
            return;
        }
        String origen=cmbOrigenRutaPreparacion.getValue();
        if(origen==null||!rutas.existeUbicacion(origen)) {
            lblMensajeRutaPreparacion.setText("El punto de partida no existe en el grafo. Registra 'Recepcion' en la vista Mapa o elige un apartamento existente.");
            return;
        }
        double total=0;
        String actual=origen;
        List<String> lineas=new ArrayList<>();
        lineas.add("INICIO: "+origen);
        for(TareaPreparacion tarea:pendientes) {
            String destino=tarea.getApartamento().getCodigo();
            if(!rutas.existeUbicacion(destino)) {
                lblMensajeRutaPreparacion.setText("Falta ubicación en el grafo: "+destino);
                return;
            }
            ResultadoRuta<String> tramo=rutas.consultarRutaMasCorta(actual,destino);
            if(!tramo.existeRuta()) {
                lblMensajeRutaPreparacion.setText("No existe camino entre "+actual+" y "+destino+". Conecta los pasillos en Mapa.");
                return;
            }
            total+=tramo.getDistanciaTotal();
            paradasCalculadas.add(destino);
            lineas.add("↓  "+String.format(Locale.ROOT,"%.1f m",tramo.getDistanciaTotal()));
            lineas.add(destino);
            actual=destino;
        }
        listaParadasRutaPreparacion.getItems().addAll(lineas);
        lblDistanciaRutaPreparacion.setText(String.format(Locale.ROOT,"%.2f m",total));
        lblMensajeRutaPreparacion.setText("Ruta calculada respetando la prioridad de la cola; cada tramo usa Dijkstra. Puedes ejecutar una simulación de 3 segundos por apartamento.");
        VBox vista=new VBox(7);
        vista.setAlignment(Pos.CENTER);
        for(String linea:lineas){
            Label etiqueta=new Label(linea);
            etiqueta.setStyle(linea.startsWith("↓")?"-fx-text-fill: #64748b;":"-fx-font-weight: bold; -fx-text-fill: #1d4f87;");
            vista.getChildren().add(etiqueta);
        }
        panelVistaRutaPreparacion.getChildren().add(vista);
        btnEjecutarRutaPreparacion.setDisable(paradasCalculadas.isEmpty());
    }

    private void ejecutarRuta() {
        if(simulando||paradasCalculadas.isEmpty()||sistema==null)return;
        if(!confirmar("Simulación de preparación", "¿Ejecutar la ruta calculada? Cada apartamento estará 3 segundos EN_PREPARACION y luego pasará a PREPARADO. Los cambios se guardarán."))return;
        // La cola puede haber cambiado desde el cálculo. Exigir el mismo orden antes de empezar.
        List<String> actuales=new ArrayList<>();
        for(TareaPreparacion t:sistema.getServicioPreparacion().listarPendientes())actuales.add(t.getApartamento().getCodigo());
        if(!actuales.equals(paradasCalculadas)) {
            mensaje("Ruta desactualizada","La cola cambió. Calcula nuevamente la ruta antes de ejecutarla.");
            btnEjecutarRutaPreparacion.setDisable(true);
            return;
        }
        simulando=true;
        indiceSimulacion=0;
        btnEjecutarRutaPreparacion.setDisable(true);
        btnCalcularRutaPreparacion.setDisable(true);
        btnIniciarPreparacion.setDisable(true);
        btnFinalizarPreparacion.setDisable(true);
        simularSiguiente();
    }

    private void simularSiguiente() {
        if(indiceSimulacion>=paradasCalculadas.size()) {
            simulando=false;
            btnCalcularRutaPreparacion.setDisable(false);
            btnIniciarPreparacion.setDisable(false);
            btnFinalizarPreparacion.setDisable(false);
            lblMensajeRutaPreparacion.setText("Simulación completada. Todos los apartamentos de la ruta quedaron preparados.");
            actualizar();
            return;
        }
        String codigo=paradasCalculadas.get(indiceSimulacion);
        try {
            TareaPreparacion siguiente=sistema.getServicioPreparacion().verSiguientePreparacion();
            if(siguiente==null||!codigo.equals(siguiente.getApartamento().getCodigo()))
                throw new IllegalStateException("Cambió la prioridad de la cola. Simulación detenida; recalcula la ruta.");
            Apartamento apartamento=sistema.getServicioPreparacion().iniciarSiguientePreparacion().getApartamento();
            sistema.guardar();
            actualizar();
            lblMensajeRutaPreparacion.setText("Preparando "+codigo+" (3 segundos de demostración)...");
            pausaSimulacion=new PauseTransition(Duration.seconds(3));
            pausaSimulacion.setOnFinished(e->{
                try {
                    sistema.getServicioPreparacion().finalizarPreparacion(apartamento);
                    sistema.guardar();
                    indiceSimulacion++;
                    actualizar();
                    simularSiguiente();
                }catch(Exception ex){detenerSimulacion();error(ex);}
            });
            pausaSimulacion.play();
        }catch(Exception ex){detenerSimulacion();error(ex);}
    }
    private void detenerSimulacion() {
        simulando=false;
        if(pausaSimulacion!=null)pausaSimulacion.stop();
        btnCalcularRutaPreparacion.setDisable(false);
        btnIniciarPreparacion.setDisable(false);
        btnFinalizarPreparacion.setDisable(false);
        btnEjecutarRutaPreparacion.setDisable(true);
        lblMensajeRutaPreparacion.setText("Simulación detenida. Revisa los estados y recalcula la ruta.");
        actualizar();
    }
    private void navegar(String archivo) {
        if(simulando){mensaje("Simulación activa","Espera a que termine la preparación antes de cambiar de pantalla.");return;}
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource(archivo));
            Scene escena=new Scene(loader.load());
            Object ctrl=loader.getController();
            if(ctrl instanceof VentanaPrincipalController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaReservasController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaCalendarioOcupacionController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaPanelUsuarioController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaMapaInteractivoController c)c.setSistema(sistema);
            ((Stage)btnMenuInicio.getScene().getWindow()).setScene(escena);
        }catch(Exception ex){error(ex);}
    }
    private void mensaje(String titulo,String texto) {
        Alert a=new Alert(Alert.AlertType.INFORMATION,texto);
        a.setTitle(titulo);a.setHeaderText(null);a.showAndWait();
    }
    private void error(Exception ex) {
        Alert a=new Alert(Alert.AlertType.ERROR,ex.getMessage()==null?ex.toString():ex.getMessage());
        a.setTitle("Preparación");a.setHeaderText("No se pudo completar la operación");a.showAndWait();
    }
}
