package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.GeneroRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.GeneroResponseDTO;
import pe.edu.upeu.BiblioBackend.entity.Genero;
import pe.edu.upeu.BiblioBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.GeneroRepository;
import pe.edu.upeu.BiblioBackend.repository.LibroRepository;
import pe.edu.upeu.BiblioBackend.service.service.GeneroService;

@Service
public class GeneroServiceImpl implements GeneroService {

    private static final Logger LOG = LoggerFactory.getLogger(GeneroServiceImpl.class);

    private final GeneroRepository generoRepository;
    private final LibroRepository libroRepository;

    public GeneroServiceImpl(
            GeneroRepository generoRepository,
            LibroRepository libroRepository) {
        this.generoRepository = generoRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    @Transactional
    public GeneroResponseDTO create(GeneroRequestDTO t) {
        LOG.info("Creando genero con nombre: {}", t.getNombre());
        String nombre = t.getNombre().trim();

        if (generoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException(
                    "Ya existe un género con el nombre " + nombre);
        }

        Genero genero = new Genero();
        genero.setNombre(nombre);
        genero.setDescripcion(t.getDescripcion());
        genero.setEstado(t.getEstado());

        Genero generoCreado = generoRepository.save(genero);

        LOG.info("Genero creado con id: {}", generoCreado.getId());

        return convertirResponse(generoCreado);
    }

    @Override
    @Transactional
    public GeneroResponseDTO update(Long id, GeneroRequestDTO t) {
        LOG.info("Actualizando genero id: {}", id);

        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Género no encontrado con id: " + id
                ));

        String nombre = t.getNombre().trim();

        if (generoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException(
                    "Ya existe un género con el nombre " + nombre);
        }

        genero.setNombre(nombre);
        genero.setDescripcion(t.getDescripcion());
        genero.setEstado(t.getEstado());

        Genero generoActualizado = generoRepository.save(genero);

        LOG.info("Genero id: {} actualizado", generoActualizado.getId());

        return convertirResponse(generoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroResponseDTO read(Long id) {
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Género no encontrado con id: " + id
                ));

        return convertirResponse(genero);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Género no encontrado con id: " + id
                ));

        if (libroRepository.existsByGeneroId(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el género porque tiene libros asociados"
            );
        }

        generoRepository.delete(genero);

        LOG.info("Genero id: {} eliminado", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GeneroResponseDTO> readAll() {
        return generoRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    private GeneroResponseDTO convertirResponse(Genero genero) {
        return new GeneroResponseDTO(
                genero.getId(),
                genero.getNombre(),
                genero.getDescripcion(),
                genero.getEstado(),
                genero.getFechaCreacion(),
                genero.getFechaModificacion()
        );
    }
}