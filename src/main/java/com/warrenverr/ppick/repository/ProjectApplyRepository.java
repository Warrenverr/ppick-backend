package com.warrenverr.ppick.repository;

import com.warrenverr.ppick.dto.UserDto;
import com.warrenverr.ppick.model.Project;
import com.warrenverr.ppick.model.ProjectApply;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectApplyRepository extends JpaRepository<ProjectApply, Integer> {

    List<ProjectApply> findAllByUser_Id(Long id);
}
