package hr.fer.zemris.java.tecaj_13.dao.jpa;

import java.util.List;

import javax.persistence.NoResultException;

import hr.fer.zemris.java.tecaj_13.dao.DAO;
import hr.fer.zemris.java.tecaj_13.dao.DAOException;
import hr.fer.zemris.java.tecaj_13.model.BlogEntry;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

public class JPADAOImpl implements DAO {

	@Override
	public BlogEntry getBlogEntry(Long id) throws DAOException {
		return JPAEMProvider.getEntityManager().find(BlogEntry.class, id);
	}

	@Override
	public BlogUser getUserByNickName(String nickName) {
		return JPAEMProvider.getEntityManager()
					.createQuery("SELECT bu FROM BlogUser as bu where bu.nickName=:n", BlogUser.class)
					.setParameter("n", nickName).getSingleResult();
	}

	@Override
	public List<BlogUser> getRegisteredUsers() {
		return JPAEMProvider.getEntityManager()
					.createQuery("SELECT bu FROM BlogUser as bu", BlogUser.class).getResultList();
	}

	
	@Override
	public List<BlogEntry> getBlogEntriesByBlogUser(BlogUser user) {
		return JPAEMProvider.getEntityManager()
				.createQuery("select b from BlogEntry as b where b.userCreator=:userCreator", BlogEntry.class).setParameter("userCreator", user).getResultList();
	}

}