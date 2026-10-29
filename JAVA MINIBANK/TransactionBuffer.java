public class TransactionBuffer {

    private final Command[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;
    private boolean finished = false;

    public TransactionBuffer(int capacity) {
        this.buffer = new Command[capacity];
    }

    public synchronized void produce(Command cmd) throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }

        buffer[in] = cmd;
        in = (in + 1) % buffer.length;
        count++;
        notifyAll();
    }

    public synchronized Command consume() throws InterruptedException {
        while (count == 0) {
            if (finished) {
                return null;
            }
            wait();
        }

        Command cmd = buffer[out];
        buffer[out] = null;
        out = (out + 1) % buffer.length;
        count--;
        notifyAll();
        return cmd;
    }

    public synchronized void finish() {
        this.finished = true;
        notifyAll();
    }

    public synchronized boolean isFinished() {
        return finished && count == 0;
    }
}
