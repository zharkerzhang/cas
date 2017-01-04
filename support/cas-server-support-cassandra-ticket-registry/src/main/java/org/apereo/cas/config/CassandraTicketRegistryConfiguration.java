package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.cassandra.CassandraProperties;
import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.serializer.JacksonJsonSerializer;
import org.apereo.cas.serializer.TicketSerializer;
import org.apereo.cas.ticket.registry.CassandraTicketRegistry;
import org.apereo.cas.ticket.registry.CassandraTicketRegistryCleaner;
import org.apereo.cas.ticket.registry.NoOpLockingStrategy;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.ticket.registry.TicketRegistryCleaner;
import org.apereo.cas.ticket.registry.dao.CassandraTicketRegistryDao;
import org.apereo.cas.ticket.registry.dao.DefaultCassandraTicketRegistryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    @ConditionalOnMissingBean(name = "cassandraTicketSerializer")
    @Bean
    public TicketSerializer cassandraTicketSerializer() {
        return new JacksonJsonSerializer();
    }

    @Bean
    public CassandraTicketRegistryDao cassandraTicketRegistryDao() {
        final CassandraProperties cassandra = casProperties.getTicket().getRegistry().getCassandra();
        return new DefaultCassandraTicketRegistryDao<>(cassandra.getContactPoints(),
                cassandra.getUsername(),
                cassandra.getPassword(),
                cassandraTicketSerializer(),
                String.class,
                cassandra.getTgtTable(),
                cassandra.getStTable(),
                cassandra.getExpiryTable(),
                cassandra.getLastRunTable());
    }

    @Bean(name = {"cassandraTicketRegistry", "ticketRegistry"})
    public TicketRegistry cassandraTicketRegistry() {
        return new CassandraTicketRegistry(cassandraTicketRegistryDao());
    }

    @Bean
    public TicketRegistryCleaner ticketRegistryCleaner(@Qualifier("logoutManager") final LogoutManager logoutManager) {
        final boolean isCleanerEnabled = casProperties.getTicket().getRegistry().getCleaner().isEnabled();
        return new CassandraTicketRegistryCleaner(cassandraTicketRegistryDao(),
                new NoOpLockingStrategy(),
                logoutManager,
                cassandraTicketRegistry(),
                isCleanerEnabled);
    }
}
