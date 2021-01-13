package Service;

import Model.Matche;
import Model.ResultMatch;
import Model.SeasonMatch;
import org.glassfish.jersey.client.ClientConfig;

import javax.json.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class FootballRestService {
    static List<Matche> matchlistSave = new ArrayList();
    static long timeSave = 0;
    static String tokenAPI = "b5e4ba82e7494192945dd5d1042fbef2";
    static Client c;
    public static final String competition = "PL";

    static {
        ClientConfig cf = new ClientConfig();
        c = ClientBuilder.newClient(cf);
    }

    public static SeasonMatch getCurrentSeason(String competitionID) {
        WebTarget target = c.target("http://api.football-data.org/v2/competitions/PL");
        Invocation.Builder builder = target.request().header("X-Auth-Token", tokenAPI);
        Response response = builder.get();
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonObject jsonObject = reader.readObject();
            JsonObject currentSeason = jsonObject.getJsonObject("currentSeason");
            int currentMatchday = currentSeason.getInt("currentMatchday");
            int id = currentSeason.getInt("id");

            return new SeasonMatch(id, currentMatchday);
        }
        return null;
    }

    public static List<Matche> getScheduleMatch(String competitionID) {
        long currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis - timeSave > 60000) {
            WebTarget target = FootballRestService.c.target("http://api.football-data.org/v2/competitions/" + competitionID);
            Invocation.Builder builder = target.request().header("X-Auth-Token", tokenAPI);
            Response response = builder.get();
            int matchday = 0;
            if (response.getStatus() == 200) {
                String respstring = response.readEntity(String.class);
                StringReader stringReader = new StringReader(respstring);
                JsonReader reader = Json.createReader(stringReader);
                JsonObject jsonObject = reader.readObject();
                JsonObject currentSeason = jsonObject.getJsonObject("currentSeason");
                matchday = currentSeason.getInt("currentMatchday");
            } else {
                System.err.println("getScheduleMatch Fail");
            }
            matchlistSave = getListOfMatch(competitionID, matchday + 1);
//            matchlistSave = getListOfMatch(competitionID, matchday - 1);
            timeSave = currentTimeMillis;
        }
        return matchlistSave;
    }

    public static List<Matche> getListOfMatch(String competitionID, int matchday) {
        WebTarget target = c.target("http://api.football-data.org/v2/competitions/" + competitionID + "/matches?matchday=" + matchday);
        Invocation.Builder builder = target.request().header("X-Auth-Token", tokenAPI);
        Response response = builder.get();
        if (response.getStatus() == 200) {
            List<Matche> matchlist = new ArrayList();
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonObject jsonObject = reader.readObject();

            JsonObject competition = jsonObject.getJsonObject("competition");
            String place = competition.getJsonObject("area").getString("name");
            String name = competition.getString("name");

            JsonArray matches = jsonObject.getJsonArray("matches");
            for (int i = 0; i < matches.size(); i++) {
                JsonObject match = matches.getJsonObject(i);
                Matche m = new Matche();
                m.setId(match.getInt("id"));
                m.setName(name);
                m.setPlace(place);
                JsonObject awayTeam = match.getJsonObject("awayTeam");
                JsonObject homeTeam = match.getJsonObject("homeTeam");
                m.setAwayTeam(awayTeam.getString("name"));
                m.setHomeTeam(homeTeam.getString("name"));
                m.setAwayTeamId(awayTeam.getInt("id"));
                m.setHomeTeamId(homeTeam.getInt("id"));
                m.setStartDate(match.getString("utcDate"));

                JsonObject score = match.getJsonObject("score");
                if(true) {
                    if (!score.get("winner").toString().equals("null")) {
                        ResultMatch rs = new ResultMatch();
                        rs.setScoreAwayTeam(score.getJsonObject("fullTime").getInt("awayTeam"));
                        rs.setScoreHomeTeam(score.getJsonObject("fullTime").getInt("homeTeam"));
                        String winner = score.getString("winner");
                        rs.setWinner(winner);
                        m.setResultmatch(rs);
                        m.setDuration(score.getString("duration"));
                    } else {
                        ResultMatch rs = new ResultMatch();
                        rs.setScoreAwayTeam(0);
                        rs.setScoreHomeTeam(0);
                        rs.setWinner("");
                        m.setResultmatch(rs);
                        m.setDuration("");
                    }
                } else {
                    ResultMatch rs = new ResultMatch();
                    rs.setScoreAwayTeam(0);
                    rs.setScoreHomeTeam(0);
                    rs.setWinner("");
                    m.setResultmatch(rs);
                    m.setDuration("");
                }
                matchlist.add(m);
            }
            return matchlist;
        }
        return null;
    }

    public static Matche getMatch(int idMatch) {
        WebTarget target = c.target("http://api.football-data.org/v2/matches/" + idMatch);
        Invocation.Builder builder = target.request().header("X-Auth-Token", tokenAPI);
        Response response = builder.get();
        if (response.getStatus() == 200) {
            String respstring = response.readEntity(String.class);
            StringReader stringReader = new StringReader(respstring);
            JsonReader reader = Json.createReader(stringReader);
            JsonObject match = reader.readObject().getJsonObject("match");
            Matche m = new Matche();
            m.setId(match.getInt("id"));
            JsonObject competition = match.getJsonObject("competition");
            String place = competition.getJsonObject("area").getString("name");
            String name = competition.getString("name");
            m.setName(name);
            m.setPlace(place);
            JsonObject awayTeam = match.getJsonObject("awayTeam");
            JsonObject homeTeam = match.getJsonObject("homeTeam");
            m.setAwayTeam(awayTeam.getString("name"));
            m.setHomeTeam(homeTeam.getString("name"));
            m.setAwayTeamId(awayTeam.getInt("id"));
            m.setHomeTeamId(homeTeam.getInt("id"));
            m.setStartDate(match.getString("utcDate"));

            JsonObject score = match.getJsonObject("score");
            if (!score.get("winner").toString().equals("null")) {
                ResultMatch rs = new ResultMatch();
                rs.setScoreAwayTeam(score.getJsonObject("fullTime").getInt("awayTeam"));
                rs.setScoreHomeTeam(score.getJsonObject("fullTime").getInt("homeTeam"));
                String winner = score.getString("winner");
                rs.setWinner(winner);
                m.setResultmatch(rs);
                m.setDuration(score.getString("duration"));
            } else {
                ResultMatch rs = new ResultMatch();
                rs.setScoreAwayTeam(0);
                rs.setScoreHomeTeam(0);
                rs.setWinner("");
                m.setResultmatch(rs);
                m.setDuration("");
            }

            return m;
        }
        return null;
    }


    public static JsonArray getJSONScheduleMatch(String competitionID) {
        List<Matche> scheduleMatches = getScheduleMatch(competitionID);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(Matche m : scheduleMatches){
            arrayBuilder.add(m.toJsonObject());
        }
        JsonArray jsonArray = arrayBuilder.build();
        return jsonArray;
    }


    public static void main(String[] args) {
        getJSONScheduleMatch("PL");
//        WebTarget target = c.target("http://api.football-data.org/v2/competitions/PL/matches?matchday=12");
//        WebTarget target = c.target("http://api.football-data.org/v2/matches/303864");
//        Invocation.Builder builder = target.request().header("X-Auth-Token", tokenAPI);
//        Response response = builder.get();
//        System.out.println(response.getStatus());
//        if (response.getStatus() == 200) {
//            String respstring = response.readEntity(String.class);
//            StringReader stringReader = new StringReader(respstring);
//            JsonReader reader = Json.createReader(stringReader);
//            JsonObject jsonObject = reader.readObject();
//            System.out.println(jsonObject.toString());
//        }

//        FootballRestService service = new FootballRestService();
//        System.out.println(service.getCurrentSeason("PL").getId());
//        System.out.println(service.getCurrentSeason("PL").getCurrentMatchDay());
//        List<Match> pl = service.getListOfMatch("PL", 15);
//        for (Match s : pl) {
//            String scrore1 = ".";
//            String scrore2 = ".";
//            if (s.getResultmatch() != null) {
//                scrore1 = s.getResultmatch().getScoreAwayTeam() + "";
//                scrore2 = s.getResultmatch().getScoreHomeTeam() + "";
//            }
//            System.out.println(s.getAwayTeam() + " " + scrore1 + " - " + scrore2 + " " + s.getHomeTeam());
//            System.out.println(s.getDuration());
//        }
    }
}
