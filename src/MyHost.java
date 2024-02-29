import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Comparator;

public class MyHost extends Host {

    private final Queue<Task> taskQueue;
    private volatile boolean isRunning;
    private volatile Task currentTask;
    private volatile long left;

    public MyHost() {
        this.taskQueue = new PriorityQueue<>(Comparator
                .comparing(Task::getPriority)
                .reversed()
                .thenComparing(Task::getStart)
                );
        this.isRunning = true;
        this.currentTask = null;
        this.left = 0;
    }

    @Override
    public void run() {
        while (isRunning) {
            
                if (!taskQueue.isEmpty()) {
                    synchronized (taskQueue) {
                    // extrag primul element din coada
                    currentTask = taskQueue.poll();
                }
            }
          
            while (currentTask != null && currentTask.getLeft() > 0) {
                    // daca am primit un task cu o prioritate mai mare decat a celui curent
                    if (!taskQueue.isEmpty() && taskQueue.peek().getPriority() > currentTask.getPriority() && currentTask.isPreemptible()) {

                    // adaug in coada de asteptare task-ul curent
                    // si sterg din coada task-ul primit
                    taskQueue.add(currentTask);

                    // iar task-ul curent devine task-ul primit
                    currentTask = taskQueue.poll(); 
                }

                try {
                    // actualizez timpul ramas al task-ului curent
                     left = currentTask.getLeft();
                    Thread.sleep(100);
                    currentTask.setLeft(currentTask.getLeft() - 100);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            if (currentTask != null && currentTask.getLeft() <= 0) 
            {
                // termin task
                currentTask.finish();
                currentTask = null;
                left = 0;
            }
            }

            
        }
    }

    @Override
    public void addTask(Task task) {
        if (isRunning) { 
                taskQueue.add(task);
        }
    }

   @Override
    public int getQueueSize() 
    {
            int queueSize = taskQueue.size();
            // daca am un task in executie
            if (currentTask != null) {
                queueSize++;
            }
            
            return queueSize;
    }



     @Override
    public synchronized long getWorkLeft() {
        int totalWorkLeft = 0;

        synchronized (taskQueue) {
            for (Task task : taskQueue) {
                totalWorkLeft += task.getLeft();
            }
        }

        return  totalWorkLeft + left;
    }

    // inchid host-ul
    @Override
    public void shutdown() {
        isRunning = false;
    }
}
