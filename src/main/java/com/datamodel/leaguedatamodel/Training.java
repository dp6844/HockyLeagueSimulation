package com.datamodel.leaguedatamodel;

import com.datamodel.gameplayconfig.IGameplayConfig;
import com.datamodel.gameplayconfig.IInjuryConfig;
import com.inputoutputmodel.ITrainingUI;
import com.inputoutputmodel.TrainingUI;

import java.sql.Date;
import java.util.ArrayList;

public class Training implements ITraining {

	private ITrainingUI trainingUI;

	public Training(){
		trainingUI = new TrainingUI();
	}

	@Override
	public float getRandomStatIncreaseProbability() {
		return ((float) Math.random());
	}

	@Override
	public void trainPlayers(IGame game) {
		ILeague currentLeague = game.getLeagues().get(0);
		Date currentDate = currentLeague.getCurrentDate();
		trainingUI.displayHeader("Stat increase check initiated for all the players on " + currentDate);
		IGameplayConfig gameplayConfig = currentLeague.getGamePlayConfig();
		ArrayList<IConference> conferencesInLeague = currentLeague.getConferences();
		for (IConference conference : conferencesInLeague) {
			ArrayList<IDivision> divisionsInConference = conference.getDivisions();
			for (IDivision division : divisionsInConference) {
				ArrayList<ITeam> teamsInDivision = division.getTeams();
				for (ITeam team : teamsInDivision) {
					IHeadCoach headCoach = team.getHeadCoach();
					ArrayList<IPlayer> playersInTeam = team.getPlayers();
					for (IPlayer player : playersInTeam) {
						increaseStatOrInjurePlayer(player, headCoach, gameplayConfig, currentDate, team);
					}
				}
			}
		}
		trainingUI.displayHeader("Stat increase check completed for all the players");
	}

	private void increaseStatOrInjurePlayer(IPlayer player, IHeadCoach coach, IGameplayConfig gameplayConfig,
											Date currentDate, ITeam team) {
		IInjuryConfig playerInjury = gameplayConfig.getInjury();
		float randomInjuryChance = playerInjury.getRandomInjuryChance();
		Date recoveryDate = playerInjury.getRecoveryDate(currentDate);
		int maxPlayerStatValue = player.getMaxPlayerStatValue();
		updatePlayerSkating(player, coach, maxPlayerStatValue, randomInjuryChance, recoveryDate, currentDate, team);
		updatePlayerShooting(player, coach, maxPlayerStatValue, randomInjuryChance, recoveryDate, currentDate, team);
		updatePlayerChecking(player, coach, maxPlayerStatValue, randomInjuryChance, recoveryDate, currentDate, team);
		updatePlayerSaving(player, coach, maxPlayerStatValue, randomInjuryChance, recoveryDate, currentDate, team);
		team.setActiveRoster();
	}

	private void updatePlayerSkating(IPlayer player, IHeadCoach coach, int maxPlayerStatValue, float injuryChance,
									 Date recoveryDate, Date currentDate, ITeam team) {
		float randomValue = getRandomStatIncreaseProbability();
		float coachSkating = coach.getHeadCoachSkating();
		if (randomValue <= coachSkating) {
			int newSkatingValue = getNewPlayerStatValue(player.getPlayerSkating(), maxPlayerStatValue);
			player.setPlayerSkating(newSkatingValue);
			trainingUI.displayStatUpdates(player.getPlayerName(), "Skating", newSkatingValue);
		} else {
			player.checkPlayerInjury(injuryChance, recoveryDate, currentDate, team);
		}
	}

	private void updatePlayerShooting(IPlayer player, IHeadCoach coach, int maxPlayerStatValue, float injuryChance,
									  Date recoveryDate, Date currentDate, ITeam team) {
		float randomValue = getRandomStatIncreaseProbability();
		float coachShooting = coach.getHeadCoachShooting();
		if (randomValue < coachShooting) {
			int newShootingValue = getNewPlayerStatValue(player.getPlayerShooting(), maxPlayerStatValue);
			player.setPlayerShooting(newShootingValue);
			trainingUI.displayStatUpdates(player.getPlayerName(), "Shooting", newShootingValue);
		} else {
			player.checkPlayerInjury(injuryChance, recoveryDate, currentDate, team);
		}
	}

	private void updatePlayerChecking(IPlayer player, IHeadCoach coach, int maxPlayerStatValue, float injuryChance,
									  Date recoveryDate, Date currentDate, ITeam team) {
		float randomValue = getRandomStatIncreaseProbability();
		float coachChecking = coach.getHeadCoachChecking();
		if (randomValue < coachChecking) {
			int newCheckingValue = getNewPlayerStatValue(player.getPlayerChecking(), maxPlayerStatValue);
			player.setPlayerChecking(newCheckingValue);
			trainingUI.displayStatUpdates(player.getPlayerName(), "Checking", newCheckingValue);
		} else {
			player.checkPlayerInjury(injuryChance, recoveryDate, currentDate, team);
		}
	}

	private void updatePlayerSaving(IPlayer player, IHeadCoach coach, int maxPlayerStatValue, float injuryChance,
									Date recoveryDate, Date currentDate, ITeam team) {
		float randomValue = getRandomStatIncreaseProbability();
		float coachSaving = coach.getHeadCoachSaving();
		if (randomValue < coachSaving) {
			int newSavingValue = getNewPlayerStatValue(player.getPlayerSaving(), maxPlayerStatValue);
			player.setPlayerSaving(newSavingValue);
			trainingUI.displayStatUpdates(player.getPlayerName(), "Saving", newSavingValue);
		} else {
			player.checkPlayerInjury(injuryChance, recoveryDate, currentDate, team);
		}
	}

	private int getNewPlayerStatValue(int statValue, int maxValue) {
		return Math.min((statValue + 1), maxValue);
	}
}