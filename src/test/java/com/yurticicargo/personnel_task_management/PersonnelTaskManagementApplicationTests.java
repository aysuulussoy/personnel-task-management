package com.yurticicargo.personnel_task_management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PersonnelTaskManagementApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void managerLoginShouldReturnJwtToken() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                {
                                  "username": "manager",
                                  "password": "manager123"
                                }
                                """))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", notNullValue()))
				.andExpect(jsonPath("$.username").value("manager"))
				.andExpect(jsonPath("$.role").value("MANAGER"));
	}

	@Test
	void managerShouldRegisterEmployeeAndAssignTask() throws Exception {
		String managerToken = loginAndGetToken("manager", "manager123");
		TestEmployee employee = registerTestEmployee(managerToken);

		mockMvc.perform(post("/api/tasks/assign")
						.param("employeeId", employee.id().toString())
						.header("Authorization", "Bearer " + managerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                {
                                  "title": "Integration test task",
                                  "description": "Task created during backend integration test."
                                }
                                """))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.title").value("Integration test task"))
				.andExpect(jsonPath("$.status").value("NEW"))
				.andExpect(jsonPath("$.assignedEmployeeId").value(employee.id()))
				.andExpect(jsonPath("$.assignedByManagerName", notNullValue()));
	}

	@Test
	void employeeShouldSeeOwnTasksButCannotSeeAllTasks() throws Exception {
		String managerToken = loginAndGetToken("manager", "manager123");
		TestEmployee employee = registerTestEmployee(managerToken);

		Long taskId = assignTestTask(managerToken, employee.id());

		String employeeToken = loginAndGetToken(employee.username(), "employee123");

		mockMvc.perform(get("/api/tasks/my")
						.header("Authorization", "Bearer " + employeeToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(taskId))
				.andExpect(jsonPath("$[0].assignedEmployeeId").value(employee.id()))
				.andExpect(jsonPath("$[0].status").value("NEW"));

		mockMvc.perform(get("/api/tasks")
						.header("Authorization", "Bearer " + employeeToken))
				.andExpect(status().isForbidden());
	}

	@Test
	void assignedEmployeeShouldCompleteOwnTask() throws Exception {
		String managerToken = loginAndGetToken("manager", "manager123");
		TestEmployee employee = registerTestEmployee(managerToken);

		Long taskId = assignTestTask(managerToken, employee.id());

		String employeeToken = loginAndGetToken(employee.username(), "employee123");

		mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
						.param("newStatus", "COMPLETED")
						.header("Authorization", "Bearer " + employeeToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(taskId))
				.andExpect(jsonPath("$.status").value("COMPLETED"))
				.andExpect(jsonPath("$.completedAt", notNullValue()));
	}

	@Test
	void managerShouldLoadEmployeeList() throws Exception {
		String managerToken = loginAndGetToken("manager", "manager123");
		TestEmployee employee = registerTestEmployee(managerToken);

		String responseBody = mockMvc.perform(get("/api/employees")
						.header("Authorization", "Bearer " + managerToken))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode employees = objectMapper.readTree(responseBody);

		boolean foundEmployee = false;

		for (JsonNode employeeNode : employees) {
			if (employeeNode.get("id").asLong() == employee.id()) {
				foundEmployee = true;

				assertEquals("Test Employee", employeeNode.get("fullName").asText());
				assertEquals("EMPLOYEE", employeeNode.get("role").asText());
				assertEquals(employee.email(), employeeNode.get("email").asText());
				assertNotNull(employeeNode.get("active"));
				assertTrue(employeeNode.get("active").asBoolean());

				break;
			}
		}

		assertTrue(foundEmployee, "Registered test employee should be listed for manager.");
	}

	@Test
	void managerShouldLoadAllTasks() throws Exception {
		String managerToken = loginAndGetToken("manager", "manager123");
		TestEmployee employee = registerTestEmployee(managerToken);
		Long taskId = assignTestTask(managerToken, employee.id());

		String responseBody = mockMvc.perform(get("/api/tasks")
						.header("Authorization", "Bearer " + managerToken))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode tasks = objectMapper.readTree(responseBody);

		boolean foundTask = false;

		for (JsonNode taskNode : tasks) {
			if (taskNode.get("id").asLong() == taskId) {
				foundTask = true;

				assertEquals("Integration test task", taskNode.get("title").asText());
				assertEquals("Task created during backend integration test.", taskNode.get("description").asText());
				assertEquals("NEW", taskNode.get("status").asText());
				assertEquals(employee.id(), taskNode.get("assignedEmployeeId").asLong());
				assertEquals("Test Employee", taskNode.get("assignedEmployeeName").asText());
				assertNotNull(taskNode.get("assignedByManagerName"));

				break;
			}
		}

		assertTrue(foundTask, "Assigned test task should be listed for manager.");
	}

	private String loginAndGetToken(String username, String password) throws Exception {
		String responseBody = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode responseJson = objectMapper.readTree(responseBody);
		return responseJson.get("token").asText();
	}

	private TestEmployee registerTestEmployee(String managerToken) throws Exception {
		String uniqueUsername = "test_employee_" + UUID.randomUUID().toString().substring(0, 8);
		String uniqueEmail = uniqueUsername + "@yurticikargo.com";

		String responseBody = mockMvc.perform(post("/api/auth/register")
						.header("Authorization", "Bearer " + managerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                {
                                  "username": "%s",
                                  "password": "employee123",
                                  "email": "%s",
                                  "fullName": "Test Employee",
                                  "role": "EMPLOYEE"
                                }
                                """.formatted(uniqueUsername, uniqueEmail)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.username").value(uniqueUsername))
				.andExpect(jsonPath("$.email").value(uniqueEmail))
				.andExpect(jsonPath("$.fullName").value("Test Employee"))
				.andExpect(jsonPath("$.role").value("EMPLOYEE"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode responseJson = objectMapper.readTree(responseBody);

		return new TestEmployee(
				responseJson.get("id").asLong(),
				uniqueUsername,
				uniqueEmail
		);
	}

	private Long assignTestTask(String managerToken, Long employeeId) throws Exception {
		String responseBody = mockMvc.perform(post("/api/tasks/assign")
						.param("employeeId", employeeId.toString())
						.header("Authorization", "Bearer " + managerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                {
                                  "title": "Integration test task",
                                  "description": "Task created during backend integration test."
                                }
                                """))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.status").value("NEW"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode responseJson = objectMapper.readTree(responseBody);
		return responseJson.get("id").asLong();
	}

	private record TestEmployee(Long id, String username, String email) {
	}
}