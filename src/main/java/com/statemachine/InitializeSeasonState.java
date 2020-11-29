package com.statemachine;
import com.datamodel.leaguedatamodel.IGame;
import com.datamodel.leaguedatamodel.IGameSchedule;
import com.datamodel.leaguedatamodel.LeagueDataModelAbstractFactory;

public class InitializeSeasonState implements IState {

	@Override
	public IState doTask() {
		StateMachineAbstractFactory stateFactory = StateMachineAbstractFactory.instance();
		IStateMachine stateMachine = stateFactory.createStateMachine(null);
		LeagueDataModelAbstractFactory factory = LeagueDataModelAbstractFactory.instance();
		IGame game = factory.createGame();
		IGameSchedule gameSchedule = factory.createGameSchedule();
		gameSchedule.scheduleRegularSeason(game, stateMachine);
		return stateFactory.createAdvanceTimeState();
	}
}