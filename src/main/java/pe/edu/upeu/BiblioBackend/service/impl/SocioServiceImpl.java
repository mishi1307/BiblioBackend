package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.SocioRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.SocioResponseDTO;
import pe.edu.upeu.BiblioBackend.entity.Socio;
import pe.edu.upeu.BiblioBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.SocioRepository;
import pe.edu.upeu.BiblioBackend.service.service.SocioService;

@Service
public class SocioServiceImpl implements SocioService {

    private static final Logger log =
            LoggerFactory.getLogger(SocioServiceImpl.class);

    private final SocioRepository socioRepository;

    public SocioServiceImpl(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    @Override
    @Transactional
    public SocioResponseDTO create(SocioRequestDTO request) {

        log.info("Registrando socio con DNI={}", request.getDni());

        String dni = request.getDni().trim();

        if (socioRepository.existsByDni(dni)) {
            throw new ReglaNegocioException(
                    "Ya existe un socio con el DNI: " + dni
            );
        }

        Socio socio = new Socio();

        socio.setDni(dni);
        socio.setNombres(request.getNombres().trim());
        socio.setApellidos(request.getApellidos().trim());
        socio.setEmail(request.getEmail().trim().toLowerCase());
        socio.setTelefono(normalizar(request.getTelefono()));
        socio.setDireccion(normalizar(request.getDireccion()));
        socio.setEstado(request.getEstado());

        Socio guardado = socioRepository.save(socio);

        log.info("Socio registrado con id={}", guardado.getId());

        return convertirResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public SocioResponseDTO read(Long id) {

        Socio socio = socioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Socio no encontrado con id: " + id
                        )
                );

        return convertirResponse(socio);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<SocioResponseDTO> readAll() {

        return socioRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional
    public SocioResponseDTO update(
            Long id,
            SocioRequestDTO request) {

        Socio socio = socioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Socio no encontrado con id: " + id
                        )
                );

        String dni = request.getDni().trim();

        if (socioRepository.existsByDniAndIdNot(dni, id)) {
            throw new ReglaNegocioException(
                    "Ya existe otro socio con el DNI: " + dni
            );
        }

        socio.setDni(dni);
        socio.setNombres(request.getNombres().trim());
        socio.setApellidos(request.getApellidos().trim());
        socio.setEmail(request.getEmail().trim().toLowerCase());
        socio.setTelefono(normalizar(request.getTelefono()));
        socio.setDireccion(normalizar(request.getDireccion()));
        socio.setEstado(request.getEstado());

        Socio actualizado = socioRepository.save(socio);

        log.info("Socio id={} actualizado", id);

        return convertirResponse(actualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Socio socio = socioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Socio no encontrado con id: " + id
                        )
                );

        socioRepository.delete(socio);

        log.info("Socio id={} eliminado", id);
    }

    private String normalizar(String valor) {

        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        return valor.trim();
    }

    private SocioResponseDTO convertirResponse(Socio socio) {

        return new SocioResponseDTO(
                socio.getId(),
                socio.getDni(),
                socio.getNombres(),
                socio.getApellidos(),
                socio.getEmail(),
                socio.getTelefono(),
                socio.getDireccion(),
                socio.getEstado(),
                socio.getFechaCreacion(),
                socio.getFechaModificacion()
        );
    }
}