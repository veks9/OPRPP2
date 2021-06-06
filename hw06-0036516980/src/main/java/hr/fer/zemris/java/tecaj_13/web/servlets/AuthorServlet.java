package hr.fer.zemris.java.tecaj_13.web.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogEntry;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

@WebServlet("/servleti/author/*")
public class AuthorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo().substring(1);
		String[] arr = pathInfo.split("/");
		String nickName = arr[0];
		
		BlogUser author = DAOProvider.getDAO().getUserByNickName(nickName);
		
		if (author == null) {
			req.setAttribute("error_msg", "There is no author with such nickname!");
			req.getRequestDispatcher("/WEB-INF/pages/Error.jsp").forward(req, resp);
			return;
		}

		
		if (arr.length == 1) {
			req.setAttribute("author", author);
			List<BlogEntry> authorPosts = DAOProvider.getDAO().getBlogEntriesByBlogUser(author);
			req.setAttribute("authorBlogEntries", authorPosts);
			req.getRequestDispatcher("/WEB-INF/pages/Author.jsp").forward(req, resp);
		}

	}
}