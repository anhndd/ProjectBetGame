package Service;

import Controller.AdministrateurController;
import Controller.ParieurController;
import Model.Matche;
import Model.Pari;
import Model.Parieur;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

// APIkey: XtIEuBoXbtAr3FAlbaVX2UqJS
// APIsecretKey: rmgxGffpka1I5l0uhoYmwSjDnmpxuvMsMEdCDW1IGpRRDAQ2lz
// Bearer token: AAAAAAAAAAAAAAAAAAAAAH6VKwEAAAAA5ARe5HkYVZ6oMyGzlBWsPHbPxek%3DDFlfjoAyUspasR733kxF1i0tMXIwVopbErYyb5P1TSDqd3q6P0
// Access token: 1341519224309772294-uv719zY4of9Qw9RABT0vMzTSXyvULy
// Access tokken secret: wIwlLQMaUBIgokAdnalHSnztgLm5gKSwhx67mjOIJd0wF

@Stateless
@Path("/parieur")
public class ParieurRestService {

    @PersistenceContext
    private EntityManager em;

    @EJB
    AdministrateurController aministrateurController;

    @EJB
    ParieurController parieurController;

    static Twitter twitter;
    String competition = "PL"; // Premier League

    static {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("XtIEuBoXbtAr3FAlbaVX2UqJS")
                .setOAuthConsumerSecret("rmgxGffpka1I5l0uhoYmwSjDnmpxuvMsMEdCDW1IGpRRDAQ2lz")
                .setOAuthAccessToken("1341519224309772294-YmAtojWAgYrIxiYdMMGbuFArgQlHw0")
                .setOAuthAccessTokenSecret("DLWejXyKRBE0ba1px9z5RXNy25fH1qhKC4w25qZGwc71m");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();

    }

    @GET
    @Path("/matchs")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getMatchs(@QueryParam("search") String search) {
        List<Matche> matches = em.createQuery("select t from Matche t").getResultList();
        if (!search.isEmpty()) {
            String searchstr = search.toLowerCase();
            List<Matche> matchFilter = new ArrayList<>();
            for (Matche m : matches) {
                if (m.getHomeTeam().toLowerCase().contains(searchstr) || m.getAwayTeam().toLowerCase().contains(searchstr) || searchstr.contains(m.getId() + "")) {
                    matchFilter.add(m);
                }
            }
        }

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Matche m : matches) {
            arrayBuilder.add(m.toJsonObject());
        }
        JsonArray jsonArray = arrayBuilder.build();
        return jsonArray;
    }

    @GET
    @Path("/match/{idmatch}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getMatch(@PathParam("idmatch") int idmatch) {
        Matche matche = em.find(Matche.class, idmatch);
        return matche.toJsonObject();
    }

    @POST
    @Path("/createPari")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject createPari(@FormParam("userId") long userId, @FormParam("pari") String paristr) {
        StringReader stringReader = new StringReader(paristr);
        JsonReader reader = Json.createReader(stringReader);
        JsonObject jsonObject = reader.readObject();
        Pari pari = new Pari();
        pari.fromJsonObject(jsonObject);

        Parieur parieur = aministrateurController.getParieur(userId);
        int money = parieur.getMoney();
        int moneyleft = money - pari.getMoney();
        if (moneyleft < 0) {
            return Json.createObjectBuilder().add("value", -1).build();
        } else {
            parieur.setMoney(moneyleft);
            List<Pari> pariLst = parieur.getPariLst();
            pariLst.add(pari);
            parieur.setPariLst(pariLst);


            em.persist(pari);
            em.persist(pari.getCote());
            em.persist(parieur);
        }
        return Json.createObjectBuilder().add("value", pari.getId()).build();
    }

    @POST
    @Path("/updatePari")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject updatePari(@FormParam("userId") long userId, @FormParam("pari") String paristr) {
        StringReader stringReader = new StringReader(paristr);
        JsonReader reader = Json.createReader(stringReader);
        JsonObject jsonObject = reader.readObject();
        Pari pari = new Pari();
        pari.fromJsonObject(jsonObject);

        Parieur parieur = aministrateurController.getParieur(userId);
        Pari pariOld = em.find(Pari.class, pari.getId());
        int money = parieur.getMoney();
        int moneyleft = money - pari.getMoney() + pariOld.getMoney();
        if (moneyleft < 0) {
            return Json.createObjectBuilder().add("value", -1).build();
        } else {
            parieur.setMoney(moneyleft);
            List<Pari> pariLst = parieur.getPariLst();
            for (Pari p : pariLst) {
                if (p.getId() == pari.getId()) {
                    p.setMoney(pari.getMoney());
                    break;
                }
            }
            parieur.setPariLst(pariLst);
            em.merge(parieur);
            em.merge(pari);
            em.merge(pari.getCote());
        }
        return Json.createObjectBuilder().add("value", pari.getId()).build();
    }

    @GET
    @Path("/pari/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getPari(@PathParam("id") long pariId) {
        return em.find(Pari.class, pariId).toJsonObject();
    }

    @GET
    @Path("/parilist/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getListPari(@PathParam("id") long userId) {
        Parieur parieur = aministrateurController.getParieur(userId);
        List<Pari> pariLst = parieur.getPariLst();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Pari p : pariLst) {
            arrayBuilder.add(p.toJsonObject());
        }
        JsonArray jsonArray = arrayBuilder.build();
        return jsonArray;
    }

//    public static void main(String[] args) {
//        ParieurRestService restService = new ParieurRestService();
//        System.out.println(restService.getMatchs(""));
////        System.out.println(restService.getMatch(303864));
//    }
}
