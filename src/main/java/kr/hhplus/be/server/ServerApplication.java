package kr.hhplus.be.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

	public static void main(String[] args) {
		// AWS t3.medium 서버 기준으로 CPU 및 메모리 설정 적용
//		System.setProperty("jdk.lang.Process.activeProcessorCount", "2");  // CPU 2개만 사용
		System.setProperty("jdk.lang.Process.activeProcessorCount", "4");  // CPU 4개 사용

		// Spring Boot 실행
		SpringApplication.run(ServerApplication.class, args);

		// 시스템 리소스 로그 출력
		logSystemResources();
	}

	public static void logSystemResources() {
		Runtime runtime = Runtime.getRuntime();
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

		System.out.println("🚀 Spring Boot Server Started with Resource Constraints:");
		System.out.println("👉 CPU Cores Available: " + runtime.availableProcessors());
		System.out.println("👉 Max Heap Memory: " + (runtime.maxMemory() / (1024 * 1024)) + " MB");
		System.out.println("👉 Initial Heap Memory: " + (heapMemoryUsage.getInit() / (1024 * 1024)) + " MB");
		System.out.println("👉 Used Heap Memory: " + (heapMemoryUsage.getUsed() / (1024 * 1024)) + " MB");
	}

}
