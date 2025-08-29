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

	// --- Your Details ---
	private static final String NAME = "John Doe";
	private static final String REG_NO = "REG12347"; // Use your registration number
	private static final String EMAIL = "john@example.com";

	// --- API URLs ---
	private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

	public static void main(String[] args) {
		SpringApplication.run(HiringTaskApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("--- Starting Hiring Task ---");
		RestTemplate restTemplate = new RestTemplate();

		// Step 1 & 2: Generate Webhook and Get Access Token
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

		// Step 3: Solve the SQL Problem
		System.out.println("Step 2: Solving SQL problem...");
		// Extract the last two digits from the registration number
		int lastTwoDigits = Integer.parseInt(REG_NO.substring(REG_NO.length() - 2));

		String finalQuery;
		if (lastTwoDigits % 2 != 0) {
			// Odd Number: Question 1
			System.out.println("Registration number ends in an odd number. Solving Question 1.");
			// IMPORTANT: Replace this with your actual SQL query for Question 1
			finalQuery = "SELECT * FROM table1 WHERE condition = 'odd';";
		} else {
			// Even Number: Question 2
			System.out.println("Registration number ends in an even number. Solving Question 2.");
			// IMPORTANT: Replace this with your actual SQL query for Question 2
			finalQuery = "SELECT * FROM table2 WHERE condition = 'even';";
		}
		System.out.println("Final SQL Query: " + finalQuery);


		// Step 4: Submit the Solution
		System.out.println("Step 3: Submitting the solution...");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", accessToken); // Note: RestTemplate automatically adds "Bearer " if the token looks like a bearer token. If not, use "Bearer " + accessToken

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

