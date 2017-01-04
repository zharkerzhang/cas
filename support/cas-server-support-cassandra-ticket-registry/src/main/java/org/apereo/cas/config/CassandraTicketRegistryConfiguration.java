package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.cassandra.CassandraProperties;
import org.apereo.cas.dao.DefaultCassandraTicketRegistryDao;
import org.apereo.cas.ticket.registry.CassandraTicketRegistryCleaner;
import org.apereo.cas.dao.NoSqlTicketRegistry;
import org.apereo.cas.ticket.registry.CassandraTicketRegistryDao;
import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.serializer.JacksonJsonSerializer;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.ticket.registry.TicketRegistryCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author David Rodriguez
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration("cassandraTicketRegistryConfiguration")
@EnableScheduling
@EnableConfigurationProperties({CasConfigurationProperties.class})
public class CassandraTicketRegistryConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    public CassandraTicketRegistryDao cassandraDao() {
        final CassandraProperties cassandra = casProperties.getTicket().getRegistry().getCassandra();
        return new DefaultCassandraTicketRegistryDao<>(cassandra.getContactPoints(),
                cassandra.getUsername(),
                cassandra.getPassword(),
                new JacksonJsonSerializer(),
                String.class,
                cassandra.getTgtTable(),
                cassandra.getStTable(),
                cassandra.getExpiryTable(),
                cassandra.getLastRunTable());
    }

    @Bean(name = {"noSqlTicketRegistry", "ticketRegistry"})
    public TicketRegistry noSqlTicketRegistry(final CassandraTicketRegistryDao cassandraDao) {
        return new NoSqlTicketRegistry(cassandraDao);
    }

    @Bean
    public TicketRegistryCleaner ticketRegistryCleaner(@Qualifier("ticketRegistry") final CassandraTicketRegistryDao cassandraDao,
                                                       @Qualifier("logoutManager") final LogoutManager logoutManager) {
        return new CassandraTicketRegistryCleaner(cassandraDao, logoutManager);
    }
}
