package projet.saca;

import java.io.Serializable;

public class ObjetInter implements Serializable {
    private String type = null;
    private String action = null;
    private String message = null;
    public ObjetInter(String type, String action, String message){
        this.type = type;
        this.action = action;
        this.message = message;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    
//    private String type;
//    private String action;
//    private Object obj;
//    public ObjetInter(String type, String action, Object obj){
//        this.type = type;
//        this.action = action;
//        this.obj = obj;
//    }

    /**
     * @return the type
     */
//    public String getType() {
//        return type;
//    }
//
//    /**
//     * @param type the type to set
//     */
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    /**
//     * @return the action
//     */
//    public String getAction() {
//        return action;
//    }
//
//    /**
//     * @param action the action to set
//     */
//    public void setAction(String action) {
//        this.action = action;
//    }
//
//    /**
//     * @return the obj
//     */
//    public Object getObj() {
//        return obj;
//    }
//
//    /**
//     * @param obj the obj to set
//     */
//    public void setObj(Object obj) {
//        this.obj = obj;
//    }
}
