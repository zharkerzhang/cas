package org.apereo.cas;

import org.apereo.cas.ticket.registry.CassandraTicketRegistryTests;
import org.apereo.cas.ticket.registry.dao.DefaultCassandraTicketRegistryDaoJacksonBinarySerializationTests;
import org.apereo.cas.ticket.registry.dao.DefaultCassandraTicketRegistryDaoJacksonStringSerializationTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DefaultCassandraTicketRegistryDaoJacksonBinarySerializationTests.class,
        DefaultCassandraTicketRegistryDaoJacksonStringSerializationTests.class,
        CassandraTicketRegistryTests.class})
public class AllTestsSuite {
}
