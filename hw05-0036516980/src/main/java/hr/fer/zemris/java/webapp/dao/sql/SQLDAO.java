package hr.fer.zemris.java.webapp.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.java.webapp.dao.DAO;
import hr.fer.zemris.java.webapp.dao.DAOException;
import hr.fer.zemris.java.webapp.model.Poll;
import hr.fer.zemris.java.webapp.model.PollOption;

/**
 * Ovo je implementacija podsustava DAO uporabom tehnologije SQL. Ova
 * konkretna implementacija očekuje da joj veza stoji na raspolaganju
 * preko {@link SQLConnectionProvider} razreda, što znači da bi netko
 * prije no što izvođenje dođe do ove točke to trebao tamo postaviti.
 * U web-aplikacijama tipično rješenje je konfigurirati jedan filter 
 * koji će presresti pozive servleta i prije toga ovdje ubaciti jednu
 * vezu iz connection-poola, a po zavrsetku obrade je maknuti.
 *  
 * @author vedran
 */
public class SQLDAO implements DAO {

	@Override
	public List<Poll> getPolls() throws DAOException {
		List<Poll> polls = new ArrayList<>();
		Connection con = SQLConnectionProvider.getConnection();

		try (PreparedStatement pst = con.prepareStatement("select id, title, message from Polls order by id")) {
			try (ResultSet rs = pst.executeQuery()) {
				while (rs != null && rs.next()) {
					Poll poll = new Poll();
					poll.setId(rs.getLong(1));
					poll.setTitle(rs.getString(2));
					poll.setMessage(rs.getString(3));
					polls.add(poll);
				}
			}
		} catch (Exception ex) {
			throw new DAOException("Error occured while trying to get polls.", ex);
		}
		return polls;
	}

	@Override
	public List<PollOption> getPollOptionsForPollID(long pollID) {
		List<PollOption> pollOptions = new ArrayList<>();
		Connection con = SQLConnectionProvider.getConnection();

		try (PreparedStatement pst = con
				.prepareStatement("select * from PollOptions where pollID = ? order by votescount desc")) {
			pst.setLong(1, pollID);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs != null && rs.next()) {
					PollOption pollOption = new PollOption();
					pollOption.setId(rs.getLong(1));
					pollOption.setTitle(rs.getString(2));
					pollOption.setSongUrl(rs.getString(3));
					pollOption.setPollId(rs.getLong(4));
					pollOption.setNumberOfVotes(rs.getLong(5));
					pollOptions.add(pollOption);
				}
			}
		} catch (Exception ex) {
			throw new DAOException("Error occured while trying to get polls.", ex);
		}
		return pollOptions;
	}

	@Override
	public void incrementVote(long pollID, long pollOptionID) {
		Connection con = SQLConnectionProvider.getConnection();

		try (PreparedStatement pst = con.prepareStatement(
				"update PollOptions " + "set votesCount = votesCount + 1 " + "where pollID = ? and id = ?");) {
			pst.setLong(1, pollID);
			pst.setLong(2, pollOptionID);
			pst.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public String getPollTitleForPollID(long pollID) {
		Connection con = SQLConnectionProvider.getConnection();

		try (PreparedStatement pst = con.prepareStatement("select * from Polls where id = ?")) {
			pst.setLong(1, pollID);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs != null && rs.next()) {
					String title = rs.getString(2);
					return title;
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
		
		return null;
	}

	@Override
	public String getPollMessageForPollID(long pollID) {
		Connection con = SQLConnectionProvider.getConnection();

		try (PreparedStatement pst = con.prepareStatement("select * from Polls where id = ?")) {
			pst.setLong(1, pollID);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs != null && rs.next()) {
					String message = rs.getString(3);
					return message;
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
		
		return null;
	}
}
