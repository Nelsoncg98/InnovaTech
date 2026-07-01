package com.innovatech.notificaciones.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @org.springframework.beans.factory.annotation.Value("${resend.api-key:PON_TU_KEY_AQUI}")
    private String resendApiKey;

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarNotificacion(@RequestBody Map<String, String> request) {
        String destinatario = request.get("destinatario");
        String asunto = request.get("asunto");
        String mensaje = request.get("mensaje"); 
        
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            // El from DEBE ser "onboarding@resend.dev" para cuentas gratuitas
            String jsonBody = "{"
                    + "\"from\": \"onboarding@resend.dev\","
                    + "\"to\": [\"" + destinatario + "\"],"
                    + "\"subject\": \"" + asunto + "\","
                    + "\"html\": \"<div style=\\\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 25px; border: 1px solid #eaeaea; border-radius: 10px; background-color: #ffffff; box-shadow: 0 4px 6px rgba(0,0,0,0.05);\\\"><div style=\\\"text-align: center; border-bottom: 2px solid #0056b3; padding-bottom: 15px; margin-bottom: 25px;\\\"><h2 style=\\\"color: #0056b3; margin: 0; font-size: 26px; font-weight: 700;\\\">InnovaTech ERP</h2><span style=\\\"color: #888; font-size: 13px; text-transform: uppercase; letter-spacing: 1px;\\\">Aviso Transaccional</span></div><p style=\\\"color: #444; font-size: 16px; line-height: 1.6;\\\">" + mensaje.replace("\n", "<br>") + "</p><div style=\\\"margin-top: 35px; padding-top: 20px; border-top: 1px solid #eaeaea; text-align: center; color: #a0a0a0; font-size: 12px;\\\">&copy; 2026 InnovaTech Retail S.A.C.<br>Este es un correo automático, por favor no responda.</div></div>\""
                    + "}";

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(jsonBody, headers);

            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.resend.com/emails", 
                    entity, 
                    String.class
            );
            
            System.out.println("✅ CORREO ENVIADO A TRAVÉS DE RESEND: " + response.getBody());
            return ResponseEntity.ok("Correo enviado");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ ERROR ENVIANDO CORREO POR RESEND: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
