package hr.fer.zemris.webapp.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

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
