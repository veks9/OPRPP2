package hr.fer.zemris.java.webapp.dao;

import hr.fer.zemris.java.webapp.dao.sql.SQLDAO;

/**
 * Singleton razred koji zna koga treba vratiti kao pružatelja
 * usluge pristupa podsustavu za perzistenciju podataka.
 * @author vedran
 *
 */
public class DAOProvider {

	private static DAO dao = new SQLDAO();
	
	/**
	 * Dohvat primjerka.
	 * 
	 * @return objekt koji enkapsulira pristup sloju za perzistenciju podataka.
	 */
	public static DAO getDao() {
		return dao;
	}
	
}