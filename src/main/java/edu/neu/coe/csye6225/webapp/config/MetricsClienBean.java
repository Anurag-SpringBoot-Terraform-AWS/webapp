package edu.neu.coe.csye6225.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

@Configuration
public class MetricsClienBean {

	@Value("${publish.metrics}")
	private boolean publishMetrics;
	
	@Value("${metrics.server.hostname}")
	private String metricsServerHost;
	
	@Value("${metrics.server.port}")
	private int metricsServerPort;
	
	
	@Bean
	public StatsDClient metricsClient() {
		if(publishMetrics)
			return new NonBlockingStatsDClient(metricsServerHost, metricsServerHost, metricsServerPort);
		return new NoOpStatsDClient();
	}
	
}
