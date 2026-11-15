package uniquindio.edu.co.proyectofinal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

/** Panel de un huésped elegido por documento. No presupone autenticación. */
public class VistaPanelUsuarioController {
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
        btnActualizarReservasUsuario.setOnAction(e->actualizar());
        btnLimpiarFiltrosUsuario.setOnAction(e->{txtBuscarReservaUsuario.clear();cmbEstadoReservaUsuario.setValue("Todos");actualizar();});
        btnVerDetalleReservaUsuario.setOnAction(e->detalleReserva(tablaReservasUsuario.getSelectionModel().getSelectedItem()));
        btnConsultarFolioUsuario.setOnAction(e->mostrarFolio(tablaReservasUsuario.getSelectionModel().getSelectedItem()));
        btnVerMovimientosFolio.setOnAction(e->mostrarFolio(tablaFoliosUsuario.getSelectionModel().getSelectedItem()));
        btnActualizarFolios.setOnAction(e->actualizar());
        btnActualizarFavoritos.setOnAction(e->actualizar());
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
        this.sistema=Objects.requireNonNull(sistema);
        this.perfiles=new ServicioPerfilHuesped(sistema.getServicioHuesped(),
                sistema.getServicioApartamento(),sistema.getServicioReserva());
        actualizar();
        // No se selecciona automáticamente el primer huésped: el operador debe identificarlo.
        elegirHuesped();
    }
    private void elegirHuesped() {
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
    }
    private void detalleFavorito(Apartamento a) {
        lblFavoritoApartamento.setText(a==null?"—":a.getCodigo()+" · "+a.getNombre());
        lblFavoritoCapacidad.setText(a==null?"—":""+a.getCapacidad());
        lblFavoritoCaracteristicas.setText(a==null?"—":a.getCaracteristicas());
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
    private void aviso(String titulo,String mensaje) {
        Alert a=new Alert(Alert.AlertType.INFORMATION,mensaje);
        a.setTitle(titulo);a.setHeaderText(null);a.showAndWait();
    }
}
