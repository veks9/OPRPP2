package hr.fer.zemris.java.webapp;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

/**
 * Klasa predstavlja listener koji na inicijalizaciji stvara tablice poll i pollOptions
 * ako one ne postoje i puni ih s podacima. Također stvara bazen veza. Na gašenju aplikacije
 * uništava bazen veza.
 * @author vedran
 *
 */
@WebListener
public class Inicijalizacija implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ResourceBundle rb;
		try (FileInputStream fis = new FileInputStream(
				sce.getServletContext().getRealPath("WEB-INF/dbsettings.properties"))) {
			rb = new PropertyResourceBundle(fis);
		} catch (IOException e) {
			throw new RuntimeException("Error while trying to read config file!");
		}

		if (!rb.containsKey("host") || !rb.containsKey("port") || !rb.containsKey("name") || !rb.containsKey("user")
				|| !rb.containsKey("password")) {
			throw new RuntimeException(
					"Something is missing in config file. It has to contain host, port, " + "name, user and password.");
		}

		String connectionURL = "jdbc:derby://" + rb.getString("host") + ":" + rb.getString("port") + "/"
				+ rb.getString("name");

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("org.apache.derby.client.ClientAutoloadedDriver");
		} catch (PropertyVetoException e1) {
			throw new RuntimeException("Error while trying to initialize pool.", e1);
		}

		cpds.setJdbcUrl(connectionURL);
		cpds.setUser(rb.getString("user"));
		cpds.setPassword(rb.getString("password"));
		cpds.setInitialPoolSize(5);
		cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);

		Connection con = null;
		try {
			con = cpds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("The connection could not be established.");
		}

		// jel postoji baza
		try {
			createTables(con);
			populateTables(sce.getServletContext(), con);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("The connection could not be established.");
		}

		sce.getServletContext().setAttribute("hr.fer.zemris.dbpool", cpds);
	}

	/**
	 * Pomoćna metoda koja puni tablicu Polls ako je prazna
	 * @param sc kontekst
	 * @param con veza
	 */
	private void populateTables(ServletContext sc, Connection con) {
		if (!tableEmpty("Polls", con))
			return;

		String fileName = sc.getRealPath("/WEB-INF/initPoll.txt");

		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName));
			for (String line : lines) {
				String[] lineSplitted = line.split("\t");
				try (PreparedStatement pst = con.prepareStatement("insert into Polls (title, message) values (?,?)",
						Statement.RETURN_GENERATED_KEYS)) {
					pst.setString(1, lineSplitted[0].trim());
					pst.setString(2, lineSplitted[1].trim());
					pst.executeUpdate();
					System.out.println("1 row inserted in POLLS");
					long pollReference = Long.parseLong(lineSplitted[2].trim());
					try (ResultSet rs = pst.getGeneratedKeys()) {
						if (rs != null) {
							rs.next();
							long id = rs.getLong(1);
							populatePollOptionsTable(sc, id, pollReference, con);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pomoćna metoda koja puni tablicu PollOptions
	 * @param sc kontekst
	 * @param id id ankete
	 * @param pollReference interni id ankete
	 * @param con veza
	 */
	private void populatePollOptionsTable(ServletContext sc, long id, long pollReference, Connection con) {
		String fileName = sc.getRealPath("/WEB-INF/initPollOptions.txt");

		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName));
			for (String line : lines) {
				String[] lineSplitted = line.split("\t");
				if (line.trim().length() == 0)
					continue;

				long parsed = Long.parseLong(lineSplitted[2].trim());
				if (parsed != pollReference)
					continue;

				try (PreparedStatement pst = con.prepareStatement(
						"insert into PollOptions (optiontitle, optionlink, pollid, votescount) values (?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS)) {
					pst.setString(1, lineSplitted[0].trim());
					pst.setString(2, lineSplitted[1].trim());
					pst.setLong(3, id);
					pst.setLong(4, Long.parseLong(lineSplitted[3].trim()));
					pst.executeUpdate();
					System.out.println("1 row inserted in POLLS OPTIONS");
				} catch(SQLException e) {
					e.printStackTrace();
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pomoćna metoda koja ispituje je li tablica prazna
	 * @param tableName naziv tablice
	 * @param con veza
	 * @return <code>true</code> ako je, inae <code>false</code>
	 */
	private boolean tableEmpty(String tableName, Connection con) {
		try (PreparedStatement ps = con.prepareStatement("select * from " + tableName)) {
			try (ResultSet rs = ps.executeQuery()) {
				return !rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Pomoćna metoda koja stvara tablice ako ne postoje
	 * @param con veza
	 * @throws SQLException
	 */
	private void createTables(Connection con) throws SQLException {
		createTableIfNotExists(con, "Polls",
				"CREATE TABLE Polls(" + "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
						+ " title VARCHAR(150) NOT NULL,  message CLOB(2048) NOT NULL)");
		createTableIfNotExists(con, "Poll options",
				"CREATE TABLE PollOptions(" + "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
						+ "optionTitle VARCHAR(100) NOT NULL," + "optionLink VARCHAR(150) NOT NULL," + "pollID BIGINT,"
						+ "votesCount BIGINT," + "FOREIGN KEY (pollID) REFERENCES Polls(id))");
	}

	/**
	 * Pomoćna metoda koja stvara tablicu ako ona ne postoji
	 * @param con veza
	 * @param tableName naziv tablice
	 * @param statement SQL upit za stvaranje tablice
	 * @throws SQLException
	 */
	private void createTableIfNotExists(Connection con, String tableName, String statement) throws SQLException {
		if (con != null) {
			ResultSet rs = con.getMetaData().getTables(null, null, tableName.toUpperCase(), null);
			if (rs.next()) {
				return;
			} else {
				try (PreparedStatement ps = con.prepareStatement(statement)) {
					ps.executeUpdate();
				} catch (SQLException ex) {
				}
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ComboPooledDataSource cpds = (ComboPooledDataSource) sce.getServletContext()
				.getAttribute("hr.fer.zemris.dbpool");
		if (cpds != null) {
			try {
				DataSources.destroy(cpds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
