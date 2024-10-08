package com.example.demo.Repository;

import com.example.demo.Entity.Autor;
import com.example.demo.Entity.Zanr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZanrRepository extends JpaRepository<Zanr, Long>{
    Optional<Zanr> findByNazivZanra(String nazivZanra);
}
