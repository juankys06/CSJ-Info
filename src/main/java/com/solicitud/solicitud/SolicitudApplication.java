package com.solicitud.solicitud;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SolicitudApplication  {
	
	public static final String APP_PATH = new ApplicationHome(SolicitudApplication.class).getDir().getPath();
	public static final String FILES_PATH = new ApplicationHome(SolicitudApplication.class).getDir().getPath().concat(File.separator).concat("attached_files");;
	
	public static void main(String[] args) throws IOException {
		new File(FILES_PATH).mkdir();
		
		SpringApplication.run(SolicitudApplication.class, args);
	}	

}
