package uniquindio.edu.co.proyectofinal;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.*;
import java.time.LocalDate;

/** Selector de perfiles sin contraseña, con registro emergente para huéspedes. */
public final class SeleccionPerfil {
    private final SistemaSGA sistema;
    private final Stage stage;
    public SeleccionPerfil(SistemaSGA sistema,Stage stage){this.sistema=sistema;this.stage=stage;}
    public void mostrar(){
        Label titulo=new Label("Aparta-Suites UQ · Seleccionar perfil");titulo.setStyle("-fx-font-size:22px;-fx-font-weight:bold");
        ComboBox<String> roles=new ComboBox<>();roles.getItems().addAll("Huésped","Recepcionista","Administrador");roles.setValue("Huésped");roles.setMaxWidth(Double.MAX_VALUE);
        TextField documento=new TextField();documento.setPromptText("Documento de identidad");
        Button entrar=new Button("Entrar");Button registrar=new Button("Crear nuevo huésped");
        Button primerAdmin=new Button("Crear administrador inicial");
        primerAdmin.setVisible(sistema.getServicioEmpleados().listarPorRol(RolEmpleado.ADMINISTRADOR).estaVacia());
        primerAdmin.setManaged(primerAdmin.isVisible());
        primerAdmin.setOnAction(e->{ registrarAdministradorInicial(); boolean falta=sistema.getServicioEmpleados().listarPorRol(RolEmpleado.ADMINISTRADOR).estaVacia(); primerAdmin.setVisible(falta); primerAdmin.setManaged(falta); });
        Label ayuda=new Label("Acceso de demostración: no requiere contraseña.");
        entrar.setMaxWidth(Double.MAX_VALUE);
        roles.valueProperty().addListener((o,a,b)->registrar.setVisible("Huésped".equals(b)));
        entrar.setOnAction(e->{
            try{
                String doc=documento.getText().strip();if(doc.isEmpty())throw new IllegalArgumentException("Escribe un documento.");
                if("Huésped".equals(roles.getValue())){
                    Huesped h=sistema.getServicioHuesped().buscarPorDocumento(doc);
                    if(h==null)throw new IllegalArgumentException("Huésped no registrado. Usa Crear nuevo huésped.");
                    SesionActual.entrarHuesped(h);
                }else{
                    Empleado emp=sistema.getServicioEmpleados().buscarPorDocumento(doc);
                    if(emp==null)throw new IllegalArgumentException("Empleado no registrado.");
                    if("Administrador".equals(roles.getValue())&&!emp.esAdministrador() || "Recepcionista".equals(roles.getValue())&&!emp.esRecepcionista())
                        throw new IllegalArgumentException("El empleado no pertenece al rol seleccionado.");
                    SesionActual.entrarEmpleado(emp);
                }
                abrirDestino();
            }catch(Exception ex){aviso(ex.getMessage());}
        });
        registrar.setOnAction(e->{Huesped nuevo=registroEmergente();if(nuevo!=null){documento.setText(nuevo.getDocumento());roles.setValue("Huésped");}});
        VBox raiz=new VBox(15,titulo,new Label("Rol"),roles,new Label("Documento"),documento,entrar,registrar,primerAdmin,ayuda);
        raiz.setPadding(new Insets(32));raiz.setStyle("-fx-background-color:#f3f6fa");
        stage.setMinWidth(540); stage.setMinHeight(420); stage.setScene(new Scene(raiz,540,420));stage.setTitle("Aparta-Suites UQ · Perfiles");stage.show();
    }
    private void registrarAdministradorInicial() {
        if (!sistema.getServicioEmpleados().listarPorRol(RolEmpleado.ADMINISTRADOR).estaVacia()) {
            aviso("Ya existe un administrador. No se permite crear otro desde el selector."); return;
        }
        Dialog<ButtonType> d=new Dialog<>(); d.initOwner(stage); d.setTitle("Administrador inicial");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,new ButtonType("Guardar",ButtonBar.ButtonData.OK_DONE));
        TextField nombre=new TextField(),doc=new TextField(),tel=new TextField(),correo=new TextField();
        DatePicker fecha=new DatePicker();
        GridPane g=new GridPane();g.setHgap(12);g.setVgap(12);g.setPadding(new Insets(18));
        String[] etiquetas={"Nombre completo","Documento","Fecha de nacimiento","Teléfono","Correo"};
        Control[] controles={nombre,doc,fecha,tel,correo};
        for(int i=0;i<etiquetas.length;i++){g.add(new Label(etiquetas[i]),0,i);g.add(controles[i],1,i);}
        d.getDialogPane().setContent(g);
        if(d.showAndWait().orElse(ButtonType.CANCEL).getButtonData()!=ButtonBar.ButtonData.OK_DONE)return;
        try {
            if(fecha.getValue()==null || fecha.getValue().isAfter(LocalDate.now()))throw new IllegalArgumentException("Fecha inválida");
            sistema.getServicioEmpleados().registrarEmpleado(nombre.getText(),fecha.getValue(),doc.getText(),tel.getText(),correo.getText(),RolEmpleado.ADMINISTRADOR);
            sistema.guardar();
            aviso("Administrador registrado. Selecciona Administrador e ingresa su documento.");
        } catch(Exception ex){aviso("No se pudo registrar el administrador: "+ex.getMessage());}
    }
    private Huesped registroEmergente(){
        Dialog<ButtonType> d=new Dialog<>();d.initOwner(stage);d.setTitle("Registrar nuevo huésped");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,new ButtonType("Guardar",ButtonBar.ButtonData.OK_DONE));
        TextField nombre=new TextField(),doc=new TextField(),tel=new TextField(),correo=new TextField();
        DatePicker fecha=new DatePicker();
        GridPane g=new GridPane();g.setHgap(12);g.setVgap(12);g.setPadding(new Insets(18));
        String[] etiquetas={"Nombre completo","Documento","Fecha de nacimiento","Teléfono","Correo"};
        Control[] controles={nombre,doc,fecha,tel,correo};
        for(int i=0;i<etiquetas.length;i++){g.add(new Label(etiquetas[i]),0,i);g.add(controles[i],1,i);}
        d.getDialogPane().setContent(g);
        if(d.showAndWait().orElse(ButtonType.CANCEL).getButtonData()!=ButtonBar.ButtonData.OK_DONE)return null;
        try{
            if(fecha.getValue()==null||fecha.getValue().isAfter(LocalDate.now()))throw new IllegalArgumentException("Fecha de nacimiento inválida.");
            Huesped h=new Huesped(nombre.getText(),fecha.getValue(),doc.getText(),tel.getText(),correo.getText());
            if(!sistema.getServicioHuesped().registrarHuesped(h))throw new IllegalArgumentException("Ya existe ese documento.");
            sistema.guardar();return h;
        }catch(Exception ex){aviso("No se pudo registrar: "+ex.getMessage());return null;}
    }
    private void abrirDestino() throws Exception{
        String archivo=SesionActual.esHuesped()?"VistaPanelUsuario.fxml":"VentanaPrincipal.fxml";
        javafx.fxml.FXMLLoader l=new javafx.fxml.FXMLLoader(getClass().getResource(archivo));
        Scene escena=new Scene(l.load());
        if(SesionActual.esHuesped())((VistaPanelUsuarioController)l.getController()).setSistema(sistema);
        else ((VentanaPrincipalController)l.getController()).setSistema(sistema);
        stage.setScene(escena);stage.setMinWidth(1050);stage.setMinHeight(650);
    }
    private void aviso(String mensaje){new Alert(Alert.AlertType.WARNING,mensaje==null?"Error":mensaje,ButtonType.OK).showAndWait();}
}
