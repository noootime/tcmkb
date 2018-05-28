import com.kiki.http.LoginUser;
import com.kiki.worker.LoginWorker;
import com.kiki.worker.OriginWorker;
import com.kiki.worker.SmartWorker;
import com.kiki.worker.WorkerChain;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Main {

    private static CloseableHttpClient httpClient;

    static {
        httpClient = HttpClients.createDefault();
    }

    public static void main(String[] args) {
        WorkerChain workerChain = new WorkerChain();
        workerChain.register(new LoginWorker(httpClient, LoginUser.ACCOUNT_FOR_ORIGIN)).register(new LoginWorker(httpClient, LoginUser.ACCOUNT_FOR_DATA)).doChain();
//        workerChain.register(new LoginWorker(httpClient, LoginUser.ACCOUNT_FOR_ORIGIN)).register(new OriginWorker(httpClient)).doChain();
//        workerChain.register(new LoginWorker(httpClient, LoginUser.ACCOUNT_FOR_DATA)).register(new SmartWorker(httpClient)).doChain();
    }
}
