package com.appeventos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImplDataJpaMy8 implements EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	//Metodo para armar el mail con Javamail
	@Override
	public void sendEmail(String para, String link) {
		
		if (para.isBlank() || para == null)
			throw new RuntimeException("Error: No se ha obtenido destinatario.");
		
		if (link.isBlank() || link == null)
			throw new RuntimeException("Error: No se ha obtenido el link de confirmación.");
		
		SimpleMailMessage correo = new SimpleMailMessage();
		correo.setTo(para);
		correo.setSubject("Confirmacion cuenta Domaine De Sault");
		correo.setText("Estimado cliente:\n"
				+ "Gracias por su interes en registrarse en Domaine De Sault y disfrutar de las ventajas del club Domaine.\n"
				+ "Por favor, pinche en el siguente link para completar el proceso: \n"
				+link);
		
		mailSender.send(correo);
		
	}

}
