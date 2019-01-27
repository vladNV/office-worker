package com.org.worker;

import com.org.worker.service.DocumentGarbageCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Timer;

@SpringBootApplication
public class OfficePdfWorkerApplication {

	@Autowired
	private DocumentGarbageCollector collector;

	public static void main(String[] args) {
		SpringApplication.run(OfficePdfWorkerApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runJobs() {
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(collector, 0, 1000 * 60 * 10);
	}

}

