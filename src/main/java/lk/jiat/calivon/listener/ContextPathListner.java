package lk.jiat.calivon.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lk.jiat.calivon.provider.MailServiceProvider;

@WebListener
public class ContextPathListner implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        MailServiceProvider.getInstance().start();
    }

    public void contextDestroyed(ServletContextEvent event) {
        MailServiceProvider.getInstance().shutdown();
    }
}
