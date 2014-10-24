package org.simple.auth.shadow.service;

import org.simple.auth.shadow.repository.IClientRepository;
import org.simple.auth.shadow.repository.IPersistenNetworkTokenRepository;
import org.simple.auth.shadow.repository.IShadowTokenRepository;

/**
 * @author Peter Schneider-Manzell
 */
public interface IRepositoryService {

    IShadowTokenRepository getShadowTokenRepository();

    IClientRepository getClientRepository();

    IPersistenNetworkTokenRepository getPersistenNetworkTokenRepository();


}
