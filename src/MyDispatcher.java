/* Implement this class. */

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class MyDispatcher extends Dispatcher {

    private int host_last_index;
     
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
        this.host_last_index = 0;
    }


    @Override
    public synchronized void addTask(Task task) {
    if(algorithm == SchedulingAlgorithm.ROUND_ROBIN)
    {     
        hosts.get(host_last_index).addTask(task);
        this.host_last_index = (this.host_last_index + 1) % hosts.size();
        
    }
    else if(algorithm == SchedulingAlgorithm.SHORTEST_QUEUE)
    {
        // caut host-ul cu coada de asteptare minima
        Host hostMin = hosts.get(0);
        int sumMin = hostMin.getQueueSize();
        for(Host host : hosts)
        { 
            int sum = host.getQueueSize();
            if(sum < sumMin)
            {
                hostMin = host;
                sumMin = sum;
            }
        } 
            hostMin.addTask(task);
    }
    else if(algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT)
    {
        synchronized(this){
         if(task.getType() == TaskType.SHORT)
            hosts.get(0).addTask(task);
         else if(task.getType() == TaskType.MEDIUM)
            hosts.get(1).addTask(task);
         else  
            hosts.get(2).addTask(task);
        }
    }
    else if(algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT)
    {
        // caut host-ul cu timpul ramas de executat minim
       Host hostMin = hosts.get(0);
       long sumMin = hostMin.getWorkLeft();
        for(Host host : hosts)
        { 
            long sum = host.getWorkLeft(); 
            if(sum < sumMin)
            {
                hostMin = host;
                sumMin = sum;
            }

        } 
            hostMin.addTask(task);
    }

    }
}
