package uniquindio.edu.co.proyectofinal;

import javafx.scene.control.Button;
import uniquindio.edu.co.proyectofinal.modelo.Huesped;
import uniquindio.edu.co.proyectofinal.modelo.Empleado;

/** Selección de perfil para la demostración académica; no es autenticación. */
public final class SesionActual {
    public enum Rol { HUESPED, RECEPCIONISTA, ADMINISTRADOR }
    private static Rol rol;
    private static Huesped huesped;
    private static Empleado empleado;
    private SesionActual() {}
    public static void entrarHuesped(Huesped h) { if(h==null)throw new IllegalArgumentException("Huésped requerido"); huesped=h; empleado=null; rol=Rol.HUESPED; }
    public static void entrarEmpleado(Empleado e) { if(e==null)throw new IllegalArgumentException("Empleado requerido"); empleado=e; huesped=null; rol=e.esAdministrador()?Rol.ADMINISTRADOR:Rol.RECEPCIONISTA; }
    public static void cerrar() { rol=null; huesped=null; empleado=null; }
    public static Rol rol() { return rol; }
    public static Huesped huesped() { return huesped; }
    public static boolean esHuesped() { return rol==Rol.HUESPED; }
    public static boolean esAdministrador() { return rol==Rol.ADMINISTRADOR; }
    public static void exigirPersonal() { if(rol!=Rol.RECEPCIONISTA && rol!=Rol.ADMINISTRADOR) throw new SecurityException("Vista reservada al personal del alojamiento"); }
    public static void exigirAdministrador() { if(!esAdministrador())throw new SecurityException("Vista reservada a administración"); }
    public static void ocultar(Button... botones) { if(esHuesped()) for(Button b:botones)if(b!=null){b.setVisible(false);b.setManaged(false);} }
    public static void ocultarSoloAdministrador(Button... botones) { if(!esAdministrador()) for(Button b:botones)if(b!=null){b.setVisible(false);b.setManaged(false);} }
}
