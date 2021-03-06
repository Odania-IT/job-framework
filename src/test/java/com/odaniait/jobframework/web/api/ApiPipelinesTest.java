package com.odaniait.jobframework.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odaniait.jobframework.models.Pipeline;
import com.odaniait.jobframework.pipeline.PipelineManager;
import factories.PipelineFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiPipelinesTest {
	private static ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private PipelineManager pipelineManager;

	private MockMvc mvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void index() throws Exception {
		mvc.perform(get("/api/pipelines")).andExpect(status().isOk());
	}

	@Test
	public void showPipeline() throws Exception {
		Pipeline pipeline = PipelineFactory.generate();
		pipelineManager.getPipelines().put(pipeline.getId(), pipeline);

		mvc.perform(get("/api/pipelines/" + pipeline.getId())).andExpect(status().isOk()).
			andExpect(jsonPath("$.pipeline.id", is(pipeline.getId())));
	}

	@Test
	public void enqueuePipeline() throws Exception {
		Pipeline pipeline = PipelineFactory.generate();
		pipelineManager.getPipelines().put(pipeline.getId(), pipeline);

		Map<String, String> data = new HashMap<>();
		data.put("p1", "test");
		data.put("p2", "dependentOption 1");
		data.put("p4", "Multiple 1,Multiple 2");

		MockHttpServletRequestBuilder postRequest = post("/api/pipelines/" + pipeline.getId());
		postRequest.content(mapper.writeValueAsString(data));
		postRequest.contentType(MediaType.APPLICATION_JSON);

		mvc.perform(postRequest).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(pipeline.getId())));
	}

	@Test
	public void requestShowView1() throws Exception {
		MockHttpServletRequestBuilder getRequest = get("/api/pipelines");
		getRequest.param("viewId", "view-1");
		ResultActions resultActions = mvc.perform(getRequest).andExpect(status().isOk());
		resultActions.andExpect(content().string(allOf(
			containsString("/pipelines/pipeline-1"),
			containsString("/pipelines/pipeline-2"),
			containsString("/pipelines/my pipeline")
		)));
	}

	@Test
	public void requestShowView2() throws Exception {
		MockHttpServletRequestBuilder getRequest = get("/api/pipelines");
		getRequest.param("viewId", "view-2");
		ResultActions resultActions = mvc.perform(getRequest).andExpect(status().isOk());
		resultActions.andExpect(content().string(allOf(
			not(containsString("/pipelines/pipeline-1")),
			containsString("/pipelines/pipeline-2"),
			not(containsString("/pipelines/my pipeline"))
		)));
	}

	@Test
	public void requestShowView3() throws Exception {
		MockHttpServletRequestBuilder getRequest = get("/api/pipelines");
		getRequest.param("viewId", "view 3");
		ResultActions resultActions = mvc.perform(getRequest).andExpect(status().isOk());
		resultActions.andExpect(content().string(allOf(
			containsString("/pipelines/pipeline-1"),
			not(containsString("/pipelines/pipeline-2")),
			not(containsString("/pipelines/my pipeline"))
		)));
	}

	@Test
	public void requestShowViewWithTags() throws Exception {
		MockHttpServletRequestBuilder getRequest = get("/api/pipelines");
		getRequest.param("viewId", "view 4");
		ResultActions resultActions = mvc.perform(getRequest).andExpect(status().isOk());
		resultActions.andExpect(content().string(allOf(
			containsString("/pipelines/pipeline-1"),
			not(containsString("/pipelines/pipeline-2")),
			containsString("/pipelines/my pipeline")
		)));
	}
}
