package xmacedo.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xmacedo.model.Reuniao;
import xmacedo.service.ReuniaoService;

@RestController
@RequestMapping("/reuniao")
public class ReuniaoResource {

	@Autowired
	private ReuniaoService reuniaoService;

	@GetMapping
	public ResponseEntity<Page<Reuniao>> get(Pageable pages) {
		return ResponseEntity.ok(reuniaoService.findAll(pages));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getOne(@PathVariable(name = "id") String id) {
		Reuniao reuniao = reuniaoService.findOne(id);
		return new ResponseEntity<>(reuniao, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Reuniao> save(@RequestBody Reuniao reuniao) {
		reuniaoService.insert(reuniao);
		return ResponseEntity.ok(reuniao);
	}
}
