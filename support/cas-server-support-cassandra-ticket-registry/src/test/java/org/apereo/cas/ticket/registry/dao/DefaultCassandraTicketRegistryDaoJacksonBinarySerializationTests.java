package org.apereo.cas.ticket.registry.dao;

import org.apereo.cas.ticket.Tickets;
import org.apereo.cas.ticket.registry.serializer.JacksonBinaryTicketSerializer;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.Rule;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * @author David Rodriguez
 *
 * @since 5.1.0
 */
public class DefaultCassandraTicketRegistryDaoJacksonBinarySerializationTests {

    @Rule
    public final CassandraCQLUnit cassandraUnit = new CassandraCQLUnit(new ClassPathCQLDataSet("schema-binary.cql"), 
            "cassandra.yaml", 120_000L);

    @Test
    public void shouldWorkWithABinarySerializer() throws Exception {
        final DefaultCassandraTicketRegistryDao<ByteBuffer> dao = new DefaultCassandraTicketRegistryDao<>("localhost", 
                "", 
                "", 
                new JacksonBinaryTicketSerializer(), 
                ByteBuffer.class,
                "cas.ticketgrantingticket", "cas.serviceticket", 
                "cas.ticket_cleaner", 
                "cas.ticket_cleaner_lastrun");

        final TicketGrantingTicketImpl tgt = Tickets.createTicketGrantingTicket("id");
        dao.addTicketGrantingTicket(tgt);
        assertEquals(tgt, dao.getTicketGrantingTicket("id"));
    }
}
