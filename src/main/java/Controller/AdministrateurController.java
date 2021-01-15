package Controller;


import Model.*;
import Service.FootballRestService;
import org.glassfish.jersey.client.ClientConfig;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Service.FootballRestService.competition;

@Stateless
public class AdministrateurController {
    @PersistenceContext
    private EntityManager em;

    public List<Parieur> getListParieur() {
        List<Parieur> list = em.createQuery("select t from Parieur t").getResultList();
        return list;
    }

    public List<Parieur> getListParieurRank() {
        List<Parieur> list = em.createQuery("select t from Parieur t order by t.money desc").getResultList();
        return list;
    }

    public List<Bookmakeur> getListBookmakeur() {
        List<Bookmakeur> list = em.createQuery("select t from Bookmakeur t").getResultList();
        return list;
    }

    public List<Matche> getListMatche() {
        List<Matche> list = em.createQuery("select t from Matche t").getResultList();
        return list;
    }

    public List<Cote> getListCote() {
        List<Cote> list = em.createQuery("select t from Cote t").getResultList();
        return list;
    }

    public List<Pari> getListPari() {
        List<Pari> list = em.createQuery("select t from Pari t").getResultList();
        return list;
    }

    public List<UserAccount> getUserAccounts() {
        List<UserAccount> list = em.createQuery("select t from UserAccount t").getResultList();
        return list;
    }

    public long createBookmakeur(Bookmakeur bookmakeur) {
        Matche matche = em.find(Matche.class, bookmakeur.getMatcheHost().getId());
        if (matche == null) {
            em.persist(bookmakeur);
            em.persist(bookmakeur.getMatcheHost());
            em.persist(bookmakeur.getMatcheHost().getResultmatch());
            em.persist(bookmakeur.getCote());
            em.persist(bookmakeur.getUserAccount());
            return bookmakeur.getId();
        }
        return -1;
    }

    public long updateBookmakeur(Bookmakeur bookmakeur) {
        em.merge(bookmakeur);
        em.merge(bookmakeur.getMatcheHost());
        em.merge(bookmakeur.getCote());
        return bookmakeur.getId();
    }

    public void deleteBookmakeur(Bookmakeur bookmakeurFace) {
        em.remove(em.contains(bookmakeurFace) ? bookmakeurFace : em.merge(bookmakeurFace));
        em.remove(em.contains(bookmakeurFace.getMatcheHost()) ? bookmakeurFace.getMatcheHost() : em.merge(bookmakeurFace.getMatcheHost()));
        em.remove(em.contains(bookmakeurFace.getCote()) ? bookmakeurFace.getCote() : em.merge(bookmakeurFace.getCote()));
        em.remove(em.contains(bookmakeurFace.getUserAccount()) ? bookmakeurFace.getUserAccount() : em.merge(bookmakeurFace.getUserAccount()));
    }

    public long createParieur(Parieur parieur) {
        UserAccount userAccount = parieur.getUserAccount();
        UserAccount userAccountFound = em.find(UserAccount.class, userAccount.getUsername());
        if (userAccountFound == null) {
            parieur.setMoney(1000); // 1000 Limcoin
            em.persist(parieur);
            em.persist(parieur.getUserAccount());
            return parieur.getId();
        }
        return -1;
    }

    public Parieur getParieur(long id) {
        return em.find(Parieur.class, id);
    }

    public Parieur getParieurByUsername(String username) {
        Query query = em.createQuery("select t from Parieur t where t.userAccount.username = :username");
        List<Parieur> parieurs = query.setParameter("username", username).getResultList();
        if (parieurs != null && !parieurs.isEmpty()) {
            return parieurs.get(0);
        }
        return null;
    }

    public Bookmakeur getBookmakeurByMatche(int idmatch) {
        try {
            Query query = em.createQuery("select b from Bookmakeur b where b.matcheHost.id = :idmatch");
            List<Bookmakeur> bookmakeurs = query.setParameter("idmatch", idmatch).getResultList();
            if (bookmakeurs != null && !bookmakeurs.isEmpty()) {
                return bookmakeurs.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bookmakeur getBookmakeur(long id) {
        return em.find(Bookmakeur.class, id);
    }

    public void updateParieur(Parieur parieur) {
        em.merge(parieur);
    }

    public void deleteParieur(Parieur parieurFace) {
        em.remove(em.contains(parieurFace) ? parieurFace : em.merge(parieurFace));
        List<Pari> pariLst = parieurFace.getPariLst();
        for (Pari pari : pariLst) {
            deletePari(pari);
        }
        em.remove(em.contains(parieurFace.getUserAccount()) ? parieurFace.getUserAccount() : em.merge(parieurFace.getUserAccount()));
    }

    public void deletePari(Pari pari) {
        em.remove(em.contains(pari) ? pari : em.merge(pari));
        em.remove(em.contains(pari.getCote()) ? pari.getCote() : em.merge(pari.getCote()));
    }

    public String createAccount(UserAccount userAccount) {
        em.persist(userAccount);
        return userAccount.getUsername();
    }

        @Schedule(dayOfWeek = "1", second = "0", minute = "01", hour = "0", persistent = false)
//    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void scheduleCheckResult() {
        try {
            System.out.println("Start scheduling");
            SeasonMatch currentSeason = FootballRestService.getCurrentSeason(competition);
            int lastMatchday = currentSeason.getCurrentMatchDay() - 1;
            List<Matche> matchlstLastDay = FootballRestService.getListOfMatch(competition, lastMatchday);
            Map<Integer, Matche> hmapMatchs = new HashMap<>();

            boolean isChange = false;
            List<Parieur> parieurlst = getListParieur();
            for (Parieur parieur : parieurlst) {
                List<Pari> pariLst = parieur.getPariLst();
                List<Pari> newpariLst = new ArrayList<>();

                for (Pari pari : pariLst) {
                    Cote cote = pari.getCote();
                    int moneybet = pari.getMoney();
                    int teamId = pari.getTeamId();
                    Matche matcheBet = pari.getMatche();
                    int idmatch = matcheBet.getId();

                    if (!hmapMatchs.containsKey(idmatch)) {
                        for (Matche m : matchlstLastDay) {
                            if (m.getId() == idmatch) {
                                hmapMatchs.put(idmatch, m);
                                break;
                            }
                        }
                        if (!hmapMatchs.containsKey(idmatch)) {
                            Matche oldMatchFound = FootballRestService.getMatch(idmatch);
                            hmapMatchs.put(idmatch, oldMatchFound);
                            System.out.println("Look for match again on REST");
                        }
                    }
                    if (hmapMatchs.containsKey(idmatch)) {
                        Matche matche = hmapMatchs.get(idmatch);
                        ResultMatch resultmatch = matche.getResultmatch();

                        if (resultmatch.getWinner().isEmpty()) {
                            newpariLst.add(pari);
                        } else {
                            if (resultmatch.getWinner().equals("DRAW")) {
                                parieur.setMoney(parieur.getMoney() + moneybet);
                                notifyResult(parieur.getTwitterName(), 0, idmatch, parieur.getMoney(), 0);
                            } else if (resultmatch.getWinner().equals("HOME_TEAM")) {
                                if (teamId == matche.getHomeTeamId()) {
                                    int moneyEarn = (int) (moneybet * ((float) cote.getExactScore() / 100));
                                    parieur.setMoney(parieur.getMoney() + moneybet + moneyEarn);
                                    notifyResult(parieur.getTwitterName(), 1, idmatch, parieur.getMoney(), moneyEarn);
                                } else {
                                    notifyResult(parieur.getTwitterName(), 2, idmatch, parieur.getMoney(), moneybet);
                                }
                            } else if (resultmatch.getWinner().equals("AWAY_TEAM")) {
                                if (teamId == matche.getAwayTeamId()) {
                                    int moneyEarn = (int) (moneybet * ((100 - (float) cote.getExactScore()) / 100));
                                    parieur.setMoney(parieur.getMoney() + moneybet + moneyEarn);
                                    notifyResult(parieur.getTwitterName(), 1, idmatch, parieur.getMoney(), moneyEarn);
                                } else {
                                    notifyResult(parieur.getTwitterName(), 2, idmatch, parieur.getMoney(), moneybet);
                                }
                            }
                            isChange = true;
                            Bookmakeur bookmakeurByMatche = getBookmakeurByMatche(pari.getMatche().getId());

                            deletePari(pari);
                            if (bookmakeurByMatche != null) {
                                deleteBookmakeur(bookmakeurByMatche);
                            }
                        }
                    } else {
                        System.err.println("Not found id match");
                    }
                }
                if (isChange) {
                    parieur.setPariLst(newpariLst);
                    updateParieur(parieur);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void notifyResult(String twitterName, int result, int idmatch, int money, int moenyearn) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/service/parieur/result/" + twitterName + "/" + result + "?idmatch=" + idmatch + "&money=" + money + "&moneyearn=" + moenyearn);

        Invocation.Builder inBuilder = target.request(MediaType.TEXT_PLAIN);
        Response response = inBuilder.get();
        if (response.getStatus() == 200) {
        }
    }
}
