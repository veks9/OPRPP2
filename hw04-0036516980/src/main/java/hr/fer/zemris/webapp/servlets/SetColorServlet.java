package hr.fer.zemris.webapp.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/setcolor")
public class SetColorServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	public SetColorServlet() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String color = req.getParameter("color");
		if(color == null)
			color = "FFFFFF";
		req.getSession().setAttribute("pickedBgCol", color);
		req.getRequestDispatcher("/colors.jsp").forward(req, resp);
	}
}
