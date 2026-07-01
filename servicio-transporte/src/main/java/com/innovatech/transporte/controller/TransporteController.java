package com.innovatech.transporte.controller;

import com.innovatech.transporte.dto.DespachoRequest;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/v1/transporte")
public class TransporteController {

    @PostMapping("/despacho")
    public ResponseEntity<Map<String, String>> generarDespacho(@RequestBody DespachoRequest request) {
        String trackingId = "TRK-OLVA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        try {
            // Asegurar que exista la carpeta
            File dir = new File("guias");
            if (!dir.exists()) dir.mkdirs();

            String filePath = "guias/Guia_Remision_" + trackingId + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            document.add(new Paragraph("========================================="));
            document.add(new Paragraph("        GUIA DE REMISION - OLVA COURIER      "));
            document.add(new Paragraph("========================================="));
            document.add(new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            document.add(new Paragraph("Tracking ID: " + trackingId));
            document.add(new Paragraph("Cliente DNI/RUC: " + (request.getClienteId() != null ? request.getClienteId() : "Consumidor Final")));
            document.add(new Paragraph("Dirección Destino: " + (request.getDireccionDestino() != null ? request.getDireccionDestino() : "Recojo en Tienda")));
            document.add(new Paragraph("Producto (SKU): " + request.getSku()));
            document.add(new Paragraph("========================================="));
            document.add(new Paragraph("InnovaTech Retail S.A.C - Gracias por su compra"));
            
            document.close();
            System.out.println("✅ [SERVICIO-TRANSPORTE] Guía PDF generada exitosamente en: " + filePath);
            
        } catch (Exception e) {
            System.err.println("❌ Error generando PDF: " + e.getMessage());
        }

        Map<String, String> response = new HashMap<>();
        response.put("trackingUrl", trackingId);
        response.put("operadorLogistico", "Olva Courier");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/guias/{trackingId}")
    public ResponseEntity<Resource> descargarGuia(@PathVariable String trackingId) {
        try {
            Path filePath = Paths.get("guias/Guia_Remision_" + trackingId + ".pdf").normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
