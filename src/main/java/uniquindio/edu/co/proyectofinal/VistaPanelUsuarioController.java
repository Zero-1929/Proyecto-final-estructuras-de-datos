package uniquindio.edu.co.proyectofinal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import java.time.LocalDateTime;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.*;
import uniquindio.edu.co.proyectofinal.servicios.ServicioPerfilHuesped;
import uniquindio.edu.co.proyectofinal.servicios.ServicioCancelacionDevolucion;

/** Panel de un huésped elegido por documento. No presupone autenticación. */
public class VistaPanelUsuarioController {
    @FXML private Button btnCambiarRol;
    @FXML private Button btnSolicitarReservaPortal;
    @FXML private Button btnCancelarReservaUsuario;
    @FXML private Button btnMenuInicio;
    @FXML private Button btnMenuReservas;
    @FXML private Button btnMenuCalendario;
    @FXML private Button btnMenuUsuario;
    @FXML private Button btnMenuPreparacion;
    @FXML private Button btnMenuMapa;
    @FXML private Button btnMenuEstadisticas;
    @FXML private Button btnMenuCargaDatos;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblDocumentoUsuario;
    @FXML private Label lblCorreoUsuario;
    @FXML private Button btnActualizarPerfil;
    @FXML private Label lblTotalReservasUsuario;
    @FXML private Label lblProximasEstancias;
    @FXML private Label lblSaldoPendienteUsuario;
    @FXML private Label lblTotalFavoritos;
    @FXML private Button btnActualizarReservasUsuario;
    @FXML private TextField txtBuscarReservaUsuario;
    @FXML private ComboBox<String> cmbEstadoReservaUsuario;
    @FXML private Button btnLimpiarFiltrosUsuario;
    @FXML private TableView<Reserva> tablaReservasUsuario;
    @FXML private TableColumn<Reserva,String> colUsuarioCodigoReserva;
    @FXML private TableColumn<Reserva,String> colUsuarioApartamento;
    @FXML private TableColumn<Reserva,String> colUsuarioEntrada;
    @FXML private TableColumn<Reserva,String> colUsuarioSalida;
    @FXML private TableColumn<Reserva,String> colUsuarioNoches;
    @FXML private TableColumn<Reserva,String> colUsuarioEstadoReserva;
    @FXML private Button btnVerDetalleReservaUsuario;
    @FXML private Button btnConsultarFolioUsuario;
    @FXML private Label lblUsuarioDetalleCodigo;
    @FXML private Label lblUsuarioDetalleApartamento;
    @FXML private Label lblUsuarioDetalleEntrada;
    @FXML private Label lblUsuarioDetalleSalida;
    @FXML private Label lblUsuarioDetalleEstado;
    @FXML private TableView<Reserva> tablaFoliosUsuario;
    @FXML private TableColumn<Reserva,String> colFolioCodigo;
    @FXML private TableColumn<Reserva,String> colFolioReserva;
    @FXML private TableColumn<Reserva,String> colFolioTotal;
    @FXML private TableColumn<Reserva,String> colFolioPagado;
    @FXML private TableColumn<Reserva,String> colFolioSaldo;
    @FXML private Button btnVerMovimientosFolio;
    @FXML private Button btnActualizarFolios;
    @FXML private TableView<Movimiento> tablaMovimientosFolioUsuario;
    @FXML private TableColumn<Movimiento,String> colMovimientoFecha;
    @FXML private TableColumn<Movimiento,String> colMovimientoConcepto;
    @FXML private TableColumn<Movimiento,String> colMovimientoTipo;
    @FXML private TableColumn<Movimiento,String> colMovimientoValor;
    @FXML private Label lblSaldoFolioSeleccionado;
    @FXML private TabPane tabsUsuario;
    @FXML private Tab tabFoliosUsuario;
    @FXML private Button btnAgregarFavorito;
    @FXML private Button btnActualizarFavoritos;
    @FXML private TableView<Apartamento> tablaFavoritosUsuario;
    @FXML private TableColumn<Apartamento,String> colFavoritoCodigo;
    @FXML private TableColumn<Apartamento,String> colFavoritoNombre;
    @FXML private TableColumn<Apartamento,String> colFavoritoBloque;
    @FXML private TableColumn<Apartamento,String> colFavoritoCapacidad;
    @FXML private TableColumn<Apartamento,String> colFavoritoDormitorios;
    @FXML private Button btnVerApartamentoFavorito;
    @FXML private Button btnQuitarFavorito;
    @FXML private Label lblFavoritoApartamento;
    @FXML private Label lblFavoritoCapacidad;
    @FXML private Label lblFavoritoCaracteristicas;
    @FXML private ComboBox<String> cmbEstadoHistorial;
    @FXML private DatePicker dpDesdeHistorial;
    @FXML private DatePicker dpHastaHistorial;
    @FXML private Button btnBuscarHistorial;
    @FXML private TableView<Reserva> tablaHistorialUsuario;
    @FXML private TableColumn<Reserva,String> colHistorialCodigo;
    @FXML private TableColumn<Reserva,String> colHistorialApartamento;
    @FXML private TableColumn<Reserva,String> colHistorialEntrada;
    @FXML private TableColumn<Reserva,String> colHistorialSalida;
    @FXML private TableColumn<Reserva,String> colHistorialCanal;
    @FXML private TableColumn<Reserva,String> colHistorialEstado;
    @FXML private Button btnVerDetalleHistorial;
    @FXML private Button btnVerFolioHistorial;
    private SistemaSGA sistema;
    private Huesped huesped;
    private ServicioPerfilHuesped perfiles;

    private static <T> void col(TableColumn<T,String> columna,Function<T,String> f) {
        columna.setCellValueFactory(c->new ReadOnlyStringWrapper(f.apply(c.getValue())));
    }
    private static String dinero(BigDecimal valor) {return "$ "+valor.toPlainString();}
    @FXML private void initialize() {
        cmbEstadoReservaUsuario.getItems().add("Todos");
        cmbEstadoHistorial.getItems().add("Todos");
        for(EstadoReserva estado:EstadoReserva.values()){
            cmbEstadoReservaUsuario.getItems().add(estado.name());
            cmbEstadoHistorial.getItems().add(estado.name());
        }
        cmbEstadoReservaUsuario.setValue("Todos");
        cmbEstadoHistorial.setValue("Todos");
        col(colUsuarioCodigoReserva,Reserva::getCodigo);
        col(colUsuarioApartamento,x->x.getApartamento().getCodigo());
        col(colUsuarioEntrada,x->x.getEstancia().getFechaEntrada().toString());
        col(colUsuarioSalida,x->x.getEstancia().getFechaSalida().toString());
        col(colUsuarioNoches,x->""+x.calcularNumeroNoches());
        col(colUsuarioEstadoReserva,x->x.getEstado().name());
        col(colFolioCodigo,x->""+x.getFolio().getId());
        col(colFolioReserva,Reserva::getCodigo);
        col(colFolioTotal,x->dinero(x.getFolio().calcularTotalCargos()));
        col(colFolioPagado,x->dinero(x.getFolio().calcularTotalPagos()));
        col(colFolioSaldo,x->dinero(x.getFolio().calcularSaldo()));
        col(colMovimientoFecha,x->x.getFecha().toString());
        col(colMovimientoConcepto,Movimiento::getDescripcion);
        col(colMovimientoTipo,x->x.getTipo().name());
        col(colMovimientoValor,x->dinero(x.getMonto()));
        col(colFavoritoCodigo,Apartamento::getCodigo);
        col(colFavoritoNombre,Apartamento::getNombre);
        col(colFavoritoBloque,Apartamento::getBloque);
        col(colFavoritoCapacidad,x->""+x.getCapacidad());
        col(colFavoritoDormitorios,x->""+x.getNumeroDormitorios());
        col(colHistorialCodigo,Reserva::getCodigo);
        col(colHistorialApartamento,x->x.getApartamento().getCodigo());
        col(colHistorialEntrada,x->x.getEstancia().getFechaEntrada().toString());
        col(colHistorialSalida,x->x.getEstancia().getFechaSalida().toString());
        col(colHistorialCanal,x->x.getCanalReserva().name());
        col(colHistorialEstado,x->x.getEstado().name());
        btnActualizarPerfil.setOnAction(e->elegirHuesped());
        btnSolicitarReservaPortal.setOnAction(e->solicitarReservaPortal());
        btnCancelarReservaUsuario.setOnAction(e->cancelarReservaConDevolucion());
        btnActualizarReservasUsuario.setOnAction(e->actualizar());
        btnLimpiarFiltrosUsuario.setOnAction(e->{txtBuscarReservaUsuario.clear();cmbEstadoReservaUsuario.setValue("Todos");actualizar();});
        btnVerDetalleReservaUsuario.setOnAction(e->detalleReserva(tablaReservasUsuario.getSelectionModel().getSelectedItem()));
        btnConsultarFolioUsuario.setOnAction(e->mostrarFolio(tablaReservasUsuario.getSelectionModel().getSelectedItem()));
        btnVerMovimientosFolio.setOnAction(e->mostrarFolio(tablaFoliosUsuario.getSelectionModel().getSelectedItem()));
        btnActualizarFolios.setOnAction(e->actualizar());
        btnActualizarFavoritos.setOnAction(e->actualizar());
        btnAgregarFavorito.setOnAction(e->agregarFavorito());
        btnVerApartamentoFavorito.setOnAction(e->detalleFavorito(tablaFavoritosUsuario.getSelectionModel().getSelectedItem()));
        btnQuitarFavorito.setOnAction(e->quitarFavorito());
        btnBuscarHistorial.setOnAction(e->actualizar());
        btnVerDetalleHistorial.setOnAction(e->detalleReserva(tablaHistorialUsuario.getSelectionModel().getSelectedItem()));
        btnVerFolioHistorial.setOnAction(e->mostrarFolio(tablaHistorialUsuario.getSelectionModel().getSelectedItem()));
        tablaReservasUsuario.getSelectionModel().selectedItemProperty().addListener((o,a,b)->detalleReserva(b));
        tablaFavoritosUsuario.getSelectionModel().selectedItemProperty().addListener((o,a,b)->detalleFavorito(b));
        txtBuscarReservaUsuario.textProperty().addListener((o,a,b)->actualizar());
        cmbEstadoReservaUsuario.valueProperty().addListener((o,a,b)->actualizar());
        cmbEstadoHistorial.valueProperty().addListener((o,a,b)->actualizar());
        btnMenuInicio.setOnAction(e->navegar("VentanaPrincipal.fxml"));
        btnMenuReservas.setOnAction(e->navegar("VistaReservas.fxml"));
        btnMenuCalendario.setOnAction(e->navegar("VistaCalendarioOcupacion.fxml"));
        btnMenuUsuario.setOnAction(e->actualizar());
        btnMenuPreparacion.setOnAction(e->navegar("VistaPanelPreparacion.fxml"));
        btnMenuMapa.setOnAction(e->navegar("VistaMapaInteractivo.fxml"));
        for(Button b:new Button[]{btnMenuEstadisticas,btnMenuCargaDatos})
            b.setOnAction(e->aviso("Módulo pendiente",b.getText()));
        actualizar();
    }
    public void setSistema(SistemaSGA sistema) {
        btnCambiarRol.setOnAction(e -> {
            try {
                sistema.guardar();
                SesionActual.cerrar();
                new SeleccionPerfil(sistema,(Stage)btnCambiarRol.getScene().getWindow()).mostrar();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,"No se pudo cambiar de rol: "+ex.getMessage()).showAndWait();
            }
        });

        SesionActual.ocultar(btnMenuInicio, btnMenuReservas, btnMenuPreparacion, btnMenuMapa, btnMenuEstadisticas, btnMenuCargaDatos);
        SesionActual.ocultarSoloAdministrador(btnMenuEstadisticas, btnMenuCargaDatos);
        if (!SesionActual.esHuesped()) {
            for (javafx.scene.control.Button b : new javafx.scene.control.Button[]{btnMenuInicio, btnMenuReservas, btnMenuPreparacion, btnMenuMapa}) { b.setVisible(true); b.setManaged(true); }
        }
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
        this.perfiles=new ServicioPerfilHuesped(sistema.getServicioHuesped(),
                sistema.getServicioApartamento(),sistema.getServicioReserva());
        actualizar();
        // No se selecciona automáticamente el primer huésped: el operador debe identificarlo.
        if (SesionActual.esHuesped()) { huesped = SesionActual.huesped(); btnActualizarPerfil.setVisible(false); btnActualizarPerfil.setManaged(false); actualizar(); }
        else elegirHuesped();
    }
    private void elegirHuesped() {
        if (SesionActual.esHuesped()) return;
        if(sistema==null)return;
        TextInputDialog d=new TextInputDialog(huesped==null?"":huesped.getDocumento());
        d.setTitle("Seleccionar huésped");
        d.setHeaderText("Perfil del huésped");
        d.setContentText("Documento:");
        d.showAndWait().ifPresent(doc->{
            Huesped encontrado=sistema.getServicioHuesped().buscarPorDocumento(doc.strip());
            if(encontrado==null)aviso("No encontrado","No existe un huésped con ese documento.");
            else {huesped=encontrado;actualizar();}
        });
    }
    private void actualizar() {
        if(lblNombreUsuario==null)return;
        lblNombreUsuario.setText(huesped==null?"Seleccione un huésped":huesped.getNombre());
        lblDocumentoUsuario.setText(huesped==null?"—":huesped.getDocumento());
        lblCorreoUsuario.setText(huesped==null?"—":huesped.getCorreo());
        if(sistema==null||huesped==null) {
            lblTotalReservasUsuario.setText("0");lblProximasEstancias.setText("0");
            lblSaldoPendienteUsuario.setText("$ 0");lblTotalFavoritos.setText("0");
            return;
        }
        var reservas=FXCollections.<Reserva>observableArrayList();
        var folios=FXCollections.<Reserva>observableArrayList();
        var historial=FXCollections.<Reserva>observableArrayList();
        int total=0,proximas=0;
        BigDecimal saldo=BigDecimal.ZERO;
        String busqueda=txtBuscarReservaUsuario.getText()==null?"":txtBuscarReservaUsuario.getText().strip().toLowerCase(Locale.ROOT);
        for(Reserva reserva:sistema.getServicioReserva().listarReservas()){
            if(!reserva.getHuesped().getDocumento().equalsIgnoreCase(huesped.getDocumento()))continue;
            total++;
            if(!reserva.getEstancia().getFechaEntrada().isBefore(LocalDate.now())
                    &&(reserva.getEstado()==EstadoReserva.CONFIRMADA||reserva.getEstado()==EstadoReserva.PENDIENTE))proximas++;
            saldo=saldo.add(reserva.getFolio().calcularSaldo());
            folios.add(reserva);
            historial.add(reserva);
            if((busqueda.isEmpty()||reserva.getCodigo().toLowerCase(Locale.ROOT).contains(busqueda)
                    ||reserva.getApartamento().getCodigo().toLowerCase(Locale.ROOT).contains(busqueda))
                    &&("Todos".equals(cmbEstadoReservaUsuario.getValue())
                    ||reserva.getEstado().name().equals(cmbEstadoReservaUsuario.getValue())))reservas.add(reserva);
        }
        tablaReservasUsuario.setItems(reservas);
        tablaFoliosUsuario.setItems(folios);
        var historialFiltrado=FXCollections.<Reserva>observableArrayList();
        for(Reserva reserva:historial) {
            if(!"Todos".equals(cmbEstadoHistorial.getValue())
                    &&!reserva.getEstado().name().equals(cmbEstadoHistorial.getValue()))continue;
            LocalDate fecha=reserva.getEstancia().getFechaEntrada();
            if(dpDesdeHistorial.getValue()!=null&&fecha.isBefore(dpDesdeHistorial.getValue()))continue;
            if(dpHastaHistorial.getValue()!=null&&fecha.isAfter(dpHastaHistorial.getValue()))continue;
            historialFiltrado.add(reserva);
        }
        tablaHistorialUsuario.setItems(historialFiltrado);
        var favoritos=FXCollections.<Apartamento>observableArrayList();
        for(String codigo:perfiles.listarFavoritos(huesped.getDocumento())) {
            for(Apartamento a:sistema.getServicioApartamento().listarApartamentos())
                if(a.getCodigo().equalsIgnoreCase(codigo)){favoritos.add(a);break;}
        }
        tablaFavoritosUsuario.setItems(favoritos);
        lblTotalReservasUsuario.setText(""+total);
        lblProximasEstancias.setText(""+proximas);
        lblSaldoPendienteUsuario.setText(dinero(saldo));
        lblTotalFavoritos.setText(""+favoritos.size());
    }
    /** Busca disponibilidad, cotiza por noche y registra solicitudes WEB pendientes. */
    private void solicitarReservaPortal() {
        if (sistema == null || huesped == null) return;
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Solicitud de reserva por portal");
        dialogo.setHeaderText("Elige fechas y ocupantes, busca apartamentos libres y cotiza");
        ButtonType enviar = new ButtonType("Enviar solicitud", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(enviar, ButtonType.CANCEL);

        DatePicker entrada = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker salida = new DatePicker(LocalDate.now().plusDays(2));
        CheckBox titularSeHospeda = new CheckBox("El titular se hospeda");
        titularSeHospeda.setSelected(true);
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre completo");
        DatePicker nacimiento = new DatePicker();
        nacimiento.setPromptText("Fecha de nacimiento");
        javafx.collections.ObservableList<Ocupante> adicionales = FXCollections.observableArrayList();
        ListView<Ocupante> lista = new ListView<>(adicionales);
        lista.setPrefHeight(95);
        lista.setCellFactory(v -> new ListCell<>() {
            @Override protected void updateItem(Ocupante o, boolean empty) {
                super.updateItem(o, empty);
                setText(empty || o == null ? null : o.getNombre() + " — " + o.getFechaNacimiento());
            }
        });
        Button agregar = new Button("Agregar ocupante");
        agregar.setOnAction(e -> {
            if (nombre.getText().isBlank() || nacimiento.getValue() == null) {
                aviso("Datos incompletos", "Escribe el nombre y la fecha de nacimiento."); return;
            }
            try {
                adicionales.add(new Ocupante(nombre.getText().strip(), nacimiento.getValue()));
                nombre.clear(); nacimiento.setValue(null);
            } catch (Exception ex) { aviso("Ocupante inválido", ex.getMessage()); }
        });
        Button quitar = new Button("Quitar seleccionado");
        quitar.setOnAction(e -> adicionales.remove(lista.getSelectionModel().getSelectedItem()));

        ComboBox<String> orden = new ComboBox<>();
        orden.getItems().addAll("Precio (menor primero)", "Capacidad", "Dormitorios");
        orden.getSelectionModel().selectFirst();
        ComboBox<Apartamento> apartamento = new ComboBox<>();
        apartamento.setPrefWidth(410);
        apartamento.setCellFactory(v -> new ListCell<>() {
            @Override protected void updateItem(Apartamento a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getCodigo() + " - " + a.getNombre()
                    + " · " + a.getCapacidad() + " personas · " + a.getNumeroDormitorios() + " dorm.");
            }
        });
        apartamento.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Apartamento a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getCodigo() + " - " + a.getNombre());
            }
        });
        Button buscar = new Button("Buscar disponibles");
        Button cotizar = new Button("Cotizar y ver noches");
        Label encontrados = new Label("Indica fechas y ocupantes, luego pulsa Buscar disponibles.");
        Label resumen = new Label("Selecciona un apartamento disponible y pulsa Cotizar.");
        resumen.setWrapText(true);
        resumen.setMaxWidth(Double.MAX_VALUE);
        resumen.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #17365D;"
            + "-fx-background-color: #EAF2FC; -fx-padding: 12; -fx-background-radius: 8;");
        TextArea desglose = new TextArea();
        desglose.setEditable(false);
        desglose.setWrapText(false);
        desglose.setPrefRowCount(5);
        desglose.setPromptText("Aquí aparecerá el desglose noche por noche.");

        GridPane formulario = new GridPane();
        formulario.setHgap(12); formulario.setVgap(9); formulario.setPadding(new Insets(10));
        formulario.addRow(0, new Label("Entrada:"), entrada);
        formulario.addRow(1, new Label("Salida:"), salida);
        formulario.add(titularSeHospeda, 1, 2);
        formulario.addRow(3, new Label("Acompañante:"), nombre);
        formulario.addRow(4, new Label("Nacimiento:"), nacimiento);
        formulario.add(new javafx.scene.layout.HBox(8, agregar, quitar), 1, 5);
        formulario.addRow(6, new Label("Ocupantes:"), lista);
        formulario.addRow(7, new Label("Ordenar por:"), orden);
        formulario.add(buscar, 1, 8);
        formulario.addRow(9, new Label("Apartamentos libres:"), apartamento);
        formulario.add(encontrados, 1, 10);
        formulario.add(cotizar, 1, 11);
        VBox contenido = new VBox(10, formulario, resumen, desglose);
        contenido.setPadding(new Insets(12));
        contenido.setPrefWidth(760);
        dialogo.getDialogPane().setContent(new ScrollPane(contenido));
        dialogo.getDialogPane().setPrefWidth(810);
        dialogo.getDialogPane().setPrefHeight(740);

        final BigDecimal[] totalCotizado = {null};
        final String[] firmaCotizada = {null};
        final boolean[] resultadosVigentes = {false};
        Runnable invalidar = () -> {
            totalCotizado[0] = null; firmaCotizada[0] = null;
            resumen.setText("Selecciona un apartamento y pulsa Cotizar.");
            desglose.clear();
        };
        Runnable invalidarBusqueda = () -> {
            invalidar.run(); resultadosVigentes[0] = false;
            apartamento.getItems().clear();
            encontrados.setText("Los criterios cambiaron. Pulsa Buscar disponibles.");
        };
        entrada.valueProperty().addListener((o,a,b) -> invalidarBusqueda.run());
        salida.valueProperty().addListener((o,a,b) -> invalidarBusqueda.run());
        titularSeHospeda.selectedProperty().addListener((o,a,b) -> invalidarBusqueda.run());
        adicionales.addListener((javafx.collections.ListChangeListener<Ocupante>) c -> invalidarBusqueda.run());
        apartamento.valueProperty().addListener((o,a,b) -> invalidar.run());
        orden.valueProperty().addListener((o,a,b) -> invalidarBusqueda.run());

        // Se usa la misma lista de ocupantes para buscar, cotizar y registrar.
        java.util.function.Supplier<ListaDoble<Ocupante>> obtenerOcupantes = () -> {
            ListaDoble<Ocupante> personas = new ListaDoble<>();
            if (titularSeHospeda.isSelected())
                personas.agregarFinal(new Ocupante(huesped.getNombre(), huesped.getFechaNacimiento(), huesped));
            for (Ocupante o : adicionales) {
                if (titularSeHospeda.isSelected()
                    && o.getNombre().equalsIgnoreCase(huesped.getNombre())
                    && o.getFechaNacimiento().equals(huesped.getFechaNacimiento()))
                    throw new IllegalArgumentException("El titular está duplicado en acompañantes.");
                personas.agregarFinal(o);
            }
            if (personas.estaVacia()) throw new IllegalArgumentException("Debe hospedarse al menos una persona.");
            return personas;
        };
        java.util.function.Supplier<Estancia> obtenerEstancia = () -> {
            if (entrada.getValue() == null || salida.getValue() == null)
                throw new IllegalArgumentException("Selecciona ambas fechas.");
            if (entrada.getValue().isBefore(LocalDate.now()))
                throw new IllegalArgumentException("La entrada no puede ser anterior a hoy.");
            return new Estancia(entrada.getValue(), salida.getValue());
        };
        Runnable buscarDisponibles = () -> {
            try {
                Estancia estancia = obtenerEstancia.get();
                ListaDoble<Ocupante> personas = obtenerOcupantes.get();
                java.util.List<Apartamento> libres = new ArrayList<>();
                for (Apartamento a : sistema.getServicioReserva().getDisponibilidad()
                        .buscarDisponibles(estancia, personas.tamano())) libres.add(a);
                Comparator<Apartamento> comparador;
                if ("Capacidad".equals(orden.getValue()))
                    comparador = Comparator.comparingInt(Apartamento::getCapacidad);
                else if ("Dormitorios".equals(orden.getValue()))
                    comparador = Comparator.comparingInt(Apartamento::getNumeroDormitorios);
                else comparador = Comparator.comparing(a ->
                    sistema.getServicioCotizacion().calcularTotal(a, estancia, personas));
                libres.sort(comparador.thenComparing(Apartamento::getCodigo));
                apartamento.getItems().setAll(libres);
                resultadosVigentes[0] = true;
                encontrados.setText(libres.isEmpty() ? "No hay apartamentos libres para esos criterios."
                    : libres.size() + " apartamento(s) disponible(s). Selecciona uno.");
                if (!libres.isEmpty()) apartamento.getSelectionModel().selectFirst();
            } catch (Exception ex) { invalidarBusqueda.run(); aviso("No se pudo buscar", ex.getMessage()); }
        };
        buscar.setOnAction(e -> buscarDisponibles.run());
        cotizar.setOnAction(e -> {
            try {
                if (!resultadosVigentes[0]) throw new IllegalArgumentException("Primero busca apartamentos disponibles.");
                Apartamento a = apartamento.getValue();
                if (a == null) throw new IllegalArgumentException("Selecciona un apartamento disponible.");
                Estancia estancia = obtenerEstancia.get();
                ListaDoble<Ocupante> personas = obtenerOcupantes.get();
                if (!sistema.getServicioReserva().getDisponibilidad()
                        .estaDisponible(a.getCodigo(), estancia, personas.tamano()))
                    throw new IllegalArgumentException("El apartamento ya no está disponible. Busca nuevamente.");
                BigDecimal total = sistema.getServicioCotizacion().calcularTotal(a, estancia, personas);
                StringBuilder detalle = new StringBuilder();
                for (var noche : sistema.getServicioCotizacion().generarDesglose(a, estancia, personas)) {
                    detalle.append(noche.fecha()).append(" | ").append(noche.temporada())
                        .append(" | pagan ").append(noche.personasQuePagan())
                        .append(" | tarifa $ ").append(noche.tarifaPorPersona().toPlainString())
                        .append(" | subtotal $ ").append(noche.subtotal().toPlainString()).append("\n");
                }
                desglose.setText(detalle.toString());
                resumen.setText("Apartamento " + a.getCodigo() + " · " + estancia.calcularNumeroNoches()
                    + " noche(s) · " + personas.tamano() + " ocupante(s)"
                    + "\nTOTAL: " + dinero(total)
                    + "\nANTICIPO (30 %, se paga en Recepción): "
                    + dinero(sistema.getServicioCotizacion().calcularAnticipo(total)));
                totalCotizado[0] = total;
                firmaCotizada[0] = a.getCodigo() + "|" + estancia.getFechaEntrada() + "|"
                    + estancia.getFechaSalida() + "|" + titularSeHospeda.isSelected() + "|" + adicionales.toString();
            } catch (Exception ex) { invalidar.run(); aviso("No se pudo cotizar", ex.getMessage()); }
        });
        dialogo.getDialogPane().lookupButton(enviar).addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            if (totalCotizado[0] == null) {
                aviso("Primero cotiza", "Busca un apartamento y cotiza antes de enviar."); e.consume();
            }
        });
        if (dialogo.showAndWait().orElse(ButtonType.CANCEL) != enviar) return;
        try {
            Apartamento a = apartamento.getValue();
            Estancia estancia = obtenerEstancia.get();
            ListaDoble<Ocupante> personas = obtenerOcupantes.get();
            String firma = a.getCodigo() + "|" + estancia.getFechaEntrada() + "|"
                + estancia.getFechaSalida() + "|" + titularSeHospeda.isSelected() + "|" + adicionales.toString();
            if (!firma.equals(firmaCotizada[0]))
                throw new IllegalStateException("Cambiaste los datos. Vuelve a cotizar.");
            if (!sistema.getServicioReserva().getDisponibilidad()
                    .estaDisponible(a.getCodigo(), estancia, personas.tamano()))
                throw new IllegalStateException("El apartamento ya no está disponible.");
            String codigo = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            int idFolio = 1 + (int)(System.currentTimeMillis() % 999_999_999L);
            Reserva reserva = new Reserva(codigo, huesped, a, estancia,
                    CanalReserva.WEB, new Folio(idFolio), LocalDateTime.now(), null, personas);
            if (!sistema.getServicioReserva().registrarReserva(reserva, personas.tamano()))
                throw new IllegalStateException("No se pudo registrar la reserva; verifica la disponibilidad.");
            reserva.getFolio().agregarMovimiento(new Movimiento(1,
                "Alojamiento " + estancia.calcularNumeroNoches() + " noche(s) · " + personas.tamano() + " ocupante(s)",
                TipoMovimiento.CARGO_ALOJAMIENTO, totalCotizado[0], LocalDateTime.now()));
            sistema.guardar(); actualizar();
            aviso("Solicitud enviada", "Reserva " + codigo + " registrada como PENDIENTE."
                + "\nRecepción registra el anticipo y confirma la reserva.");
        } catch (Exception ex) { aviso("No se pudo solicitar", ex.getMessage()); }
    }

    private void detalleReserva(Reserva r) {
        lblUsuarioDetalleCodigo.setText(r==null?"—":r.getCodigo());
        lblUsuarioDetalleApartamento.setText(r==null?"—":r.getApartamento().getCodigo());
        lblUsuarioDetalleEntrada.setText(r==null?"—":r.getEstancia().getFechaEntrada().toString());
        lblUsuarioDetalleSalida.setText(r==null?"—":r.getEstancia().getFechaSalida().toString());
        lblUsuarioDetalleEstado.setText(r==null?"—":r.getEstado().name());
    }
    private void mostrarFolio(Reserva r) {
        if(r==null){aviso("Selecciona una reserva","Primero selecciona un folio.");return;}
        var movimientos=FXCollections.<Movimiento>observableArrayList();
        for(Movimiento m:r.getFolio().getMovimientos())movimientos.add(m);
        tablaMovimientosFolioUsuario.setItems(movimientos);
        lblSaldoFolioSeleccionado.setText(dinero(r.getFolio().calcularSaldo()));
        tablaFoliosUsuario.getSelectionModel().select(r);
        tablaFoliosUsuario.scrollTo(r);
        tabsUsuario.getSelectionModel().select(tabFoliosUsuario);
    }
    private void detalleFavorito(Apartamento a) {
        lblFavoritoApartamento.setText(a==null?"—":a.getCodigo()+" · "+a.getNombre());
        lblFavoritoCapacidad.setText(a==null?"—":""+a.getCapacidad());
        lblFavoritoCaracteristicas.setText(a==null?"—":a.getCaracteristicas());
    }
    private void agregarFavorito() {
        if(sistema==null||huesped==null){aviso("Selecciona un huésped","Primero selecciona un perfil.");return;}
        var opciones=FXCollections.<Apartamento>observableArrayList();
        for(Apartamento a:sistema.getServicioApartamento().listarApartamentos())
            opciones.add(a);
        if(opciones.isEmpty()){aviso("Sin apartamentos","No hay apartamentos registrados.");return;}
        var etiquetas=FXCollections.<String>observableArrayList();
        for(Apartamento apartamento:opciones)
            etiquetas.add(apartamento.getCodigo()+" - "+apartamento.getNombre());
        ChoiceDialog<String> dialogo=new ChoiceDialog<>(etiquetas.get(0),etiquetas);
        dialogo.setTitle("Agregar a favoritos");
        dialogo.setHeaderText("Selecciona el apartamento que quieres guardar");
        dialogo.setContentText("Apartamento:");
        dialogo.showAndWait().ifPresent(seleccion->{
            Apartamento a=null;
            for(int i=0;i<etiquetas.size();i++){
                if(etiquetas.get(i).equals(seleccion)){
                    a=opciones.get(i);
                    break;
                }
            }
            if(a==null)return;
            try {
                boolean agregado=perfiles.agregarFavorito(huesped.getDocumento(),a.getCodigo());
                if(!agregado){aviso("Ya es favorito",a.getCodigo()+" ya está en tu lista.");return;}
                sistema.guardar();
                actualizar();
                for(Apartamento favorito:tablaFavoritosUsuario.getItems())
                    if(favorito.getCodigo().equalsIgnoreCase(a.getCodigo())) {
                        tablaFavoritosUsuario.getSelectionModel().select(favorito);
                        tablaFavoritosUsuario.scrollTo(favorito);
                        break;
                    }
            }catch(Exception ex){aviso("Error al guardar",ex.toString());}
        });
    }
    private void quitarFavorito() {
        if(huesped==null)return;
        Apartamento a=tablaFavoritosUsuario.getSelectionModel().getSelectedItem();
        if(a==null){aviso("Selecciona un favorito","Primero selecciona un apartamento.");return;}
        Alert confirmar=new Alert(Alert.AlertType.CONFIRMATION,"¿Quitar "+a.getCodigo()+" de favoritos?",ButtonType.YES,ButtonType.NO);
        if(confirmar.showAndWait().orElse(ButtonType.NO)!=ButtonType.YES)return;
        try {
            perfiles.quitarFavorito(huesped.getDocumento(),a.getCodigo());
            sistema.guardar();
            actualizar();
        }catch(Exception ex){aviso("Error al guardar",ex.toString());}
    }
    private void navegar(String fxml) {
        if (SesionActual.esHuesped() && !fxml.equals("VistaCalendarioOcupacion.fxml") && !fxml.equals("VistaPanelUsuario.fxml")) { aviso("Acceso restringido", "Vista reservada al personal."); return; }
        if(sistema==null)return;
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource(fxml));
            Scene scene=new Scene(loader.load());
            Object ctrl=loader.getController();
            if(ctrl instanceof VentanaPrincipalController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaReservasController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaCalendarioOcupacionController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaPanelPreparacionController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaMapaInteractivoController c)c.setSistema(sistema);
            ((Stage)btnMenuInicio.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){aviso("Error de navegación",ex.toString());}
    }
    /** Previsualiza el reintegro completo antes de cancelar la reserva del huésped. */
    private void cancelarReservaConDevolucion() {
        if (sistema == null || huesped == null) return;
        Reserva reserva = tablaReservasUsuario.getSelectionModel().getSelectedItem();
        if (reserva == null) {
            aviso("Selecciona una reserva", "Selecciona primero la reserva que deseas cancelar.");
            return;
        }
        if (!reserva.getHuesped().getDocumento().equals(huesped.getDocumento())) {
            aviso("Acceso restringido", "Solo puedes cancelar tus propias reservas.");
            return;
        }
        if (reserva.getEstado() != EstadoReserva.PENDIENTE
                && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            aviso("No se puede cancelar", "Solo puedes cancelar reservas PENDIENTES o CONFIRMADAS.");
            return;
        }
        try {
            ServicioCancelacionDevolucion servicio =
                    new ServicioCancelacionDevolucion(sistema.getServicioReserva());
            BigDecimal porcentaje = new BigDecimal("100");
            BigDecimal pagado = reserva.getFolio().calcularTotalPagos();
            BigDecimal devolucion = servicio.previsualizarDevolucion(reserva.getCodigo(), porcentaje);
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Cancelar reserva");
            confirmacion.setHeaderText("¿Cancelar la reserva " + reserva.getCodigo() + "?");
            confirmacion.setContentText("Total pagado: " + dinero(pagado)
                    + "\nPolítica de devolución: 100 %"
                    + "\nImporte a devolver: " + dinero(devolucion)
                    + "\n\nLa devolución se registrará en el folio."
                    + " La entrega del dinero se gestiona en Recepción.");
            Optional<ButtonType> respuesta = confirmacion.showAndWait();
            if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) return;
            int siguienteId = 1;
            for (Movimiento movimiento : reserva.getFolio().getMovimientos()) {
                if (movimiento.getId() >= siguienteId) siguienteId = movimiento.getId() + 1;
            }
            servicio.cancelarYRegistrarDevolucion(reserva.getCodigo(), porcentaje, siguienteId);
            sistema.guardar();
            actualizar();
            aviso("Reserva cancelada", "La reserva se canceló correctamente. Devolución registrada: "
                    + dinero(devolucion) + ". Gestiona la entrega del dinero en Recepción.");
        } catch (Exception ex) {
            aviso("No se pudo cancelar", ex.getMessage() == null ? ex.toString() : ex.getMessage());
        }
    }

    private void aviso(String titulo,String mensaje) {
        Alert a=new Alert(Alert.AlertType.INFORMATION,mensaje);
        a.setTitle(titulo);a.setHeaderText(null);a.showAndWait();
    }
}
