package View;

import Controller.AdministrateurController;
import Controller.BookmakeurController;
import Model.Bookmakeur;
import Model.Cote;
import Model.Matche;
import Model.UserAccount;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class BookmakeurFace implements Serializable {
    long id;
    Matche matcheHost;
    Cote cote;

    String username;
    String password;

    @EJB
    BookmakeurController controller;

    @EJB
    AdministrateurController administrateurController;

    List<Matche> macths;

    public Matche getMatchHost() {
        return matcheHost;
    }

    public void setMatchHost(Matche matcheHost) {
        this.matcheHost = matcheHost;
    }

    public Cote getCote() {
        return cote;
    }

    public void setCote(Cote cote) {
        this.cote = cote;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Matche> getMacths() {
        return controller.getScheduleMatche();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long createBookmaker(Matche matche){
        Bookmakeur bookmakeur = new Bookmakeur();
        bookmakeur.setMatcheHost(matche);
        UserAccount bookmaker = new UserAccount();
        bookmaker.setUsername(username);
        bookmaker.setPassword(password);
        bookmaker.setRole(1);
        bookmakeur.setUserAccount(bookmaker);
        Cote cote = new Cote();
        cote.setExactScore(50);
        bookmakeur.setCote(cote);
        long rs = administrateurController.createBookmakeur(bookmakeur);
        controller.notifyNewMatch(matche.getId());
        return rs;
    }
}
