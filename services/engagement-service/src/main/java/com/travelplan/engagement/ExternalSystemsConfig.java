package com.travelplan.engagement;
import org.neo4j.driver.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.context.annotation.*;
@Configuration class ExternalSystemsConfig {
 @Bean(destroyMethod="close") Driver neo4jDriver(@Value("${app.neo4j-uri}")String uri,@Value("${app.neo4j-auth}")String auth){String[]parts=auth.split("/",2);if(parts.length!=2)throw new IllegalStateException("NEO4J_AUTH must use username/password format.");return GraphDatabase.driver(uri,AuthTokens.basic(parts[0],parts[1]));}
}
