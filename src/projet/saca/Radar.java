package projet.saca;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Radar {

    public int PAUSE = 1000;
    public int tempsMiseAJour = 10000;
    public int tempsPasse = 0;
    private static ArrayList<String> listAvion = new ArrayList<String>();
    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    boolean ouvrir_communication() {
        boolean conxOuverte = false;
        try {
            socket = new Socket("localhost", Saca.portRadar);
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

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        Radar r = new Radar();
        r.lancerRadar();
    }

    void actionSurListe() throws ClassNotFoundException {
        try {
            ObjetInter objetInter = null;
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            //objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            while ((objetInter = (ObjetInter) objectInputStream.readObject()) != null) {
                switch (objetInter.getAction().toLowerCase()) {
                    case "envoi_donnees":
                        String nouvelleNumeroAvion = retourneNumeroAvion(objetInter.getMessage());
                        boolean numAvionExiste = false;
                        for (String stringAvion : listAvion) {
                            if (nouvelleNumeroAvion.equals(retourneNumeroAvion(stringAvion))) {
                                numAvionExiste = true;
                                break;
                            }
                        }
                        if (numAvionExiste == false) {
                            System.out.println("L'avion numero " + nouvelleNumeroAvion + " est détectée par radar et envoyée au controlleur");
                            listAvion.add(objetInter.getMessage());
                        } else {
                            
                            int indexAvion = chercherIndexAvion(nouvelleNumeroAvion);
                            if (indexAvion > 0) {
                                listAvion.set(indexAvion, objetInter.getMessage());
                            }
                        }
                        break;
                    case "avoir_liste":
                        envoie_listeAvion();
                        break;
                }

            }
            objectInputStream.close();
        } catch (IOException ex) {

        }
    }

    String retourneNumeroAvion(String avion) {
        String numeroAvion = "";
        String[] avionSansVirgule = avion.split(",");
        if (avionSansVirgule[0].length() > 0) {
            String[] numerAvionArray = avionSansVirgule[0].split(":");
            numeroAvion = numerAvionArray[1].trim();
        }
        return numeroAvion;
    }

    void lancerRadar() throws ClassNotFoundException {
        System.out.println("Le radar a démarré");
        while (true) {

            try {
                ouvrir_communication();
                Thread.sleep(PAUSE);
                actionSurListe();
                tempsPasse += PAUSE;
                if (tempsPasse == tempsMiseAJour) {
                    //afficherListAvion(); 
                    tempsPasse = 0;
                }
                fermer_communication();
            } catch (InterruptedException ex) {
                
            }
        }
        
    }

    void afficherListAvion() {
        System.out.println("===========================");
        System.out.println("Liste des avions");
        for (String avion : listAvion) {
            System.out.println(avion);
        }
        System.out.println("===========================");
    }

    int chercherIndexAvion(String numeroAvion) {
        int indexAvion = 0;
        for (int i = 0; i < listAvion.size(); i++) {
            String[] avionSansVirgule = listAvion.get(i).split(",");
            if (avionSansVirgule[0].length() > 0) {
                String[] numerAvionArray = avionSansVirgule[0].split(":");
                if (numeroAvion.equals(numerAvionArray[1].trim())) {
                    indexAvion = i;
                    break;
                }
            }
        }
        return indexAvion;
    }
    
    void envoie_listeAvion() throws IOException {
        //if (socket.isConnected()) {
            try {
                Socket socketControlleur = new Socket("localhost", Saca.portControlleur);
                objectOutputStream = new ObjectOutputStream(socketControlleur.getOutputStream()); //socket.getOutputStream()
                objectOutputStream.writeObject(new ObjetInter("Radar", "envoi_liste", listAvion.toString()));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                objectOutputStream.flush();
                objectOutputStream.close();
                
            } catch (IOException ex) {
                System.out.println("Exception " + ex.getMessage());
                objectOutputStream.close();
            }
//        } else {
//            System.out.println("Socket non connecte !");
//        }
    }
}
