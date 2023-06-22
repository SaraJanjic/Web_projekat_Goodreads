package com.example.demo.controller;

import com.example.demo.Entity.Knjiga;
import com.example.demo.Entity.Korisnik;
import com.example.demo.Repository.KorisnikRepository;
import com.example.demo.dto.*;
import com.example.demo.service.KnjigaService;
import com.example.demo.service.KorisnikService;
import com.example.demo.service.PolicaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class KorisnikRestController {

    @Autowired
    private KorisnikService korisnikService;

    @Autowired
    private final KorisnikRepository korisnikRepository;

    public KorisnikRestController(KorisnikRepository korisnikRepository) {
        this.korisnikRepository = korisnikRepository;
    }

    @GetMapping("/api/korisnici")
    public List<Korisnik> pregledajSveKorisnike() {
        return korisnikRepository.findAll();
    }

    @Autowired
    private KnjigaService knjigaService;

    @Autowired
    private PolicaService policaService;

    @GetMapping("/api/")
    public String welcome() {
        return "Hello from api!";
    }

    @PostMapping("/api/register")
    public ResponseEntity register(@RequestBody RegistracijaDto dto) {
        return korisnikService.register(dto);
    }

    @PostMapping("api/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto, HttpSession session) {
        // proverimo da li su podaci validni
        if (loginDto.getEmail().isEmpty() || loginDto.getPassword().isEmpty())
            return new ResponseEntity("Invalid login data", HttpStatus.BAD_REQUEST);

        Korisnik loggedKorisnik = korisnikService.login(loginDto.getEmail(), loginDto.getPassword());
        if (loggedKorisnik == null)
            return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);

        session.setAttribute("korisnik", loggedKorisnik);
        return new ResponseEntity(loggedKorisnik.getKorisnickePolice(), HttpStatus.OK);
    }

    @PostMapping("api/logout")
    public ResponseEntity Logout(HttpSession session) {
        Korisnik loggedKorisnik = (Korisnik) session.getAttribute("korisnik");

        if (loggedKorisnik == null)
            return new ResponseEntity("Forbidden", HttpStatus.FORBIDDEN);

        session.invalidate();
        return new ResponseEntity("Successfully logged out", HttpStatus.OK);
    }

    @GetMapping("/api/korisnik")
    public ResponseEntity<List<KorisnikDto>> getKorisnik(HttpSession session) {
        List<Korisnik> korisnikList = korisnikService.findAll();

        Korisnik loggedKorisnik = (Korisnik) session.getAttribute("korisnik");
        if (loggedKorisnik == null) {
            System.out.println("Nema sesije");
        } else {
            System.out.println(loggedKorisnik);
        }

        List<KorisnikDto> dtos = new ArrayList<>();
        for (Korisnik korisnik : korisnikList) {
            KorisnikDto dto = new KorisnikDto(korisnik);
            dtos.add(dto);
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/api/korisnik/{id}")
    public ResponseEntity getKorisnik(@PathVariable(name = "id") Long id, HttpSession session) {
        Korisnik korisnik = (Korisnik) session.getAttribute("korisnik");
        if (korisnik == null)
            return new ResponseEntity("Niste prijavljeni", HttpStatus.FORBIDDEN);
        else {
            System.out.println(korisnik.getUserName()); //getIme
            session.invalidate();
            return new ResponseEntity(korisnikService.findOne(id), HttpStatus.OK);
        }
    }
      /* @GetMapping("api/knjiga/{naziv}")
        public Knjiga getKnjiga(@PathVariable(name = "naziv") String naslov, HttpSession session) {
           Knjiga knjiga = (Knjiga) session.getAttribute(naslov);
           System.out.println(knjiga.getNaslov()); //getIme
            session.invalidate();
            return korisnikService.findOne(naslov);
        }*/

    @PostMapping("/api/save-korisnik")
    public String saveKorisnik(@RequestBody Korisnik korisnik) {
        this.korisnikService.save(korisnik);
        return "Successfully saved a user!";
    }
    @PutMapping("/{id}/azuriraj-profil")

    public ResponseEntity<String> azurirajProfil(
            @PathVariable Long id,
            @RequestBody ProfilDto profilDto,
            @RequestParam String trenutnaLozinka

    ) {

        boolean uspeh = korisnikService.azurirajProfil(id, profilDto, trenutnaLozinka);
        if (uspeh) {
            return ResponseEntity.ok("Profil je uspešno ažuriran.");
        } else {
            return ResponseEntity.badRequest().body("Ažuriranje profila nije uspelo. Proverite ispravnost unetih podataka.");
        }

    }
}


