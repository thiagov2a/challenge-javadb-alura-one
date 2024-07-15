package com.thiagov2a.booktrackr;

import com.thiagov2a.booktrackr.main.Principal;
import com.thiagov2a.booktrackr.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BooktrackrApplication implements CommandLineRunner {

    @Autowired
    private LibroRepository libroRepository;

    public static void main(String[] args) {
        SpringApplication.run(BooktrackrApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal(libroRepository);
        principal.menu();
    }
}
