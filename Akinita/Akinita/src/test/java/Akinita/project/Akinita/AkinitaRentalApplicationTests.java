package Akinita.project.Akinita;

import Akinita.project.Akinita.Entities.Actors.Owner;
import Akinita.project.Akinita.Entities.Actors.User;
import Akinita.project.Akinita.Services.OwnerService;
import Akinita.project.Akinita.Services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AkinitaRentalApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private UserService userService;

	@MockBean
	private OwnerService ownerService;


	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void testRegisterOwner_Success() throws Exception {
		User mockUser = new User();
		mockUser.setId(1);
		mockUser.setUsername("testuser");
		mockUser.setEmail("test@example.com");

		Mockito.when(userService.existsUser(anyString())).thenReturn(false);
		Mockito.when(userService.existsEmail(anyString())).thenReturn(false);
		Mockito.when(ownerService.existsTelephone(anyString())).thenReturn(false);
		Mockito.when(userService.saveUser(any(User.class), anyString())).thenReturn(mockUser);
		Mockito.doNothing().when(ownerService).save(any(Owner.class));

		mockMvc.perform(post("/saveUser")
						.param("role", "ROLE_OWNER")
						.param("username", "testuser")
						.param("password", "password123")
						.param("email", "test@example.com")
						.param("firstname", "John")
						.param("lastname", "Doe")
						.param("telephone", "1234567890"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	public void testRegisterOwner_UsernameExists() throws Exception {
		Mockito.when(userService.existsUser(anyString())).thenReturn(true);

		mockMvc.perform(post("/saveUser")
						.param("role", "ROLE_OWNER")
						.param("username", "existinguser")
						.param("password", "password123")
						.param("email", "test@example.com")
						.param("firstname", "John")
						.param("lastname", "Doe")
						.param("telephone", "1234567890"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/register"))
				.andExpect(model().attributeExists("isError"))
				.andExpect(model().attribute("isError", true))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Username already exists!"));
	}


	@Test
	public void testRegisterOwner_TelephoneExists() throws Exception {
		Mockito.when(userService.existsUser(anyString())).thenReturn(false);
		Mockito.when(userService.existsEmail(anyString())).thenReturn(false);
		Mockito.when(ownerService.existsTelephone(anyString())).thenReturn(true);

		mockMvc.perform(post("/saveUser")
						.param("role", "ROLE_OWNER")
						.param("username", "testuser")
						.param("password", "password123")
						.param("email", "test@example.com")
						.param("firstname", "John")
						.param("lastname", "Doe")
						.param("telephone", "1234567890"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/register"))
				.andExpect(model().attributeExists("isError"))
				.andExpect(model().attribute("isError", true))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Telephone already exists!"));
	}

	@Test
	public void testRegisterOwner_InvalidTelephoneLength() throws Exception {
		Mockito.when(userService.existsUser(anyString())).thenReturn(false);
		Mockito.when(userService.existsEmail(anyString())).thenReturn(false);

		mockMvc.perform(post("/saveUser")
						.param("role", "ROLE_OWNER")
						.param("username", "testuser")
						.param("password", "password123")
						.param("email", "test@example.com")
						.param("firstname", "John")
						.param("lastname", "Doe")
						.param("telephone", "12345")) // Too short
				.andExpect(status().isOk())
				.andExpect(view().name("auth/register"))
				.andExpect(model().attributeExists("isError"))
				.andExpect(model().attribute("isError", true))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Telephone must be 10 digits"));
	}
}
