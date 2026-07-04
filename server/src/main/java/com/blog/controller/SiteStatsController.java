package com.blog.controller;

import com.blog.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/site/stats")
public class SiteStatsController {

    private static final String SITE_VISIT_TABLE = "test.blog_site_visit";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public Result<Map<String, Object>> getStats() {
        return Result.success(readStats());
    }

    @PostMapping("/visit")
    public Result<Map<String, Object>> recordVisit(@RequestBody(required = false) Map<String, String> body,
                                                   HttpServletRequest request) {
        String visitorId = clean(body == null ? "" : body.get("visitorId"), 80);
        String path = clean(body == null ? "" : body.get("path"), 500);
        if (visitorId.isBlank()) {
            visitorId = fallbackVisitorId(request);
        }
        if (path.isBlank()) {
            path = "/";
        }

        jdbcTemplate.update(
                "INSERT INTO " + SITE_VISIT_TABLE + " (visitor_id, path, user_agent, ip_address, create_time) VALUES (?, ?, ?, ?, NOW())",
                visitorId,
                path,
                clean(request.getHeader("User-Agent"), 500),
                clean(clientIp(request), 100)
        );
        return Result.success(readStats());
    }

    private Map<String, Object> readStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        Long pageViews = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + SITE_VISIT_TABLE, Long.class);
        Long uniqueVisitors = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT visitor_id) FROM " + SITE_VISIT_TABLE, Long.class);
        String lastVisit = jdbcTemplate.queryForObject(
                "SELECT DATE_FORMAT(MAX(create_time), '%Y-%m-%d %H:%i:%s') FROM " + SITE_VISIT_TABLE,
                String.class
        );
        stats.put("pageViews", pageViews == null ? 0 : pageViews);
        stats.put("uniqueVisitors", uniqueVisitors == null ? 0 : uniqueVisitors);
        stats.put("lastVisit", lastVisit);
        return stats;
    }

    private String fallbackVisitorId(HttpServletRequest request) {
        String raw = clientIp(request) + "|" + clean(request.getHeader("User-Agent"), 500);
        return "anon-" + DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private String clean(String value, int maxLength) {
        if (value == null) return "";
        String cleaned = value.trim();
        if (cleaned.length() <= maxLength) return cleaned;
        return cleaned.substring(0, maxLength);
    }
}
