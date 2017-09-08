package projet.saca;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Saca {
    public static final int portAvion = 2000;
    public static final int portRadar = 3000;
    public static final int portControlleur = 4000;
    public static ServerSocket serveurPortAvion;
    public static ServerSocket serveurPortRadar;
    public static ServerSocket serveurPortControlleur;
    
    public static Socket socketAvion;
    public static Socket socketRadar;
    public static Socket socketControlleur;
    public static void main(String []args) throws IOException, ClassNotFoundException{
        new Saca().demarerServeur();
    }
    
    public void demarerServeur() throws IOException, ClassNotFoundException{
        serveurPortAvion = new ServerSocket(portAvion);
        serveurPortAvion.setReuseAddress(true);
        serveurPortRadar = new ServerSocket(portRadar);
        serveurPortRadar.setReuseAddress(true);
        serveurPortControlleur = new ServerSocket(portControlleur);
        serveurPortControlleur.setReuseAddress(true);
        //ServerSocket serveurPortControlleur = new ServerSocket(portControlleur);
        System.out.println("Le serveur a démarré");
        while(true){
            try {
                socketAvion = serveurPortAvion.accept();
                SacaThread sacaThreadAvion = new SacaThread(socketAvion);
                sacaThreadAvion.start();
                //sacaThreadAvion.join();
                
                socketRadar = serveurPortRadar.accept();
                SacaThread sacaThreadRadar = new SacaThread(socketRadar);
                sacaThreadRadar.start();
                
                
                socketControlleur = serveurPortControlleur.accept();
                SacaThread sacaThreadControlleur = new SacaThread(socketControlleur);
                sacaThreadControlleur.start();
            } catch (Exception ex) {
                //Logger.getLogger(Saca.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Message d'erreur : " + ex.getMessage());
            }
        }    
    }
}
