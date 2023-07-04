package com.solicitud.solicitud.mail;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.*;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class ThymeleafConfig {
	@Bean
	public TemplateEngine htmlTemplateEngine() {
	    final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	    templateEngine.setTemplateResolver(htmlTemplateResolver());
	    
	    return templateEngine;
	}
	
	@Bean
	public TemplateEngine textTemplateEngine() {
	    final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	    templateEngine.setTemplateResolver(textTemplateResolver());
	    
	    return templateEngine;
	}

    private ITemplateResolver htmlTemplateResolver(){
    	ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("/templates/html/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        emailTemplateResolver.setCacheable(false);
        
        return emailTemplateResolver;
    }
    
    private ITemplateResolver textTemplateResolver() {
    	ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    	templateResolver.setPrefix("/templates/text/");
    	templateResolver.setSuffix(".txt");
    	templateResolver.setTemplateMode(TemplateMode.TEXT);
    	templateResolver.setCharacterEncoding("UTF8");
    	templateResolver.setCacheable(false);
    	
    	return templateResolver;
    }
}