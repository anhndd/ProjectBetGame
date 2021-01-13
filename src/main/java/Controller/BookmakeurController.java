package Controller;

import Model.Matche;
import Service.FootballRestService;
import org.glassfish.jersey.client.ClientConfig;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static Service.FootballRestService.competition;

@Stateless
public class BookmakeurController {
    @PersistenceContext
    private EntityManager em;

    public List<Matche> getScheduleMatche() {
        return FootballRestService.getScheduleMatch(competition);
    }

    public JsonArray getScheduleMatcheJson(String search) {
        List<Matche> matches = new ArrayList();
        if (search.isEmpty()) {
            matches = FootballRestService.getScheduleMatch(competition);
        } else {
            String searchstr = search.toLowerCase();
            List<Matche> allMatches = FootballRestService.getScheduleMatch(competition);
            for (Matche m : allMatches) {
                if (m.getHomeTeam().toLowerCase().contains(searchstr) || m.getAwayTeam().toLowerCase().contains(searchstr) || searchstr.contains(m.getId() + "")) {
                    matches.add(m);
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

    public Matche getMatche(int idmatch) {
        Matche matche = FootballRestService.getMatch(idmatch);
        return matche;
    }

    public int removeMatche(Matche matche) {
        em.remove(matche);
        em.remove(matche.getResultmatch());
        return 1;
    }

    public void notifyNewMatch(int idmatch) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/service/parieur/newmatch/"+idmatch);

        Invocation.Builder inBuilder = target.request(MediaType.TEXT_PLAIN);
        Response response = inBuilder.get();
        if (response.getStatus() == 200) {
        }
    }
}