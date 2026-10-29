package uvm;

import javax.swing.*;
import java.awt.*;

public class HiloLimpiador extends Thread {
    private final VentanaMatrizHilos ventana;
    private volatile boolean Activo = true;
    private volatile boolean Pausa = false;

    public HiloLimpiador(VentanaMatrizHilos ventana)
    {
        this.ventana = ventana;
    }

    public void Pausar()
    {
        Pausa = !Pausa;
        synchronized (this) {
            notifyAll();
        }
    }

    public boolean EstaPausado()
    {
        return Pausa;
    }

    public void Terminar()
    {
        Activo = false;
        synchronized (this)
        {
            notifyAll();
        }
        interrupt();
    }

    private void EsperarPausa() throws InterruptedException
    {
        synchronized (this)
        {
            while (Pausa && Activo) wait();
        }
    }

    @Override
    public void run()
    {
        while (Activo)
            {
            try
            {
                EsperarPausa();
                if (!Activo) break;

                JLabel[][] Matriz = ventana.GetMatrizLabels();
                int Modificadas = 0;

                for (int f = 0; f < 15; f++)
                    {
                        for (int c = 0; c < 15; c++)
                            {
                            if (!"-".equals(Matriz[f][c].getText()) || !Matriz[f][c].getForeground().equals(Color.BLACK)) {
                            Modificadas++;
                        }
                    }
                }

                if (Modificadas > 112) {
                    ventana.LimpiarBloque();
                }

                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                if (!Activo) break;
            }
        }
    }
}