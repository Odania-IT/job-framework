package com.odaniait.jobframework.executors;

import com.odaniait.jobframework.config.JobFrameworkConfig;
import com.odaniait.jobframework.models.*;
import factories.BuildFactory;
import factories.PipelineFactory;
import factories.StepFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StepExecutorTest {
	private Pipeline pipeline = new Pipeline();

	@Mock
	private BuildExecutor buildExecutor;

	@Mock
	private JobFrameworkConfig jobFrameworkConfig;

	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);

		when(jobFrameworkConfig.getForExitCode(0)).thenReturn(ResultStatus.SUCCESS);
		when(jobFrameworkConfig.getForExitCode(1)).thenReturn(ResultStatus.FAILED);
		pipeline = PipelineFactory.generate();
	}

	@Test
	public void executeSimpleStep() {
		for (Step step : pipeline.getSteps()) {
			Build build = BuildFactory.generate();

			StepExecutor stepExecutor = new StepExecutor(pipeline, step, build, buildExecutor, jobFrameworkConfig);
			stepExecutor.run();

			// Assert output of each job
			BuildJobResult buildJobResult = build.getResults().get(step.getName());
			for (Job job : step.getJobs()) {
				assertThat(buildJobResult.getOutput(), CoreMatchers.containsString(job.getName()));
			}
			verify(buildExecutor).executeTrigger(step);
		}
	}

	@Test
	public void testFailedJob() {
		Build build = BuildFactory.generate();
		Step step = StepFactory.generate();
		Job job = step.getJobs().get(0);
		job.setScript("exit 1");

		StepExecutor stepExecutor = new StepExecutor(pipeline, step, build, buildExecutor, jobFrameworkConfig);
		stepExecutor.run();

		assertEquals(CurrentState.FAILED, build.getStepStates().get(step.getName()));
		verify(buildExecutor).executeTrigger(step);
	}
}
