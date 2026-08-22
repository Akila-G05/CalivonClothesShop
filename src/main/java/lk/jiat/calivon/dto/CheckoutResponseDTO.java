package lk.jiat.calivon.dto;

import java.io.Serializable;

public class CheckoutResponseDTO{
    private String message;
    private boolean status;
    private PayHereDTO payHereDTO;

    public CheckoutResponseDTO(boolean status, String message, PayHereDTO payHereDTO) {
        this.status = status;
        this.message = message;
        this.payHereDTO = payHereDTO;
    }

    public CheckoutResponseDTO(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public PayHereDTO getPayHereDTO() {
        return payHereDTO;
    }

    public void setPayHereDTO(PayHereDTO payHereDTO) {
        this.payHereDTO = payHereDTO;
    }
}
