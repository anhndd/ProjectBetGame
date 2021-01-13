package View;


import Controller.AdministrateurController;
import Model.Parieur;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class HallOfFameFace implements Serializable {
    @EJB
    AdministrateurController administrateurController;

    private List<Parieur> parieurs;

    public List<Parieur> getParieurs() {
        return administrateurController.getListParieurRank();
    }
}
