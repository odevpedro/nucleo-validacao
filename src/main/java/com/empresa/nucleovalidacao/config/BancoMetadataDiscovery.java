package com.empresa.nucleovalidacao.config;

import com.empresa.nucleovalidacao.model.metadata.ParameterMode;
import com.empresa.nucleovalidacao.model.metadata.ProcedureParameter;
import com.empresa.nucleovalidacao.model.metadata.ProcedureSignature;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BancoMetadataDiscovery {

    private static final Logger log = LoggerFactory.getLogger(BancoMetadataDiscovery.class);

    private final JdbcTemplate jdbcTemplate;
    private final ConcurrentHashMap<String, ProcedureSignature> procedureCache = new ConcurrentHashMap<>();

    public BancoMetadataDiscovery(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void discover() {
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            var meta = conn.getMetaData();
            var schemas = new String[]{"BANK_CORE"};

            for (String schema : schemas) {
                try (var procedures = meta.getProcedures(null, schema, null)) {
                    while (procedures.next()) {
                        var pkg = procedures.getString("SPECIFIC_NAME");
                        var procName = procedures.getString("PROCEDURE_NAME");
                        var ref = pkg != null ? schema + "." + pkg + "." + procName
                                : schema + "." + procName;

                        if (pkg != null && pkg.contains(".")) {
                            var parts = pkg.split("\\.");
                            if (parts.length >= 2) {
                                ref = schema + "." + parts[0] + "." + procName;
                            }
                        }

                        var params = discoverParameters(meta, schema, procName);
                        var sig = new ProcedureSignature(schema, pkg, procName, ref, params);
                        procedureCache.put(ref, sig);
                        log.debug("Procedure descoberta: {}", ref);
                    }
                }
            }
            log.info("Metadata discovery concluido: {} procedures encontradas", procedureCache.size());
        } catch (Exception e) {
            log.warn("Nao foi possivel descobrir metadata do banco: {}", e.getMessage());
        }
    }

    private List<ProcedureParameter> discoverParameters(DatabaseMetaData meta, String schema, String procedureName) {
        var params = new ArrayList<ProcedureParameter>();
        try (var columns = meta.getProcedureColumns(null, schema, procedureName, null)) {
            while (columns.next()) {
                var name = columns.getString("COLUMN_NAME");
                var jdbcType = columns.getInt("DATA_TYPE");
                var typeName = columns.getString("TYPE_NAME");
                var position = columns.getInt("SEQUENCE");
                var mode = mapMode(columns.getShort("COLUMN_TYPE"));

                if (name != null) {
                    params.add(new ProcedureParameter(name, jdbcType, typeName, position, mode));
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao descobrir parametros da procedure {}: {}", procedureName, e.getMessage());
        }
        return params;
    }

    private ParameterMode mapMode(short columnType) {
        return switch (columnType) {
            case DatabaseMetaData.procedureColumnIn -> ParameterMode.IN;
            case DatabaseMetaData.procedureColumnOut -> ParameterMode.OUT;
            case DatabaseMetaData.procedureColumnInOut -> ParameterMode.INOUT;
            case DatabaseMetaData.procedureColumnReturn -> ParameterMode.RETURN;
            default -> ParameterMode.IN;
        };
    }

    public ProcedureSignature getSignature(String procedureRef) {
        return procedureCache.get(procedureRef);
    }

    public boolean hasSignature(String procedureRef) {
        return procedureCache.containsKey(procedureRef);
    }
}
