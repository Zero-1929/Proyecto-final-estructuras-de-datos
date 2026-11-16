package uniquindio.edu.co.proyectofinal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.*;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.*;
import uniquindio.edu.co.proyectofinal.servicios.ServicioRutas;
import uniquindio.edu.co.proyectofinal.estructuras.ResultadoRuta;

/** Visualización esquemática de grafo; las conexiones requieren configuración real. */
public class VistaMapaInteractivoController {
    @FXML private Button btnMenuInicio;
    @FXML private Button btnMenuReservas;
    @FXML private Button btnMenuCalendario;
    @FXML private Button btnMenuUsuario;
    @FXML private Button btnMenuPreparacion;
    @FXML private Button btnMenuMapa;
    @FXML private Button btnMenuEstadisticas;
    @FXML private Button btnMenuCargaDatos;
    @FXML private Label lblTotalNodosApartamento;
    @FXML private Label lblTotalPuntosServicio;
    @FXML private Label lblTotalAristas;
    @FXML private Label lblDistanciaDijkstra;
    @FXML private Button btnActualizarMapa;
    @FXML private ComboBox<String> cmbBloqueMapa;
    @FXML private ComboBox<String> cmbPisoMapa;
    @FXML private TextField txtBuscarNodoMapa;
    @FXML private Button btnRestablecerVistaMapa;
    @FXML private ScrollPane scrollPlanoGrafo;
    @FXML private Pane panelGrafoEdificio;
    @FXML private Label lblMapaVacio;
    @FXML private Label lblMapaNodoNombre;
    @FXML private Label lblMapaNodoTipo;
    @FXML private Label lblMapaNodoCodigo;
    @FXML private Label lblMapaNodoUbicacion;
    @FXML private Label lblMapaNodoEstado;
    @FXML private ListView<String> listaConexionesNodo;
    @FXML private ComboBox<String> cmbOrigenDijkstra;
    @FXML private ComboBox<String> cmbDestinoDijkstra;
    @FXML private Button btnCalcularDijkstra;
    @FXML private Button btnLimpiarRutaMapa;
    @FXML private ListView<String> listaCaminoDijkstra;
    @FXML private Label lblResultadoDistanciaRuta;
    @FXML private Label lblCantidadNodosRuta;
    @FXML private Label lblMensajeDijkstra;
    @FXML private Button btnNuevoPuntoServicio;
    @FXML private Button btnConectarNodos;
    @FXML private Button btnEliminarConexion;
    private SistemaSGA sistema;
    private ServicioRutas<String> rutas;
    private final Map<String,Apartamento> apartamentos=new LinkedHashMap<>();
    private final Map<String,double[]> posiciones=new LinkedHashMap<>();
    private final Map<String,String> tipos=new LinkedHashMap<>();
    private final Map<String,String> ubicaciones=new LinkedHashMap<>();
    private final Set<String> aristas=new LinkedHashSet<>();
    private List<String> camino=List.of();
    private String seleccionado;
    private boolean cambiando;

    @FXML private void initialize() {
        cmbBloqueMapa.getItems().add("Todos");
        cmbPisoMapa.getItems().add("Todos");
        cmbBloqueMapa.setValue("Todos");
        cmbPisoMapa.setValue("Todos");
        btnActualizarMapa.setOnAction(e->actualizar());
        btnNuevoPuntoServicio.setOnAction(e->nuevoPunto());
        btnConectarNodos.setOnAction(e->conectar());
        btnEliminarConexion.setOnAction(e->eliminarConexion());
        btnRestablecerVistaMapa.setOnAction(e->{
            txtBuscarNodoMapa.clear();cmbBloqueMapa.setValue("Todos");
            cmbPisoMapa.setValue("Todos");camino=List.of();actualizar();
        });
        txtBuscarNodoMapa.textProperty().addListener((o,a,b)->dibujar());
        cmbBloqueMapa.valueProperty().addListener((o,a,b)->dibujar());
        cmbPisoMapa.valueProperty().addListener((o,a,b)->dibujar());
        btnCalcularDijkstra.setOnAction(e->calcular());
        btnLimpiarRutaMapa.setOnAction(e->{camino=List.of();listaCaminoDijkstra.getItems().clear();
            lblResultadoDistanciaRuta.setText("—");lblCantidadNodosRuta.setText("0");dibujar();});
        btnMenuInicio.setOnAction(e->navegar("VentanaPrincipal.fxml"));
        btnMenuReservas.setOnAction(e->navegar("VistaReservas.fxml"));
        btnMenuCalendario.setOnAction(e->navegar("VistaCalendarioOcupacion.fxml"));
        btnMenuUsuario.setOnAction(e->navegar("VistaPanelUsuario.fxml"));
        btnMenuPreparacion.setOnAction(e->navegar("VistaPanelPreparacion.fxml"));
        btnMenuMapa.setOnAction(e->actualizar());
        for(Button b:new Button[]{btnMenuEstadisticas,btnMenuCargaDatos})
            b.setOnAction(e->alerta("Módulo pendiente: "+b.getText()));
    }
    public void setSistema(SistemaSGA sistema) {
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

        this.sistema=Objects.requireNonNull(sistema);
        // El proyecto no tiene grafo en SistemaSGA: se utiliza el registro compartido.
        this.rutas=MapaGrafoCompartido.obtener(sistema);
        actualizar();
    }
    private void actualizar() {
        if(sistema==null)return;
        apartamentos.clear();posiciones.clear();tipos.clear();ubicaciones.clear();aristas.clear();
        rutas=MapaGrafoCompartido.obtener(sistema);
        for(Apartamento a:sistema.getServicioApartamento().listarApartamentos())apartamentos.put(a.getCodigo(),a);
        List<String> nodos=new ArrayList<>();
        for(String nodo:rutas.obtenerMapa())nodos.add(nodo);
        // Plano por bloques: columnas para A/B/C y una franja inferior para servicios.
        Map<String,List<String>> porBloque = new LinkedHashMap<>();
        List<String> servicios = new ArrayList<>();
        for(String nodo:nodos) {
            Apartamento a=apartamentos.get(nodo);
            tipos.put(nodo,a==null?"Punto de servicio":"Apartamento");
            ubicaciones.put(nodo,a==null?"Punto de servicio":a.getBloque()+" · piso "+a.getPiso());
            if(a==null) servicios.add(nodo);
            else porBloque.computeIfAbsent(a.getBloque(), k->new ArrayList<>()).add(nodo);
        }
        List<String> bloques=new ArrayList<>(porBloque.keySet());
        Collections.sort(bloques);
        int columnas=Math.max(3,bloques.size());
        for(int b=0;b<bloques.size();b++) {
            List<String> grupo=porBloque.get(bloques.get(b));
            grupo.sort(Comparator.comparingInt((String codigo)->apartamentos.get(codigo).getPiso())
                .thenComparing(Comparator.naturalOrder()));
            for(int i=0;i<grupo.size();i++) {
                int columna=i%2, fila=i/2;
                posiciones.put(grupo.get(i),new double[]{145+b*430+columna*170,135+fila*125});
            }
        }
        int filasMax=1;
        for(List<String> grupo:porBloque.values()) filasMax=Math.max(filasMax,(grupo.size()+1)/2);
        double yServicios=190+filasMax*125;
        servicios.sort(
                Comparator.comparingInt(
                        (String n) -> n.toLowerCase(Locale.ROOT).contains("recep") ? 0 : 1
                ).thenComparing(Comparator.naturalOrder())
        );
        for(int i=0;i<servicios.size();i++)
            posiciones.put(servicios.get(i),new double[]{145+(i%7)*185,yServicios+(i/7)*115});
        for(int x=0;x<nodos.size();x++)for(int y=x+1;y<nodos.size();y++)
            if(rutas.estanConectadas(nodos.get(x),nodos.get(y)))
                aristas.add(nodos.get(x)+"|"+nodos.get(y));
        cambiando=true;
        try {
            String bloque=cmbBloqueMapa.getValue(),piso=cmbPisoMapa.getValue();
            cmbBloqueMapa.getItems().setAll("Todos");
            cmbPisoMapa.getItems().setAll("Todos");
            for(Apartamento a:apartamentos.values()) {
                if(!cmbBloqueMapa.getItems().contains(a.getBloque()))cmbBloqueMapa.getItems().add(a.getBloque());
                String p=""+a.getPiso();
                if(!cmbPisoMapa.getItems().contains(p))cmbPisoMapa.getItems().add(p);
            }
            cmbBloqueMapa.setValue(cmbBloqueMapa.getItems().contains(bloque)?bloque:"Todos");
            cmbPisoMapa.setValue(cmbPisoMapa.getItems().contains(piso)?piso:"Todos");
            String origen=cmbOrigenDijkstra.getValue(),destino=cmbDestinoDijkstra.getValue();
            cmbOrigenDijkstra.getItems().setAll(nodos);cmbDestinoDijkstra.getItems().setAll(nodos);
            if(origen!=null&&nodos.contains(origen))cmbOrigenDijkstra.setValue(origen);
            if(destino!=null&&nodos.contains(destino))cmbDestinoDijkstra.setValue(destino);
        }finally{cambiando=false;}
        lblTotalNodosApartamento.setText(""+apartamentos.size());
        lblTotalPuntosServicio.setText(""+(nodos.size()-apartamentos.size()));
        lblTotalAristas.setText(""+rutas.obtenerCantidadConexiones());
        dibujar();
    }
    private boolean visible(String nodo) {
        Apartamento a=apartamentos.get(nodo);
        String buscar=txtBuscarNodoMapa.getText()==null?"":txtBuscarNodoMapa.getText().strip().toLowerCase(Locale.ROOT);
        if(!nodo.toLowerCase(Locale.ROOT).contains(buscar))return false;
        if(a==null)return "Todos".equals(cmbBloqueMapa.getValue())&&"Todos".equals(cmbPisoMapa.getValue());
        return ("Todos".equals(cmbBloqueMapa.getValue())||a.getBloque().equals(cmbBloqueMapa.getValue()))
            &&("Todos".equals(cmbPisoMapa.getValue())||(""+a.getPiso()).equals(cmbPisoMapa.getValue()));
    }
    private void dibujar() {
        if(cambiando||rutas==null)return;
        panelGrafoEdificio.getChildren().clear();
        double ancho=1350, alto=640;
        for(double[] p:posiciones.values()) {ancho=Math.max(ancho,p[0]+110);alto=Math.max(alto,p[1]+105);}
        panelGrafoEdificio.setPrefSize(ancho,alto);
        // Fondos de bloque, sin alterar las conexiones ni los pesos reales del grafo.
        Set<String> bloquesDibujados=new LinkedHashSet<>();
        for(Apartamento a:apartamentos.values()) bloquesDibujados.add(a.getBloque());
        List<String> ordenBloques=new ArrayList<>(bloquesDibujados);
        Collections.sort(ordenBloques);
        for(int b=0;b<ordenBloques.size();b++) {
            Rectangle fondo=new Rectangle(55+b*430,58,390,Math.max(270,alto-285));
            fondo.setArcWidth(18);fondo.setArcHeight(18);
            fondo.setFill(Color.web(b%2==0?"#E1ECF7":"#E7EEF8"));
            fondo.setStroke(Color.web("#CBD8E7"));
            fondo.setMouseTransparent(true);
            Label titulo=new Label("BLOQUE "+ordenBloques.get(b));
            titulo.setLayoutX(75+b*430);titulo.setLayoutY(72);
            titulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #28496E;");
            titulo.setMouseTransparent(true);
            panelGrafoEdificio.getChildren().addAll(fondo,titulo);
        }
        for(String arista:aristas) {
            String[] extremos=arista.split("\\|");
            if(!visible(extremos[0])||!visible(extremos[1]))continue;
            double[] a=posiciones.get(extremos[0]),b=posiciones.get(extremos[1]);
            boolean ruta=false;
            for(int i=0;i<camino.size()-1;i++)
                if((camino.get(i).equals(extremos[0])&&camino.get(i+1).equals(extremos[1]))
                ||(camino.get(i).equals(extremos[1])&&camino.get(i+1).equals(extremos[0])))ruta=true;
            Line linea=new Line(a[0],a[1],b[0],b[1]);
            linea.setStroke(ruta?Color.web("#168B70"):Color.web("#9AA9B9"));
            linea.setStrokeWidth(ruta?5:1.5);
            linea.setOpacity(ruta?1:0.48);
            linea.setMouseTransparent(true);
            panelGrafoEdificio.getChildren().add(linea);
        }
        for(var entrada:posiciones.entrySet()){
            String nodo=entrada.getKey();if(!visible(nodo))continue;
            double[] p=entrada.getValue();
            Circle c=new Circle(p[0],p[1],21);
            c.setFill(nodo.equals(seleccionado)?Color.web("#E7A23B"):
                camino.contains(nodo)?Color.web("#168B70"):
                apartamentos.containsKey(nodo)?Color.web("#3B6B9D"):Color.web("#7955A6"));
            c.setStroke(Color.WHITE);c.setStrokeWidth(2);
            c.setOnMouseClicked(e->seleccionar(nodo));
            Label l=new Label(nodo);l.setLayoutX(p[0]-65);l.setLayoutY(p[1]+26);
            l.setMinWidth(130);l.setStyle("-fx-alignment:center;-fx-font-weight:bold;");
            l.setOnMouseClicked(e->seleccionar(nodo));
            panelGrafoEdificio.getChildren().addAll(c,l);
        }
        lblMapaVacio.setVisible(posiciones.isEmpty());
        if(posiciones.isEmpty())lblMapaVacio.setText("No hay vértices registrados.");
        lblMensajeDijkstra.setText(aristas.isEmpty()?
                "Sin conexiones configuradas: registra los pasillos reales antes de calcular rutas.":
                "Distribución esquemática; los pesos del grafo determinan las rutas.");
    }
    private void seleccionar(String nodo) {
        seleccionado=nodo;
        lblMapaNodoNombre.setText(nodo);
        lblMapaNodoCodigo.setText(nodo);
        lblMapaNodoTipo.setText(tipos.getOrDefault(nodo,"—"));
        lblMapaNodoUbicacion.setText(ubicaciones.getOrDefault(nodo,"—"));
        Apartamento a=apartamentos.get(nodo);
        lblMapaNodoEstado.setText(a==null?"—":a.getEstado().name());
        listaConexionesNodo.getItems().clear();
        for(String otro:posiciones.keySet())if(!otro.equals(nodo)&&rutas.estanConectadas(nodo,otro))
            listaConexionesNodo.getItems().add(otro+" · peso "+rutas.consultarPesoConexion(nodo,otro));
        dibujar();
    }
    private void calcular() {
        if(rutas==null)return;
        String a=cmbOrigenDijkstra.getValue(),b=cmbDestinoDijkstra.getValue();
        if(a==null||b==null){alerta("Selecciona origen y destino.");return;}
        try {
            ResultadoRuta<String> resultado=rutas.consultarRutaMasCorta(a,b);
            camino=new ArrayList<>();listaCaminoDijkstra.getItems().clear();
            if(resultado.existeRuta()) {
                for(int i=0;i<resultado.getCantidad();i++)camino.add(resultado.getNodo(i));
                listaCaminoDijkstra.getItems().setAll(camino);
                lblResultadoDistanciaRuta.setText(""+resultado.getDistanciaTotal());
                lblDistanciaDijkstra.setText(""+resultado.getDistanciaTotal());
                lblCantidadNodosRuta.setText(""+resultado.getCantidad());
            }else {
                lblResultadoDistanciaRuta.setText("Sin ruta");
                lblDistanciaDijkstra.setText("—");lblCantidadNodosRuta.setText("0");
            }
            dibujar();
        }catch(Exception ex){alerta(ex.getMessage());}
    }
    private void nuevoPunto() {
        if(sistema==null)return;
        TextInputDialog dialog=new TextInputDialog();
        dialog.setTitle("Nuevo punto de servicio");
        dialog.setHeaderText("Agregar recepción, escalera, ascensor o pasillo");
        dialog.setContentText("Nombre único:");
        dialog.showAndWait().ifPresent(nombre->{
            try {
                MapaGrafoCompartido.registrarServicio(sistema,nombre);
                actualizar();
            }catch(Exception ex){alerta(ex.getMessage());}
        });
    }
    private void conectar() {editarConexion(false);}
    private void eliminarConexion() {editarConexion(true);}
    private void editarConexion(boolean eliminar) {
        if(sistema==null)return;
        List<String> nodos=new ArrayList<>(posiciones.keySet());
        if(nodos.size()<2){alerta("Registra al menos dos ubicaciones.");return;}
        Dialog<ButtonType> dialog=new Dialog<>();
        dialog.setTitle(eliminar?"Eliminar pasillo":"Conectar ubicaciones");
        dialog.setHeaderText(eliminar?"Selecciona una conexión existente":"Indica las ubicaciones y la distancia real en metros");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        ComboBox<String> origen=new ComboBox<>();
        ComboBox<String> destino=new ComboBox<>();
        origen.getItems().addAll(nodos);destino.getItems().addAll(nodos);
        origen.setPrefWidth(240);destino.setPrefWidth(240);
        origen.setValue(cmbOrigenDijkstra.getValue()!=null?cmbOrigenDijkstra.getValue():nodos.get(0));
        destino.setValue(cmbDestinoDijkstra.getValue()!=null?cmbDestinoDijkstra.getValue():nodos.get(1));
        TextField peso=new TextField();
        peso.setPromptText("Ej. 12.5");
        GridPane grid=new GridPane();grid.setHgap(10);grid.setVgap(10);grid.setPadding(new Insets(12));
        grid.addRow(0,new Label("Origen:"),origen);
        grid.addRow(1,new Label("Destino:"),destino);
        if(!eliminar)grid.addRow(2,new Label("Metros:"),peso);
        dialog.getDialogPane().setContent(grid);
        if(dialog.showAndWait().orElse(ButtonType.CANCEL)!=ButtonType.OK)return;
        try {
            if(eliminar)MapaGrafoCompartido.desconectar(sistema,origen.getValue(),destino.getValue());
            else MapaGrafoCompartido.conectar(sistema,origen.getValue(),destino.getValue(),
                    Double.parseDouble(peso.getText().strip().replace(',','.')));
            camino=List.of();listaCaminoDijkstra.getItems().clear();
            lblResultadoDistanciaRuta.setText("—");lblDistanciaDijkstra.setText("—");
            lblCantidadNodosRuta.setText("0");
            actualizar();
        }catch(Exception ex){alerta("No se pudo modificar el grafo: "+ex.getMessage());}
    }
    private void navegar(String archivo) {
        if(sistema==null)return;
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource(archivo));
            Scene scene=new Scene(loader.load());
            Object ctrl=loader.getController();
            if(ctrl instanceof VentanaPrincipalController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaReservasController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaCalendarioOcupacionController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaPanelUsuarioController c)c.setSistema(sistema);
            else if(ctrl instanceof VistaPanelPreparacionController c)c.setSistema(sistema);
            ((Stage)btnMenuInicio.getScene().getWindow()).setScene(scene);
        }catch(Exception ex){alerta(ex.toString());}
    }
    private void alerta(String mensaje) {
        new Alert(Alert.AlertType.INFORMATION,mensaje).showAndWait();
    }
}
