package ma.emsi.plaquesimmatriculation.Controllers;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/brand")
@CrossOrigin(origins = "http://localhost:4200")
public class BrandCarController {

    private final String FLASK_URL = "http://localhost:5000/detect_brand"; // URL de l'API Flask
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> detectBrand(@RequestParam("file") MultipartFile file) {
        try {
            // Préparer les données multipart pour la requête
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();  // Utiliser le nom original du fichier
                }
            });

            // Créer l'en-tête multipart
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Préparer la requête HTTP
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Envoyer la requête à Flask
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                    FLASK_URL,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, String>>() {}
            );

            // Récupérer la marque à partir de la réponse
            String brand = response.getBody().get("brand");
            System.out.println(brand);

            // Créer la réponse JSON
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("brand", brand);

            // Retourner la réponse JSON à Angular
            return ResponseEntity.ok(responseBody);
        } catch (IOException e) {
            // En cas d'erreur, retourner un message d'erreur en JSON
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors du traitement de l'image");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
