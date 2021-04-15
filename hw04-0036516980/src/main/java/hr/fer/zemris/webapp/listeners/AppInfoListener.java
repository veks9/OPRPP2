package hr.fer.zemris.webapp.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

/**
 * Listener koji na inicijalizaciji postavlja kontekstnu varijablu
 * time koja predstavlja vrijeme u milisekundama kad je pokrenuta aplikacija
 * @author vedran
 *
 */
@WebListener
public class AppInfoListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		long currentTime = System.currentTimeMillis();
		sce.getServletContext().setAttribute("time", currentTime);

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
