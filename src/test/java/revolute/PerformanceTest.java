package revolute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Babatope Festus Application Concurrency and Performance Test
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerformanceTest {

    @Test
    public void aTestTransferWith10ConcurrencySystemsInParallel() throws Exception {
        int threads = 10;
        Map<String, Integer> facts = runConcurrencyTest(threads);
        Integer overlaps = facts.get("OVERLAPS");
        Integer records = facts.get("RECORDS");
        System.out.println("Must be greater than 0 " + overlaps);
        assertTrue("Must be greater than 0", overlaps > 0);
        assertEquals(records.intValue(), threads);
    }

    @Test
    public void bTestTransferWith100ConcurrencySystemsInParallel() throws Exception {
        int threads = 100;
        Map<String, Integer> facts = runConcurrencyTest(threads);
        Integer overlaps = facts.get("OVERLAPS");
        Integer records = facts.get("RECORDS");
        System.out.println("Must be greater than 0 " + overlaps);
        assertTrue("Must be greater than 0", overlaps > 0);
        assertEquals(records.intValue(), threads);
    }

    @Test
    public void cTestTransferWith1000ConcurrencySystemsInParallel() throws Exception {
        int threads = 1000;
        Map<String, Integer> facts = runConcurrencyTest(threads);
        Integer overlaps = facts.get("OVERLAPS");
        Integer records = facts.get("RECORDS");
        System.out.println("Must be greater than 0 " + overlaps);
        assertTrue("Must be greater than 0", overlaps > 0);
        assertEquals(records.intValue(), threads);
    }

    @Test
    public void dTestTransferWith10000ConcurrencySystemsInParallel() throws Exception {
        int threads = 10000;
        Map<String, Integer> facts = runConcurrencyTest(threads);
        Integer overlaps = facts.get("OVERLAPS");
        Integer records = facts.get("RECORDS");
        System.out.println("Must be greater than 0 " + overlaps);
        assertTrue("Must be greater than 0", overlaps > 0);
        assertEquals(records.intValue(), threads);
    }

    public Map<String, Integer> runConcurrencyTest(int threads) throws Exception {
        final Accounts accounts = (Accounts) Accounts.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean running = new AtomicBoolean();
        AtomicInteger overlaps = new AtomicInteger();
        ExecutorService service = Executors.newFixedThreadPool(threads);
        Collection<Future<String>> futures = new ArrayList<>(threads);
        for (int t = 0; t < threads; ++t) {
            final String name = String.format("Transfer #%d", t);
            futures.add(service.submit(() -> transfer(running, overlaps, latch, accounts, name)));
        }
        latch.countDown();
        Set<String> ids = new HashSet<>();
        for (Future<String> f : futures) {
            ids.add(f.get());
        }
        Map<String, Integer> facts = new HashMap<>();
        facts.put("OVERLAPS", overlaps.get());
        facts.put("RECORDS", ids.size());
        return facts;
    }

    private String transfer(AtomicBoolean running, AtomicInteger overlaps,
            CountDownLatch latch, Accounts accounts, String name) throws InterruptedException {
        latch.await();
        if (running.get()) {
            overlaps.incrementAndGet();
        }
        running.set(true);
        String id = accounts.add(new Account(name, BigDecimal.ZERO));
        running.set(false);
        return id;
    }
}
