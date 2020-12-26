package View;

import Controller.ParieurController;
import Model.Matche;
import Model.Pari;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
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
    DetailMatch detailMatch;


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
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String detailMatch(int id) {
        detailMatch.setId(id);
        return "";
    }

    public List<Matche> getMacths() {
        return data.getListMatch("");
    }
}
