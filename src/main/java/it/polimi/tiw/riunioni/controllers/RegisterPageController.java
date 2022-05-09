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

import it.polimi.tiw.riunioni.beans.RegisterErrorBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;

/**
 * This Servlet has the sole function to process the page template
 * filling in blank values
 */
@WebServlet("/register.html")
public class RegisterPageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;   
	
    public RegisterPageController() {
        super();
    }
    
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path = "register.html";
		
		ctx.setVariable("errorMsg", "");
		ctx.setVariable("error", new RegisterErrorBean());
		this.templateEngine.process(path, ctx, response.getWriter());
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException ,IOException {
		doGet(req, resp);
	}
}
