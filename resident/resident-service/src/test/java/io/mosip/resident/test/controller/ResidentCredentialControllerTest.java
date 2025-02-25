package io.mosip.resident.test.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.controller.ResidentCredentialController;
import io.mosip.resident.dto.*;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.ResidentCredentialService;
import io.mosip.resident.test.ResidentTestBootApplication;
import io.mosip.resident.util.AuditUtil;
import io.mosip.resident.util.EventEnum;
import io.mosip.resident.validator.RequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import javax.validation.Valid;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResidentTestBootApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class ResidentCredentialControllerTest {

    @MockBean
    private ResidentCredentialService residentCredentialService;

    @Mock
    CbeffImpl cbeff;

    @MockBean
    private RequestValidator validator;

    @Mock
    private AuditUtil audit;

    @MockBean
    private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> encryptor;

    @MockBean
    @Qualifier("selfTokenRestTemplate")
    private RestTemplate residentRestTemplate;

    @InjectMocks
    ResidentCredentialController residentCredentialController;

    @Autowired
    private MockMvc mockMvc;

    Gson gson = new GsonBuilder().serializeNulls().create();

    String reqJson;

    ResidentCredentialResponseDto credentialReqResponse;

    CredentialCancelRequestResponseDto credentialCancelReqResponse;

    CredentialRequestStatusResponseDto credentialReqStatusResponse;

    PartnerCredentialTypePolicyDto partnerCredentialTypeReqResponse;

    String reqCredentialEventJson;

    byte[] pdfbytes;

    @Before
    public void setup() throws Exception {
        credentialReqStatusResponse = new CredentialRequestStatusResponseDto();
        credentialCancelReqResponse = new CredentialCancelRequestResponseDto();
        credentialReqResponse = new ResidentCredentialResponseDto();
        partnerCredentialTypeReqResponse = new PartnerCredentialTypePolicyDto();
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(residentCredentialController).build();
        ResidentCredentialRequestDto credentialRequestDto = new ResidentCredentialRequestDto();
        credentialRequestDto.setIndividualId("123456");
        reqJson = gson.toJson(credentialRequestDto);
        pdfbytes = "uin".getBytes();
    }

    @Test
    public void testCreateRequestGenerationSuccess() throws Exception {

        Mockito.when(residentCredentialService.reqCredential(Mockito.any())).thenReturn(credentialReqResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/req/credential").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqJson.getBytes())).andExpect(status().isOk());

    }

    @Test
    public void testCancelRequestSuccess() throws Exception {

        Mockito.when(residentCredentialService.cancelCredentialRequest(Mockito.any()))
                .thenReturn(credentialCancelReqResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/req/credential/cancel/requestId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

    }

    @Test
    public void testgetCredentialRequestStatusSuccess() throws Exception {

        Mockito.when(residentCredentialService.getStatus(Mockito.any())).thenReturn(credentialReqStatusResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/req/credential/status/requestId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

    }

    @Test
    public void testgGetCardSuccess() throws Exception {

        Mockito.when(residentCredentialService.getCard(Mockito.any())).thenReturn(pdfbytes);

        mockMvc.perform(MockMvcRequestBuilders.get("/req/card/requestId")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());

    }

    @Test
    public void testGetCredentialTypesSuccess() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/credential/types").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

    }

    @Test
    public void testPartnerIdCredentialType() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/req/policy/partnerId/1/credentialType/credentialType").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

}
