package hr.fer.zemris.java.webapp.dao;

import java.util.List;

import hr.fer.zemris.java.webapp.model.Poll;
import hr.fer.zemris.java.webapp.model.PollOption;

/**
 * Sučelje prema podsustavu za perzistenciju podataka.
 * @author vedran
 *
 */
public interface DAO {

	/**
	 * Dohvaća sve ankete iz baze podataka
	 * @return sve ankete iz baze podataka
	 */
	List<Poll> getPolls();

	/**
	 * Dohvaća opcije za anketu sa predanim id-jem
	 * @param pollID anketa za koju se traže opcije
	 * @return opcije za anketu sa predanim id-jem
	 */
	List<PollOption> getPollOptionsForPollID(long pollID);
	
	/**
	 * U anketi sa id-jem pollID i opciji pollOptionID se uvećava
	 * broj glasova za jedan
	 * @param pollID id ankete
	 * @param pollOptionID id opcije u anketi
	 */
	void incrementVote(long pollID, long pollOptionID);

	/**
	 * Dohvaća naziv ankete preko id-a
	 * @param pollID id ankete
	 * @return naziv ankete
	 */
	String getPollTitleForPollID(long pollID);
	
	/**
	 * Dohvaća poruku ankete preko id-a
	 * @param pollID id ankete
	 * @return poruka ankete
	 */
	String getPollMessageForPollID(long pollID);
}