package org.apereo.cas.ticket.registry;

import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.ticket.registry.dao.CassandraTicketRegistryDao;
import org.apereo.cas.ticket.registry.support.LockingStrategy;

/**
 * @author David Rodriguez
 * @since 5.1.0
 */
public class CassandraTicketRegistryCleaner extends DefaultTicketRegistryCleaner {

    private final CassandraTicketRegistryDao ticketRegistryDao;
    
    public CassandraTicketRegistryCleaner(
            final CassandraTicketRegistryDao ticketRegistryDao,
            final LockingStrategy lockingStrategy,
            final LogoutManager logoutManager,
            final TicketRegistry ticketRegistry,
            final boolean isCleanerEnabled) {
        super(lockingStrategy, logoutManager, ticketRegistry, isCleanerEnabled);
        this.ticketRegistryDao = ticketRegistryDao;
    }

    @Override
    protected void cleanInternal() {
        this.ticketRegistryDao.getExpiredTicketGrantingTickets().forEach(ticket -> {
            this.logoutManager.performLogout(ticket);
            this.ticketRegistryDao.deleteTicketGrantingTicket(ticket.getId());
        });
    }
}
