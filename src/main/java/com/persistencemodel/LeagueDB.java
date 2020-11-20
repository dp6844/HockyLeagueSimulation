package com.persistencemodel;

import com.datamodel.leaguedatamodel.ILeague;
import com.datamodel.leaguedatamodel.League;
import com.inputoutputmodel.DisplayToUser;
import com.inputoutputmodel.IDisplayToUser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LeagueDB implements ILeagueDB {

    private final IDisplayToUser displayToUser = new DisplayToUser();

    @Override
    public boolean checkIfLeagueExists(String leagueName) {
        JSONArray existingLeagues = getDBLeaguesArray();
        boolean leagueExists = findLeague(existingLeagues, leagueName);
        return leagueExists;
    }

    @Override
    public ArrayList<ILeague> loadLeaguesFromTeamName(String teamName) {
        ArrayList<ILeague> leaguesMatched = new ArrayList<>();
        JSONArray existingLeagues = getDBLeaguesArray();
        JSONArray mappedLeagues = fetchLeaguesHavingTeam(existingLeagues, teamName);
        for (Object currentLeague : mappedLeagues){
            JSONObject leagueObject = (JSONObject) currentLeague;
            ILeague league = new League();
            league.setLeagueId((int) (long) leagueObject.get("leagueId"));
            league.setLeagueName((String) leagueObject.get("leagueName"));
            league.setCurrentDate(getFormattedDate((String) leagueObject.get("currentDate")));
            IConferenceDB conferenceDB = new ConferenceDB();
            JSONArray conferences = (JSONArray) leagueObject.get("conferences");
            conferenceDB.loadConferences(conferences, league);
            IGameplayConfigDB gameplayConfigDB = new GameplayConfigDB();
            JSONObject gameplayConfig = (JSONObject) leagueObject.get("gameplayConfig");
            gameplayConfigDB.loadGameplayConfig(gameplayConfig, league);
            ICoachDB coachDB = new CoachDB();
            JSONArray coaches = (JSONArray) leagueObject.get("coaches");
            coachDB.loadCoaches(coaches, league);
            IManagerDB managerDB = new ManagerDB();
            JSONArray managers = (JSONArray) leagueObject.get("managers");
            managerDB.loadManagers(managers, league);
            IFreeAgentDB freeAgentDB = new FreeAgentDB();
            JSONArray freeAgents = (JSONArray) leagueObject.get("freeAgents");
            freeAgentDB.loadFreeAgents(freeAgents, league);
            leaguesMatched.add(league);
        }
        return leaguesMatched;
    }

    private Date getFormattedDate(String dateValue) {
        Date formattedDate = null;
        if (dateValue == null) {
            return formattedDate;
        }
        try {
            java.util.Date dateNew = new SimpleDateFormat("MMM dd, yyyy").parse(dateValue);
            formattedDate = new Date(dateNew.getTime());
        } catch (java.text.ParseException e) {
            displayToUser.displayMsgToUser(e.getLocalizedMessage());
        }
        return formattedDate;
    }

    private JSONArray fetchLeaguesHavingTeam(JSONArray leagues, String teamName) {
        JSONArray leaguesMatched = new JSONArray();
        for (Object currentLeague : leagues) {
            JSONObject league = (JSONObject) currentLeague;
            JSONArray conferences = (JSONArray) league.get("conferences");
            boolean matchFound = false;
            for (Object currentConference : conferences) {
                if (matchFound) {
                    break;
                }
                JSONObject conference = (JSONObject) currentConference;
                JSONArray divisions = (JSONArray) conference.get("divisions");
                for (Object currentDivision : divisions) {
                    if (matchFound) {
                        break;
                    }
                    JSONObject division = (JSONObject) currentDivision;
                    JSONArray teams = (JSONArray) division.get("teams");
                    for (Object currentTeam : teams) {
                        if (matchFound) {
                            break;
                        }
                        JSONObject team = (JSONObject) currentTeam;
                        String currentTeamName = (String) team.get("teamName");
                        String createdBy = (String) team.get("teamCreatedBy");
                        if (isNotNull(createdBy) && createdBy.equals("user") && currentTeamName.equals(teamName)) {
                            leaguesMatched.add(currentLeague);
                            matchFound = true;
                        }
                    }
                }
            }
        }
        return leaguesMatched;
    }

    private boolean isNotNull(String text) {
        if (text == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean findLeague(JSONArray leagues, String leagueName) {
        boolean leagueFound = false;
        for (Object league : leagues) {
            JSONObject currentLeague = (JSONObject) league;
            String currentLeagueName = (String) currentLeague.get("leagueName");
            if (leagueName.equals(currentLeagueName)) {
                leagueFound = true;
                break;
            }
        }
        return leagueFound;
    }

    private JSONArray getDBLeaguesArray() {
        JSONArray existingLeagues = new JSONArray();
        FileReader fileReader = null;
        String filePath = generateFilePath();
        try {
            JSONParser jsonParser = new JSONParser();
            fileReader = new FileReader(filePath);
            Object leagueDb = jsonParser.parse(fileReader);
            existingLeagues = (JSONArray)leagueDb;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return existingLeagues;
    }

    private String generateFilePath() {
        return Constants.STORAGE_PATH;
    }
}