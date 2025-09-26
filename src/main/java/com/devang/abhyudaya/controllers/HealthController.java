package com.devang.abhyudaya.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired(required = false)
    private DataSource dataSource;
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    @Autowired(required = false)
    private Environment environment;
    
    @Autowired(required = false)
    private BuildProperties buildProperties;

    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> healthInfo = new LinkedHashMap<>();
        
        // Application info
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Application details
        Map<String, Object> appInfo = new LinkedHashMap<>();
        if (buildProperties != null) {
            appInfo.put("name", buildProperties.getName());
            appInfo.put("version", buildProperties.getVersion());
            appInfo.put("build_time", buildProperties.getTime());
        } else {
            appInfo.put("name", "abhyudaya");
            appInfo.put("version", "development");
        }
        
        if (environment != null) {
            appInfo.put("active_profiles", String.join(",", environment.getActiveProfiles()));
            appInfo.put("server_port", environment.getProperty("server.port", "8080"));
        }
        
        healthInfo.put("application", appInfo);
        
        // Database health
        Map<String, Object> dbHealth = new LinkedHashMap<>();
        if (dataSource != null && jdbcTemplate != null) {
            try {
                // Test basic connectivity
                long startTime = System.currentTimeMillis();
                Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                long responseTime = System.currentTimeMillis() - startTime;
                
                dbHealth.put("status", result != null && result == 1 ? "UP" : "DOWN");
                dbHealth.put("response_time_ms", responseTime);
                
                // Database version and info
                try {
                    String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
                    dbHealth.put("database", "PostgreSQL");
                    dbHealth.put("version", version != null ? version.split(" ")[1] : "unknown");
                } catch (Exception e) {
                    dbHealth.put("version", "unavailable");
                }
                
                // Connection pool info (HikariCP)
                Map<String, Object> poolInfo = new LinkedHashMap<>();
                if (dataSource instanceof HikariDataSource) {
                    HikariDataSource hikariDS = (HikariDataSource) dataSource;
                    HikariPoolMXBean poolBean = hikariDS.getHikariPoolMXBean();
                    
                    poolInfo.put("active_connections", poolBean.getActiveConnections());
                    poolInfo.put("idle_connections", poolBean.getIdleConnections());
                    poolInfo.put("total_connections", poolBean.getTotalConnections());
                    poolInfo.put("threads_awaiting_connection", poolBean.getThreadsAwaitingConnection());
                    poolInfo.put("maximum_pool_size", hikariDS.getMaximumPoolSize());
                    poolInfo.put("minimum_idle", hikariDS.getMinimumIdle());
                    poolInfo.put("connection_timeout", hikariDS.getConnectionTimeout());
                    poolInfo.put("idle_timeout", hikariDS.getIdleTimeout());
                    poolInfo.put("max_lifetime", hikariDS.getMaxLifetime());
                }
                dbHealth.put("connection_pool", poolInfo);
                
                // PostgreSQL specific connection info
                try {
                    Map<String, Object> pgConnections = new LinkedHashMap<>();
                    
                    // Total connections by state
                    String connByStateQuery = """
                        SELECT state, count(*) as count 
                        FROM pg_stat_activity 
                        WHERE state IS NOT NULL 
                        GROUP BY state
                        ORDER BY count DESC
                        """;
                    
                    jdbcTemplate.query(connByStateQuery, rs -> {
                        String state = rs.getString("state");
                        int count = rs.getInt("count");
                        pgConnections.put("connections_" + state.toLowerCase(), count);
                    });
                    
                    // Total connections
                    Integer totalConnections = jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM pg_stat_activity", Integer.class);
                    pgConnections.put("total_database_connections", totalConnections);
                    
                    // Max connections allowed
                    Integer maxConnections = jdbcTemplate.queryForObject(
                        "SELECT setting::int FROM pg_settings WHERE name = 'max_connections'", Integer.class);
                    pgConnections.put("max_connections_allowed", maxConnections);
                    
                    dbHealth.put("postgresql_stats", pgConnections);
                    
                } catch (Exception e) {
                    dbHealth.put("postgresql_stats", "unavailable: " + e.getMessage());
                }
                
            } catch (Exception e) {
                dbHealth.put("status", "DOWN");
                dbHealth.put("error", e.getMessage());
            }
        } else {
            dbHealth.put("status", "NOT_CONFIGURED");
            dbHealth.put("message", "DataSource or JdbcTemplate not available");
        }
        
        healthInfo.put("database", dbHealth);
        
        // Memory information
        Map<String, Object> memoryInfo = new LinkedHashMap<>();
        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // JVM Memory
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        Map<String, Object> jvmMemory = new LinkedHashMap<>();
        jvmMemory.put("total_mb", totalMemory / (1024 * 1024));
        jvmMemory.put("used_mb", usedMemory / (1024 * 1024));
        jvmMemory.put("free_mb", freeMemory / (1024 * 1024));
        jvmMemory.put("max_mb", maxMemory == Long.MAX_VALUE ? "unlimited" : maxMemory / (1024 * 1024));
        jvmMemory.put("usage_percentage", Math.round((double) usedMemory / totalMemory * 100));
        
        memoryInfo.put("jvm", jvmMemory);
        
        // Heap Memory
        Map<String, Object> heapMemory = new LinkedHashMap<>();
        heapMemory.put("used_mb", memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024));
        heapMemory.put("committed_mb", memoryBean.getHeapMemoryUsage().getCommitted() / (1024 * 1024));
        heapMemory.put("max_mb", memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024));
        
        memoryInfo.put("heap", heapMemory);
        
        // Non-Heap Memory
        Map<String, Object> nonHeapMemory = new LinkedHashMap<>();
        nonHeapMemory.put("used_mb", memoryBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024));
        nonHeapMemory.put("committed_mb", memoryBean.getNonHeapMemoryUsage().getCommitted() / (1024 * 1024));
        nonHeapMemory.put("max_mb", memoryBean.getNonHeapMemoryUsage().getMax() / (1024 * 1024));
        
        memoryInfo.put("non_heap", nonHeapMemory);
        
        healthInfo.put("memory", memoryInfo);
        
        // System information
        Map<String, Object> systemInfo = new LinkedHashMap<>();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        systemInfo.put("os_name", osBean.getName());
        systemInfo.put("os_version", osBean.getVersion());
        systemInfo.put("os_arch", osBean.getArch());
        systemInfo.put("available_processors", osBean.getAvailableProcessors());
        systemInfo.put("system_load_average", osBean.getSystemLoadAverage());
        
        // Java version info
        systemInfo.put("java_version", System.getProperty("java.version"));
        systemInfo.put("java_vendor", System.getProperty("java.vendor"));
        systemInfo.put("java_home", System.getProperty("java.home"));
        
        healthInfo.put("system", systemInfo);
        
        // Uptime
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        Map<String, Object> uptimeInfo = new LinkedHashMap<>();
        uptimeInfo.put("uptime_ms", uptime);
        uptimeInfo.put("uptime_seconds", uptime / 1000);
        uptimeInfo.put("uptime_minutes", uptime / (1000 * 60));
        uptimeInfo.put("uptime_hours", uptime / (1000 * 60 * 60));
        
        healthInfo.put("uptime", uptimeInfo);
        
        return healthInfo;
    }
    
    @GetMapping("/simple")
    public String simpleHealthCheck() {
        StringBuilder report = new StringBuilder();
        
        report.append("=== APPLICATION HEALTH REPORT ===\n");
        report.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        report.append("Status: UP\n\n");
        
        // Quick DB check
        if (dataSource != null && jdbcTemplate != null) {
            try {
                long startTime = System.currentTimeMillis();
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                long responseTime = System.currentTimeMillis() - startTime;
                
                report.append("Database: UP (").append(responseTime).append("ms)\n");
                
                if (dataSource instanceof HikariDataSource) {
                    HikariDataSource hikariDS = (HikariDataSource) dataSource;
                    HikariPoolMXBean poolBean = hikariDS.getHikariPoolMXBean();
                    report.append("DB Connections: ")
                          .append(poolBean.getActiveConnections()).append(" active, ")
                          .append(poolBean.getIdleConnections()).append(" idle, ")
                          .append(poolBean.getTotalConnections()).append(" total\n");
                }
            } catch (Exception e) {
                report.append("Database: DOWN - ").append(e.getMessage()).append("\n");
            }
        } else {
            report.append("Database: NOT CONFIGURED\n");
        }
        
        // Memory
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        report.append("Memory: ").append(usedMemory).append("/").append(totalMemory).append(" MB (")
              .append(Math.round((double) usedMemory / totalMemory * 100)).append("%)\n");
        
        // Uptime
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeHours = uptimeMs / (1000 * 60 * 60);
        long uptimeMinutes = (uptimeMs % (1000 * 60 * 60)) / (1000 * 60);
        report.append("Uptime: ").append(uptimeHours).append("h ").append(uptimeMinutes).append("m\n");
        
        return report.toString();
    }
}
