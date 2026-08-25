package za.gov.gautengems.itsupport.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    // =========================================================
    // ID
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // RESET TOKEN
    // =========================================================

    @Column(nullable = false, unique = true, length = 100)
    private String token;


    // =========================================================
    // USER
    // =========================================================

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;


    // =========================================================
    // EXPIRATION TIME
    // =========================================================

    @Column(nullable = false)
    private LocalDateTime expiryDate;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PasswordResetToken() {
    }


    // =========================================================
    // CONVENIENCE CONSTRUCTOR
    // =========================================================

    public PasswordResetToken(
            String token,
            User user,
            LocalDateTime expiryDate) {

        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }


    // =========================================================
    // GET TOKEN
    // =========================================================

    public String getToken() {
        return token;
    }


    // =========================================================
    // SET TOKEN
    // =========================================================

    public void setToken(String token) {
        this.token = token;
    }


    // =========================================================
    // GET USER
    // =========================================================

    public User getUser() {
        return user;
    }


    // =========================================================
    // SET USER
    // =========================================================

    public void setUser(User user) {
        this.user = user;
    }


    // =========================================================
    // GET EXPIRY DATE
    // =========================================================

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }


    // =========================================================
    // SET EXPIRY DATE
    // =========================================================

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }


    // =========================================================
    // CHECK IF TOKEN HAS EXPIRED
    // =========================================================

    public boolean isExpired() {

        return LocalDateTime.now()
                .isAfter(expiryDate);
    }
}