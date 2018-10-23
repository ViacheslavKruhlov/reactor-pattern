import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Map<String, CompletableFuture<String>> messages = new ConcurrentHashMap<>();

        MessageEventHandler messageEventHandler1 = new MessageEventHandler("resources/oop.txt");
        MessageEventHandler messageEventHandler2 = new MessageEventHandler("resources/oop1.txt");
        MessageEventHandler messageEventHandler3 = new MessageEventHandler("resources/oop2.txt");

        Reactor reactor = new Reactor();

        messages.put("message1", reactor.addEventHandler(messageEventHandler1));
        messages.put("message2", reactor.addEventHandler(messageEventHandler2));
        messages.put("message3", reactor.addEventHandler(messageEventHandler3));

        reactor.handleEvents();

        MessageEventHandler messageEventHandler4 = new MessageEventHandler("resources/oop.txt");
        MessageEventHandler messageEventHandler5 = new MessageEventHandler("resources/oop2.txt");

        messages.put("message4", reactor.addEventHandler(messageEventHandler4));
        messages.put("message5", reactor.addEventHandler(messageEventHandler5));

        int messagesShown = 0;
        while (messagesShown != 5) {
            for (Map.Entry<String, CompletableFuture<String>> entry : messages.entrySet()) {
                String key = entry.getKey();
                CompletableFuture<String> value = entry.getValue();
                if (value.isDone()) {
                    try {
                        System.out.println(String.format("%s: %s\n\n\n", key, value.get()));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    messages.remove(key);
                    messagesShown++;
                }
            }
        }

        System.exit(0);
    }
}
