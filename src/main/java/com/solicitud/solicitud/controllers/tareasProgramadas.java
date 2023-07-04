package com.solicitud.solicitud.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.mail.MessagingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.exception.MSQueryException;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.UserRepository;
import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.User;

import groovy.lang.Singleton;

@Singleton
@RestController
public class tareasProgramadas {

    @Autowired
    private MSGraphController msgraphController;
    @Autowired
    private FormularioRepository formularioRepository;

    @Autowired
    private UserRepository userRepository;

    private Lock queueLock = new ReentrantLock();

    @Autowired
    Environment environment;

    @Async
    @Scheduled(fixedDelay = 60000)
    public void downloadEmailsFormularios()
            throws MSQueryException, MessagingException, IOException, URISyntaxException {

        if (!queueLock.tryLock()) {
            System.out.println(LocalDateTime.now().toString() + " - La sincronizacion ya esta en ejecución");
            return;
        }

        try {

            List<Formulario> formularios = formularioRepository.findAll();
            formularios.forEach(formulario -> {

                try {
                    if (formulario.getActivo()) {
                        msgraphController.downloadEmailsT(formulario.getId());
                        System.out.println(LocalDateTime.now().toString()
                                + " - Correos del formulario" + formulario.getDescripcion());
                    }
                } catch (MSQueryException | MessagingException | IOException | URISyntaxException | ParseException e) {
                    e.printStackTrace();
                }

            });
            System.out.println(LocalDateTime.now().toString() + " - Fin de tarea programada");

        } finally {
            queueLock.unlock();
        }

    }

    @Async
    @Scheduled(fixedDelay = 86400000)
    public void updateOrCreateUsersFromExternalDb() {

        System.out.println(LocalDateTime.now().toString() + " - Sincronizando usuarios");

        // Obtén la fecha de hace dos días
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        // Formatea la fecha en el formato adecuado para la consulta SQL
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = twoDaysAgo.format(formatter);

        // Crea la consulta SQL con la cláusula WHERE para filtrar los resultados por
        // createDate
        String sql = "SELECT userId, screenname, emailaddress, password_, greeting FROM User_ WHERE createDate > '"
                + formattedDate + "' OR modifiedDate > '"
                + formattedDate + "'";

        int batchSize = 20; // ajusta el tamaño del lote según sea necesario

        // Crea el DataSource utilizando las propiedades del archivo
        // application.properties
        DataSource dataSource = DataSourceBuilder.create()
                .url(environment.getProperty("spring.datasource.secondary.url"))
                .username(environment.getProperty("spring.datasource.secondary.username"))
                .password(environment.getProperty("spring.datasource.secondary.password"))
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .build();

        // Crea el objeto JdbcTemplate utilizando el DataSource recién creado
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            List<User> usersToSave = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Long userId = (Long) row.get("userId");
                User user = userRepository.findById(userId).orElse(new User());
                user.setUserId(userId);
                user.setScreenname((String) row.get("screenname"));
                user.setEmailaddress((String) row.get("emailaddress"));
                user.setPassword_((String) row.get("password_"));
                user.setGreeting((String) row.get("greeting"));
                usersToSave.add(user);

                // Guarda el lote de usuarios cuando se alcanza el tamaño del lote deseado
                if (usersToSave.size() >= batchSize) {
                    userRepository.saveAll(usersToSave);
                    usersToSave.clear();
                }
            }
            // Guarda los usuarios restantes después del último lote
            if (!usersToSave.isEmpty()) {
                userRepository.saveAll(usersToSave);
            }
            System.out.println(LocalDateTime.now().toString() + " - Fin de la sincronizacion de usuarios");
        } catch (DataAccessException e) {
            System.out.println(
                    LocalDateTime.now().toString() + " - Error en la tarea programada de usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
