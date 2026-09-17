package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.LibroRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.LibroResponseDTO;
import pe.edu.upeu.BiblioBackend.entity.Genero;
import pe.edu.upeu.BiblioBackend.entity.Libro;
import pe.edu.upeu.BiblioBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.DetallePrestamoRepository;
import pe.edu.upeu.BiblioBackend.repository.GeneroRepository;
import pe.edu.upeu.BiblioBackend.repository.LibroRepository;
import pe.edu.upeu.BiblioBackend.service.service.LibroService;

@Service
public class LibroServiceImpl implements LibroService {

    private static final Logger log =
            LoggerFactory.getLogger(LibroServiceImpl.class);

    private final LibroRepository libroRepository;
    private final GeneroRepository generoRepository;
    private final DetallePrestamoRepository detallePrestamoRepository;

    public LibroServiceImpl(
            LibroRepository libroRepository,
            GeneroRepository generoRepository,
            DetallePrestamoRepository detallePrestamoRepository) {

        this.libroRepository = libroRepository;
        this.generoRepository = generoRepository;
        this.detallePrestamoRepository = detallePrestamoRepository;
    }

    @Override
    @Transactional
    public LibroResponseDTO create(LibroRequestDTO request) {

        log.info("Creando libro con ISBN={}", request.getIsbn());

        String isbn = request.getIsbn().trim();

        if (libroRepository.existsByIsbn(isbn)) {
            throw new ReglaNegocioException(
                    "Ya existe un libro con el ISBN: " + isbn
            );
        }

        Genero genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Género no encontrado con id: " +
                                        request.getGeneroId()
                        )
                );

        Libro libro = new Libro();

        libro.setTitulo(request.getTitulo().trim());
        libro.setAutor(request.getAutor().trim());
        libro.setIsbn(isbn);
        libro.setCostoReposicion(request.getCostoReposicion());
        libro.setStock(request.getStock());
        libro.setEstado(request.getEstado());
        libro.setGenero(genero);

        Libro guardado = libroRepository.save(libro);

        log.info("Libro creado con id={}", guardado.getId());

        return convertirResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponseDTO read(Long id) {

        Libro libro = libroRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Libro no encontrado con id: " + id
                        )
                );

        return convertirResponse(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<LibroResponseDTO> readAll() {

        return libroRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional
    public LibroResponseDTO update(
            Long id,
            LibroRequestDTO request) {

        Libro libro = libroRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Libro no encontrado con id: " + id
                        )
                );

        String isbn = request.getIsbn().trim();

        if (libroRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new ReglaNegocioException(
                    "Ya existe otro libro con el ISBN: " + isbn
            );
        }

        Genero genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Género no encontrado con id: " +
                                        request.getGeneroId()
                        )
                );

        libro.setTitulo(request.getTitulo().trim());
        libro.setAutor(request.getAutor().trim());
        libro.setIsbn(isbn);
        libro.setCostoReposicion(request.getCostoReposicion());
        libro.setStock(request.getStock());
        libro.setEstado(request.getEstado());
        libro.setGenero(genero);

        Libro actualizado = libroRepository.save(libro);

        log.info("Libro id={} actualizado", id);

        return convertirResponse(actualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Libro libro = libroRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Libro no encontrado con id: " + id
                        )
                );

        if (detallePrestamoRepository.existsByLibroId(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el libro porque tiene detalles de préstamo asociados"
            );
        }

        libroRepository.delete(libro);

        log.info("Libro id={} eliminado", id);
    }

    private LibroResponseDTO convertirResponse(Libro libro) {

        return new LibroResponseDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getIsbn(),
                libro.getCostoReposicion(),
                libro.getStock(),
                libro.getEstado(),
                libro.getGenero().getId(),
                libro.getGenero().getNombre(),
                libro.getFechaCreacion(),
                libro.getFechaModificacion()
        );
    }
}