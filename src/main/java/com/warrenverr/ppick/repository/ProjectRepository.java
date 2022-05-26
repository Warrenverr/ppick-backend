package com.warrenverr.ppick.repository;

import com.warrenverr.ppick.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.List;

import java.util.List;
import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Page<Project> findAll(Specification<Project> specification, Pageable pageable);
    List<Project> findAllByProjectMember_Id(Long id);
    Optional<Project> findByApplyList_Id(Integer id);

    @Query(value = "SELECT p FROM Project p JOIN p.skill ps where ps = :skill")
    List<Project> findAllBySkill(@Param("skill") String skill);


    Page<Project> findByContest(Integer contest_id, Pageable pageable);
}
