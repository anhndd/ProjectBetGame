package View;

import Controller.AdministrateurController;
import Model.Bookmakeur;
import Model.Parieur;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Named
@SessionScoped
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
public class AdministrateurFace implements Serializable {
    @EJB
    AdministrateurController controller;
    List<Bookmakeur> bookmakeurs;
    List<Parieur> parieurs;
    HashMap<Long, Boolean> editable = new HashMap<>();
    boolean isEditable = false;

    public void createParieur() {

    }

    public void createBookmaker() {

    }

    public void updateParieur() {

    }

    public void updateBookmaker() {

    }

    public void removeParieur() {

    }

    public void removeBookmaker() {

    }

    public List<Bookmakeur> getBookmakeurs() {
        bookmakeurs = controller.getListBookmakeur();
        return bookmakeurs;
    }

    public List<Parieur> getParieurs() {
        if(!isEditable) {
            parieurs = controller.getListParieur();
            for (Parieur p : parieurs) {
                if (!editable.containsKey(p.getId())) {
                    editable.put(p.getId(), false);
                }
            }
        }
        return parieurs;
    }

    public boolean isEditable(Long id) {
        if (editable.containsKey(id)) return editable.get(id);
        return false;
    }

    public void editButton(Long id) {
        isEditable = true;
        editable.put(id, true);
    }

    public void cancelButton(Long id) {
        editable.put(id, false);
        isEditable = false;
    }

    public void updateName(Long id, String name) {
        isEditable = false;
        Parieur p = controller.getParieur(id);
        p.setName(name);
        controller.updateParieur(p);
        editable.put(id, false);
    }

    public void deleteParieu(Long id) {
        Parieur p = controller.getParieur(id);
        controller.deleteParieur(p);
    }
}