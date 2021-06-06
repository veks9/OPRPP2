package hr.fer.zemris.java.tecaj_13.web.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;
import hr.fer.zemris.java.tecaj_13.model.Util;

@WebServlet("/servleti/main")
public class MainServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<BlogUser> users = DAOProvider.getDAO().getRegisteredUsers();
		req.setAttribute("registeredUsers", users);
		
		req.getRequestDispatcher("/WEB-INF/pages/Homepage.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String nickName = req.getParameter("nickName");
		String password = req.getParameter("password");
		
		BlogUser user = DAOProvider.getDAO().getUserByNickName(nickName);
		if(user == null) {
			req.setAttribute("loginError", "No user for nickname: " + nickName);
			req.getRequestDispatcher("/WEB-INF/pages/Homepage.jsp").forward(req, resp);
			return;
		}
		
		String passwordHash = Util.getHashOfPassword(password);
		if (!user.getPasswordHash().equals(passwordHash)) {
			req.setAttribute("loginError",
					"Incorrect password for user: " + nickName);
			req.getRequestDispatcher("/WEB-INF/pages/Homepage.jsp").forward(req, resp);
			return;
		}
		
		req.getSession().setAttribute("current.user.id", user.getId());
		req.getSession().setAttribute("current.user.fn", user.getFirstName());
		req.getSession().setAttribute("current.user.ln", user.getLastName());
		req.getSession().setAttribute("current.user.nickName", user.getNickName());
		resp.sendRedirect("../");

	}
}
