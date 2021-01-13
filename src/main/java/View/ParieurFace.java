package View;

import Controller.AdministrateurController;
import Controller.ParieurController;
import Model.Matche;
import Model.Pari;
import Model.Parieur;
import Model.UserAccount;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class ParieurFace implements Serializable {
    long id;
    String name;
    int money;
    @EJB
    ParieurController data;
    List<Matche> macths;
    @Inject
    DetailMatchFace detailMatchFace;
    @Inject
    LoginFace loginFace;
    @EJB
    AdministrateurController administrateurController;

    public int pickPariMatch(int idmatch, Pari pari) {
        return 0;
    }

    public int updatePariMatch(int idmatch, Pari pari) {
        return 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        UserAccount user = loginFace.getAuthenticationController().getUser();

        if (user != null) {
            Parieur parieurByUsername = administrateurController.getParieurByUsername(user.getUsername());
            this.money = parieurByUsername.getMoney();
        }
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String detailMatch(int id) {
        detailMatchFace.setId(id);
        return "";
    }

    public List<Matche> getMacths() {
        return data.getListMatch("");
    }
}
