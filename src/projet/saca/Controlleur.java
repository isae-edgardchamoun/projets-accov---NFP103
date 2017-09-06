package projet.saca;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private ArrayList<String> listAvion = new ArrayList<String>();
    private final Object lock = new Object();
    private static Map<String, String> listControlleurAttache = new HashMap<String, String>();
    static int compteurControlleur = 1;
    static boolean choisirAvion = false;
    
    boolean ouvrir_communication() {
        boolean conxOuverte = false;
        try {
            socket = new Socket("localhost", Saca.portControlleur);
            conxOuverte = true;
        } catch (IOException ex) {
            //System.out.println(ex.getStackTrace());
            return conxOuverte;
        }
        return conxOuverte;
    }

    void fermer_communication() {

        try {
            if (socket.isConnected()) {
                socket.close();
            }
        } catch (IOException ex) {
        }
    }
 
   
    public void demandeListAvion() throws ClassNotFoundException, IOException{
        try {
            ObjetInter objetInter = null;
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            while ((objetInter = (ObjetInter) objectInputStream.readObject()) != null) {
                synchronized(lock){
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
                                listAvion.add(objetInter.getMessage());
                            } else {
                                int indexAvion = chercherIndexAvion(nouvelleNumeroAvion);
                                if (indexAvion > 0) {
                                    listAvion.set(indexAvion, objetInter.getMessage());
                                }
                            }
                            break;
                    }
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
    
    @Override
    public void run() {
        //System.out.println("Starting to loop.");
        while (keepRunning) {
            ouvrir_communication();
            //System.out.println("Running loop...");
            try {
                demandeListAvion();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Controlleur.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Controlleur.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            switch(selectionMenu){
                case 1:
                    {    
                        afficherListAvion(); 
                    }
                    break;
                    
                case 2:
                    {    
                        choisirAvion = true;
                        System.out.println("Choisir nom de l'avion de la liste"); 
                        
                        Scanner scanner = new Scanner(System.in);
                        String nomAvionChoisi = "";
                        nomAvionChoisi = scanner.nextLine();
                        
                        
                        //VOIR SI L'AVION EXISTE DEJA DANS LA LISTE DES AVIONS
                        boolean avionExisteListeAvion =  false;
                        for (String avion : listAvion) {
                            if(nomAvionChoisi == retourneNumeroAvion(avion)){
                                avionExisteListeAvion = true;
                            }
                        }
                        
                        boolean avionExisteListeControlleurAttache = false;
                        boolean controlleurExisteListeControlleurAttache = false;
                        //VOIR SI LE CONTROLLEUR OU L'AVION EXISTE DEJA DANS LA LISTE LISTECONTROLLEURATTACHE
                        for (Map.Entry<String, String> entrySet : listControlleurAttache.entrySet()) {
                            String key = entrySet.getKey();
                            String value = entrySet.getValue();
                            if(value == nomAvionChoisi){
                                avionExisteListeControlleurAttache = true;
                            }
                            
                            if(key == Thread.currentThread().getName()){
                                controlleurExisteListeControlleurAttache = true;
                            }
                        }
                        
                        //AJOUTER L'INSTANCE A LA LISTE 
                        if(avionExisteListeAvion == true && avionExisteListeControlleurAttache == false && controlleurExisteListeControlleurAttache == false){
                            listControlleurAttache.put(Thread.currentThread().getName(), nomAvionChoisi);
                            System.out.println("Pilote " + Thread.currentThread().getName() + " est déjà attaché à l'avion " + nomAvionChoisi); 
                        }else{
                            if(!avionExisteListeAvion){
                                System.out.println("Avion n'existe pas dans la liste des avions"); 
                            }
                            
                            if(avionExisteListeControlleurAttache){
                                System.out.println("Avion est déjà attachée à un pilote"); 
                            }
                            
                            if(controlleurExisteListeControlleurAttache){
                                System.out.println("Pilote est déjà attaché à une avion"); 
                            }
                        }
                        
                        System.out.println(listControlleurAttache.toString()); 
                        choisirAvion = false;
                    }
                    break;
            }
            
            if(selectionMenu != 0 && selectionMenu != -1){
                afficherMenu();
            }
            fermer_communication();
        }
        //afficherMenu();
        //System.out.println("Done looping.");
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
        t.setName("Controlleur " + compteurControlleur);
        compteurControlleur++;
        t.start();
        
        System.out.println("Choisir du menu : ");
        System.out.println("--------------------------------");
        System.out.println("0 - Quitter Menu");
        System.out.println("1 - Afficher liste des avions");
        System.out.println("2 - Attacher pilote à une avion");
        Scanner s = new Scanner(System.in);
        //while ((selectionMenu = s.nextInt()) != 0 && selectionMenu != 2);

        while (s.hasNextInt() && !choisirAvion){
            selectionMenu = s.nextInt();
        }
        
        //test.keepRunning = false;
        //t.interrupt();  // cancel current sleep.
    }
    
    public void afficherListAvion(){
        if(listAvion.size() > 0){
            for (String avion : listAvion) {
                System.out.println(avion);
            }
        }
    }
}
