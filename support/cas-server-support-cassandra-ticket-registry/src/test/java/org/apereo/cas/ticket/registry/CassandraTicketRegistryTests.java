package org.apereo.cas.ticket.registry;

import org.apereo.cas.ticket.registry.serializer.JacksonStringTicketSerializer;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.apereo.cas.ticket.registry.dao.CassandraTicketRegistryDao;
import org.apereo.cas.ticket.registry.dao.DefaultCassandraTicketRegistryDao;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David Rodriguez
 *
 * @since 5.1.0
 */
public class CassandraTicketRegistryTests {

    @Rule
    public CassandraCQLUnit cassandraUnit = new CassandraCQLUnit(new ClassPathCQLDataSet("schema.cql"), 
            "cassandra.yaml", 120_000L);
    private CassandraTicketRegistryDao dao;

    @Before
    public void setUp() throws Exception {
        dao = new DefaultCassandraTicketRegistryDao<>("localhost", 
                "", "", 
                new JacksonStringTicketSerializer(), 
                String.class, 
                "cas.ticketgrantingticket",
                "cas.serviceticket", 
                "cas.ticket_cleaner", 
                "cas.ticket_cleaner_lastrun");
    }

    @Test
    public void shouldRetrieveATicket() throws Exception {
        final CassandraTicketRegistry ticketRegistry = new CassandraTicketRegistry(dao);
        final String ticketId = "TGT-1234";
        final TicketGrantingTicketImpl ticket = TicketCreatorUtils.defaultTGT(ticketId);
        ticketRegistry.addTicket(ticket);

        assertEquals(ticket, ticketRegistry.getTicket(ticketId));
    }
}
