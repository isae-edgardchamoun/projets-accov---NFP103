package projet.saca;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Avion implements Serializable {
    Random rand = new Random();
    
    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    
    public int ALTMAX = 20000;
    public int ALTMIN = 0;

    public int VITMAX = 1000;
    public int VITMIN = 200;

    public int PAUSE = 2000;
    
    private int posX;
    private int posY;
    private int posZ;
    private int VX;
    private int VY;
    private int VZ;
    private int altitude;
    private int cap;
    private boolean poussee;
    private int acceleration;

    // numéro de vol de l'avion : code sur 5 caractéres
    private String numero_vol = "";
    
    
    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }
    
    public void setPosY(int posY) {
        this.posY = posY;
    }
    
    public int getPosZ() {
        return posZ;
    }

    public void setPosZ(int posZ) {
        this.posZ = posZ;
    }

    public int getVX() {
        return VX;
    }

    public void setVX(int VX) {
        this.VX = VX;
    }

    public int getVY() {
        return VY;
    }

    public void setVY(int VY) {
        this.VY = VY;
    }

    public int getVZ() {
        return VZ;
    }

    public void setVZ(int VZ) {
        this.VZ = VZ;
    }

    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public boolean isPoussee() {
        return poussee;
    }

    public void setPoussee(boolean poussee) {
        this.poussee = poussee;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
//        Socket socket = new Socket("localhost", Saca.portAvion);
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//        ObjetInter objetInter = new ObjetInter("Avion", "AFFICHAGE CLASSE");
//        objectOutputStream.writeObject(objetInter);
//        socket.close();
       Avion a = new Avion();
       a.initialiser_avion();
       a.se_deplacer();
    }

    boolean ouvrir_communication() {
        boolean conxOuverte = false;
        try {
            socket = new Socket("localhost", Saca.portAvion);
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
//
    void envoyer_caracteristiques() {
        try {
            if(socket.isConnected()){
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(new ObjetInter("Avion", "envoi_donnees", get_donnees()) );
                objectOutputStream.close();
            }else{
                System.out.println("Socket non connecte !");
            }
            
        } catch (IOException ex) {
        }
        // fonction à implémenter qui envoie l'ensemble des caractéristiques
        // courantes de l'avion au gestionnaire de vols
    }
// initialise aléatoirement les paramétres initiaux de l'avion
    int getRandomNumber() {
        return rand.nextInt(50) + 1;
    }
//
    private static char rndChar() {
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);

    }
//
    void initialiser_avion() {
        // intialisation des paramétres de l'avion
        posX = 1000 + getRandomNumber() % 1000;
        posY = 1000 + getRandomNumber() % 1000;
        posZ = 1000 + getRandomNumber() % 1000;
        altitude = 900 + getRandomNumber() % 100;

        cap = getRandomNumber() % 360;
        VX = 600 + getRandomNumber() % 200;
        VY = 600 + getRandomNumber() % 200;
        VZ = 600 + getRandomNumber() % 200;
        
        poussee = false;
        acceleration = 0;
        
        // initialisation du numero de l'avion : chaine de 5 caracteres 
        // formée de 2 lettres puis 3 chiffres
        numero_vol += rndChar();
        numero_vol += rndChar();
        Random r = new Random();
        numero_vol += (char)(r.nextInt(26) + 'a');
        numero_vol += (char)(r.nextInt(26) + 'a');
        numero_vol += (char)(r.nextInt(26) + 'a');
        System.out.println("L'avion " + numero_vol + " a démaré");
    }
//// modifie la valeur de l'avion avec la valeur pass�e en param�tre
//
    void changer_vitesse(int vitX, int vitY, int vitZ) {
        if (vitX < 0) {
            VX = 0;
        } else if (vitX > VITMAX) {
            VX = VITMAX;
        } else {
            VX = vitX;
        }
        
        if (vitY < 0) {
            VY = 0;
        } else if (vitY > VITMAX) {
            VY = VITMAX;
        } else {
            VY = vitY;
        }
        
        if (vitZ < 0) {
            VZ = 0;
        } else if (vitZ > VITMAX) {
            VZ = VITMAX;
        } else {
            VZ = vitZ;
        }
    }
//
// modifie le cap de l'avion avec la valeur passée en paramètre
    void changer_cap(int capParam) {
        if ((capParam >= 0) && (capParam < 360)) {
            cap = capParam;
        }
    }
//
//// modifie l'altitude de l'avion avec la valeur passée en paramètre
    void changer_altitude(int alt) {
        if (alt < 0) {
            altitude = 0;
        } else if (alt > ALTMAX) {
            altitude = ALTMAX;
        } else {
            altitude = alt;
        }
    }
//
//// affiche les caractéristiques courantes de l'avion
    void afficher_donnees() {
        System.out.println(get_donnees());
    }
//
    String get_donnees() {
        return "Avion : " + numero_vol.toString() + ", posX : " + posX + ", posY : " + posY + ", posZ : " + posZ + ", altitude : " + altitude + ", VX : " + VX 
                + ", VY : " + VY + ", VZ : " + VZ + ", cap :" + cap + ", poussee : " + poussee + ", acceleration : " + acceleration;
    }
//
//// recalcule la localisation de l'avion en fonction de sa vitesse et de son cap
    void calcul_deplacement() {
        double cosinus, sinus;
        double dep_x, dep_y;
        int nb;

        if (VX < VITMIN || VY < VITMIN || VZ < VITMIN) {
            System.out.println("Vitesse trop faible : crash de l'avion\n");
            fermer_communication();
            return;
        }
        
        if (altitude == 0) {
            System.out.println("L'avion s'est ecrase au sol\n");
            fermer_communication();
            return;
        }

        cosinus = Math.cos(cap * 2 * Math.PI / 360);
        sinus = Math.sin(cap * 2 * Math.PI / 360);

        dep_x = cosinus * ((VX + VY + VZ) / 3) * 10 / VITMIN;
        dep_y = sinus * ((VX + VY + VZ) / 3) * 10 / VITMIN;

        if ((dep_x > 0) && (dep_x < 1)) {
            dep_x = 1;
        }
        if ((dep_x < 0) && (dep_x > -1)) {
            dep_x = -1;
        }

        if ((dep_y > 0) && (dep_y < 1)) {
            dep_y = 1;
        }
        if ((dep_y < 0) && (dep_y > -1)) {
            dep_y = -1;
        }

        posX = posX + (int) dep_x;
        posY = posY + (int) dep_y;

        //afficher_donnees();
    }
//
    void se_deplacer() {
        while (true) {

            try {
                ouvrir_communication();
                Thread.sleep(PAUSE);
                calcul_deplacement();
                envoyer_caracteristiques();
                fermer_communication();
            } catch (InterruptedException ex) {
            }
        }
    }
}
