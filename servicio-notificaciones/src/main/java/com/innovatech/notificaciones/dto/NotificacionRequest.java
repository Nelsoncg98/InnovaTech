package com.innovatech.notificaciones.dto;

import lombok.Data;
import java.util.Map;

@Data
public class NotificacionRequest {
    private String destinatarioEmail;
    private String destinatarioTelefono;
    private String canal; // EMAIL, SMS, WHATSAPP
    private String tipoPlantilla; // COMPRA_CONFIRMADA, BOLETA_ELECTRONICA, TRACKING_ACTUALIZADO
    private Map<String, Object> parametros;
}
