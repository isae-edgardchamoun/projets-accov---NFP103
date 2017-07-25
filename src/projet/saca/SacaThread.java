package projet.saca;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static projet.saca.Saca.portRadar;

public class SacaThread extends Thread{
    Socket socket = null;
    SacaThread(Socket socket){
        this.socket = socket;
    }
    
    public void run(){
        try {
            ObjetInter objetInter = null;
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            while ((objetInter = (ObjetInter)objectInputStream.readObject()) != null) {
                switch (socket.getLocalPort()) {
                    case 2000:
                        //LES TACHES D'AVION
                        System.out.println(objetInter.getMessage());
                        if(objetInter.getAction().toLowerCase().equals("envoi_donnees") && Saca.socketRadar.isConnected()){
                            //ServerSocket socketServerRadar = new ServerSocket(portRadar);
                            //Socket socketRadar = socketServerRadar.accept();
                            ObjectOutputStream objectOutputStreamRadar = new ObjectOutputStream(Saca.socketRadar.getOutputStream());
                            objectOutputStreamRadar.writeObject(objetInter);
                            objectOutputStreamRadar.flush();
                            objectOutputStreamRadar.close();
                        }
                        break;
                    case 3000:
                        //LES TACHES DU RADAR
                        //System.out.println(objetInter.toString());
                        if(objetInter.getAction().toLowerCase().equals("envoi_liste") && Saca.socketControlleur.isConnected()){
                            ObjectOutputStream objectOutputStreamControllleur = new ObjectOutputStream(Saca.socketControlleur.getOutputStream());
                            objectOutputStreamControllleur.writeObject(objetInter);
                            objectOutputStreamControllleur.flush();
                            objectOutputStreamControllleur.close();
                        }
                        break;
                    case 4000:
                        //LES TACHES DU CONTROLLEUR
                        //System.out.println(objetInter.toString());
                        if (objetInter.getAction().toLowerCase().equals("avoir_liste") && Saca.socketRadar.isConnected()) {
                            ObjectOutputStream objectOutputStreamRadar = new ObjectOutputStream(Saca.socketRadar.getOutputStream());
                            objectOutputStreamRadar.writeObject(objetInter);
                            objectOutputStreamRadar.flush();
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                            }
                            objectOutputStreamRadar.close();
                            System.out.println("END STREAM RADAR !!");
                        }
                        
                        break;
                }
                
            }
            objectInputStream.close();
            socket.close();
            
        } catch (IOException ex) {
            //System.out.println("Radar exception : " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            //System.out.println("Radar exception : " + ex.getMessage());
        }
        
    }
}
