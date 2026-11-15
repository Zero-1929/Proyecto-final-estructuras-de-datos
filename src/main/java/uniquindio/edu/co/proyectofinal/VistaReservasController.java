package uniquindio.edu.co.proyectofinal;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.*;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;

/** Vista de reservas; todas las operaciones utilizan la instancia compartida de SistemaSGA. */
public class VistaReservasController {
    @FXML private Label lblTotalReservas,lblPendientes,lblConfirmadas,lblEnCurso,
            lblDetalleTitular,lblDetalleApartamento,lblDetalleOcupantes,lblDetalleNoches,lblDetalleFolio,
            lblTotalCotizacion,lblAnticipo,lblCantidadCola,lblResultadoExterno;
    @FXML private TextField txtBuscarReserva,txtDocumentoTitular,txtApartamentoSeleccionado,
            txtHoraLlegada,txtNombreOcupante;
    @FXML private ComboBox<String> cmbEstadoReserva,cmbCanalReserva,cmbOrdenDisponibilidad,cmbNuevoCanal;
    @FXML private DatePicker dpFiltroEntrada,dpBusquedaEntrada,dpBusquedaSalida,dpNuevaEntrada,
            dpNuevaSalida,dpNacimientoOcupante;
    @FXML private Spinner<Integer> spnCantidadPersonas;
    @FXML private CheckBox chkTitularSeHospeda;
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva,String> colReservaCodigo,colReservaTitular,colReservaApartamento,
            colReservaEntrada,colReservaSalida,colReservaCanal,colReservaEstado;
    @FXML private TableView<Apartamento> tablaDisponibilidad;
    @FXML private TableColumn<Apartamento,String> colDisponibleCodigo,colDisponibleNombre,
            colDisponibleBloque,colDisponibleCapacidad,colDisponibleDormitorios,colDisponiblePrecio,colDisponibleEstado;
    @FXML private TableView<Ocupante> tablaOcupantesNuevaReserva;
    @FXML private TableColumn<Ocupante,String> colOcupanteNombre,colOcupanteNacimiento,colOcupantePago;
    @FXML private TableView<SolicitudExterna> tablaColaExterna,tablaConflictos;
    @FXML private TableColumn<SolicitudExterna,String> colColaOrden,colColaSolicitud,colColaRecibida,
            colColaTitular,colColaApartamento,colColaEntrada,colColaSalida,colColaEstado,
            colConflictoSolicitud,colConflictoApartamento,colConflictoFechas,colConflictoReserva,colConflictoMotivo;
    @FXML private Button btnLimpiarFiltros,btnVerDetalle,btnConfirmarReserva,btnCancelarReserva,
            btnCheckIn,btnCheckOut,btnVerFolio,btnRegistrarPago,btnBuscarDisponibilidad,btnSeleccionarApartamento,
            btnAgregarOcupante,btnQuitarOcupante,btnCotizar,btnCrearReserva,btnActualizarCola,
            btnProcesarSiguiente,btnActualizarConflictos,btnMenuInicio,btnMenuReservas,
            btnMenuCalendario,btnMenuUsuario,btnMenuPreparacion,btnMenuMapa,btnMenuEstadisticas,
            btnMenuCargaDatos;

    private SistemaSGA sistema;
    private final javafx.collections.ObservableList<Ocupante> ocupantes = FXCollections.observableArrayList();
    private BigDecimal ultimaCotizacion;
    private Apartamento apartamentoElegido;
    private Estancia estanciaCotizada;
    private String documentoTitularCotizado;
    private boolean titularIncluidoCotizado;

    @FXML private void initialize() {
        cmbEstadoReserva.getItems().add("Todos");
        for (EstadoReserva e:EstadoReserva.values()) cmbEstadoReserva.getItems().add(e.name());
        cmbEstadoReserva.setValue("Todos");
        cmbCanalReserva.getItems().add("Todos");
        for (CanalReserva c:CanalReserva.values()) {
            cmbCanalReserva.getItems().add(c.name());
            cmbNuevoCanal.getItems().add(c.name());
        }
        cmbCanalReserva.setValue("Todos");
        cmbNuevoCanal.setValue(CanalReserva.RECEPCION.name());
        cmbOrdenDisponibilidad.getItems().addAll("Código","Capacidad","Precio");
        cmbOrdenDisponibilidad.setValue("Código");
        spnCantidadPersonas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,30,2));
        dpBusquedaEntrada.setValue(LocalDate.now().plusDays(1));
        dpBusquedaSalida.setValue(LocalDate.now().plusDays(2));
        dpNuevaEntrada.setValue(dpBusquedaEntrada.getValue());
        dpNuevaSalida.setValue(dpBusquedaSalida.getValue());

        columna(colReservaCodigo,Reserva::getCodigo);
        columna(colReservaTitular,x->x.getHuesped().getNombre());
        columna(colReservaApartamento,x->x.getApartamento().getCodigo());
        columna(colReservaEntrada,x->x.getEstancia().getFechaEntrada().toString());
        columna(colReservaSalida,x->x.getEstancia().getFechaSalida().toString());
        columna(colReservaCanal,x->x.getCanalReserva().name());
        columna(colReservaEstado,x->x.getEstado().name());
        columna(colDisponibleCodigo,Apartamento::getCodigo);
        columna(colDisponibleNombre,Apartamento::getNombre);
        columna(colDisponibleBloque,Apartamento::getBloque);
        columna(colDisponibleCapacidad,x->""+x.getCapacidad());
        columna(colDisponibleDormitorios,x->""+x.getNumeroDormitorios());
        columna(colDisponiblePrecio,x->x.getTarifaBaja()==null?"—":x.getTarifaBaja().toPlainString());
        columna(colDisponibleEstado,x->x.getEstado().name());
        columna(colOcupanteNombre,Ocupante::getNombre);
        columna(colOcupanteNacimiento,x->x.getFechaNacimiento().toString());
        columna(colOcupantePago,x->x.getFechaNacimiento().plusYears(6).isAfter(
                dpNuevaEntrada.getValue()==null?LocalDate.now():dpNuevaEntrada.getValue())?"No":"Sí");
        tablaOcupantesNuevaReserva.setItems(ocupantes);
        columna(colColaOrden,x->x.getFechaRecepcion().toString());
        columna(colColaSolicitud,SolicitudExterna::getIdSolicitud);
        columna(colColaRecibida,x->x.getFechaRecepcion().toString());
        columna(colColaTitular,SolicitudExterna::getNombreSolicitante);
        columna(colColaApartamento,SolicitudExterna::getCodigoApartamento);
        columna(colColaEntrada,x->x.getFechaEntrada().toString());
        columna(colColaSalida,x->x.getFechaSalida().toString());
        columna(colColaEstado,x->x.getEstado().name());
        columna(colConflictoSolicitud,SolicitudExterna::getIdSolicitud);
        columna(colConflictoApartamento,SolicitudExterna::getCodigoApartamento);
        columna(colConflictoFechas,x->x.getFechaEntrada()+" / "+x.getFechaSalida());
        columna(colConflictoReserva,x->"—");
        columna(colConflictoMotivo,x->x.getMotivoRechazo()==null?"—":x.getMotivoRechazo());

        txtBuscarReserva.textProperty().addListener((o,a,b)->actualizarReservas());
        cmbEstadoReserva.valueProperty().addListener((o,a,b)->actualizarReservas());
        cmbCanalReserva.valueProperty().addListener((o,a,b)->actualizarReservas());
        dpFiltroEntrada.valueProperty().addListener((o,a,b)->actualizarReservas());
        tablaReservas.getSelectionModel().selectedItemProperty().addListener((o,a,b)->detalle(b));
        btnLimpiarFiltros.setOnAction(e->{txtBuscarReserva.clear();cmbEstadoReserva.setValue("Todos");
            cmbCanalReserva.setValue("Todos");dpFiltroEntrada.setValue(null);actualizarReservas();});
        btnVerDetalle.setOnAction(e->detalle(tablaReservas.getSelectionModel().getSelectedItem()));
        btnConfirmarReserva.setOnAction(e->operarReserva("Confirmar"));
        btnCancelarReserva.setOnAction(e->operarReserva("Cancelar"));
        btnCheckIn.setOnAction(e->operarReserva("Check-in"));
        btnCheckOut.setOnAction(e->operarReserva("Check-out"));
        btnVerFolio.setOnAction(e->verFolio());
        btnRegistrarPago.setOnAction(e->registrarPago());
        btnBuscarDisponibilidad.setOnAction(e->buscarDisponibilidad());
        btnSeleccionarApartamento.setOnAction(e->seleccionarApartamento());
        btnAgregarOcupante.setOnAction(e->agregarOcupante());
        btnQuitarOcupante.setOnAction(e->{
            Ocupante o=tablaOcupantesNuevaReserva.getSelectionModel().getSelectedItem();
            if(o!=null){ocupantes.remove(o);invalidarCotizacion();}
        });
        btnCotizar.setOnAction(e->cotizar());
        btnCrearReserva.setOnAction(e->crearReserva());
        btnActualizarCola.setOnAction(e->actualizarExterno());
        btnProcesarSiguiente.setOnAction(e->procesarExterno());
        btnActualizarConflictos.setOnAction(e->actualizarExterno());
        btnMenuInicio.setOnAction(e->abrirInicio());
        btnMenuReservas.setOnAction(e->actualizarTodo());
        btnMenuCalendario.setOnAction(e->abrirCalendario());
        btnMenuUsuario.setOnAction(e->abrirUsuario());
        btnMenuPreparacion.setOnAction(e->abrirPreparacion());
        btnMenuMapa.setOnAction(e->abrirMapa());
        for (Button b:new Button[]{btnMenuEstadisticas,btnMenuCargaDatos})
            b.setOnAction(e->informar("Pendiente",b.getText()+" todavía no tiene su controlador conectado."));
        dpNuevaEntrada.valueProperty().addListener((o,a,b)->invalidarCotizacion());
        dpNuevaSalida.valueProperty().addListener((o,a,b)->invalidarCotizacion());
        cmbNuevoCanal.valueProperty().addListener((o,a,b)->invalidarCotizacion());
        txtDocumentoTitular.textProperty().addListener((o,a,b)->invalidarCotizacion());
        chkTitularSeHospeda.selectedProperty().addListener((o,a,b)->invalidarCotizacion());
        actualizarTodo();
    }
    private static <T> void columna(TableColumn<T,String> col,Function<T,String> f) {
        col.setCellValueFactory(c->new ReadOnlyStringWrapper(f.apply(c.getValue())));
    }
    public void setSistema(SistemaSGA sistema) {
        this.sistema=Objects.requireNonNull(sistema);
        actualizarTodo();
    }
    private void actualizarTodo() { actualizarReservas();actualizarExterno(); }
    private void actualizarReservas() {
        if(sistema==null)return;
        var datos=FXCollections.<Reserva>observableArrayList();
        int total=0,pend=0,conf=0,curso=0;
        for(Reserva r:sistema.getServicioReserva().listarReservas()) {
            total++;
            switch(r.getEstado()) {
                case PENDIENTE->pend++;
                case CONFIRMADA->conf++;
                case EN_CURSO->curso++;
                default->{}
            }
            String q=txtBuscarReserva.getText()==null?"":txtBuscarReserva.getText().strip().toLowerCase();
            if(!q.isEmpty()&&!r.getCodigo().toLowerCase().contains(q)
                    &&!r.getHuesped().getNombre().toLowerCase().contains(q)
                    &&!r.getApartamento().getCodigo().toLowerCase().contains(q))continue;
            if(!"Todos".equals(cmbEstadoReserva.getValue())
                    &&!r.getEstado().name().equals(cmbEstadoReserva.getValue()))continue;
            if(!"Todos".equals(cmbCanalReserva.getValue())
                    &&!r.getCanalReserva().name().equals(cmbCanalReserva.getValue()))continue;
            if(dpFiltroEntrada.getValue()!=null
                    &&!dpFiltroEntrada.getValue().equals(r.getEstancia().getFechaEntrada()))continue;
            datos.add(r);
        }
        tablaReservas.setItems(datos);
        lblTotalReservas.setText(""+total);lblPendientes.setText(""+pend);
        lblConfirmadas.setText(""+conf);lblEnCurso.setText(""+curso);
    }
    private void detalle(Reserva r) {
        lblDetalleTitular.setText(r==null?"—":r.getHuesped().getNombre());
        lblDetalleApartamento.setText(r==null?"—":r.getApartamento().getCodigo());
        lblDetalleOcupantes.setText(r==null?"—":"Consultar ocupantes en el registro");
        lblDetalleNoches.setText(r==null?"—":""+r.calcularNumeroNoches());
        lblDetalleFolio.setText(r==null?"—":""+r.getFolio().getId());
    }
    private Reserva seleccionada() {
        Reserva r=tablaReservas.getSelectionModel().getSelectedItem();
        if(r==null)throw new IllegalArgumentException("Selecciona una reserva.");
        return r;
    }
    private void operarReserva(String operacion) {
        try {
            Reserva r=seleccionada();
            if(!confirmar("Confirmar operación",operacion+" reserva "+r.getCodigo()+"?"))return;
            switch(operacion) {
                case "Confirmar"->{
                    if (!asegurarCargoAlojamiento(r)) return;
                    BigDecimal total = r.getFolio().calcularTotalCargos();
                    sistema.getServicioReserva().confirmarReserva(r.getCodigo(), total);
                }
                case "Cancelar"->sistema.getServicioReserva().cancelarReserva(r.getCodigo());
                case "Check-in"->sistema.getServicioReserva().realizarCheckIn(r.getCodigo());
                case "Check-out"->{
                    if (r.getFolio().calcularSaldo().signum() > 0)
                        throw new IllegalStateException("Hay un saldo pendiente de $ " + dinero(r.getFolio().calcularSaldo())
                                + ". Registra el pago antes de finalizar la estancia.");
                    sistema.getServicioReserva().realizarCheckOut(r.getCodigo());
                }
            }
            guardar();actualizarTodo();informar("Operación completada",operacion+" realizado.");
        }catch(Exception ex){error(ex);}
    }
    private static String dinero(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static int siguienteMovimientoId(Folio folio) {
        int maximo = 0;
        for (Movimiento movimiento : folio.getMovimientos())
            maximo = Math.max(maximo, movimiento.getId());
        if (maximo == Integer.MAX_VALUE) throw new IllegalStateException("No quedan identificadores de movimiento.");
        return maximo + 1;
    }

    /**
     * Los datos históricos y algunas reservas externas no guardan ocupantes ni edades.
     * Por ello no se inventa un precio: se solicita el número de personas que pagan.
     * Nunca duplica un cargo ya registrado ni modifica directamente el TXT.
     */
    private boolean asegurarCargoAlojamiento(Reserva reserva) throws IOException {
        Folio folio = reserva.getFolio();
        if (folio.calcularTotalCargos().signum() > 0) return true;
        if (reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.NO_PRESENTADO)
            throw new IllegalStateException("No se puede regularizar una reserva cancelada o no presentada.");

        TextInputDialog dialogo = new TextInputDialog("1");
        dialogo.setTitle("Regularizar alojamiento - " + reserva.getCodigo());
        dialogo.setHeaderText("Esta reserva no tiene cargo de alojamiento registrado");
        dialogo.setContentText("No se conservó el detalle de ocupantes de esta reserva."
                + "\nIndica cuántas personas pagan (menores de 6 años no pagan):");
        Optional<String> respuesta = dialogo.showAndWait();
        if (respuesta.isEmpty()) return false;
        int personas;
        try {
            personas = Integer.parseInt(respuesta.get().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Introduce un número entero de personas que pagan.");
        }
        if (personas < 1 || personas > reserva.getApartamento().getCapacidad())
            throw new IllegalArgumentException("Las personas que pagan deben estar entre 1 y la capacidad del apartamento.");

        BigDecimal total = BigDecimal.ZERO;
        for (LocalDate noche : reserva.getEstancia()) {
            TipoTemporada temporada = sistema.getServicioCotizacion().obtenerTemporada(noche);
            BigDecimal tarifa = reserva.getApartamento().obtenerTarifa(temporada);
            total = total.add(tarifa.multiply(BigDecimal.valueOf(personas)));
        }
        if (total.signum() <= 0)
            throw new IllegalStateException("La tarifa calculada es cero. Revisa el apartamento y las temporadas.");
        if (!confirmar("Registrar cargo de alojamiento",
                "Reserva: " + reserva.getCodigo()
                        + "\nPersonas que pagan: " + personas
                        + "\nNoches: " + reserva.calcularNumeroNoches()
                        + "\nCargo a registrar: $ " + dinero(total)
                        + "\n\nEste cargo se añadirá al folio y se guardará. ¿Continuar?")) return false;
        folio.agregarMovimiento(new Movimiento(siguienteMovimientoId(folio),
                "Cargo de alojamiento regularizado - " + reserva.getCodigo(),
                TipoMovimiento.CARGO_ALOJAMIENTO, total, LocalDateTime.now()));
        guardar();
        actualizarTodo();
        return true;
    }

    private void registrarPago() {
        try {
            Reserva r = seleccionada();
            if (r.getEstado() == EstadoReserva.CANCELADA || r.getEstado() == EstadoReserva.NO_PRESENTADO)
                throw new IllegalStateException("No se pueden registrar pagos para una reserva cancelada o no presentada.");
            if (!asegurarCargoAlojamiento(r)) return;
            Folio folio = r.getFolio();
            BigDecimal total = folio.calcularTotalCargos();
            BigDecimal saldo = folio.calcularSaldo();
            if (saldo.signum() <= 0)
                throw new IllegalStateException("Esta reserva ya está pagada. Saldo pendiente: $ " + dinero(saldo));

            BigDecimal anticipoRequerido = sistema.getServicioCotizacion().calcularAnticipo(total);
            BigDecimal pagado = folio.calcularTotalPagos();
            BigDecimal anticipoFaltante = anticipoRequerido.subtract(pagado).max(BigDecimal.ZERO).min(saldo);
            boolean esWeb = r.getCanalReserva() == CanalReserva.WEB;

            ButtonType botonAnticipo = new ButtonType("Cobrar anticipo 30 %", ButtonBar.ButtonData.OTHER);
            ButtonType botonSaldo = new ButtonType("Cobrar saldo completo", ButtonBar.ButtonData.OTHER);
            ButtonType botonAbono = new ButtonType("Otro abono", ButtonBar.ButtonData.OTHER);
            Alert opciones = new Alert(Alert.AlertType.CONFIRMATION);
            opciones.setTitle("Cobros de reserva " + r.getCodigo());
            opciones.setHeaderText("Selecciona qué deseas cobrar");
            opciones.setContentText("Total alojamiento: $ " + dinero(total)
                    + "\nPagado: $ " + dinero(pagado)
                    + "\nSaldo pendiente: $ " + dinero(saldo)
                    + (esWeb ? "\nAnticipo WEB obligatorio para confirmar (30 %): $ " + dinero(anticipoRequerido)
                    : "\nEn RECEPCIÓN el anticipo no es obligatorio."));
            opciones.getButtonTypes().clear();
            if (esWeb && anticipoFaltante.signum() > 0) opciones.getButtonTypes().add(botonAnticipo);
            opciones.getButtonTypes().addAll(botonSaldo, botonAbono, ButtonType.CANCEL);
            Optional<ButtonType> eleccion = opciones.showAndWait();
            if (eleccion.isEmpty() || eleccion.get() == ButtonType.CANCEL) return;

            BigDecimal monto;
            String concepto;
            if (eleccion.get() == botonAnticipo) {
                monto = anticipoFaltante;
                concepto = "Pago de anticipo WEB (30 %) - " + r.getCodigo();
            } else if (eleccion.get() == botonSaldo) {
                monto = saldo;
                concepto = pagado.signum() > 0
                        ? "Pago de saldo restante - " + r.getCodigo()
                        : "Pago completo de alojamiento - " + r.getCodigo();
            } else {
                TextInputDialog dialog = new TextInputDialog(dinero(saldo));
                dialog.setTitle("Otro abono - " + r.getCodigo());
                dialog.setHeaderText("Saldo pendiente: $ " + dinero(saldo));
                dialog.setContentText("Valor del abono (COP):");
                Optional<String> respuesta = dialog.showAndWait();
                if (respuesta.isEmpty()) return;
                monto = new BigDecimal(respuesta.get().trim().replace(",", "."));
                concepto = "Abono a alojamiento - " + r.getCodigo();
            }
            if (monto.signum() <= 0 || monto.compareTo(saldo) > 0)
                throw new IllegalArgumentException("El cobro debe ser mayor que cero y no superar el saldo pendiente.");
            if (!confirmar("Confirmar cobro", "Registrar cobro de $ " + dinero(monto)
                    + "\nConcepto: " + concepto + "?")) return;
            folio.agregarMovimiento(new Movimiento(siguienteMovimientoId(folio),
                    concepto, TipoMovimiento.PAGO, monto, LocalDateTime.now()));
            guardar();
            verFolio();
        } catch (Exception ex) { error(ex); }
    }

    private void verFolio() {
        try {
            Reserva r=seleccionada();
            Folio f = r.getFolio();
            StringBuilder detalle = new StringBuilder("Cargos: $ " + dinero(f.calcularTotalCargos())
                    + "\nPagos: $ " + dinero(f.calcularTotalPagos())
                    + "\nSaldo pendiente: $ " + dinero(f.calcularSaldo())
                    + "\nMovimientos: " + f.cantidadMovimientos() + "\n");
            for (Movimiento m : f.getMovimientos())
                detalle.append("\n").append(m.getFecha()).append(" | ").append(m.getTipo())
                        .append(" | $ ").append(dinero(m.getMonto())).append(" | ").append(m.getDescripcion());
            informar("Folio " + f.getId(), detalle.toString());
        }catch(Exception ex){error(ex);}
    }

    private Huesped titular() {
        String documento = txtDocumentoTitular.getText().strip();
        Huesped h = sistema.getServicioHuesped().buscarPorDocumento(documento);
        if (h == null) throw new IllegalArgumentException("El titular debe estar registrado en Huéspedes.");
        return h;
    }

    private ListaDoble<Ocupante> ocupantesParaCotizar(Huesped h) {
        ListaDoble<Ocupante> lista = new ListaDoble<>();
        if (chkTitularSeHospeda.isSelected())
            lista.agregarFinal(new Ocupante(h.getNombre(), h.getFechaNacimiento(), h));
        for (Ocupante o : ocupantes) {
            if (o.getNombre().equalsIgnoreCase(h.getNombre())
                    && o.getFechaNacimiento().equals(h.getFechaNacimiento()))
                throw new IllegalArgumentException("El titular ya figura como acompañante. No lo agregues dos veces.");
            lista.agregarFinal(o);
        }
        if (lista.estaVacia()) throw new IllegalArgumentException("Debe hospedarse al menos una persona.");
        return lista;
    }

    private Estancia fechas(DatePicker entrada,DatePicker salida) {
        return new Estancia(Objects.requireNonNull(entrada.getValue(),"Fecha de entrada requerida"),
                Objects.requireNonNull(salida.getValue(),"Fecha de salida requerida"));
    }
    private void buscarDisponibilidad() {
        try {
            Estancia fechas=fechas(dpBusquedaEntrada,dpBusquedaSalida);
            var resultado=FXCollections.<Apartamento>observableArrayList();
            for(Apartamento a:sistema.getServicioReserva().getDisponibilidad()
                    .buscarDisponibles(fechas,spnCantidadPersonas.getValue()))resultado.add(a);
            Comparator<Apartamento> orden=switch(cmbOrdenDisponibilidad.getValue()) {
                case "Capacidad"->Comparator.comparingInt(Apartamento::getCapacidad);
                case "Precio"->Comparator.comparing(Apartamento::getTarifaBaja);
                default->Comparator.comparing(Apartamento::getCodigo);
            };
            resultado.sort(orden);
            tablaDisponibilidad.setItems(resultado);
            informar("Disponibilidad","Se encontraron "+resultado.size()+" apartamentos.");
        }catch(Exception ex){error(ex);}
    }
    private void seleccionarApartamento() {
        Apartamento a=tablaDisponibilidad.getSelectionModel().getSelectedItem();
        if(a==null){error(new IllegalArgumentException("Selecciona un apartamento disponible."));return;}
        apartamentoElegido=a;
        txtApartamentoSeleccionado.setText(a.getCodigo());
        dpNuevaEntrada.setValue(dpBusquedaEntrada.getValue());
        dpNuevaSalida.setValue(dpBusquedaSalida.getValue());
        invalidarCotizacion();
    }
    private void agregarOcupante() {
        try {
            if(txtNombreOcupante.getText().isBlank()||dpNacimientoOcupante.getValue()==null)
                throw new IllegalArgumentException("Nombre y nacimiento del ocupante son obligatorios.");
            ocupantes.add(new Ocupante(txtNombreOcupante.getText().strip(),dpNacimientoOcupante.getValue()));
            txtNombreOcupante.clear();dpNacimientoOcupante.setValue(null);invalidarCotizacion();
        }catch(Exception ex){error(ex);}
    }
    private void invalidarCotizacion() {
        ultimaCotizacion=null;estanciaCotizada=null;documentoTitularCotizado=null;
        if(lblTotalCotizacion!=null)lblTotalCotizacion.setText("—");
        if(lblAnticipo!=null)lblAnticipo.setText("—");
    }
    private void cotizar() {
        try {
            if (apartamentoElegido == null) throw new IllegalArgumentException("Selecciona un apartamento.");
            Huesped h = titular();
            Estancia estancia = fechas(dpNuevaEntrada, dpNuevaSalida);
            ListaDoble<Ocupante> lista = ocupantesParaCotizar(h);
            if (!sistema.getServicioReserva().getDisponibilidad().estaDisponible(
                    apartamentoElegido.getCodigo(), estancia, lista.tamano()))
                throw new IllegalStateException("El apartamento no está disponible o supera su capacidad.");
            ultimaCotizacion = sistema.getServicioCotizacion().calcularTotal(apartamentoElegido, estancia, lista);
            if (ultimaCotizacion.signum() <= 0)
                throw new IllegalStateException("La cotización es $0 porque ninguno de los ocupantes paga. Revisa las edades y el titular.");
            estanciaCotizada = estancia;
            documentoTitularCotizado = h.getDocumento();
            titularIncluidoCotizado = chkTitularSeHospeda.isSelected();
            lblTotalCotizacion.setText("$ " + dinero(ultimaCotizacion));
            lblAnticipo.setText("$ " + dinero(sistema.getServicioCotizacion().calcularAnticipo(ultimaCotizacion)));
        } catch (Exception ex) { invalidarCotizacion(); error(ex); }
    }

    private void crearReserva() {
        try {
            if (ultimaCotizacion == null || estanciaCotizada == null)
                throw new IllegalStateException("Primero debes cotizar.");
            if (apartamentoElegido == null) throw new IllegalArgumentException("Selecciona apartamento.");
            if (!estanciaCotizada.getFechaEntrada().equals(dpNuevaEntrada.getValue())
                    || !estanciaCotizada.getFechaSalida().equals(dpNuevaSalida.getValue()))
                throw new IllegalStateException("Cambiaste fechas; vuelve a cotizar.");
            Huesped h = titular();
            if (!h.getDocumento().equals(documentoTitularCotizado)
                    || chkTitularSeHospeda.isSelected() != titularIncluidoCotizado)
                throw new IllegalStateException("Cambiaste el titular; vuelve a cotizar.");
            ListaDoble<Ocupante> lista = ocupantesParaCotizar(h);
            BigDecimal totalActual = sistema.getServicioCotizacion().calcularTotal(apartamentoElegido, estanciaCotizada, lista);
            if (totalActual.compareTo(ultimaCotizacion) != 0)
                throw new IllegalStateException("La cotización cambió; vuelve a cotizar.");
            CanalReserva canal = CanalReserva.valueOf(cmbNuevoCanal.getValue());
            if (canal == CanalReserva.PLATAFORMA_EXTERNA)
                throw new IllegalArgumentException("Para plataformas externas utiliza la pestaña Canal externo.");
            String codigo = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            int folioId = (int)(System.currentTimeMillis() % 1_000_000_000);
            Reserva r = new Reserva(codigo, h, apartamentoElegido, estanciaCotizada, canal, new Folio(folioId));
            if (!txtHoraLlegada.getText().isBlank())
                r.setHoraLlegadaEstimada(LocalTime.parse(txtHoraLlegada.getText().strip()));
            if (!sistema.getServicioReserva().registrarReserva(r, lista.tamano()))
                throw new IllegalStateException("Reserva duplicada o fechas ocupadas.");
            r.getFolio().agregarMovimiento(new Movimiento(siguienteMovimientoId(r.getFolio()),
                    "Alojamiento " + r.calcularNumeroNoches() + " noche(s) · " + lista.tamano() + " ocupante(s)",
                    TipoMovimiento.CARGO_ALOJAMIENTO, totalActual, LocalDateTime.now()));
            guardar();
            informar("Reserva registrada", codigo + " (PENDIENTE)\nCargo de alojamiento: $ " + dinero(totalActual)
                    + "\nAnticipo WEB requerido: $ " + dinero(sistema.getServicioCotizacion().calcularAnticipo(totalActual))
                    + "\nSelecciona la reserva y pulsa 'Registrar pago' para abonar o liquidar.");
            ocupantes.clear(); invalidarCotizacion(); actualizarReservas();
        } catch (Exception ex) { error(ex); }
    }

    private void actualizarExterno() {
        if(sistema==null)return;
        var cola=FXCollections.<SolicitudExterna>observableArrayList();
        for(SolicitudExterna s:sistema.getServicioExterno().listarSolicitudesPendientes())cola.add(s);
        tablaColaExterna.setItems(cola);
        lblCantidadCola.setText(""+cola.size());
        var conflictos=FXCollections.<SolicitudExterna>observableArrayList();
        for(SolicitudExterna s:sistema.getServicioExterno().listarHistorial())
            if(s.getEstado()==EstadoSolicitud.RECHAZADA)conflictos.add(s);
        tablaConflictos.setItems(conflictos);
    }
    private void procesarExterno() {
        try {
            if(sistema.getServicioExterno().estaVacia()){informar("Cola externa","No hay solicitudes pendientes.");return;}
            SolicitudExterna s=sistema.getServicioExterno().verSiguienteSolicitud();
            if(!confirmar("Procesar solicitud",s.getIdSolicitud()+" · "+s.getCodigoApartamento()))return;
            Reserva r=sistema.getServicioExterno().procesarSiguiente();
            guardar();actualizarTodo();
            lblResultadoExterno.setText(r==null?"Solicitud rechazada. Consulta conflictos.":"Reserva creada: "+r.getCodigo());
        }catch(Exception ex){error(ex);}
    }
    private void abrirMapa() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VistaMapaInteractivo.fxml"));
            Scene scene=new Scene(loader.load());
            ((VistaMapaInteractivoController)loader.getController()).setSistema(sistema);
            ((Stage)btnMenuMapa.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){error(ex);}
    }

    private void abrirPreparacion() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VistaPanelPreparacion.fxml"));
            Scene scene=new Scene(loader.load());
            ((VistaPanelPreparacionController)loader.getController()).setSistema(sistema);
            ((Stage)btnMenuPreparacion.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){error(ex);}
    }

    private void abrirUsuario() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VistaPanelUsuario.fxml"));
            Scene scene=new Scene(loader.load());
            ((VistaPanelUsuarioController)loader.getController()).setSistema(sistema);
            ((Stage)btnMenuUsuario.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){error(ex);}
    }

    private void abrirCalendario() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VistaCalendarioOcupacion.fxml"));
            Scene scene=new Scene(loader.load());
            ((VistaCalendarioOcupacionController)loader.getController()).setSistema(sistema);
            ((Stage)btnMenuCalendario.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){error(ex);}
    }

    private void abrirInicio() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VentanaPrincipal.fxml"));
            Scene scene=new Scene(loader.load());
            ((VentanaPrincipalController)loader.getController()).setSistema(sistema);
            Stage stage=(Stage)btnMenuInicio.getScene().getWindow();
            stage.setScene(scene);
        }catch(Exception ex){error(ex);}
    }
    private void guardar() throws java.io.IOException { sistema.guardar(); }
    private boolean confirmar(String titulo,String mensaje) {
        Alert a=new Alert(Alert.AlertType.CONFIRMATION,mensaje,ButtonType.YES,ButtonType.NO);
        a.setTitle(titulo);return a.showAndWait().orElse(ButtonType.NO)==ButtonType.YES;
    }
    private void informar(String titulo,String mensaje) {
        Alert a=new Alert(Alert.AlertType.INFORMATION,mensaje,ButtonType.OK);
        a.setTitle(titulo);a.setHeaderText(null);a.showAndWait();
    }
    private void error(Exception ex) {
        Alert a=new Alert(Alert.AlertType.ERROR,ex.getMessage()==null?ex.toString():ex.getMessage());
        a.setTitle("Error de reservas");a.setHeaderText("No se pudo completar la operación");a.showAndWait();
    }
}
