package Service;

import Controller.AdministrateurController;
import Controller.ParieurController;
import Model.Matche;
import Model.Parieur;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

// APIkey: XtIEuBoXbtAr3FAlbaVX2UqJS
// APIsecretKey: rmgxGffpka1I5l0uhoYmwSjDnmpxuvMsMEdCDW1IGpRRDAQ2lz
// Bearer token: AAAAAAAAAAAAAAAAAAAAAH6VKwEAAAAA5ARe5HkYVZ6oMyGzlBWsPHbPxek%3DDFlfjoAyUspasR733kxF1i0tMXIwVopbErYyb5P1TSDqd3q6P0
// Access token: 1341519224309772294-uv719zY4of9Qw9RABT0vMzTSXyvULy
// Access tokken secret: wIwlLQMaUBIgokAdnalHSnztgLm5gKSwhx67mjOIJd0wF

@Stateless
@Path("/service")
public class RestService {
    static Twitter twitter;
    String competition = "PL"; // Premier League

    @EJB
    ParieurController parieurController;

    @EJB
    AdministrateurController administrateurController;

    // <jvm-options>-Djavax.net.ssl.trustStorePassword=123456</jvm-options>
//       <jvm-options>-Djavax.net.ssl.keyStorePassword=123456</jvm-options>
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
    @Path("/parieur/result/{twitter}/{result}")
    public int notifyResult(@PathParam("twitter") String twitterName, @PathParam("result") int result, @QueryParam("idmatch") int idmatch, @QueryParam("money") int money, @QueryParam("moneyearn") int moneyEarn) {
        Matche detailMatch = parieurController.getDetailMatch(idmatch);
        try {
            String directMessage ="";
            if (result == 0) {
                directMessage = "Hi, your match is draw.\n" + detailMatch.getHomeTeam() + " " + detailMatch.getResultmatch().getScoreHomeTeam()
                        + " - " + detailMatch.getResultmatch().getScoreAwayTeam() + " " + detailMatch.getAwayTeam()
                        + "\nMoney earn " + moneyEarn + " Limcoin"
                        + "\nCurrent money " + money + " Limcoin";
            } else if (result == 1) {
                directMessage = "Hi, you have won.\n" + detailMatch.getHomeTeam() + " " + detailMatch.getResultmatch().getScoreHomeTeam()
                        + " - " + detailMatch.getResultmatch().getScoreAwayTeam() + " " + detailMatch.getAwayTeam()
                        + "\nMoney earn " + moneyEarn + " Limcoin"
                        + "\nCurrent money " + money + " Limcoin";
            } else if (result == 2) {
                directMessage = "Sorry, you lose.\n" + detailMatch.getHomeTeam() + " " + detailMatch.getResultmatch().getScoreHomeTeam()
                        + " - " + detailMatch.getResultmatch().getScoreAwayTeam() + " " + detailMatch.getAwayTeam()
                        + "\nMoney lost " + moneyEarn + " Limcoin"
                        + "\nCurrent money " + money + " Limcoin";
            }
            twitter.sendDirectMessage(twitterName, directMessage);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @GET
    @Path("/parieur/newmatch/{idmatch}")
    public int notifyNewMatch(@PathParam("idmatch") int idmatch) {
        Matche detailMatch = parieurController.getDetailMatch(idmatch);
        List<Parieur> listParieur = administrateurController.getListParieur();
        for (Parieur parieur : listParieur) {
            if (isExistUserTwitter(parieur.getTwitterName())) {
                String twitterName = parieur.getTwitterName();
                String directMessage = "There is new match \n" + detailMatch.getHomeTeam() + " - " + detailMatch.getAwayTeam();
                try {
                    twitter.sendDirectMessage(twitterName, directMessage);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        }
        return 1;
    }

    @GET
    @Path("/checkuser")
    public boolean isExistUserTwitter(@QueryParam("username") String username) {
        try {
            ResponseList<User> userFound = twitter.users().lookupUsers(username);
            if (userFound != null) {
                return true;
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return false;
    }
}
