package projet.saca;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controlleur implements Runnable {
    static int selectionMenu = -1;
    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    private Avion avion;
    static volatile boolean keepRunning = true;
    static Controlleur test ;
    static Thread t;
    
    boolean ouvrir_communication() {
        boolean conxOuverte = false;
        try {
            socket = new Socket("localhost", Saca.portControlleur);
            conxOuverte = true;
        } catch (IOException ex) {
            //System.out.println(ex.getStackTrace());
            return conxOuverte;
        }
        // fonction à implémenter qui permet d'entrer en communication via TCP
        // avec le gestionnaire de vols
        return conxOuverte;
    }

    void fermer_communication() {

        try {
            if (socket.isConnected()) {
                socket.close();
            }
        } catch (IOException ex) {
        }

        // fonction à implémenter qui permet de fermer la communication
        // avec le gestionnaire de vols
    }

    void lancerControlleur() throws ClassNotFoundException, IOException {
        System.out.println("Choisir avion : ");
        while (true) {
            ouvrir_communication();
            //Thread.sleep(PAUSE);
            
            //demandeListAvion();
            //afficherListAvion();
            fermer_communication();

        }
    }
    
    void demandeListAvion(){
        try {
            ObjetInter objetInter = null;
            //objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new ObjetInter("Controlleur", "avoir_liste", ""));
            objectOutputStream.flush();
            //objectOutputStream.close();
            
            //while ((objetInter = (ObjetInter) objectInputStream.readObject()) != null) {
                //System.out.println(objetInter.getMessage());
            //}
            //objectInputStream.close();
        } catch (IOException ex) {

        }
    }
    
    void afficherListAvion() throws ClassNotFoundException, IOException{
        try {
            ObjetInter objetInter = null;
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            //objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            while ((objetInter = (ObjetInter) objectInputStream.readObject()) != null) {
                    switch (objetInter.getAction().toLowerCase()) {
                        case "envoi_liste":
                            objetInter.getMessage().toString();
                            break;
                    }
            }
            objectInputStream.close();
            
        } catch (IOException ex) {

        }
    }
    
    public void run() {
        System.out.println("Starting to loop.");
        while (keepRunning) {
            ouvrir_communication();
            System.out.println("Running loop...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                switch(selectionMenu){
                    case 0:
                        //System.out.println("0 est selectionné");
                        
                        
                        {
                            try {
                                Thread.sleep(3000);
                                demandeListAvion();
                                Thread.sleep(3000);
                                afficherListAvion();
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(Controlleur.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Controlleur.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InterruptedException ex) {
                            }
                            
                        }
                        break;
                }

            }
            fermer_communication();
        }
        afficherMenu();
        System.out.println("Done looping.");
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        test = new Controlleur();

        afficherMenu();
        
        
        //Controlleur c = new Controlleur();
        //c.lancerControlleur();
    }
    
    static void afficherMenu(){
        selectionMenu = -1;
        keepRunning = true;
        t = new Thread(test);
        t.start();
        
        System.out.println("Choisir du menu : ");
        System.out.println("--------------------------------");
        System.out.println("0 - Quitter Menu");
        System.out.println("1 - Afficher liste des avions");
        System.out.println("2 - Attacher pilote à une avion");
        Scanner s = new Scanner(System.in);
        while ((selectionMenu = s.nextInt()) != 0);

        test.keepRunning = false;
        t.interrupt();  // cancel current sleep.
    }
}
