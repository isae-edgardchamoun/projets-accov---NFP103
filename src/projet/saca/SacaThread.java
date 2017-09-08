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
                        
                        if(objetInter.getAction().toLowerCase().equals("envoi_donnees") && Saca.socketRadar.isConnected()){
                            System.out.println("Echangement d'informations entre SACA et Radar");
                            ObjectOutputStream objectOutputStreamRadar = new ObjectOutputStream(Saca.socketRadar.getOutputStream());
                            objectOutputStreamRadar.writeObject(objetInter);
                            objectOutputStreamRadar.flush();
                            objectOutputStreamRadar.close();
                        }
                        
                        if(objetInter.getAction().toLowerCase().equals("envoi_donnees") && Saca.socketControlleur.isConnected()){
                            System.out.println("Echangement d'informations entre SACA et Controlleur");
                            ObjectOutputStream objectOutputStreamControlleur = new ObjectOutputStream(Saca.socketControlleur.getOutputStream());
                            objectOutputStreamControlleur.writeObject(objetInter);
                            objectOutputStreamControlleur.flush();
                            objectOutputStreamControlleur.close();
                        }
                        break;
                    case 3000:
                        //LES TACHES DU RADAR
                        //System.out.println(objetInter.toString());
//                        if(objetInter.getAction().toLowerCase().equals("envoi_liste") && Saca.socketControlleur.isConnected()){
//                            ObjectOutputStream objectOutputStreamControllleur = new ObjectOutputStream(Saca.socketControlleur.getOutputStream());
//                            objectOutputStreamControllleur.writeObject(objetInter);
//                            objectOutputStreamControllleur.flush();
//                            objectOutputStreamControllleur.close();
//                        }
                        break;
                    case 4000:
                        //LES TACHES DU CONTROLLEUR
                        //System.out.println(objetInter.toString());
//                        if (objetInter.getAction().toLowerCase().equals("avoir_liste") && Saca.socketRadar.isConnected()) {
//                            ObjectOutputStream objectOutputStreamRadar = new ObjectOutputStream(Saca.socketRadar.getOutputStream());
//                            objectOutputStreamRadar.writeObject(objetInter);
//                            objectOutputStreamRadar.flush();
//                            objectOutputStreamRadar.close();
//                        }else{
//                            
//                            if(objetInter.getAction().toLowerCase().equals("envoi_liste") && Saca.socketControlleur.isConnected()){
//                                ObjectOutputStream objectOutputStreamControllleur = new ObjectOutputStream(Saca.socketControlleur.getOutputStream());
//                                objectOutputStreamControllleur.writeObject(objetInter);
//                                objectOutputStreamControllleur.flush();
//                                objectOutputStreamControllleur.close();
//                            }
//                        }
                        
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
