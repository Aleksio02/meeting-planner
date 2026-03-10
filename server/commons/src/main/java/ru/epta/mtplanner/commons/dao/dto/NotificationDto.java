package ru.epta.mtplanner.commons.dao.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "notifications")
public class NotificationDto extends AbstractEntityDto {

    @Column(nullable = false)
    private UUID sender;

    @Column(nullable = false)
    private UUID receiver;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> content = new HashMap<>();
}
