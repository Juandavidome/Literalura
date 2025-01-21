package com.example.literatura.literalura;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private LibroRepository libroRepository;

	@Autowired
	private AutorRepository autorRepository;

	@Autowired
	private ConsumoAPI consumoAPI;

	public static void main(String[] args) {
		SpringApplication.run(Principal.class, args);
	}

	@Override
	public void run(String... args) {
		Scanner sc = new Scanner(System.in);
		ObjectMapper objectMapper = new ObjectMapper();

		while (true) {
			System.out.println("\nMenú:");
			System.out.println("1. Buscar libro por título");
			System.out.println("2. Listar libros registrados");
			System.out.println("3. Listar autores registrados");
			System.out.println("4. Listar autores vivos en un año específico");
			System.out.println("5. Listar libros por idioma");
			System.out.println("6. Salir");

			System.out.print("Selecciona una opción: ");
			int opcion = sc.nextInt();
			sc.nextLine(); // Limpiar buffer

			switch (opcion) {
				case 1:
					System.out.print("Ingresa el título del libro: ");
					String titulo = sc.nextLine();
					String url = "https://gutendex.com/books/?search=" + titulo.replace(" ", "+");
					String json = consumoAPI.obtenerDatos(url);

					// Parsear JSON y guardar libro
					try {
						JsonNode rootNode = objectMapper.readTree(json);
						JsonNode results = rootNode.path("results");

						if (results.isArray()) {
							for (JsonNode node : results) {
								String tituloLibro = node.path("title").asText();
								String idioma = node.path("languages").get(0).asText();

								// Verificar si el libro ya existe
								if (libroRepository.existsById(node.path("id").asLong())) {
									System.out.println("El libro ya está registrado.");
								} else {
									Libro libro = new Libro();
									libro.setTitulo(tituloLibro);
									libro.setIdioma(idioma);

									libroRepository.save(libro);
									System.out.println("Libro registrado: " + tituloLibro);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case 2:
					List<Libro> libros = libroRepository.findAll();
					libros.forEach(libro -> System.out.println(libro.getTitulo() + " - " + libro.getIdioma()));
					break;

				case 3:
					List<Autor> autores = autorRepository.findAll();
					autores.forEach(autor -> System.out.println(autor.getNombre()));
					break;

				case 4:
					System.out.print("Ingresa el año: ");
					int anio = sc.nextInt();
					LocalDate fecha = LocalDate.of(anio, 1, 1);
					List<Autor> autoresVivos = autorRepository.findByFechaFallecimientoAfter(fecha);
					autoresVivos.forEach(autor -> System.out.println(autor.getNombre()));
					break;

				case 5:
					System.out.print("Ingresa el idioma (ES, EN, FR, PT): ");
					String idioma = sc.nextLine().toUpperCase();
					List<Libro> librosPorIdioma = libroRepository.findAll();
					librosPorIdioma.stream()
							.filter(libro -> libro.getIdioma().equalsIgnoreCase(idioma))
							.forEach(libro -> System.out.println(libro.getTitulo()));
					break;

				case 6:
					System.exit(0);
					break;

				default:
					System.out.println("Opción no válida.");
					break;
			}
		}
	}
}
