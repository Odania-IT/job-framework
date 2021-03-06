package com.odaniait.jobframework.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ViewsTest {
	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void requestShowView1() throws Exception {
		ResultActions resultActions = mvc.perform(get("/views/view-1")).andExpect(status().isOk());
		resultActions.andExpect(content().string(containsString("viewId = 'view-1")));
	}

	@Test
	public void requestShowView2() throws Exception {
		ResultActions resultActions = mvc.perform(get("/views/view-2")).andExpect(status().isOk());
		resultActions.andExpect(content().string(containsString("viewId = 'view-2")));
	}

	@Test
	public void requestShowView3() throws Exception {
		ResultActions resultActions = mvc.perform(get("/views/view 3")).andExpect(status().isOk());
		resultActions.andExpect(content().string(containsString("viewId = 'view 3")));
	}

	@Test
	public void requestShowViewWithTags() throws Exception {
		ResultActions resultActions = mvc.perform(get("/views/view 4")).andExpect(status().isOk());
		resultActions.andExpect(content().string(containsString("viewId = 'view 4")));
	}
}
