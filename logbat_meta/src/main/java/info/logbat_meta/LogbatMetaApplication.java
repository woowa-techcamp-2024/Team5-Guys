package info.logbat_meta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class LogbatMetaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogbatMetaApplication.class, args);
    }

    @Controller
    public static class HomeController {

        @GetMapping("/")
        public String home() {
            return "redirect:/projects";
        }
    }

}
