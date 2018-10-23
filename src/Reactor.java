import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Reactor {

    private static final int MAX_BUFFER_SIZE = 5;

    private Thread eventExecutor;
    private List<MessageEventHandler> eventHandlers = new ArrayList<>();

    public CompletableFuture<String> addEventHandler(MessageEventHandler eventHandler) {
        synchronized (this) {
            eventHandlers.add(eventHandler);
            this.notifyAll();
            return eventHandler.getMessage();
        }
    }

    public void handleEvents() {
        synchronized (this) {
            if (eventExecutor == null) {
                eventExecutor = new Thread(new EventExecutor());
                eventExecutor.start();
            } else {
                this.notifyAll();
            }
        }
    }

    private class EventExecutor implements Runnable {

        @Override
        public void run() {
            synchronized (this) {
                while (true) {
                    try {
                        Thread.sleep(100);

                        if (eventHandlers.isEmpty()) {

                            this.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    Iterator<MessageEventHandler> eventIterator = eventHandlers.iterator();

                    while (eventIterator.hasNext()) {
                        MessageEventHandler eventHandler = eventIterator.next();
                        try {
                            int available = eventHandler.getHandler().available();
                            if (available <= 0) {
                                eventHandler.handleEvent();
                                eventIterator.remove();
                            } else {
                                int bufferSize = getBufferSize(available);
                                byte[] buffer = new byte[bufferSize];
                                eventHandler.getHandler().read(buffer, 0, bufferSize);
                                eventHandler.storeData(buffer);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private int getBufferSize(int available) {
            return available > MAX_BUFFER_SIZE ? MAX_BUFFER_SIZE : available;
        }
    }

}
