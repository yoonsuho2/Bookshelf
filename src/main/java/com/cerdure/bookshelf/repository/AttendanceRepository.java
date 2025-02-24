package com.cerdure.bookshelf.repository;

import com.cerdure.bookshelf.domain.member.Attendance;
import com.cerdure.bookshelf.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    public Attendance findByMemberAndRegDate(Member member, LocalDate regDate);
    public List<Attendance> findByMemberAndRegDateAfter(Member member, LocalDate startDate);
}
