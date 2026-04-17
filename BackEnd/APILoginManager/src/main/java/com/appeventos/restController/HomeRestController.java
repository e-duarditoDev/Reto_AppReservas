package com.appeventos.restController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appeventos.model.dto.UsuarioDto;
import com.appeventos.model.entity.Perfil;
import com.appeventos.model.entity.Usuario;
import com.appeventos.model.entity.UsuarioPerfiles;
import com.appeventos.model.entity.UsuarioPerfilesId;
import com.appeventos.model.entity.UsuarioTemp;
import com.appeventos.security.JwtSecurityService;
import com.appeventos.service.EmailService;
import com.appeventos.service.PerfilService;
import com.appeventos.service.UsuarioPerfilesService;
import com.appeventos.service.UsuarioService;
import com.appeventos.service.UsuarioTempService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class HomeRestController {

	@Autowired
    private PasswordEncoder passdwordEncoder;

	@Autowired
	private EmailService emailServ;
	
	@Autowired
	private UsuarioTempService usuarioTempServ;
	
	@Autowired
	private UsuarioService usuarioServ;
	
	@Autowired
	private PerfilService perfilServ;
	
	@Autowired
	private UsuarioPerfilesService usuPerfServ;
	
	@Autowired
	private JwtSecurityService jwtSecurityService;

	
	//Este endPoint es llamado en el formulario inicial de registrar el mail y crear un a contrasegna
	@PostMapping("/confirmar-email")
	public ResponseEntity<?>registroMail(@RequestBody UsuarioDto usuarioDto){
		
		//si se encuentra en la tabla temporal no ha terminado el proceso de confirmacion por mail
		if (usuarioTempServ.existsByEmail(usuarioDto.getEmail()))
			return ResponseEntity.badRequest().body("Peticion en curso. Revise su email.");
		
		//si esta en la tabla de usuario, ya se ha registrado
		if (usuarioServ.existsByEmail(usuarioDto.getEmail()) || usuarioServ.existsByAliasUsuario(usuarioDto.getEmail()))
			return ResponseEntity.badRequest().body("Usuario ya registrado. Inicie sesion con su cuenta.");
		
		//Generar token aleatorio para el link del mail(no tiene que ver con el jwt)
		String tokenTemp = UUID.randomUUID().toString();
		
		UsuarioTemp ut = new UsuarioTemp();
		
		ut.setEmail(usuarioDto.getEmail());
		ut.setPassword(passdwordEncoder.encode(usuarioDto.getPassword()));//encripta el password
		ut.setFechaExpiracion(LocalDateTime.now().plusMinutes(10));
		ut.setToken(tokenTemp);
		ut.setAliasUsuario(usuarioDto.getEmail().split("@")[0]);//toma el correo, y lo parte a partir de delimitador @ excluido
		
		//insertar en la tabla temporal el usuario cuando se confirme el mail se borrara
		usuarioTempServ.insertOne(ut);
		
		// Link de confirmación en local
		String link = "http://localhost:5173/confirm-mail?token=" + tokenTemp;
      
		//envia el email montado en sendEmail() junto con el link de confirmacion
		emailServ.sendEmail(usuarioDto.getEmail(), link);
      
	
		//El tokenTemp se le pasa luego al endPoint alta-usuario
		return ResponseEntity.ok(tokenTemp);
	}
	
	@PostMapping("/alta-cliente/{tokenTemp}")// GetMapping porque un clic sobre un link siempre es GET (excepcion convencional)
	public ResponseEntity<?> altaCliente (@PathVariable String tokenTemp){
		
		if (tokenTemp.isBlank() || tokenTemp == null)
			return ResponseEntity.badRequest().body("Token no proporcionado o nulo.");
		
		//Buscar el usuario temporal por token
		UsuarioTemp ut = usuarioTempServ.findByToken(tokenTemp);
		
		//no arroja resultados, el token debe estar corrupto o no existe en la tabla
		if(ut == null)
			return ResponseEntity.badRequest().body("No se ha encontrado un usuario temporal.");
		
		//Si el token esta expirado, borrar registro y lanzar badrequest
		if (ut.getFechaExpiracion().isBefore(LocalDateTime.now())) {
			usuarioTempServ.deleteOne(ut.getId());
			
			return ResponseEntity.badRequest().body("Token expirado. Vuelva a registar el email.");
		}
		
		//Crear la lista, buscar y comprobar si el perfil existe
		String nombrePerfil = "CLIENTE";
		Perfil perfil = perfilServ.findByNombre(nombrePerfil);
		
		if(perfil == null)
			return ResponseEntity.badRequest().body("No existe un perfil "+nombrePerfil+".");
		

		//contruir el usuarioDto para registro en tabla
		Usuario usuario = new Usuario ();
		usuario.setAliasUsuario(ut.getAliasUsuario());
		usuario.setPassword(ut.getPassword());
		usuario.setEmail(ut.getEmail());
		usuario.setFechaRegistro(LocalDate.now());
		
		//Insertar el usuario en la tabla usuario y borrar el temporal
		usuarioServ.insertarUno(usuario);
		usuarioTempServ.deleteOne(ut.getId());
		
		System.out.println("Username: "+usuario.getAliasUsuario()+"\n"+"Perfil: "+perfil.getIdPerfil());

		//Construir el UsuarioPerfilesId
		UsuarioPerfilesId usuarioPerfilesId = new UsuarioPerfilesId (
				usuario.getUsername(),//Para que funcione tiene que seguir el mismo orden de los atributos del objeto
				perfil.getIdPerfil()
				);
		
		//Construir el UsuarioPerfiles
		UsuarioPerfiles usuarioPerfiles = new UsuarioPerfiles (
				usuarioPerfilesId,
				usuario, 
				perfil
				);
		
		//Insertar el UsuarioPerfiles
		usuPerfServ.insertarUno(usuarioPerfiles);
			
		return ResponseEntity.ok().body("Usuario creado con éxito.");
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login (@RequestBody UsuarioDto usuDto){

		Usuario usuario = usuarioServ.findByEmail(usuDto.getEmail());

		if(usuario == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no registrado o credenciales incorrectas.");

		if (!passdwordEncoder.matches(usuDto.getPassword(), usuario.getPassword()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no registrado o credenciales incorrectas.");

		String token = jwtSecurityService.generateToken(
				usuario.getAliasUsuario(),
				usuario.getAuthorities());

		return ResponseEntity.ok(token);

	}

}
