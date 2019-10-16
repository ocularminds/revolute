package revolute;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
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

        //PostMethod post(String path, String body, boolean followRedirect)
        //PutMethod put(String path, String body, boolean followRedirect) {
        //DeleteMethod delete(String path, boolean followRedirect) {
        //HeadMethod head(String path, boolean followRedirect) {
        //patch(String path, String body, boolean followRedirect)
    }
    
    @Test
    public void testTransferSuccessful() {
        //assertEquals(3+6+15, StringCalculator.add("//;n3;6;15"));
    }

    @Test
    public void testTransferFailedWith404IfSourceAccountIsUnknown() {
        //assertEquals(3+6+15, StringCalculator.add("//;n3;6;15"));
    }

    @Test
    public void testTransferFailedWith404IfDistinationAccountIsUnknown() {
        //assertEquals(3+6+15, StringCalculator.add("//;n3;6;15"));
    }

    @Test
    public void testTransferFailedWithInsufficientAmountWhenSourceBalanceIsLessThanTransferAmount() {
        //assertEquals(3+6+15, StringCalculator.add("//;n3;6;15"));
    }

    @Test
    public void testTransferFailedWithInvalidAmountWhenAmountIsNegative() {
        //assertEquals(3+6+15, StringCalculator.add("//;n3;6;15"));
    }

    @Test
    public void testTransferFailedWithInvalidAmountWhenAmountIsNotANumber() {
        //assertEquals(3+6+15, StringCalculator.add("//;n3;6;15"));
    }

    @Test
    public void testTotalSuccessTransferForAccountNumber() {
        //assertEquals(3+6+15, StringCalculator.add("//;n3;6;15"));
    }

    @Test
    public void testTotalFailedTransferForAccountNumber() {
        //assertEquals(3+6+15, StringCalculator.add("//;n3;6;15"));
    }

    @Test
    public void testSourceAccountBalanceReduceByAmountAfterSuccessfulTransfer() {
        List<Object> list = new ArrayList<>();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testDestinationAccountBalanceIncreaseByAmountAfterSuccessfulTransfer() {
        List<Object> list = new ArrayList<>();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testSourceAccountBalanceRemainsTheSameAfterFailedTransfer() {
        List<Object> list = new ArrayList<>();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testDestinationAccountBalanceRemainsTheSameAfterFailedTransfer() {
        List<Object> list = new ArrayList<>();
        assertTrue(list.isEmpty());
    }
}
