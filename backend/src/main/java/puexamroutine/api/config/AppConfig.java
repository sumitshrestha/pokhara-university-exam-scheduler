package puexamroutine.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import puexamroutine.control.Controller;
import puexamroutine.control.database.DatabaseReader;
import puexamroutine.control.interfaces.DomainListener;
import puexamroutine.control.routinegeneration.interfaces.SystemStepState;

@Configuration
public class AppConfig {

    @Value("${app.db.url:jdbc:mysql://localhost:3306/}")
    private String dbUrl;

    @Value("${app.db.name:puroutine}")
    private String dbName;

    @Value("${app.db.user:root}")
    private String username;

    @Value("${app.db.pass:root}")
    private String password;

    @Value("${scheduler.min-gap:1}")
    private int minGap;

    @Value("${scheduler.max-gap:3}")
    private int maxGap;

    @Bean
    public Controller controller() throws Exception {
        String normalizedDbUrl = dbUrl.endsWith("/") ? dbUrl : dbUrl + "/";

        Controller controller = new Controller(
            normalizedDbUrl,
                "com.mysql.cj.jdbc.Driver",
                username,
                password.toCharArray(),
                DatabaseReader.getDefaultCourseCodeField(),
                DatabaseReader.getDefaultProgramField(),
                DatabaseReader.getDefaultRegularCourseTableName(),
                DatabaseReader.getDefaultCourseTable(),
                DatabaseReader.getDefaultSemesterFieldName(),
                dbName,
                DatabaseReader.getDefaultBackTable(),
                DatabaseReader.getDefaultCandidateIDField(),
                DatabaseReader.getDefaultProgramTableName(),
                DatabaseReader.getDefaultFacultyFieldName(),
                DatabaseReader.getDefaultLevelFieldName(),
                minGap,
                maxGap
        );

        // The legacy scheduler expects a listener; API mode uses a no-op implementation.
        controller.setListener(new DomainListener() {
            @Override
            public void notify(SystemStepState state) {
                // No-op in stateless API mode.
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isPaused() {
                return false;
            }
        });

        return controller;
    }
}
