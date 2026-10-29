package uvm;
import javax.swing.*;
import java.awt.*;
public class HiloClima extends Thread {
    private final VentanaMatrizHilos ventana;
    private volatile boolean activo = true;
    private volatile boolean pausa = false;
    private volatile boolean activado = false;
    public HiloClima(VentanaMatrizHilos ventana) {
        this.ventana = ventana;
    }
    public void activar() {
        activado = true;
        synchronized (this) {
            notifyAll();
        }
    }
    public void pausarOContinuar() {
        pausa = !pausa;
        synchronized (this) {
            notifyAll();
        }
    }
    public boolean estaPausado() {
        return pausa;
    }
    public void terminar() {
        activo = false;
        synchronized (this) {
            notifyAll();
        }
        interrupt();
    }
    private void esperarEstado() throws InterruptedException {
        synchronized (this) {
            while (activo && (pausa || !activado)) wait();
        }
    }
    @Override
    public void run() {
        while (activo) {
            try {
                esperarEstado();
                if (!activo) break;
                JLabel[][] matriz = ventana.getMatrizLabels();
                for (int f = 0; f < 15 && activo; f++) {
                    for (int c = 0; c < 15 && activo; c++) {
                        esperarEstado();
                        if (!activo) break;
                        Color color = matriz[f][c].getForeground();
                        Color nuevo;
                        if (color.equals(Color.BLACK)) {
                            nuevo = Color.RED;
                        } else if (color.equals(Color.RED)) {
                            nuevo = Color.BLUE;
                        } else {
                            nuevo = Color.BLACK;
                        }
                        ventana.actualizarColor(f, c, nuevo);
                        Thread.sleep(30);
                    }
                }
            } catch (InterruptedException e) {
                if (!activo) break;
            }
        }
    }
}