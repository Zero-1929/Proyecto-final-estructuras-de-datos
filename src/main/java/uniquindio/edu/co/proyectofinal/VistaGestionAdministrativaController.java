package uniquindio.edu.co.proyectofinal;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uniquindio.edu.co.proyectofinal.excepciones.FechasInvalidasException;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.*;

/** Gestión administrativa respaldada por los servicios ABB del SGA. */
public class VistaGestionAdministrativaController {
    @FXML private BorderPane raiz;
    private SistemaSGA sistema;
    private final TabPane pestañas = new TabPane();

    public void setSistema(SistemaSGA sistema) {
        SesionActual.exigirAdministrador();
        this.sistema = Objects.requireNonNull(sistema);
        construir();
    }

    private void construir() {
        Label titulo = new Label("Gestión administrativa · Árboles de búsqueda");
        titulo.setStyle("-fx-font-size: 23px; -fx-font-weight: bold; -fx-text-fill: #142943;");
        Button volver = new Button("Volver al inicio");
        volver.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("VentanaPrincipal.fxml"));
                Scene escena = new Scene(loader.load());
                ((VentanaPrincipalController) loader.getController()).setSistema(sistema);
                ((Stage) raiz.getScene().getWindow()).setScene(escena);
            } catch (Exception ex) { error(ex); }
        });
        HBox cabecera = new HBox(24, volver, titulo);
        cabecera.setPadding(new Insets(20));
        cabecera.setStyle("-fx-background-color: white;");
        raiz.setTop(cabecera);
        pestañas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        pestañas.getTabs().setAll(tabEmpleados(), tabApartamentos(), tabHuespedes(), tabTemporadas(), tabTarifas());
        raiz.setCenter(pestañas);
    }

    private Tab tabEmpleados() {
        TableView<Empleado> tabla = new TableView<>();
        columna(tabla,"Documento",Empleado::getDocumento);
        columna(tabla,"Nombre",Empleado::getNombre);
        columna(tabla,"Rol",e->e.getRol().name());
        columna(tabla,"Teléfono",Empleado::getTelefono);
        TextField doc=campo("Documento"), nombre=campo("Nombre completo"), telefono=campo("Teléfono"), correo=campo("Correo");
        DatePicker nacimiento=new DatePicker(); nacimiento.setPromptText("Nacimiento");
        ComboBox<RolEmpleado> rol=new ComboBox<>(FXCollections.observableArrayList(RolEmpleado.values()));
        rol.setPromptText("Rol");
        tabla.getSelectionModel().selectedItemProperty().addListener((o,a,e)->{
            if(e!=null){doc.setText(e.getDocumento());nombre.setText(e.getNombre());nacimiento.setValue(e.getFechaNacimiento());telefono.setText(e.getTelefono());correo.setText(e.getCorreo());rol.setValue(e.getRol());}
        });
        Runnable refrescar=()->tabla.getItems().setAll(iterar(sistema.getServicioEmpleados().listarEmpleados()));
        Button registrar=boton("Registrar",()->{
            Empleado e=sistema.getServicioEmpleados().registrarEmpleado(nombre.getText(),nacimiento.getValue(),doc.getText(),telefono.getText(),correo.getText(),rol.getValue());
            if(e.esRecepcionista()) sistema.getServicioTurnos().registrarRecepcionista(e);
        },refrescar);
        Button modificar=boton("Actualizar datos / rol",()->{
            Empleado e=exigir(tabla); e.setNombre(nombre.getText()); e.setFechaNacimiento(nacimiento.getValue()); e.setTelefono(telefono.getText());e.setCorreo(correo.getText());
            RolEmpleado nuevo=Objects.requireNonNull(rol.getValue(),"Seleccione rol");
            if(e.getRol()!=nuevo){
                if(e.esRecepcionista())sistema.getServicioTurnos().retirarRecepcionista(e);
                e.setRol(nuevo);
                if(e.esRecepcionista())sistema.getServicioTurnos().registrarRecepcionista(e);
            }
        },refrescar);
        Button eliminar=boton("Eliminar seleccionado",()->{
            Empleado e=exigir(tabla);
            if(e.esAdministrador())throw new IllegalStateException("No se eliminan administradores desde esta pantalla para proteger la sesión activa.");
            if(!confirmar("¿Eliminar al empleado " + e.getNombre() + "?"))return;
            if(e.esRecepcionista())sistema.getServicioTurnos().retirarRecepcionista(e);
            sistema.getServicioEmpleados().eliminarEmpleado(e.getDocumento());
        },refrescar);
        refrescar.run();
        return panel("Empleados (ABB por documento)",tabla,new HBox(8,doc,nombre,nacimiento,telefono,correo,rol),new HBox(8,registrar,modificar,eliminar));
    }

    private Tab tabApartamentos() {
        TableView<Apartamento> tabla=new TableView<>();
        columna(tabla,"Código",Apartamento::getCodigo);columna(tabla,"Nombre",Apartamento::getNombre);
        columna(tabla,"Bloque",Apartamento::getBloque);columna(tabla,"Piso",a->""+a.getPiso());
        columna(tabla,"Dormitorios",a->""+a.getNumeroDormitorios());columna(tabla,"Capacidad",a->""+a.getCapacidad());
        TextField codigo=campo("Código"),nombre=campo("Nombre"),bloque=campo("Bloque"),piso=campo("Piso"),dorm=campo("Dormitorios"),cap=campo("Capacidad"),car=campo("Características");
        tabla.getSelectionModel().selectedItemProperty().addListener((o,an,a)->{
            if(a!=null){codigo.setText(a.getCodigo());nombre.setText(a.getNombre());bloque.setText(a.getBloque());piso.setText(""+a.getPiso());dorm.setText(""+a.getNumeroDormitorios());cap.setText(""+a.getCapacidad());car.setText(a.getCaracteristicas());}
        });
        Runnable refrescar=()->tabla.getItems().setAll(iterar(sistema.getServicioApartamento().listarApartamentos()));
        Button registrar=boton("Registrar",()->{
            Apartamento a=new Apartamento(codigo.getText(),nombre.getText(),bloque.getText(),Integer.parseInt(piso.getText()),Integer.parseInt(dorm.getText()),Integer.parseInt(cap.getText()),car.getText());
            if(!sistema.getServicioApartamento().registrarApartamento(a))throw new IllegalArgumentException("Código duplicado");
        },refrescar);
        Button modificar=boton("Actualizar seleccionado",()->{
            Apartamento a=exigir(tabla);
            sistema.getServicioApartamento().actualizarApartamento(a.getCodigo(),nombre.getText(),bloque.getText(),Integer.parseInt(piso.getText()),Integer.parseInt(dorm.getText()),Integer.parseInt(cap.getText()),car.getText());
        },refrescar);
        Button eliminar=boton("Eliminar seleccionado",()->{
            Apartamento a=exigir(tabla);
            for(Reserva r:sistema.getServicioReserva().listarReservas())
                if(r.getApartamento().getCodigo().equals(a.getCodigo()))throw new IllegalStateException("No se puede eliminar: tiene reservas asociadas.");
            throw new IllegalStateException("Elimine apartamentos solo junto con su nodo del mapa. La eliminación desde esta pantalla está bloqueada para no dañar el grafo.");
        },refrescar);
        refrescar.run();
        Label aviso=new Label("Nota: para nuevos apartamentos, configure también su conexión en el mapa y sus tarifas.");
        return panel("Apartamentos (índice ABB por código)",tabla,new HBox(8,codigo,nombre,bloque,piso,dorm,cap,car),new HBox(8,registrar,modificar,eliminar),aviso);
    }

    private Tab tabHuespedes() {
        TableView<Huesped> tabla=new TableView<>();
        columna(tabla,"Documento",Huesped::getDocumento);columna(tabla,"Nombre",Huesped::getNombre);
        columna(tabla,"Teléfono",Huesped::getTelefono);columna(tabla,"Correo",Huesped::getCorreo);
        TextField doc=campo("Documento"),nombre=campo("Nombre"),telefono=campo("Teléfono"),correo=campo("Correo");
        DatePicker nacimiento=new DatePicker();nacimiento.setPromptText("Nacimiento");
        tabla.getSelectionModel().selectedItemProperty().addListener((o,an,h)->{
            if(h!=null){doc.setText(h.getDocumento());nombre.setText(h.getNombre());telefono.setText(h.getTelefono());correo.setText(h.getCorreo());nacimiento.setValue(h.getFechaNacimiento());}
        });
        Runnable refrescar=()->tabla.getItems().setAll(iterar(sistema.getServicioHuesped().listarHuespedes()));
        Button registrar=boton("Registrar",()->{
            if(!sistema.getServicioHuesped().registrarHuesped(new Huesped(nombre.getText(),nacimiento.getValue(),doc.getText(),telefono.getText(),correo.getText())))
                throw new IllegalArgumentException("Documento duplicado");
        },refrescar);
        Button modificar=boton("Actualizar seleccionado",()->{
            Huesped h=exigir(tabla);h.setNombre(nombre.getText());h.setFechaNacimiento(nacimiento.getValue());h.setTelefono(telefono.getText());h.setCorreo(correo.getText());
        },refrescar);
        Button eliminar=boton("Eliminar seleccionado",()->{
            Huesped h=exigir(tabla);
            for(Reserva r:sistema.getServicioReserva().listarReservas())
                if(r.getHuesped().getDocumento().equals(h.getDocumento()))throw new IllegalStateException("No se puede eliminar: tiene historial de reservas.");
            if(!confirmar("¿Eliminar huésped " + h.getNombre() + "?"))return;
            sistema.getServicioHuesped().eliminarHuesped(h.getDocumento());
        },refrescar);
        refrescar.run();
        return panel("Huéspedes (ABB por documento)",tabla,new HBox(8,doc,nombre,nacimiento,telefono,correo),new HBox(8,registrar,modificar,eliminar));
    }


    private Tab tabTemporadas() {
        TableView<Temporada> tabla = new TableView<>();
        columna(tabla, "Tipo", t -> t.getTipo().name());
        columna(tabla, "Desde (incl.)", t -> t.getFechaInicio().toString());
        columna(tabla, "Hasta (incl.)", t -> t.getFechaFin().toString());
        ComboBox<TipoTemporada> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoTemporada.MEDIA, TipoTemporada.ALTA));
        tipo.setPromptText("Tipo de temporada");
        DatePicker inicio = new DatePicker(), fin = new DatePicker();
        inicio.setPromptText("Fecha inicial");
        fin.setPromptText("Fecha final (incluida)");
        tabla.getSelectionModel().selectedItemProperty().addListener((o, anterior, t) -> {
            if (t != null) {
                tipo.setValue(t.getTipo());
                inicio.setValue(t.getFechaInicio());
                fin.setValue(t.getFechaFin());
            }
        });
        Runnable refrescar = () -> tabla.getItems().setAll(iterar(sistema.getServicioTemporada().obtenerTemporadas()));
        Button registrar = boton("Registrar temporada", () -> {
            if (tipo.getValue() == null || inicio.getValue() == null || fin.getValue() == null)
                throw new IllegalArgumentException("Seleccione el tipo y ambas fechas.");
            try {
                sistema.registrarTemporada(tipo.getValue(), inicio.getValue(), fin.getValue());
            } catch (FechasInvalidasException e) {
                throw new RuntimeException(e);
            }
        }, refrescar);
        Button eliminar = boton("Eliminar seleccionada", () -> {
            Temporada t = exigir(tabla);
            if (!confirmar("¿Eliminar la temporada " + t.getTipo() + " del " + t.getFechaInicio() + " al " + t.getFechaFin() + "?")) return;
            if (!sistema.getServicioCotizacion().eliminarTemporada(t))
                throw new IllegalStateException("La temporada no figura en el motor de cotización.");
            if (!sistema.getServicioTemporada().eliminarTemporada(t)) {
                sistema.getServicioCotizacion().registrarTemporada(t);
                throw new IllegalStateException("No se pudo quitar la temporada del calendario.");
            }
        }, refrescar);
        refrescar.run();
        Label nota = new Label("BAJA se aplica automáticamente fuera de los períodos MEDIA y ALTA. Las fechas son inclusivas y no pueden solaparse.");
        nota.setWrapText(true);
        return panel("Temporadas y calendario", tabla, new HBox(8, tipo, inicio, fin), new HBox(8, registrar, eliminar), nota);
    }

    private Tab tabTarifas() {
        TableView<Apartamento> tabla = new TableView<>();
        columna(tabla, "Código", Apartamento::getCodigo);
        columna(tabla, "Apartamento", Apartamento::getNombre);
        columna(tabla, "BAJA / persona", a -> valorTarifa(a.getTarifaBaja()));
        columna(tabla, "MEDIA / persona", a -> valorTarifa(a.getTarifaMedia()));
        columna(tabla, "ALTA / persona", a -> valorTarifa(a.getTarifaAlta()));
        TextField baja = campo("Tarifa BAJA"), media = campo("Tarifa MEDIA"), alta = campo("Tarifa ALTA");
        tabla.getSelectionModel().selectedItemProperty().addListener((o, anterior, a) -> {
            if (a != null) {
                baja.setText(valorTarifa(a.getTarifaBaja()));
                media.setText(valorTarifa(a.getTarifaMedia()));
                alta.setText(valorTarifa(a.getTarifaAlta()));
            }
        });
        Runnable refrescar = () -> tabla.getItems().setAll(iterar(sistema.getServicioApartamento().listarApartamentos()));
        Button guardar = boton("Guardar tarifas del apartamento", () -> {
            Apartamento a = exigir(tabla);
            BigDecimal tarifaBaja = leerTarifa(baja.getText(), "BAJA");
            BigDecimal tarifaMedia = leerTarifa(media.getText(), "MEDIA");
            BigDecimal tarifaAlta = leerTarifa(alta.getText(), "ALTA");
            sistema.getServicioApartamento().configurarTarifa(a.getCodigo(), TipoTemporada.BAJA, tarifaBaja);
            sistema.getServicioApartamento().configurarTarifa(a.getCodigo(), TipoTemporada.MEDIA, tarifaMedia);
            sistema.getServicioApartamento().configurarTarifa(a.getCodigo(), TipoTemporada.ALTA, tarifaAlta);
            tabla.refresh();
        }, refrescar);
        refrescar.run();
        Label nota = new Label("Valores por persona y noche. Se usan en las cotizaciones futuras; no recalculan folios de reservas existentes.");
        nota.setWrapText(true);
        return panel("Tarifas por apartamento", tabla, new HBox(8, baja, media, alta), new HBox(8, guardar), nota);
    }

    private String valorTarifa(BigDecimal valor) {
        return valor == null ? "" : valor.toPlainString();
    }

    private BigDecimal leerTarifa(String texto, String tipo) {
        if (texto == null || texto.isBlank())
            throw new IllegalArgumentException("Ingrese la tarifa " + tipo + ".");
        try {
            BigDecimal valor = new BigDecimal(texto.trim());
            if (valor.signum() < 0) throw new IllegalArgumentException("La tarifa " + tipo + " no puede ser negativa.");
            return valor;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Tarifa " + tipo + " inválida. Use números sin separadores de miles.");
        }
    }

    private <T> void columna(TableView<T> tabla,String nombre,java.util.function.Function<T,String> getter){
        TableColumn<T,String> col=new TableColumn<>(nombre);
        col.setCellValueFactory(c->new javafx.beans.property.ReadOnlyStringWrapper(getter.apply(c.getValue())));
        col.setPrefWidth(145);tabla.getColumns().add(col);
    }
    private TextField campo(String texto){TextField c=new TextField();c.setPromptText(texto);c.setPrefWidth(140);return c;}
    private Tab panel(String titulo,TableView<?> tabla,HBox campos,HBox acciones,javafx.scene.Node... extras){
        Label etiqueta=new Label(titulo);etiqueta.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        ScrollPane scroll=new ScrollPane(campos);scroll.setFitToHeight(true);scroll.setFitToWidth(false);scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox cuerpo=new VBox(14,etiqueta,tabla,scroll,acciones);cuerpo.getChildren().addAll(extras);cuerpo.setPadding(new Insets(20));
        VBox.setVgrow(tabla,Priority.ALWAYS);
        Tab tab=new Tab(titulo.substring(0,titulo.indexOf(' ')));tab.setContent(cuerpo);return tab;
    }
    private Button boton(String titulo,Runnable operacion,Runnable refrescar){
        Button b=new Button(titulo);b.setOnAction(e->{try{operacion.run();sistema.guardar();refrescar.run();}catch(Exception ex){error(ex);}});return b;
    }
    private <T> T exigir(TableView<T> tabla){T t=tabla.getSelectionModel().getSelectedItem();if(t==null)throw new IllegalStateException("Seleccione un registro de la tabla.");return t;}
    private <T> List<T> iterar(Iterable<T> datos){List<T> r=new ArrayList<>();for(T t:datos)r.add(t);return r;}
    private boolean confirmar(String texto){return new Alert(Alert.AlertType.CONFIRMATION,texto,ButtonType.YES,ButtonType.NO).showAndWait().orElse(ButtonType.NO)==ButtonType.YES;}
    private void error(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()==null?ex.toString():ex.getMessage()).showAndWait();}
}
