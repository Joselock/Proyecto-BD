import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.net.URL;

public class TestFXMLLoading {
    public static void main(String[] args) {
        String[] fxmlPaths = {
            "/com/mycompany/gestorui/views/Reportes/ResumenProceso.fxml",
            "/com/mycompany/gestorui/views/Reportes/RevisarTurnos.fxml",
            "/com/mycompany/gestorui/views/Reportes/NoAtendidos.fxml"
        };
        
        for (String fxmlPath : fxmlPaths) {
            try {
                URL resource = TestFXMLLoading.class.getResource(fxmlPath);
                if (resource == null) {
                    System.err.println("❌ No se encontró: " + fxmlPath);
                    continue;
                }
                
                Parent view = FXMLLoader.load(resource);
                System.out.println("✅ Cargado exitosamente: " + fxmlPath);
            } catch (Exception ex) {
                System.err.println("❌ Error cargando: " + fxmlPath);
                System.err.println("   " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
