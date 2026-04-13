package ru.epta.mtplanner.meeting.dao.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.model.request.GetListInviteRequest;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class InviteSpecification {
    public static Specification<InviteDto> build(GetListInviteRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (filter.getMeetingId() != null) {
                predicates.add(cb.equal(root.get("meeting").get("id"), filter.getMeetingId()));
            }

            if (filter.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), filter.getUserId()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sentAt"), filter.getStartDate()));
            }

            LocalDateTime finishDate = filter.getFinishDate() != null ? filter.getFinishDate() : LocalDateTime.now();
            predicates.add(cb.lessThanOrEqualTo(root.get("sentAt"), finishDate));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
