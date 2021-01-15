package Controller;

import Model.Matche;
import Model.Pari;
import org.glassfish.jersey.client.ClientConfig;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ParieurController {

    @PersistenceContext
    private EntityManager em;

    public List<Matche> getListMatch(String search) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/parieur/matchs?search=" + search);

        Invocation.Builder inBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = inBuilder.get();
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonArray jsonArray = reader.readArray();

            List<Matche> matches = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                Matche m = new Matche();
                m.fromJsonObject(jsonObject);
                matches.add(m);
            }
            return matches;
        }
        return null;
    }

    public JsonArray getListMatchJson(String search) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/parieur/matchs?search=" + search);

        Invocation.Builder inBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = inBuilder.get();
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonArray jsonArray = reader.readArray();
            return jsonArray;
        }
        return null;
    }

    public Matche getDetailMatch(Integer id) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/parieur/match/" + id);

        Invocation.Builder inBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = inBuilder.get();
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonObject jsonObject = reader.readObject();
            Matche m = new Matche();
            m.fromJsonObject(jsonObject);
            System.out.println("Test " + jsonObject);
            return m;
        }
        return null;
    }

    public long createPari(long userId, Pari pari) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/parieur/createPari");

        Invocation.Builder inBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = inBuilder.post(Entity.form(new Form().param("userId",userId+"").param("pari", pari.toJsonObject().toString())));
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonObject jsonObject = reader.readObject();
            return jsonObject.getInt("value");
        }
        return -1;
    }

    public long updatePari(long userId, Pari pari) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/parieur/updatePari");

        Invocation.Builder inBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = inBuilder.post(Entity.form(new Form().param("userId",userId+"").param("pari", pari.toJsonObject().toString())));
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonObject jsonObject = reader.readObject();
            return jsonObject.getInt("value");
        }
        return -1;
    }

    public Pari getPari(long pariId) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/parieur/pari/"+pariId);

        Invocation.Builder inBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = inBuilder.get();
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonObject jsonObject = reader.readObject();
            Pari pari = new Pari();
            return pari.fromJsonObject(jsonObject);
        }
        return null;
    }

    public List<Pari> getListPari(long userId) {
        ClientConfig cf = new ClientConfig();
        Client c = ClientBuilder.newClient(cf);
        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/parieur/parilist/"+userId);

        Invocation.Builder inBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = inBuilder.get();
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonArray jsonArray = reader.readArray();

            List<Pari> pariList = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                Pari p = new Pari();
                p.fromJsonObject(jsonObject);
                pariList.add(p);
            }
            return pariList;
        }
        return null;
    }

//    public static void main(String[] args) {
//        ClientConfig cf = new ClientConfig();
//        Client c = ClientBuilder.newClient(cf);
//        WebTarget target = c.target("http://localhost:8080/ProjectBetGame-1.0-SNAPSHOT/rest/service/parieur/matchs?search=");
//
//        Invocation.Builder inBuilder = target.request(MediaType.APPLICATION_JSON);
//        Response response = inBuilder.get();
//        if (response.getStatus() == 200) {
//            String respstring = response.readEntity(String.class);
//            StringReader stringReader = new StringReader(respstring);
//            JsonReader reader = Json.createReader(stringReader);
//            JsonArray jsonArray = reader.readArray();
//
//        }
//    }
}
