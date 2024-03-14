package com.sergon.blogpost.repository;

import com.sergon.blogpost.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long>
{
}
