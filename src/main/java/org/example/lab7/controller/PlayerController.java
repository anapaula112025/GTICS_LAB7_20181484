package org.example.lab7.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.lab7.entity.Player;
import org.example.lab7.repository.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
public class PlayerController {
    final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }


    //Listado del leaderboard

    @GetMapping(value = "player/{region}")
    public List<Player> listarPorRegion(@PathVariable("region") String region) {

            return playerRepository.listarplayerporregion(region);

    }

    // Agregar nuevo jugador
    @PostMapping(value = {"player"})
    public ResponseEntity<HashMap<String, Object>> guardarPlayer(
            @RequestBody Player player,
            @RequestParam(value = "fetchId", required = false) boolean fetchId) {

        HashMap<String, Object> responseJson = new HashMap<>();

        List<Player> players = playerRepository.findAll();

        for(Player jugador : players)
        {
            if (jugador.getMmr()< player.getMmr()){
                player.setPosition(jugador.getMmr());
                jugador.setPosition(jugador.getMmr()-1);

            }
        }



        playerRepository.save(player);
        if (fetchId) {
            responseJson.put("id", player.getId());
        }
        responseJson.put("estado", "creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
    }

    // ACTUALIZAR
    @PutMapping(value = {"player"}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<HashMap<String, Object>> actualizar(Player player) {

        HashMap<String, Object> rpta = new HashMap<>();

        if (player.getId() > 0) {

            Optional<Player> byId = playerRepository.findById(player.getId());
            if (byId.isPresent()) {
                Player productFromDb = byId.get();

                if (player.getName() != null)
                    productFromDb.setName(player.getName());

                if (player.getMmr() != 0)
                    productFromDb.setMmr(player.getMmr());

                if (player.getPosition() != 0)
                    productFromDb.setPosition(player.getPosition());

                if (player.getRegion() != null)
                    productFromDb.setRegion(player.getRegion());

                playerRepository.save(productFromDb);
                rpta.put("estado", "actualizado");
                return ResponseEntity.ok(rpta);
            } else {
                rpta.put("estado", "error");
                rpta.put("msg", "El player a actualizar no existe");
                return ResponseEntity.badRequest().body(rpta);
            }
        } else {
            rpta.put("estado", "error");
            rpta.put("msg", "debe enviar un player con ID");
            return ResponseEntity.badRequest().body(rpta);
        }
    }

    @DeleteMapping(value = "player/{id}")
    public ResponseEntity<HashMap<String, Object>> borrar(@PathVariable("id") String idStr){

        HashMap<String, Object> rpta = new HashMap<>();
        try{
            int id = Integer.parseInt(idStr);

            if(playerRepository.existsById(id)){
                playerRepository.deleteById(id);
                rpta.put("estado","borrado exitoso");
                return ResponseEntity.ok(rpta);
            }else{
                rpta.put("estado","error");
                rpta.put("msg","el ID enviado no existe");
                return ResponseEntity.badRequest().body(rpta);
            }
        }catch (NumberFormatException e){
            rpta.put("estado","error");
            rpta.put("msg","el ID debe ser un número");
            return ResponseEntity.badRequest().body(rpta);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, String>> gestionException(HttpServletRequest request) {
        HashMap<String, String> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar un player");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }














}

