import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class MessageEventHandler {

    private InputStream file;
    private StringBuilder storedData;
    private CompletableFuture<String> message;

    public MessageEventHandler(String filePath) throws FileNotFoundException {
        file = new BufferedInputStream(new FileInputStream(new File(filePath)));
        storedData = new StringBuilder();
        message = new CompletableFuture<>();
    }

    public InputStream getHandler() {
        return file;
    }

    public void storeData(byte[] data) {
        for (byte symbol : data) {
            storedData.append((char) symbol);
        }
    }

    public void handleEvent() {
        message.complete(storedData.toString());
    }

    public CompletableFuture<String> getMessage() {
        return message;
    }
}
