package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.riunioni.utils.ConnectionHandler;

@WebServlet("/home.html")
public class HomeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection conn;   
    private TemplateEngine templateEngine;
    
    public HomeController() {
        super();
    }

    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		this.conn = ConnectionHandler.getConnection(servletContext);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String test = "ciao";
		
		// you must access this page through a login, otherwise an error will be displayed
		if(request.getSession().getAttribute("user") == null) {
			request.getSession().setAttribute("noLogin", true);
			ctx.setVariable("errorNoLogin", "YOU MUST LOGIN BEFORE ACCESSING THIS PAGE");
			this.templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("noLogin", false);
			ctx.setVariable("foo", test);
			this.templateEngine.process(path, ctx, response.getWriter());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
