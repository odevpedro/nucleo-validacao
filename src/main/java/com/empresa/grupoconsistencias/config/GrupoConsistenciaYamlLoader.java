package com.empresa.grupoconsistencias.config;

import com.empresa.grupoconsistencias.model.dto.ConfiguracaoGruposDTO;
import com.empresa.grupoconsistencias.model.dto.GrupoDefinicaoDTO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GrupoConsistenciaYamlLoader {

    private static final Logger log = LoggerFactory.getLogger(GrupoConsistenciaYamlLoader.class);

    private final ConcurrentHashMap<Integer, GrupoDefinicaoDTO> cache = new ConcurrentHashMap<>();

    @Value("${grupos.configuracao:classpath:configuracoes-grupos.yaml}")
    private Resource configResource;

    @PostConstruct
    public void load() {
        try (InputStream is = configResource.getInputStream()) {
            var yaml = new Yaml();
            var config = yaml.loadAs(is, ConfiguracaoGruposDTO.class);
            if (config != null && config.grupos() != null) {
                config.grupos().forEach((id, def) -> {
                    cache.put(id, def);
                    log.debug("Grupo carregado: {} - {}", id, def.nome());
                });
            }
            log.info("Configuracao de grupos carregada: {} grupos", cache.size());
        } catch (Exception e) {
            log.error("Erro ao carregar configuracoes-grupos.yaml", e);
        }
    }

    public GrupoDefinicaoDTO getGrupo(Integer id) {
        return cache.get(id);
    }

    public boolean exists(Integer id) {
        return cache.containsKey(id);
    }

    public Map<Integer, GrupoDefinicaoDTO> getAll() {
        return Map.copyOf(cache);
    }
}
