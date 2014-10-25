package org.absolutegalaber.simpleoauth.shadow.listener;

import org.absolutegalaber.simpleoauth.shadow.service.AuthService;
import org.absolutegalaber.simpleoauth.shadow.service.ClientService;
import org.absolutegalaber.simpleoauth.shadow.service.IRepositoryService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Peter Schneider-Manzell
 */
public abstract class RepositoryServiceInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        registerRepositoryService();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void registerRepositoryService() {
        IRepositoryService IRepositoryService = getRepositoryService();
        ClientService.setClientRepository(IRepositoryService.getClientRepository());
        AuthService.setRepositoryService(IRepositoryService);
    }

    protected abstract IRepositoryService getRepositoryService();
}
