package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.AvioSessionBean;
import beans.AvioSessionBeanRemote;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AvioSessionBeanRemote bean = (AvioSessionBeanRemote) request.getSession().getAttribute("bean");
		if (bean != null) {
			request.getSession().removeAttribute("bean");
			bean.logout();
		}
		request.getSession().setAttribute("message", "logged out.");

		request.getRequestDispatcher("index.jsp").forward(request, response);

		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AvioSessionBeanRemote bean = doLookup();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		boolean succ = bean.login(username, password);
		if (succ) {
			request.getSession().setAttribute("bean", bean);
			request.getRequestDispatcher("home.jsp").forward(request, response);
		}else {
			request.getRequestDispatcher("error.jsp").forward(request, response);
		}
		
	}
	
	private Context initialContext;

	private final String PKG_INTERFACES = "org.jboss.ejb.client.naming";

	public Context getInitialContext() throws NamingException {
		if (initialContext == null) {
			Properties properties = new Properties();
			properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);
			initialContext = new InitialContext(properties);
		}
		return initialContext;
	}

	private String getLookupName() {
		
		// The app name is the application name of the deployed EJBs. This is typically the ear name without the .ear suffix. 
        final String appName = "DusanMandic2AvioEAR";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the EJB deployment, without the .jar suffix.
        final String moduleName = "DusanMandic2AvioEJB";
        // JBossAS allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = AvioSessionBean.class.getSimpleName();
        // the remote interface fully qualified class name
        final String interfaceName = AvioSessionBeanRemote.class.getName();
        // let's do the lookup
		String name = "ejb:" + appName + "/" + moduleName + "/" +
				distinctName    + "/" + beanName + "!" + interfaceName + "?stateful";
		return name;
	}

	private AvioSessionBeanRemote doLookup() {
		Context context = null;
		AvioSessionBeanRemote bean = null;
		try {
			context = getInitialContext();
			String lookupName = getLookupName();
			System.out.println("JNDI ime:   "+lookupName);
			bean = (AvioSessionBeanRemote) context.lookup(lookupName);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return bean;
	}

	
	

}
