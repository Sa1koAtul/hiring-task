package com.example.hiring_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.hiring_task.dto.SolutionRequest;
import com.example.hiring_task.dto.UserDetailsRequest;
import com.example.hiring_task.dto.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class HiringTaskApplication implements CommandLineRunner {


	private static final String NAME = "Atulesh Kumar Verma";
	private static final String REG_NO = "22BCE11675";
	private static final String EMAIL = "atul2004v@gmail.com";


	private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

	public static void main(String[] args) {
		SpringApplication.run(HiringTaskApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("--- Starting Hiring Task ---");
		RestTemplate restTemplate = new RestTemplate();


		System.out.println("Step 1: Generating webhook...");
		UserDetailsRequest userDetails = new UserDetailsRequest(NAME, REG_NO, EMAIL);
		WebhookResponse webhookResponse = restTemplate.postForObject(GENERATE_WEBHOOK_URL, userDetails, WebhookResponse.class);

		if (webhookResponse == null || webhookResponse.getWebhook() == null || webhookResponse.getAccessToken() == null) {
			System.err.println("Failed to generate webhook. Response was null.");
			return;
		}

		String webhookUrl = webhookResponse.getWebhook();
		String accessToken = webhookResponse.getAccessToken();
		System.out.println("Webhook URL received: " + webhookUrl);
		System.out.println("Access Token received.");


		System.out.println("Step 2: Solving SQL problem...");
		// Extract the last two digits from the registration number
		int lastTwoDigits = Integer.parseInt(REG_NO.substring(REG_NO.length() - 2));

		String finalQuery;


		finalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE DAY(p.PAYMENT_TIME) <> 1 ORDER BY p.AMOUNT DESC LIMIT 1;";
		System.out.println("Final SQL Query: " + finalQuery);



		System.out.println("Step 3: Submitting the solution...");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", accessToken);

		SolutionRequest solution = new SolutionRequest(finalQuery);
		HttpEntity<SolutionRequest> requestEntity = new HttpEntity<>(solution, headers);

		try {
			restTemplate.exchange(webhookUrl, HttpMethod.POST, requestEntity, String.class);
			System.out.println("Solution submitted successfully!");
		} catch (Exception e) {
			System.err.println("Failed to submit solution: " + e.getMessage());
		}

		System.out.println("--- Hiring Task Finished ---");
	}
}

