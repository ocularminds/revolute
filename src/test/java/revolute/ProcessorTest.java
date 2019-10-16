package revolute;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.ClassRule;
import spark.Spark;

public class ProcessorTest {

    final Gson gson = new Gson();
    static final String PORT = "4567";

    @ClassRule
    public static SparkServer<WebServer> server = new SparkServer<>(WebServer.class, new Integer(PORT));

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Spark.stop();
    }

    @Test
    public void serverRespondsSuccessfully() throws HttpClientException {
        GetMethod request = server.get("/", false);
        HttpResponse response = server.execute(request);
        assertEquals(200, response.code());

        request = server.get("/ping", false);
        request.addHeader("Test-Header", "test");
        response = server.execute(request);
        assertEquals(200, response.code());
        assertEquals("Pong", new String(response.body()));
        assertNotNull(server.getApplication());
    }

    @Test
    public void testTransferSuccessful() throws HttpClientException {
        String account1 = createAccount("Test Account 1", new BigDecimal("70000"));
        String account2 = createAccount("Test Account 2", new BigDecimal("50000"));
        HttpResponse response = transfer(account1, account2, new BigDecimal("23000"));
        System.out.println("response.message -> " + response.message());
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.SUCCESS_APPROVAL, fault.getError());
    }

    @Test
    public void testTransferFailedWhenSourceAccountIsUnknown() throws HttpClientException {
        String account1 = "10000000000";
        String account2 = "210000000000";
        HttpResponse response = transfer(account1, account2, BigDecimal.TEN);
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.NO_ACCOUNT_FOUND, fault.getError());
    }

    @Test
    public void testTransferFailedWhenDistinationAccountIsUnknown() throws HttpClientException {
        String account1 = "10000000000";
        String account2 = "210000000000";
        HttpResponse response = transfer(account1, account2, BigDecimal.TEN);
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.NO_ACCOUNT_FOUND, fault.getError());
    }

    @Test
    public void testTransferFailedWithInsufficientAmountWhenSourceBalanceIsLessThanTransferAmount() throws HttpClientException {
        String account1 = createAccount("Test Account 1", new BigDecimal("7000"));
        String account2 = createAccount("Test Account 2", new BigDecimal("5000"));
        HttpResponse response = transfer(account1, account2, new BigDecimal("12500"));
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.INSUFFICIENT_FUNDS, fault.getError());
    }

    @Test
    public void testTransferFailedWithInvalidAmountWhenAmountIsNegative() throws HttpClientException {
        String account1 = createAccount("Test Account 1", new BigDecimal("800"));
        String account2 = createAccount("Test Account 2", new BigDecimal("590"));
        BigDecimal amount = new BigDecimal("-12").multiply(BigDecimal.TEN);
        HttpResponse response = transfer(account1, account2, amount);
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.INVALID_AMOUNT, fault.getError());
    }

    @Test
    public void testTotalSuccessTransferForAccountNumber() throws HttpClientException {
        String account1 = createAccount("Volume Account 1", new BigDecimal("70000"));
        String account2 = createAccount("Volume Account 2", new BigDecimal("50000"));
        for (int x = 0; x < 20; x++) {
            transfer(account1, account2, new BigDecimal("350"));
        }
        GetMethod request = server.get("/accounts/" + account1 + "/events", false);
        HttpResponse response = server.execute(request);
        assertEquals(200, response.code());
        List<String> records = gson.fromJson(new String(response.body()), List.class);
        assertEquals(20, records.size());
    }

    @Test
    public void testSourceAccountBalanceReduceByAmountAfterSuccessfulTransfer() throws HttpClientException {
        String account1 = createAccount("Balanced Account 1", new BigDecimal("8000"));
        String account2 = createAccount("Balanced Account 2", new BigDecimal("5590"));
        BigDecimal amount = new BigDecimal("4753");
        BigDecimal target = new BigDecimal("3247");
        HttpResponse response = transfer(account1, account2, amount);
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.SUCCESS_APPROVAL, fault.getError());
        Account account = getAccount(account1);
        assertNotNull(account);
        assertTrue(account.getBalance().compareTo(target) == 0);

    }

    @Test
    public void testDestinationAccountBalanceIncreaseByAmountAfterSuccessfulTransfer() throws HttpClientException {
        String account1 = createAccount("Balanced Account 1", new BigDecimal("8000"));
        String account2 = createAccount("Balanced Account 2", new BigDecimal("5590"));
        BigDecimal amount = new BigDecimal("4753");
        BigDecimal target = new BigDecimal("10343");
        HttpResponse response = transfer(account1, account2, amount);
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.SUCCESS_APPROVAL, fault.getError());
        Account account = getAccount(account2);
        assertNotNull(account);
        assertTrue(account.getBalance().compareTo(target) == 0);
    }

    @Test
    public void testSourceAccountBalanceRemainsTheSameAfterFailedTransfer() throws HttpClientException {
        BigDecimal target = new BigDecimal("5590");
        String account1 = createAccount("Same Balance Account 1", target);
        String account2 = createAccount("Same Balance Account 2", new BigDecimal("5590"));
        BigDecimal amount = new BigDecimal("-4753");
        HttpResponse response = transfer(account1, account2, amount);
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.INVALID_AMOUNT, fault.getError());
        Account account = getAccount(account1);
        assertNotNull(account);
        assertTrue(account.getBalance().compareTo(target) == 0);
    }

    @Test
    public void testDestinationAccountBalanceRemainsTheSameAfterFailedTransfer() throws HttpClientException {
        BigDecimal target = new BigDecimal("5590");
        String account1 = createAccount("Same Balance Account 1", target);
        String account2 = createAccount("Same Balance Account 2", target);
        BigDecimal amount = new BigDecimal("-4753");
        HttpResponse response = transfer(account1, account2, amount);
        assertEquals(200, response.code());
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        assertEquals(Fault.INVALID_AMOUNT, fault.getError());
        Account account = getAccount(account2);
        assertNotNull(account);
        assertTrue(account.getBalance().compareTo(target) == 0);
    }

    private String createAccount(String name, BigDecimal balance) throws HttpClientException {
        Account account = new Account(name, balance);
        PostMethod request = server.post("/accounts", gson.toJson(account), true);
        System.out.println("request ->" + gson.toJson(account));
        HttpResponse response = server.execute(request);
        System.out.println("server response -> " + new String(response.body()));
        Fault fault = gson.fromJson(new String(response.body()), Fault.class);
        return (String) fault.getData();
    }

    private Account getAccount(String id) throws HttpClientException {
        GetMethod request = server.get("/accounts/" + id, false);
        HttpResponse response = server.execute(request);
        return gson.fromJson(new String(response.body()), Account.class);
    }

    private HttpResponse transfer(String account1, String account2, BigDecimal amount) throws HttpClientException {
        Transfer transfer = new Transfer(account1, account2, amount);
        PostMethod request = server.post("/transfer", gson.toJson(transfer), true);
        System.out.println("transfer request -> " + gson.toJson(transfer));
        HttpResponse response = server.execute(request);
        return response;
    }
}
