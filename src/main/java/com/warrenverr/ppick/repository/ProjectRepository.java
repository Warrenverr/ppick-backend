package com.warrenverr.ppick.repository;

import com.warrenverr.ppick.dto.ProjectApplyDto;
import com.warrenverr.ppick.model.Project;
import com.warrenverr.ppick.model.ProjectApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Page<Project> findAll(Specification<Project> specification, Pageable pageable);
    List<Project> findAllByProjectMember_Id(Long id);
    Optional<Project> findByApplyList_Id(Integer id);
}
