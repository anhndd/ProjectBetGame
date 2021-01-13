package View;

import Controller.AdministrateurController;
import Controller.ParieurController;
import Model.Pari;
import Model.Parieur;
import Model.UserAccount;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


@Named
@SessionScoped
public class HistoryBetFace implements Serializable {
    HashMap<Long, Boolean> editable = new HashMap<>();
    boolean isEditable = false;
    private Integer money;

    List<Pari> paris;

    @Inject
    LoginFace loginFace;

    @EJB
    AdministrateurController administrateurController;

    @EJB
    ParieurController parieurController;

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public boolean isEditable(Long id) {
        if (editable.containsKey(id)) return editable.get(id);
        return false;
    }

    public void editButton(Long id) {
        isEditable = true;
        editable.put(id, true);
        for(long l : editable.keySet()) {
            System.out.println("test close " + l + " - " + editable.get(l));
        }
    }

    public void cancelButton(Long id) {
        editable.put(id, false);
        isEditable = false;
    }

    public void updateMoney(Long id, Integer money) {
        UserAccount user = loginFace.getAuthenticationController().getUser();
        if (user != null) {
            isEditable = false;
            Pari p = parieurController.getPari(id);
            Parieur parieurByUsername = administrateurController.getParieurByUsername(user.getUsername());
            p.setMoney(money);
            parieurController.updatePari(parieurByUsername.getId(), p);
            editable.put(id, false);
        }
    }
    public List<Pari> getParis() {
        UserAccount user = loginFace.getAuthenticationController().getUser();
        if (user != null) {
            if(!isEditable) {
                Parieur parieurByUsername = administrateurController.getParieurByUsername(user.getUsername());
                paris =  parieurByUsername.getPariLst();
                for (Pari p : paris) {
                    if (!editable.containsKey(p.getId())) {
                        editable.put(p.getId(), false);
                    }
                }
                System.out.println("TEST" + editable);
                return paris;
            }
        }
        return paris;
    }
}
