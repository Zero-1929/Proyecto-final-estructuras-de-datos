package uniquindio.edu.co.proyectofinal;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.*;
import uniquindio.edu.co.proyectofinal.servicios.HistorialTiemposPreparacion;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

/** Estadísticas calculadas sobre los registros reales del SGA. */
public class VistaEstadisticasController {
    @FXML private DatePicker dpEstadisticasDesde, dpEstadisticasHasta;
    @FXML private ComboBox<String> cmbEstadisticasBloque;
    @FXML private Button btnAplicarFiltrosEstadisticas, btnActualizarEstadisticas;
    @FXML private Button btnMenuInicio, btnMenuReservas, btnMenuCalendario, btnMenuUsuario,
            btnMenuPreparacion, btnMenuMapa, btnMenuEstadisticas, btnMenuCargaDatos;
    @FXML private Label lblEstadisticasOcupacion, lblEstadisticasIngresos,
            lblEstadisticasConflictos, lblEstadisticasTiempoPreparacion,
            lblIngresosTotalPeriodo, lblTotalConflictosCanal, lblPromedioGeneralPreparacion;
    @FXML private TableView<List<String>> tablaOcupacionApartamento, tablaOcupacionTemporada,
            tablaIngresosPeriodo, tablaReservasPorCanal, tablaConflictosCanal, tablaTiempoPreparacion;
    @FXML private TableColumn<List<String>,String> colOcupacionApartamento,colOcupacionBloque,
            colOcupacionNoches,colOcupacionDisponibles,colOcupacionPorcentaje,
            colTemporadaNombre,colTemporadaNochesOcupadas,colTemporadaNochesDisponibles,colTemporadaPorcentaje,
            colIngresosPeriodo,colIngresosReservas,colIngresosValor,
            colCanalNombre,colCanalTotal,colCanalPendientes,colCanalConfirmadas,colCanalEnCurso,
            colCanalFinalizadas,colCanalCanceladas,colCanalNoShow,
            colConflictoFecha,colConflictoCanal,colConflictoApartamento,colConflictoSolicitud,
            colConflictoMotivo,colConflictoResultado,
            colTiempoApartamento,colTiempoPreparaciones,colTiempoPromedio,colTiempoUltima;
    @FXML private VBox panelGraficoOcupacionApartamento,panelGraficoOcupacionTemporada,
            panelGraficoIngresos,panelGraficoCanales,panelGraficoConflictos,panelGraficoPreparacion;
    private SistemaSGA sistema;
    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es","CO"));
    private static final Set<EstadoReserva> OCUPAN = EnumSet.of(EstadoReserva.CONFIRMADA,
            EstadoReserva.EN_CURSO, EstadoReserva.FINALIZADA);

    @FXML private void initialize() {
        LocalDate hoy=LocalDate.now();
        dpEstadisticasDesde.setValue(hoy.withDayOfMonth(1));
        dpEstadisticasHasta.setValue(hoy);
        cmbEstadisticasBloque.setItems(FXCollections.observableArrayList("Todos"));
        cmbEstadisticasBloque.setValue("Todos");
        configurar(tablaOcupacionApartamento,colOcupacionApartamento,colOcupacionBloque,colOcupacionNoches,colOcupacionDisponibles,colOcupacionPorcentaje);
        configurar(tablaOcupacionTemporada,colTemporadaNombre,colTemporadaNochesOcupadas,colTemporadaNochesDisponibles,colTemporadaPorcentaje);
        configurar(tablaIngresosPeriodo,colIngresosPeriodo,colIngresosReservas,colIngresosValor);
        configurar(tablaReservasPorCanal,colCanalNombre,colCanalTotal,colCanalPendientes,colCanalConfirmadas,colCanalEnCurso,colCanalFinalizadas,colCanalCanceladas,colCanalNoShow);
        configurar(tablaConflictosCanal,colConflictoFecha,colConflictoCanal,colConflictoApartamento,colConflictoSolicitud,colConflictoMotivo,colConflictoResultado);
        configurar(tablaTiempoPreparacion,colTiempoApartamento,colTiempoPreparaciones,colTiempoPromedio,colTiempoUltima);
        btnAplicarFiltrosEstadisticas.setOnAction(e->actualizar());
        btnActualizarEstadisticas.setOnAction(e->actualizar());
        btnMenuEstadisticas.setOnAction(e->actualizar());
        conectar(btnMenuInicio,"VentanaPrincipal.fxml");
        conectar(btnMenuReservas,"VistaReservas.fxml");
        conectar(btnMenuCalendario,"VistaCalendarioOcupacion.fxml");
        conectar(btnMenuUsuario,"VistaPanelUsuario.fxml");
        conectar(btnMenuPreparacion,"VistaPanelPreparacion.fxml");
        conectar(btnMenuMapa,"VistaMapaInteractivo.fxml");
        btnMenuCargaDatos.setOnAction(e->new Alert(Alert.AlertType.INFORMATION,
                "La carga de datos se gestiona desde los módulos existentes.").showAndWait());
    }
    @SafeVarargs private final void configurar(TableView<List<String>> tabla,TableColumn<List<String>,String>... columnas) {
        for(int i=0;i<columnas.length;i++) {
            final int indice=i;
            columnas[i].setCellValueFactory(c->new ReadOnlyStringWrapper(c.getValue().get(indice)));
        }
        tabla.setPlaceholder(new Label("Sin registros para los filtros seleccionados"));
    }
    public void setSistema(SistemaSGA sistema) {
        this.sistema=Objects.requireNonNull(sistema);
        TreeSet<String> bloques=new TreeSet<>();
        for(Apartamento a:sistema.getServicioApartamento().listarApartamentos()) bloques.add(a.getBloque());
        cmbEstadisticasBloque.getItems().setAll("Todos");
        cmbEstadisticasBloque.getItems().addAll(bloques);
        cmbEstadisticasBloque.setValue("Todos");
        actualizar();
    }
    private void conectar(Button boton,String vista) { boton.setOnAction(e->{
        if(sistema==null)return;
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource(vista));
            Scene scene=new Scene(loader.load());
            Object c=loader.getController();
            if(c instanceof VentanaPrincipalController x)x.setSistema(sistema);
            else if(c instanceof VistaReservasController x)x.setSistema(sistema);
            else if(c instanceof VistaCalendarioOcupacionController x)x.setSistema(sistema);
            else if(c instanceof VistaPanelUsuarioController x)x.setSistema(sistema);
            else if(c instanceof VistaPanelPreparacionController x)x.setSistema(sistema);
            else if(c instanceof VistaMapaInteractivoController x)x.setSistema(sistema);
            ((Stage)boton.getScene().getWindow()).setScene(scene);
        } catch(Exception ex) { new Alert(Alert.AlertType.ERROR,"No se pudo abrir la vista: "+ex.getMessage()).showAndWait(); }
    }); }
    private boolean incluido(Apartamento a) {
        String bloque=cmbEstadisticasBloque.getValue();
        return bloque==null || bloque.equals("Todos") || bloque.equalsIgnoreCase(a.getBloque());
    }
    private static List<String> fila(Object... valores) {
        List<String> r=new ArrayList<>();
        for(Object v:valores)r.add(String.valueOf(v));
        return r;
    }
    private static String porcentaje(long n,long d) { return d==0?"0,0 %":String.format(Locale.forLanguageTag("es-CO"),"%.1f %%",100.0*n/d); }
    private static long noches(Reserva r,LocalDate desde,LocalDate hastaExclusivo) {
        LocalDate inicio=r.getEstancia().getFechaEntrada().isAfter(desde)?r.getEstancia().getFechaEntrada():desde;
        LocalDate fin=r.getEstancia().getFechaSalida().isBefore(hastaExclusivo)?r.getEstancia().getFechaSalida():hastaExclusivo;
        return inicio.isBefore(fin)?ChronoUnit.DAYS.between(inicio,fin):0;
    }
    private void actualizar() {
        if(sistema==null)return;
        LocalDate desde=dpEstadisticasDesde.getValue(), hasta=dpEstadisticasHasta.getValue();
        if(desde==null||hasta==null||hasta.isBefore(desde)) {
            new Alert(Alert.AlertType.WARNING,"Selecciona un intervalo válido (Desde ≤ Hasta).").showAndWait();return;
        }
        LocalDate fin=hasta.plusDays(1);
        long dias=ChronoUnit.DAYS.between(desde,fin);
        List<Apartamento> apartamentos=new ArrayList<>();
        for(Apartamento a:sistema.getServicioApartamento().listarApartamentos()) if(incluido(a)) apartamentos.add(a);
        List<Reserva> reservas=new ArrayList<>();
        for(Reserva r:sistema.getServicioReserva().listarReservas()) if(incluido(r.getApartamento())) reservas.add(r);
        ocupacion(apartamentos,reservas,desde,fin,dias);
        ingresos(reservas,desde,fin);
        canales(reservas,desde,fin);
        conflictos(desde,fin);
        preparacion();
    }
    private void ocupacion(List<Apartamento> apartamentos,List<Reserva> reservas,LocalDate desde,LocalDate fin,long dias) {
        List<List<String>> filas=new ArrayList<>();
        XYChart.Series<String,Number> barras=new XYChart.Series<>();
        long ocupadas=0, disponibles=dias*apartamentos.size();
        for(Apartamento a:apartamentos) {
            long n=0;
            for(Reserva r:reservas) if(r.getApartamento().getCodigo().equalsIgnoreCase(a.getCodigo())&&OCUPAN.contains(r.getEstado())) n+=noches(r,desde,fin);
            n=Math.min(n,dias); ocupadas+=n;
            filas.add(fila(a.getCodigo(),a.getBloque(),n,dias,porcentaje(n,dias)));
            barras.getData().add(new XYChart.Data<>(a.getCodigo(),dias==0?0:100.0*n/dias));
        }
        tablaOcupacionApartamento.setItems(FXCollections.observableArrayList(filas));
        lblEstadisticasOcupacion.setText(porcentaje(ocupadas,disponibles));
        graficoBarras(panelGraficoOcupacionApartamento,"Ocupación (%)",barras);
        List<List<String>> temporadas=new ArrayList<>();
        XYChart.Series<String,Number> serie=new XYChart.Series<>();
        for(TipoTemporada tipo:TipoTemporada.values()) {
            long nochesDisponibles=0,nochesOcupadas=0;
            for(LocalDate d=desde;d.isBefore(fin);d=d.plusDays(1)) {
                if(sistema.getServicioTemporada().obtenerTipoTemporada(d)!=tipo)continue;
                nochesDisponibles+=apartamentos.size();
                for(Apartamento a:apartamentos) for(Reserva r:reservas) {
                    if(!OCUPAN.contains(r.getEstado())||!a.getCodigo().equalsIgnoreCase(r.getApartamento().getCodigo()))continue;
                    if(!d.isBefore(r.getEstancia().getFechaEntrada())&&d.isBefore(r.getEstancia().getFechaSalida())){nochesOcupadas++;break;}
                }
            }
            temporadas.add(fila(tipo,nochesOcupadas,nochesDisponibles,porcentaje(nochesOcupadas,nochesDisponibles)));
            serie.getData().add(new XYChart.Data<>(tipo.toString(),nochesDisponibles==0?0:100.0*nochesOcupadas/nochesDisponibles));
        }
        tablaOcupacionTemporada.setItems(FXCollections.observableArrayList(temporadas));
        graficoBarras(panelGraficoOcupacionTemporada,"Ocupación por temporada (%)",serie);
    }
    private void ingresos(List<Reserva> reservas,LocalDate desde,LocalDate fin) {
        Map<YearMonth,BigDecimal> pagos=new TreeMap<>();
        Map<YearMonth,Set<String>> codigos=new TreeMap<>();
        BigDecimal total=BigDecimal.ZERO;
        for(Reserva r:reservas) for(Movimiento m:r.getFolio().getMovimientos()) {
            if(m.getTipo()!=TipoMovimiento.PAGO)continue;
            LocalDate fecha=m.getFecha().toLocalDate();
            if(fecha.isBefore(desde)||!fecha.isBefore(fin))continue;
            YearMonth mes=YearMonth.from(fecha);
            pagos.merge(mes,m.getMonto(),BigDecimal::add);
            codigos.computeIfAbsent(mes,k->new HashSet<>()).add(r.getCodigo());
            total=total.add(m.getMonto());
        }
        List<List<String>> filas=new ArrayList<>();
        XYChart.Series<String,Number> serie=new XYChart.Series<>();
        for(var e:pagos.entrySet()) {
            String mes=e.getKey().toString();
            filas.add(fila(mes,codigos.get(e.getKey()).size(),moneda.format(e.getValue())));
            serie.getData().add(new XYChart.Data<>(mes,e.getValue()));
        }
        tablaIngresosPeriodo.setItems(FXCollections.observableArrayList(filas));
        lblIngresosTotalPeriodo.setText(moneda.format(total));
        lblEstadisticasIngresos.setText(moneda.format(total));
        graficoBarras(panelGraficoIngresos,"Pagos registrados",serie);
    }
    private void canales(List<Reserva> reservas,LocalDate desde,LocalDate fin) {
        List<List<String>> filas=new ArrayList<>();
        XYChart.Series<String,Number> serie=new XYChart.Series<>();
        for(CanalReserva canal:CanalReserva.values()) {
            int[] cont=new int[6];
            for(Reserva r:reservas) {
                LocalDate fecha=r.getFechaCreacion().toLocalDate();
                if(r.getCanalReserva()!=canal||fecha.isBefore(desde)||!fecha.isBefore(fin))continue;
                switch(r.getEstado()) {
                    case PENDIENTE -> cont[0]++;
                    case CONFIRMADA -> cont[1]++;
                    case EN_CURSO -> cont[2]++;
                    case FINALIZADA -> cont[3]++;
                    case CANCELADA -> cont[4]++;
                    case NO_PRESENTADO -> cont[5]++;
                }
            }
            int total=Arrays.stream(cont).sum();
            filas.add(fila(canal,total,cont[0],cont[1],cont[2],cont[3],cont[4],cont[5]));
            serie.getData().add(new XYChart.Data<>(canal.toString(),total));
        }
        tablaReservasPorCanal.setItems(FXCollections.observableArrayList(filas));
        graficoBarras(panelGraficoCanales,"Reservas creadas por canal",serie);
    }
    private void conflictos(LocalDate desde,LocalDate fin) {
        List<List<String>> filas=new ArrayList<>();
        Map<String,Integer> porCanal=new TreeMap<>();
        // El sistema conserva las solicitudes externas rechazadas y su motivo.
        // No clasificar rechazos administrativos como conflictos de disponibilidad.
        for(SolicitudExterna s:sistema.getServicioExterno().listarHistorial()) {
            if(s.getEstado()!=EstadoSolicitud.RECHAZADA)continue;
            LocalDate fecha=s.getFechaRecepcion().toLocalDate();
            if(fecha.isBefore(desde)||!fecha.isBefore(fin))continue;
            Apartamento a=sistema.getServicioApartamento().buscarPorCodigo(s.getCodigoApartamento());
            if(a!=null&&!incluido(a))continue;
            String motivo=s.getMotivoRechazo();
            if(motivo==null)continue;
            String normal=motivo.toLowerCase(Locale.ROOT);
            if(!(normal.contains("disponib")||normal.contains("solap")||normal.contains("ocupad")||normal.contains("conflict")||normal.contains("reserv")))continue;
            filas.add(fila(fecha,"EXTERNO",s.getCodigoApartamento(),s.getIdSolicitud(),motivo,"RECHAZADA"));
            porCanal.merge("EXTERNO",1,Integer::sum);
        }
        tablaConflictosCanal.setItems(FXCollections.observableArrayList(filas));
        lblTotalConflictosCanal.setText(String.valueOf(filas.size()));
        lblEstadisticasConflictos.setText(String.valueOf(filas.size()));
        XYChart.Series<String,Number> serie=new XYChart.Series<>();
        porCanal.forEach((k,v)->serie.getData().add(new XYChart.Data<>(k,v)));
        graficoBarras(panelGraficoConflictos,"Rechazos por disponibilidad registrados",serie);
    }
    private void preparacion() {
        LocalDate desde=dpEstadisticasDesde.getValue(),fin=dpEstadisticasHasta.getValue().plusDays(1);
        Map<String,List<HistorialTiemposPreparacion.Registro>> agrupados=new TreeMap<>();
        long totalSegundos=0;int total=0;
        for(var r:HistorialTiemposPreparacion.listar()) {
            LocalDate fecha=r.fin().toLocalDate();
            if(fecha.isBefore(desde)||!fecha.isBefore(fin))continue;
            Apartamento a=sistema.getServicioApartamento().buscarPorCodigo(r.apartamento());
            if(a==null||!incluido(a))continue;
            agrupados.computeIfAbsent(r.apartamento(),k->new ArrayList<>()).add(r);
            totalSegundos+=r.segundos();total++;
        }
        List<List<String>> filas=new ArrayList<>();
        XYChart.Series<String,Number> serie=new XYChart.Series<>();
        for(var e:agrupados.entrySet()) {
            long segundos=0;LocalDateTime ultima=LocalDateTime.MIN;
            for(var r:e.getValue()) {segundos+=r.segundos();if(r.fin().isAfter(ultima))ultima=r.fin();}
            long promedio=segundos/e.getValue().size();
            filas.add(fila(e.getKey(),e.getValue().size(),duracion(promedio),ultima.toString().replace('T',' ')));
            serie.getData().add(new XYChart.Data<>(e.getKey(),promedio/60.0));
        }
        tablaTiempoPreparacion.setItems(FXCollections.observableArrayList(filas));
        String promedio=total==0?"Sin registros":duracion(totalSegundos/total);
        lblPromedioGeneralPreparacion.setText(promedio);
        lblEstadisticasTiempoPreparacion.setText(promedio);
        graficoBarras(panelGraficoPreparacion,"Tiempo promedio (minutos)",serie);
    }
    private static String duracion(long segundos) {
        return String.format("%d min %02d s",segundos/60,segundos%60);
    }
    private void graficoBarras(VBox panel,String titulo,XYChart.Series<String,Number> datos) {
        panel.getChildren().clear();
        if(datos.getData().isEmpty()) {panel.getChildren().add(new Label("Sin datos en este período"));return;}
        CategoryAxis x=new CategoryAxis();NumberAxis y=new NumberAxis();
        BarChart<String,Number> chart=new BarChart<>(x,y);
        chart.setTitle(titulo);chart.setLegendVisible(false);chart.setAnimated(false);
        chart.getData().add(datos);chart.setMinHeight(190);chart.setPrefHeight(220);
        panel.getChildren().add(chart);
    }
}
