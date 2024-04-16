package com.hjj.messageserver.homework.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.StopWatch;

public class HomeworkReceiver {
	
	@RabbitListener(queues = "#{commandQueue.name}")
	public void receiveCommandQueue(String in) throws InterruptedException {
		receive(in, "commandQueue");
	}
	@RabbitListener(queues = "#{userQueue.name}")
	public void receiveUserQueue(String in) throws InterruptedException {
		receive(in, "userQueue");
	}
	@RabbitListener(queues = "#{roomQueue.name}")
	public void receiveRoomQueue(String in) throws InterruptedException {
		receive(in, "roomQueue");
	}
    
    public void receive(String in, String receiver) throws InterruptedException {
		StopWatch watch = new StopWatch();
		watch.start();
		System.out.println("instance " + receiver + " [x] Received '"+ in + "'");
		doWork(in);
		watch.stop();
		System.out.println("instance " + receiver + " [x] Done in "
		    + watch.getTotalTimeSeconds() + "s");
	}

    private void doWork(String in) throws InterruptedException {
		for (char ch : in.toCharArray()) {
			if (ch == '.') {
				Thread.sleep(1000);
			}
		}
	}
}
