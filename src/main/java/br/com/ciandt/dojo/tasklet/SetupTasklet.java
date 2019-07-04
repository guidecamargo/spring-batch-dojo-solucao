package br.com.ciandt.dojo.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SetupTasklet implements Tasklet {

    @Value("${region}")
    private String region;

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().putString("REGION",
                        this.region);

        return RepeatStatus.FINISHED;
    }

}
