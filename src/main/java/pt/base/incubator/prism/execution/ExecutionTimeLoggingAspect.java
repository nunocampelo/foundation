package pt.base.incubator.prism.execution;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeLoggingAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeLoggingAspect.class);

	@Around("@annotation(LogExecutionTime)")
	public Object doLogExecutionTime(ProceedingJoinPoint method) throws Throwable {

		long executionTime = System.currentTimeMillis();
		final Object result = method.proceed();
		executionTime = System.currentTimeMillis() - executionTime;

		LOGGER.info("{} executed in {} seconds", method.getSignature(), executionTime / 1000D);
		return result;

	}

}
