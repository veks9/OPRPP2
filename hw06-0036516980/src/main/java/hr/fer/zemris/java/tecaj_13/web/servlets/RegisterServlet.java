package hr.fer.zemris.java.tecaj_13.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.java.tecaj_13.dao.jpa.JPAEMProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;
import hr.fer.zemris.java.tecaj_13.model.BlogUserRegisterForm;

@WebServlet("/servleti/register")
public class RegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/WEB-INF/pages/Register.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		BlogUserRegisterForm form = new BlogUserRegisterForm();
		form.fillFromHttpRequest(req);
		form.validate();
		
		if(form.hasErrors()) {
			req.setAttribute("form", form);
			req.getRequestDispatcher("/WEB-INF/pages/Register.jsp").forward(req, resp);
			return;
		}
		
		BlogUser user = new BlogUser();
		form.toBlogUser(user);
		
		JPAEMProvider.getEntityManager().persist(user);
		
		req.getSession().setAttribute("current.user.id", user.getId());
		req.getSession().setAttribute("current.user.fn", user.getFirstName());
		req.getSession().setAttribute("current.user.ln", user.getLastName());
		req.getSession().setAttribute("current.user.nick", user.getNickName());
		
		resp.sendRedirect("servleti/main");
	}
}
