package com.empresa.nucleovalidacao.config;

import com.empresa.nucleovalidacao.model.dto.ConfiguracaoValidacaoDTO;
import com.empresa.nucleovalidacao.model.dto.GrupoValidacaoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NucleoValidacaoYamlLoader {

    private static final Logger log = LoggerFactory.getLogger(NucleoValidacaoYamlLoader.class);

    private final ConcurrentHashMap<Integer, GrupoValidacaoDTO> cache = new ConcurrentHashMap<>();

    @Value("${validacao.configuracao:classpath:configuracoes-validacao.yaml}")
    private Resource configResource;

    @PostConstruct
    public void load() {
        try (InputStream is = configResource.getInputStream()) {
            var mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            mapper.setPropertyNamingStrategy(
                    com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
            var config = mapper.readValue(is, ConfiguracaoValidacaoDTO.class);
            if (config != null && config.validacao() != null) {
                config.validacao().forEach((id, def) -> {
                    cache.put(id, def);
                    log.debug("Grupo carregado: {} - {}", id, def.nome());
                });
            }
            log.info("Configuracao de grupos carregada: {} grupos", cache.size());
        } catch (Exception e) {
            log.error("Erro ao carregar configuracoes-validacao.yaml", e);
        }
    }

    public GrupoValidacaoDTO getGrupo(Integer id) {
        return cache.get(id);
    }

    public boolean exists(Integer id) {
        return cache.containsKey(id);
    }

    public Map<Integer, GrupoValidacaoDTO> getAll() {
        return Map.copyOf(cache);
    }
}
