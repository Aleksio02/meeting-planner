package ru.epta.mtplanner.meeting.dao.specification;

import jakarta.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;

public class MeetingSpecification {
    public static Specification<MeetingDto> build(GetListMeetingRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (filter.getOwnerId() != null) {
                predicates.add(cb.equal(root.get("ownerId").get("id"), filter.getOwnerId()));
            }

            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startsAt"), filter.getStartDate()));
            }

            if (filter.getFinishDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startsAt"), filter.getFinishDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
